package org.hydra.renamer;

import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

import org.hydra.util.Utils;

public class ClassInfo extends AccessableNode implements Comparable<ClassInfo> {
    private ClassMap map;
    // internalName like java/lang/String
    private String className;
    // direct superClass
    private String superClassName;
    // outer class name
    private String outerClass;
    private Set<String> innerClassNames = new TreeSet<String>();
    // all interfaces
    private Set<String> interfaceNames = new TreeSet<String>();
    // direct children
    private Set<String> childrenNames = new TreeSet<String>();

    private Set<MethodInfo> methods = new TreeSet<MethodInfo>();
    private Set<FieldInfo> fields = new TreeSet<FieldInfo>();

    public ClassInfo(ClassMap map, String name, int access) {
        super(access);
        this.map = map;
        this.className = name;
        this.map.addClassInfo(this);
    }

    public void setSuperClass(String superClass) {
        this.superClassName = superClass;
    }

    public String getClassName() {
        return className;
    }

    public String getClassShortName() {
        return Utils.getShortName(this.className);
    }

    public ClassInfo getSuperClass() {
        return this.map.getClassInfo(this.superClassName);
    }

    public String getSuperClassName() {
        return this.superClassName;
    }

    public Set<String> getInterfaceNames() {
        return this.interfaceNames;
    }

    public Set<ClassInfo> getInterfaces() {
        return str2info(this.interfaceNames);
    }

    private Set<ClassInfo> str2info(Set<String> names) {
        Set<ClassInfo> info = new TreeSet<ClassInfo>();
        for (String s : names) {
            if (this.map.getClassInfo(s) != null) {
                info.add(this.map.getClassInfo(s));
            }
        }
        return info;
    }

    public Set<String> getChildrenNames() {
        return this.childrenNames;
    }

    public Set<MethodInfo> getMethods() {
        return Collections.unmodifiableSet(methods);
    }

    public Set<FieldInfo> getFields() {
        return Collections.unmodifiableSet(fields);
    }

    public String toString() {
        StringBuilder buff = new StringBuilder();
        buff.append("ClassInfo{").append(this.isInterface() ? "interface" : "class").append(":").append(this.className);
        if (this.superClassName != null) {
            buff.append(",super:").append(superClassName);
        }
        if (this.interfaceNames.size() > 0) {
            if (this.isInterface()) {
                buff.append(",superInterfaces[");
            } else {
                buff.append(",interfaces[");
            }
            for (String info : this.interfaceNames) {
                buff.append(info).append(",");
            }
            buff.append("]");
        }
        if (this.fields.size() > 0) {
            buff.append(",fields:[");
            for (FieldInfo p : this.fields) {
                buff.append(p.getName()).append("/").append(p.getDesc()).append(",");
            }
            buff.append("]");
        }
        if (this.methods.size() > 0) {
            buff.append(",methods:[");
            for (MethodInfo p : this.methods) {
                buff.append(p.getName()).append("/").append(p.getDesc()).append(",");
            }
            buff.append("]");
        }
        buff.append("}");
        return buff.toString();
    }

    public void addMethod(MethodInfo methodInfo) {
        this.methods.add(methodInfo);
        methodInfo.setEnclosedClass(this);
    }

    public void addField(FieldInfo fieldInfo) {
        this.fields.add(fieldInfo);
        fieldInfo.setEnclosedClass(this);
    }

    public void addInterface(String classInfo) {
        this.interfaceNames.add(classInfo);
    }

    public void addChild(String info) {
        this.childrenNames.add(info);
    }

    public void setOuterClass(String name) {
        this.outerClass = name;
    }

    public void addInnerClass(String name) {
        this.innerClassNames.add(name);
    }

    public Set<String> getDependencies() {
        Set<String> set = new TreeSet<String>() {
            private static final long serialVersionUID = 8452117487482010642L;

            @Override
            public boolean add(String e) {
                if (e != null) {
                    return super.add(e);
                }
                return false;
            }

        };

        set.add(this.outerClass);
        set.add(this.superClassName);
        for (String s : this.interfaceNames) {
            set.add(s);
        }
        for (FieldInfo s : this.fields) {
            set.add(s.getType());
        }
        for (MethodInfo m : this.methods) {
            set.addAll(m.getDependencies());
        }

        return set;
    }

    @Override
    public int compareTo(ClassInfo o) {
        return this.className.compareTo(o.className);
    }
}
