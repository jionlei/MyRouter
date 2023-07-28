package com.example.router_api;

import java.util.HashMap;
import java.util.Map;

public class Router {

    // 自定义router 生成路径这里要写上
    public static String APT_PACKAGE_NAME = "customrouter_apt";
    public static String FILE_GROUP_NAME = "ARouter$$Group$$";

    private String path;
   private String group;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }
}
