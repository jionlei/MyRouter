package com.example.annotation.bean;

import javax.lang.model.element.Element;

public class RouterBean {
    public enum ROUTER_TYPE {
        ACTIVITY
    }

    public RouterBean() {

    }

    public RouterBean(String mPath, String mGroup) {
        this.mPath = mPath;
        this.mGroup = mGroup;
    }

    public RouterBean(ROUTER_TYPE type, Class<?> myClass, String mPath, String mGroup) {
        this.type = type;
        this.myClass = myClass;
        this.mPath = mPath;
        this.mGroup = mGroup;
    }

    public RouterBean(ROUTER_TYPE type, Element element, Class<?> myClass, String mPath, String mGroup) {
        this.type = type;
        this.element = element;
        this.myClass = myClass;
        this.mPath = mPath;
        this.mGroup = mGroup;
    }

    public static RouterBean create(ROUTER_TYPE type,
                                    Class<?> myClass,
                                    String mPath, String mGroup) {

        return new RouterBean(type, myClass, mPath, mGroup);
    }

    private ROUTER_TYPE type;
    private Element element;

    private Class<?> myClass;

    private String mPath;

    private String mGroup;

    public ROUTER_TYPE getType() {
        return type;
    }

    public void setType(ROUTER_TYPE type) {
        this.type = type;
    }

    public Element getElement() {
        return element;
    }

    public void setElement(Element element) {
        this.element = element;
    }

    public Class<?> getMyClass() {
        return myClass;
    }

    public void setMyClass(Class<?> myClass) {
        this.myClass = myClass;
    }

    public String getPath() {
        return mPath;
    }

    public void setPath(String mPath) {
        this.mPath = mPath;
    }

    public String getGroup() {
        return mGroup;
    }

    public void setGroup(String mGroup) {
        this.mGroup = mGroup;
    }


    @Override
    public String toString() {
        return "RouterBean{" +
                "type=" + type +
                ", element=" + element +
                ", myClass=" + myClass +
                ", mPath='" + mPath + '\'' +
                ", mGroup='" + mGroup + '\'' +
                '}';
    }
}
