package thito.nodeflow.javadoc.exporter;

import com.google.gson.*;
import org.jsoup.*;
import org.jsoup.nodes.*;
import org.jsoup.select.*;
import thito.nodeflow.javadoc.*;
import thito.nodeflow.javadoc.element.*;
import thito.nodeflow.javadoc.element.reference.*;
import thito.nodeflow.javadoc.tokenizer.*;

import java.io.*;
import java.lang.reflect.*;
import java.net.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.function.*;

public class JD16Exporter {
    public static void main(String[] args) {
        String outDir = System.getProperty("outputDirectory");
        String javaDocsUrl = System.getProperty("javaDocsUrl");
//        if (outDir == null) outDir = "Generated Docs";
//        if (javaDocsUrl == null) javaDocsUrl = "https://docs.oracle.com/en/java/javase/16/docs/api/";
        System.out.println("Output Directory: "+outDir);
        System.out.println("Java Docs URL: "+javaDocsUrl);
        if (outDir == null || javaDocsUrl == null) return;
        File outputDirectory = new File(outDir);
        JD16Exporter exporter = new JD16Exporter(javaDocsUrl, url -> {
            try (InputStreamReader reader = new InputStreamReader(new URL(url).openStream())) {
                char[] buffer = new char[1024 * 8];
                int len;
                StringBuilder builder = new StringBuilder();
                while ((len = reader.read(buffer, 0, buffer.length)) != -1) {
                    builder.append(buffer, 0, len);
                }
                return builder.toString();
            } catch (Throwable t) {
                throw new RuntimeException(t);
            }
        });
        try {
            exporter.export(outputDirectory);
        } catch (Exception e) {
            if (exporter.pool != null) {
                exporter.pool.shutdown();
            }
            throw new RuntimeException("Failed to export", e);
        }
    }
    private Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private Map<String, String> noteMapping = new HashMap<>();
    private Map<String, JavaClass> map = new HashMap<>();
    private Map<String, String> urlMap = new HashMap<>();
    private File outputDirectory;
    private String baseURL;
    private Function<String, String> htmlSupplier;
    private ExecutorService pool = Executors.newFixedThreadPool(20);
    private Map<String, CompletableFuture<String>> downloaded = new HashMap<>();

    public JD16Exporter(String baseURL, Function<String, String> htmlSupplier) {
        if (!baseURL.endsWith("/")) {
            baseURL += "/";
        }
        this.baseURL = baseURL;
        this.htmlSupplier = htmlSupplier;
        noteMapping.put("See Also:", "see");
        noteMapping.put("Throws:", "throws");
        noteMapping.put("API Note:", "apiNote");
        noteMapping.put("Implementation Requirements:", "implSpec");
        noteMapping.put("Implementation Note:", "implNote");
        noteMapping.put("Since:", "since");
        noteMapping.put("Parameters:", "param");
        noteMapping.put("Returns:", "return");
    }

    private CompletableFuture<String> download(String url) {
        CompletableFuture<String> future = downloaded.get(url);
        if (future == null) {
            downloaded.put(url, future = new CompletableFuture<>());
            CompletableFuture<String> finalFuture = future;
            pool.submit(() -> {
                finalFuture.complete(htmlSupplier.apply(url));
            });
        }
        return future;
    }

    public void export(File outputDirectory) throws Exception {
        this.outputDirectory = outputDirectory;
        listAllClasses();
        File target = new File(outputDirectory, "class-list.json");
        Files.writeString(target.toPath(), gson.toJson(map.keySet()));
    }

    private void listAllClasses() throws Exception {
        String url = baseURL + "allclasses-index.html";
        Document doc = Jsoup.parse(htmlSupplier.apply(url), baseURL);
        int count = 0;
        for (Element e : doc.select(".col-first.all-classes-table a")) {
            String href = e.attr("abs:href");
            if (href != null && href.startsWith(baseURL)) {
                count++;
                System.out.println("START DOWNLOADING ("+count+") "+e.text()+" (" + href+ ")");
                download(href);
            }
        }
        int size = downloaded.size();
        for (CompletableFuture<?> f : downloaded.values()) {
            f.thenAccept(result -> {
                int progress = downloadProgress.incrementAndGet();
                System.out.println("DOWNLOADING [" + progress + "/" + size + "]");
            });
        }
        for (CompletableFuture<?> f : downloaded.values()) {
            f.get();
        }
        for (String u : new HashSet<>(downloaded.keySet())) readClass(u);
        pool.shutdown();
    }

    private AtomicInteger downloadProgress = new AtomicInteger();

    private String readClassName(String url) throws Exception {
        String name = urlMap.get(url);
        if (name == null) {
            name = readClass(url).getName();
            urlMap.put(url, name);
        }
        return name;
    }

    private JavaClass readClass(String url) throws Exception {
        Document doc = Jsoup.parse(download(url).get(), url);
        JavaClass javaClass = new JavaClass();
        Elements subTitle = doc.select(".header .sub-title");
        for (Element e : subTitle) {
            String text = e.text();
            if (text != null) {
                if (text.toLowerCase().startsWith("package ")) {
                    javaClass.setPackageName(text.substring("package ".length()));
                } else if (text.toLowerCase().startsWith("module ")) {
                    javaClass.setModuleName(text.substring("module ".length()));
                }
            }
        }
        if (javaClass.getModuleName() == null) {
            javaClass.setModuleName("ALL-UNNAMED");
        }
        Element title = doc.selectFirst(".header > h1");
        Objects.requireNonNull(title);
        String s = title.ownText();
        if (s.startsWith("Class ")) {
            s = s.substring("Class ".length());
        } else if (s.startsWith("Record Class ")) {
            s = s.substring("Record Class ".length());
        } else if (s.startsWith("Enum Class ")) {
            s = s.substring("Enum Class ".length());
        } else if (s.startsWith("Interface ")) {
            s = s.substring("Interface ".length());
        } else if (s.startsWith("Enum ")) {
            s = s.substring("Enum ".length());
        }
        String simpleName = s.split("<")[0].replace('.', '$'); // also handles the $ for inner classes
        urlMap.put(url, javaClass.getPackageName() != null ? javaClass.getPackageName() + "." + simpleName : simpleName);
        String signatureData = extractData(doc.selectFirst(".type-signature")).toString();
        JavaTokenizer declaration = new JavaTokenizer(0, signatureData.toCharArray());
        declaration.eatWhitespace(); // optional

        // CLASS ANNOTATION
        List<JavaAnnotation> annotations = readAnnotations(declaration);

        declaration.eatWhitespace(); // optional or crucial?
        // CLASS MODIFIERS
        javaClass.setModifiers(declaration.eatModifiers());

        declaration.eatWhitespace(); // optional
        // ANNOTATION ALSO GOES AFTER MODIFIER LIKE WTF
        annotations.addAll(readAnnotations(declaration));

        declaration.eatWhitespace(); // optional
        // CLASS DECLARATION
        javaClass.setType(declaration.eatClassType());
        if (javaClass.getType() == ClassType.INTERFACE) {
            javaClass.setModifiers(javaClass.getModifiers() | Modifier.INTERFACE);
        }
        declaration.eatWhitespace();
        String simpleTypeName = declaration.eatTypeName();
        String pkName = javaClass.getPackageName();
        javaClass.setName(pkName == null || pkName.isEmpty() ? simpleTypeName.replace('.', '$') : pkName + "." + simpleTypeName.replace('.', '$'));
        int lastDot = simpleTypeName.lastIndexOf('.');
        if (lastDot >= 0) {
            simpleTypeName = simpleTypeName.substring(lastDot + 1);
        }
        javaClass.setSimpleName(simpleTypeName);
        declaration.eatWhitespace();
        javaClass.setGenericParameters(readGenericParameters(declaration));
        declaration.eatWhitespace();
        // TO ELIMINATE RECORD TYPE
        readParameters(declaration);
        declaration.eatWhitespace();
        if (declaration.eat("extends")) {
            declaration.eatWhitespace();
            // interface A extends B, C
            TypeReference[] references = declaration.eatSplit(',');
            if (references.length == 1) {
                javaClass.setSuperClass(references[0]);
            } else if (references.length > 1) {
                javaClass.setInterfaces(references);
            }
        } else {
            javaClass.setSuperClass(new ClassTypeReference("java.lang.Object"));
        }
        if (javaClass.getInterfaces() != null) {
            declaration.eatWhitespace();
            if (declaration.eat("implements")) {
                declaration.eatWhitespace();
                javaClass.setInterfaces(declaration.eatSplit(','));
            }
        }
        String name = javaClass.getName();
        JavaClass existing = map.get(javaClass.getModuleName() + "/" + name);
        if (existing != null) return existing;
        map.put(javaClass.getModuleName() + "/" + name, javaClass);
        scanNotes(doc.selectFirst(".description .notes:last-child"), javaClass);
        Elements nestedClasses = doc.select(".nested-class-summary .member-name-link > a:nth-child(1)");
        Set<String> subCl = new HashSet<>();
        for (Element e : nestedClasses) {
            String href = e.attr("abs:href");
            if (href != null && !href.isEmpty()) {
                subCl.add(readClass(href).getName());
            }
        }
        Element classNotes = doc.selectFirst(".description .block");
        if (classNotes != null) {
            javaClass.setComment(classNotes.html());
        }
        if (!subCl.isEmpty()) javaClass.setInnerClasses(subCl.toArray(new String[0]));
        Elements allFields = doc.select(".field-details .member-list .detail");
        List<JavaMember> members = new ArrayList<>();
        for (Element e : allFields) {
            JavaField field = new JavaField();
            String memberSignature = extractData(e.selectFirst(".member-signature")).toString();
            JavaTokenizer memberDeclaration = new JavaTokenizer(0, memberSignature.toCharArray());
            memberDeclaration.eatWhitespace();
            List<JavaAnnotation> annotationList = readAnnotations(memberDeclaration);
            memberDeclaration.eatWhitespace();
            field.setModifiers(memberDeclaration.eatModifiers());
            memberDeclaration.eatWhitespace();
            // second annotation
            annotationList.addAll(readAnnotations(memberDeclaration));
            field.setAnnotations(annotationList.toArray(new JavaAnnotation[0]));
            memberDeclaration.eatWhitespace();
            field.setType(memberDeclaration.eatType());
            memberDeclaration.eatWhitespace();
            field.setName(memberDeclaration.eatName());
            Element element = e.selectFirst(".block");
            if (element != null) {
                field.setComment(element.text());
            }
            scanNotes(e.selectFirst(".notes"), field);
            members.add(field);
        }
        Elements allMethods = doc.select(".method-details .member-list .detail");
        for (Element e : allMethods) {
            JavaMethod method = new JavaMethod();
            String memberSignature = extractData(e.selectFirst(".member-signature")).toString();
            JavaTokenizer memberDeclaration = new JavaTokenizer(0, memberSignature.toCharArray());
            memberDeclaration.eatWhitespace();
            List<JavaAnnotation> annotationList = readAnnotations(memberDeclaration);
            memberDeclaration.eatWhitespace();
            if (memberDeclaration.eat("default")) {
                method.setDefaultMethod(true);
            }
            memberDeclaration.eatWhitespace();
            method.setModifiers(memberDeclaration.eatModifiers());
            memberDeclaration.eatWhitespace();
            // second annotation
            annotationList.addAll(readAnnotations(memberDeclaration));
            method.setAnnotations(annotationList.toArray(new JavaAnnotation[0]));
            memberDeclaration.eatWhitespace();
            method.setGenericParameters(readGenericParameters(memberDeclaration));
            memberDeclaration.eatWhitespace();
            method.setReturnType(memberDeclaration.eatType());
            memberDeclaration.eatWhitespace();
            method.setName(memberDeclaration.eatName());
            memberDeclaration.eatWhitespace();
            method.setParameters(readParameters(memberDeclaration).toArray(new JavaMethod.Parameter[0]));
            memberDeclaration.eatWhitespace();
            if (memberDeclaration.eat("throws")) {
                memberDeclaration.eatWhitespace();
                method.setThrowsClasses(memberDeclaration.eatSplit(','));
            }
            Element element = e.selectFirst(".block");
            if (element != null) {
                method.setComment(element.text());
            }
            scanNotes(e.selectFirst(".notes"), method);
            members.add(method);
        }
        Elements allConstructors = doc.select(".constructor-details .member-list .detail");
        for (Element e : allConstructors) {
            JavaMethod method = new JavaMethod();
            String memberSignature = extractData(e.selectFirst(".member-signature")).toString();
            JavaTokenizer memberDeclaration = new JavaTokenizer(0, memberSignature.toCharArray());
            memberDeclaration.eatWhitespace();
            List<JavaAnnotation> annotationList = readAnnotations(memberDeclaration);
            memberDeclaration.eatWhitespace();
            method.setModifiers(memberDeclaration.eatModifiers());
            memberDeclaration.eatWhitespace();
            // second annotation place
            annotationList.addAll(readAnnotations(memberDeclaration));
            method.setAnnotations(annotationList.toArray(new JavaAnnotation[0]));
            memberDeclaration.eatWhitespace();
            method.setGenericParameters(readGenericParameters(memberDeclaration));
            memberDeclaration.eatWhitespace();
            if (!memberDeclaration.eat(javaClass.getSimpleName())) throw new IllegalArgumentException("invalid constructor: "+memberSignature+" expected: "+javaClass.getName());
            memberDeclaration.eatWhitespace();
            method.setParameters(readParameters(memberDeclaration).toArray(new JavaMethod.Parameter[0]));
            memberDeclaration.eatWhitespace();
            if (memberDeclaration.eat("throws")) {
                memberDeclaration.eatWhitespace();
                method.setThrowsClasses(memberDeclaration.eatSplit(','));
            }
            Element element = e.selectFirst(".block");
            if (element != null) {
                method.setComment(element.text());
            }
            scanNotes(e.selectFirst(".notes"), method);
            members.add(method);
        }
        javaClass.setMembers(members.toArray(new JavaMember[0]));
        String moduleName = javaClass.getModuleName();
        File target = new File(outputDirectory, (moduleName == null || moduleName.isEmpty() ? "ALL-UNNAMED" : moduleName) + "/" + javaClass.getName().replace('.', '/') + ".json");
        target.getParentFile().mkdirs();
        System.out.println("WRITING "+javaClass.getName());
        Files.writeString(target.toPath(), gson.toJson(javaClass));
        return javaClass;
    }

    private TypeReference[] readGenericParameters(JavaTokenizer tokenizer) {
        if (tokenizer.eat('<')) {
            TypeReference[] references = tokenizer.eatGenericSplit(',');
            tokenizer.eatWhitespace();
            if (tokenizer.eat('>')) {
                return references;
            }
        }
        return null;
    }

    private List<JavaAnnotation> readAnnotations(JavaTokenizer javaTokenizer) {
        return javaTokenizer.eatAnnotations();
    }

    private List<JavaMethod.Parameter> readParameters(JavaTokenizer tokenizer) {
        int mark = tokenizer.getIndex();
        if (tokenizer.eat('(')) {
            List<JavaMethod.Parameter> params = new ArrayList<>();
            while (tokenizer.hasNext()) {
                tokenizer.eatWhitespace();
                JavaAnnotation[] annotations = readAnnotations(tokenizer).toArray(new JavaAnnotation[0]);
                tokenizer.eatWhitespace();
                JavaMethod.Parameter parameter = tokenizer.eatParameter();
                if (parameter == null) break;
                parameter.setAnnotations(annotations);
                params.add(parameter);
                if (parameter.getVarArgs() != null) break;
                tokenizer.eatWhitespace();
                if (!tokenizer.eat(',')) break;
            }
            tokenizer.eatWhitespace();
            if (!tokenizer.eat(')')) {
                tokenizer.setIndex(mark);
                return null;
            }
            return params;
        }
        return null;
    }

    private StringBuilder extractData(Element elements) throws Exception {
        StringBuilder signature = new StringBuilder();
        for (Node node : elements.childNodes()) {
            if (node instanceof TextNode) {
                signature.append(((TextNode) node).text());
            } else if (node instanceof Element) {
                String title = node.attr("title");
                if (title.startsWith("enum class in ") ||
                title.startsWith("class in ") ||
                title.startsWith("interface in ") ||
                title.startsWith("class or interface in ") ||
                title.startsWith("enum in ")) {
                    String[] split = title.split(" in ");
                    String pkg = split[1];
                    String simpleName = ((Element) node).text().replace('.', '$');
                    if (simpleName.startsWith("@")) {
                        signature.append("@");
                        simpleName = simpleName.substring(1);
                    }
                    if (pkg.equalsIgnoreCase("<unnamed>")) {
                        signature.append(simpleName);
                    } else {
                        signature.append(pkg).append(".").append(simpleName);
                    }
                } else if (title.startsWith("type property in ") || title.startsWith("type parameter in ")) {
                    String[] split = title.split(" in ");
                    String pkg = split[1];
                    String simpleName = ((Element) node).text();
                    if (pkg.equalsIgnoreCase("<unnamed>")) {
                        signature.append(simpleName);
                    } else {
                        signature.append(readClassName(node.attr("abs:href"))).append("#").append(simpleName);
                    }
                } else {
                    signature.append(extractData((Element) node));
                }
            }
        }
        return signature;
    }

    private void scanNotes(Element notes, JavaMember member) {
        if (notes == null) return;
        Element header = null;
        List<String> list = null;
        for (Element note : notes.children()) {
            if (note.tagName().equals("dt")) {
                if (header != null) {
                    member.getTagMap().computeIfAbsent(noteMapping.getOrDefault(header.text(), header.text()), x -> new ArrayList<>()).addAll(list);
                }
                header = note;
                list = new ArrayList<>();
            } else if (note.tagName().equals("dd")) {
                if (list != null) {
                    Elements a = note.select("a");
                    a.forEach(e -> e.attr("href", e.attr("abs:href")));
                    list.add(note.html());
                }
            }
        }
        if (header != null) {
            member.getTagMap().computeIfAbsent(noteMapping.getOrDefault(header.text(), header.text()), x -> new ArrayList<>()).addAll(list);
        }
    }


}
