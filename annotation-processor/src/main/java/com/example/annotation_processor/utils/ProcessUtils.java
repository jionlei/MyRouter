package com.example.annotation_processor.utils;

import com.example.annotation.bean.RouterBean;

import java.util.Map;

import javax.tools.Diagnostic;

public class ProcessUtils {
    public static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }

    public static boolean isEmpty(Map<?, ?> map) {
        return map == null || map.size() == 0;
    }

    public static boolean checkPath(String path) throws IllegalArgumentException{
        if (ProcessUtils.isEmpty(path) && !path.startsWith("/")) {
            throw new IllegalArgumentException("@MyRouter group is illegal");
            //messager.printMessage(Diagnostic.Kind.ERROR, "@MyRouter path is illegal");
        }
        // path 只有一个并且是 /
        if(path.lastIndexOf("/") == 0) {
            throw new IllegalArgumentException("@MyRouter group is illegal");
        }
        //   messager.printMessage(Diagnostic.Kind.ERROR, "@MyRouter path is illegal");
        return true;
    }

    public static String getGroupFromPath(String path) throws IllegalArgumentException {
        if (checkPath(path)) {
            return path.substring(1, path.indexOf("/", 1));
        } else {
            throw new IllegalArgumentException("获取不合法的Group参数");
        }
    }

    public static boolean handleGroupName(String group, String groupFromPath, String moduleName, RouterBean routerBean) throws IllegalArgumentException{
// 处理group
        if (group.isEmpty()) {  // 默认缺省状态下
            //校验 path 和 module name
            if (!groupFromPath.equals(moduleName)) {  // group 不一致
                throw new IllegalArgumentException("@MyRouter group is illegal");
          //      messager.printMessage(Diagnostic.Kind.ERROR, "@MyRouter group is illegal");
            }
            routerBean.setGroup(groupFromPath);
        } else if (!group.equals(moduleName) || !groupFromPath.equals(moduleName)) {
        //    messager.printMessage(Diagnostic.Kind.ERROR, "@MyRouter group is illegal");
            throw new IllegalArgumentException("@MyRouter group is illegal");
        }
        return true;
    }
}
