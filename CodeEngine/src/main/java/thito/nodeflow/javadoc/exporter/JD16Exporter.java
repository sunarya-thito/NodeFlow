package thito.nodeflow.javadoc.exporter;

import com.google.gson.*;
import org.jsoup.*;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.*;
import org.jsoup.select.*;
import thito.nodeflow.javadoc.*;
import thito.nodeflow.javadoc.tokenizer.*;

import java.io.*;
import java.lang.reflect.*;
import java.nio.file.*;
import java.util.*;
import java.util.function.*;

public class JD16Exporter {
    public static void main(String[] args) {
        File outputDirectory = new File(System.getProperty("outputDirectory"));
        String javaDocsUrl = System.getProperty("javaDocsUrl");
        System.out.println("Output Directory: "+outputDirectory);
        System.out.println("Java Docs URL: "+javaDocsUrl);
        JD16Exporter exporter = new JD16Exporter(javaDocsUrl, url -> {
            try {
                return Jsoup.connect(url).execute().body();
            } catch (Throwable t) {
                throw new RuntimeException(t);
            }
        });
        try {
            exporter.export(outputDirectory);
        } catch (IOException e) {
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

    public void export(File outputDirectory) throws IOException {
        this.outputDirectory = outputDirectory;
        listAllClasses();
        File target = new File(outputDirectory, "class-list.json");
        Files.writeString(target.toPath(), gson.toJson(map.keySet()));
    }

    private void listAllClasses() throws IOException {
        String url = baseURL + "allclasses-index.html";
        Document doc = Jsoup.parse(htmlSupplier.apply(url), baseURL);
        for (Element e : doc.select(".col-first.all-classes-table a")) {
            String href = e.attr("abs:href");
            if (href != null && href.startsWith(baseURL)) {
                readClass(href);
            }
        }
    }

    private String readClassName(String url) throws IOException {
        String name = urlMap.get(url);
        if (name == null) {
            name = readClass(url).getName();
            urlMap.put(url, name);
        }
        return name;
    }

    private JavaClass readClass(String url) throws IOException {
        Document doc = Jsoup.parse(htmlSupplier.apply(url), url);
        JavaClass javaClass = new JavaClass();
        Element module = doc.selectFirst("body > div.flex-box > div > main > div.header > div:nth-child(1) > a");
        if (module != null) {
            String text = module.ownText();
            if (text != null && !text.isEmpty()) {
                javaClass.setModuleName(text);
            }
        }
        Element pkg = doc.selectFirst("body > div.flex-box > div > main > div.header > div:nth-child(2) > a");
        if (pkg != null) {
            String text = pkg.ownText();
            if (text != null && !text.isEmpty()) {
                javaClass.setPackageName(text);
            }
        }
        Element title = doc.selectFirst("body > div.flex-box > div > main > div > h1");
        Objects.requireNonNull(title);
        String[] s = title.ownText().split(" ");
        String simpleName = s[1].split("<")[0].replace('.', '$'); // also handles the $ for inner classes
        urlMap.put(url, javaClass.getPackageName() != null ? javaClass.getPackageName() + "." + simpleName : simpleName);
        String signatureData = extractData(doc.selectFirst(".type-signature")).toString();
        TypeTokenizer declaration = new TypeTokenizer(0, signatureData.toCharArray());
        declaration.eatWhitespace(); // optional

        // CLASS ANNOTATION
        JavaAnnotation[] annotations = readAnnotations(declaration);
        javaClass.setAnnotations(annotations);

        declaration.eatWhitespace(); // optional
        // CLASS MODIFIERS
        javaClass.setModifiers(declaration.eatModifiers());

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
        if (declaration.eat("extends")) {
            declaration.eatWhitespace();
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
                System.out.println("READING INNER "+href);
                subCl.add(readClass(href).getName());
            }
        }
        if (!subCl.isEmpty()) javaClass.setInnerClasses(subCl.toArray(new String[0]));
        Elements allFields = doc.select(".field-details .member-list .detail");
        List<JavaMember> members = new ArrayList<>();
        for (Element e : allFields) {
            JavaField field = new JavaField();
            String memberSignature = extractData(e.selectFirst(".member-signature")).toString();
            TypeTokenizer memberDeclaration = new TypeTokenizer(0, memberSignature.toCharArray());
            memberDeclaration.eatWhitespace();
            field.setAnnotations(readAnnotations(memberDeclaration));
            memberDeclaration.eatWhitespace();
            field.setModifiers(memberDeclaration.eatModifiers());
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
            TypeTokenizer memberDeclaration = new TypeTokenizer(0, memberSignature.toCharArray());
            memberDeclaration.eatWhitespace();
            method.setAnnotations(readAnnotations(memberDeclaration));
            memberDeclaration.eatWhitespace();
            if (memberDeclaration.eat("default")) {
                method.setDefaultMethod(true);
            }
            memberDeclaration.eatWhitespace();
            method.setModifiers(memberDeclaration.eatModifiers());
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
            TypeTokenizer memberDeclaration = new TypeTokenizer(0, memberSignature.toCharArray());
            memberDeclaration.eatWhitespace();
            method.setAnnotations(readAnnotations(memberDeclaration));
            memberDeclaration.eatWhitespace();
            method.setModifiers(memberDeclaration.eatModifiers());
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
        File target = new File(outputDirectory, (moduleName == null || moduleName.isEmpty() ? "UNNAMED" : moduleName) + "/" + javaClass.getName().replace('.', '/') + ".json");
        target.getParentFile().mkdirs();
        System.out.println("WRITING "+javaClass.getName());
        Files.writeString(target.toPath(), gson.toJson(javaClass));
        return javaClass;
    }

    private TypeReference[] readGenericParameters(TypeTokenizer tokenizer) {
        if (tokenizer.eat('<')) {
            TypeReference[] references = tokenizer.eatGenericSplit(',');
            tokenizer.eatWhitespace();
            if (tokenizer.eat('>')) {
                return references;
            }
        }
        return null;
    }

    private JavaAnnotation[] readAnnotations(TypeTokenizer typeTokenizer) {
        List<JavaAnnotation> annotations = new ArrayList<>();
        while (typeTokenizer.hasNext()) {
            typeTokenizer.eatWhitespace();
            JavaAnnotation annotation = typeTokenizer.eatAnnotation();
            if (annotation == null) break;
            annotations.add(annotation);
        }
        return annotations.toArray(new JavaAnnotation[0]);
    }

    private List<JavaMethod.Parameter> readParameters(TypeTokenizer tokenizer) {
        List<JavaMethod.Parameter> params = new ArrayList<>();
        tokenizer.eatWhitespace();
        tokenizer.eat('(');
        while (tokenizer.hasNext()) {
            tokenizer.eatWhitespace();
            JavaAnnotation[] annotations = readAnnotations(tokenizer);
            tokenizer.eatWhitespace();
            JavaMethod.Parameter parameter = tokenizer.eatParameter();
            if (parameter == null) break;
            parameter.setAnnotations(annotations);
            params.add(parameter);
            if (parameter.isVarargs()) break;
            tokenizer.eatWhitespace();
            if (!tokenizer.eat(',')) break;
        }
        tokenizer.eatWhitespace();
        if (!tokenizer.eat(')')) throw new IllegalArgumentException("invalid param: "+tokenizer);
        return params;
    }

    private StringBuilder extractData(Element elements) throws IOException {
        StringBuilder signature = new StringBuilder();
        for (Node node : elements.childNodes()) {
            if (node instanceof TextNode) {
                signature.append(((TextNode) node).text());
            } else if (node instanceof Element) {
                String title = node.attr("title");
                if (title.startsWith("enum class in ") ||
                title.startsWith("class in ") ||
                title.startsWith("interface in ")) {
                    String[] split = title.split(" in ");
                    String pkg = split[1];
                    String simpleName = ((Element) node).text().replace('.', '$');
                    if (pkg.equalsIgnoreCase("<unnamed>")) {
                        signature.append(simpleName);
                    } else {
                        signature.append(pkg).append(".").append(simpleName);
                    }
                } else if (title.startsWith("type property in ")) {
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
