package com.example.annotation_processor.config;


public interface ProcessorConfig {
    // MyRouter.class.getCanonicalName()
    String ROUTER_PACKAGE_NAME = "com.example.annotation.MyRouter";  //需要处理的注解
    String MODULE_NAME = "moduleName";

    String APT_PACKAGE_NAME = "packageNameForAPT";

    String ACTIVITY_PACKAGE_NAME = "android.app.Activity";  // 所有acitivity继承的基类，apt判断 MyRouter 注解是否正确用在了Activity类上

    String ROUTER_API_PACKAGE_NAME = "com.example.router_api";

    String ROUTER_API_GROUP = ROUTER_API_PACKAGE_NAME + ".MyRouterGroup";

    String ROUTER_API_PATH = ROUTER_API_PACKAGE_NAME + ".MyRouterPath";

    String PATH_METHOD_NAME = "getPathMap";

    String GROUP_METHOD_NAME = "getGroupMap";

    String PATH_MAP = "pathMap";


    String GROUP_MAP = "groupMap";

    String PATH_FILE_NAME = "ARouter$$Path$$";

    String GROUP_FILE_NAME = "ARouter$$Group$$";
}
