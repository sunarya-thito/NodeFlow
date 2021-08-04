package thito.nodeflow.library;

import org.jsoup.*;
import org.jsoup.nodes.*;
import thito.nodeflow.api.config.*;

import java.io.*;
import java.util.*;

public class StandardJavaDocsParser implements JavaDocsParser {
    private final List<ClassMember> members = new ArrayList<>();
    @Override
    public ClassMember parse(String string) {
        Document doc = Jsoup.parse(string);
        Element packageElement = doc.selectFirst("div.header > div > a");
        Element identity = doc.selectFirst("section.description > pre");
        Element descriptionElement = doc.selectFirst("section.description > div.block");
        Element implementations = doc.selectFirst("section.description > dl > dd");
        String packageName = textOrNull(packageElement);
        ArrayList<String> allies = new ArrayList<>();
        for (String ally : doc.selectFirst(".inheritance").text().split("\n")) {
            allies.add(ally.trim());
        }
        for (Node node : implementations.childNodes()) {
            if (node instanceof TextNode) {
                for (String cl : ((TextNode) node).text().split(",")) {
                    cl = cl.trim();
                    if (!cl.isEmpty()) {
                        allies.add(cl);
                    }
                }
            } else if (node instanceof Element) {
                String title = node.attr("title");
                String inner = ((Element) node).text();
                int index = title.lastIndexOf(' ');
                if (index >= 0) {
                    title = title.substring(index).trim();
                }
                allies.add(title + '.' + inner);
            }
        }
        extract(identity);
        ClassMember classMember = new ClassMember(identity.text());
        classMember.packageName = packageName;
        classMember.description = descriptionElement != null ? descriptionElement.html() : "";
        Class<? extends Member> memberType = null;
        for (Element e : doc.select("li.blockList")) {
            extract(e);
            StringBuilder content = new StringBuilder();
            for (String x : e.wholeText().split("\n")) {
                x = x.trim();
                if (!x.isEmpty()) {
                    if (content.length() > 0) {
                        content.append('\n');
                    }
                    content.append(x);
                }
            }
            StringTokenizer tokenizer = new StringTokenizer(content.toString(), "\n");
            int count = 0;
            while (tokenizer.hasMoreTokens()) {
                count++;
                String token = tokenizer.nextToken();
                if (count == 1) {
                    if (token.equals("Constructor Details")) {
                        memberType = ConstructorMember.class;
                        break;
                    } else if (token.equals("Method Details")) {
                        memberType = MethodMember.class;
                        break;
                    } else if (token.equals("Field Details")) {
                        memberType = FieldMember.class;
                        break;
                    }
                }
                if (memberType != null) {
                    // the Member Name
                    // Code...
                    // Description
                    if (tokenizer.hasMoreTokens()) {
                        StringBuilder builder = new StringBuilder();
                        while (tokenizer.hasMoreTokens()) {
                            String code = tokenizer.nextToken();
                            builder.append(code+" ");
                            if (memberType == MethodMember.class || memberType == ConstructorMember.class) {
                                if (code.endsWith(")")) {
                                    break;
                                }
                            } else {
                                if (code.endsWith(token)) {
                                    break;
                                }
                            }
                        }
                        Member member;
                        if (memberType == ConstructorMember.class) {
                            member = new ConstructorMember(builder.toString());
                        } else if (memberType == MethodMember.class) {
                            member = new MethodMember(builder.toString());
                        } else {
                            member = new FieldMember(builder.toString());
                        }
                        builder = new StringBuilder();
                        while (tokenizer.hasMoreTokens()) {
                            builder.append(tokenizer.nextToken()+"\n");
                        }
                        member.description = builder.toString().trim();
                        classMember.add(member);
                    }
                }
            }
        }
        members.add(classMember);
        return classMember;
    }

    @Override
    public List<ClassMember> getClasses() {
        return members;
    }

    public static void extract(Element element) {
        for (Element find : element.getElementsByTag("a")) {
            String title = find.attr("title");
            if (title != null && title.contains(" in ")) {
                int index = title.lastIndexOf(' ');
                if (index >= 0) {
                    title = title.substring(index).trim();
                }
                find.replaceWith(new TextNode(title + "." + find.text()));
            }
        }
    }

    private static String textOrNull(Element element) {
        return element == null ? null : element.text();
    }

    @Override
    public String exportGeneralized() {
        Section allClasses = Section.newMap();
        for (ClassMember classMember : members) {
            Section classSection = Section.newMap();
            if (classMember.superclass != null) {
                classSection.set(fromTypeToSection(classMember.superclass), "superclass");
            }
            if (classMember.interfaces != null) {
                ListSection list = Section.newList();
                for (TypeCode i : classMember.interfaces) {
                    list.add(fromTypeToSection(i));
                }
                if (!list.isEmpty()) {
                    classSection.set(list, "interfaces");
                }
            }
            putMember(classSection, classMember);
            String name = classMember.name;
            String pack = classMember.packageName;
            if (pack != null && !pack.isEmpty()) {
                name = pack + "." + name;
            }
            ListSection list = Section.newList();
            for (ConstructorMember constructorMember : classMember.constructors) {
                list.add(fromConstructorToSection(constructorMember));
            }
            if (!list.isEmpty()) {
                classSection.set(list, "constructors");
            }
            list = Section.newList();
            for (FieldMember fieldMember : classMember.fields) {
                list.add(fromFieldToSection(fieldMember));
            }
            if (!list.isEmpty()) {
                classSection.set(list, "fields");
            }
            list = Section.newList();
            for (MethodMember methodMember : classMember.methods) {
                list.add(fromMethodToSection(methodMember));
            }
            if (!list.isEmpty()) {
                classSection.set(list, "methods");
            }
            allClasses.set(classSection, name);
        }
        StringWriter writer = new StringWriter();
        Section.saveYaml(allClasses, writer);
        return writer.toString();
    }

    private Section fromMethodToSection(MethodMember method) {
        Section section = Section.newMap();
        section.set(fromTypeToSection(method.type), "type");
        section.set(method.name, "name");
        putMember(section, method);
        if (method.arguments != null) {
            ListSection list = Section.newList();
            for (FieldMember arg : method.arguments) {
                list.add(fromFieldToSection(arg));
            }
            if (!list.isEmpty()) {
                section.set(list, "arguments");
            }
        }
        return section;
    }

    private Section fromConstructorToSection(ConstructorMember constructor) {
        Section section = Section.newMap();
        putMember(section, constructor);
        if (constructor.arguments != null) {
            ListSection list = Section.newList();
            for (FieldMember arg : constructor.arguments) {
                list.add(fromFieldToSection(arg));
            }
            if (!list.isEmpty()) {
                section.set(list, "arguments");
            }
        }
        return section;
    }

    private Section fromFieldToSection(FieldMember field) {
        Section section = Section.newMap();
        section.set(fromTypeToSection(field.type), "type");
        section.set(field.name, "name");
        putMember(section, field);
        return section;
    }

    private Section putMember(Section section, Member member) {
        section.set(member.modifiers, "modifiers");
        section.set(member.description, "description");
        if (member.generics != null) {
            ListSection list = Section.newList();
            for (GenericTypeCode g : member.generics) {
                list.add(fromTypeToSection(g));
            }
            if (!list.isEmpty()) {
                section.set(list, "generics");
            }
        }
        if (member.annotations != null) {
            ListSection list = Section.newList();
            for (AnnotationCode a : member.annotations) {
                list.add(fromAnnotationToSection(a));
            }
            if (!list.isEmpty()) {
                section.set(list, "annotations");
            }
        }
        return section;
    }

    private Section fromAnnotationToSection(AnnotationCode code) {
        Section section = Section.newMap();
        section.set(code.getType(), "name");
        Group group = code.getGroup();
        if (group != null) {
            ListSection list = Section.newList();
            for (JavaTokenizer tokenizer : group.getMembers()) {
                JavaTokenizer.AnnotationValue value = tokenizer.eatAnnotationValue();
                if (value != null && value.getValue() != null) {
                    Section val = Section.newMap();
                    val.set(value.getName(), "name");
                    Object constantValue = value.getValue().eatConstantValue();
                    val.set(put(constantValue), "value");
                    list.add(val);
                }
            }
            if (!list.isEmpty()) {
                section.set("values", list);
            }
        }
        return section;
    }

    private Object put(Object value) {
        if (value instanceof Object[]) {
            ListSection sec = Section.newList();
            for (Object obj : (Object[]) value) {
                sec.add(put(obj));
            }
            return sec;
        }
        if (value instanceof JavaTokenizer.EnumValue) {
            Section section = Section.newMap();
            section.set(((JavaTokenizer.EnumValue) value).getEnumType(), "enum");
            section.set(((JavaTokenizer.EnumValue) value).getValue(), "value");
            return section;
        }
        return value;
    }

    private Section fromTypeToSection(TypeCode code) {
        Section section = Section.newMap();
        section.set(code.getName(), "name");
        if (code.getGenerics() != null) {
            ListSection list = Section.newList();
            for (TypeCode generic : code.getGenerics()) {
                list.add(fromTypeToSection(generic));
            }
            section.set(section, "generics");
        }
        if (code instanceof GenericTypeCode && ((GenericTypeCode) code).direction != null) {
            section.set(((GenericTypeCode) code).direction.name(), "direction");
        }
        return section;
    }

}
