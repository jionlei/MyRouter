package com.example.annotation_processor.config;


public interface ProcessorConfig {
    // MyRouter.class.getCanonicalName()
    String ROUTER_PACKAGE_NAME = "com.example.annotation.MyRouter";  //需要处理的注解

    String PARAMETER_CONTEXT_PACKAGE_NAME = "android.content.Context";

    String MODULE_NAME = "moduleName";

    String KEY_APT_PACKAGE_NAME = "packageNameForAPT";

    String ACTIVITY_PACKAGE_NAME = "android.app.Activity";  // 所有acitivity继承的基类，apt判断 MyRouter 注解是否正确用在了Activity类上

    String ROUTER_API_PACKAGE_NAME = "com.example.router_api";

    String ROUTER_API_GROUP = ROUTER_API_PACKAGE_NAME + ".MyRouterGroup";

    String ROUTER_API_PATH = ROUTER_API_PACKAGE_NAME + ".MyRouterPath";

    String PATH_METHOD_NAME = "getPathMap";

    String GROUP_METHOD_NAME = "getGroupMap";

    String PATH_MAP = "pathMap";


    String GROUP_MAP = "groupMap";

    String PATH_FILE_NAME = "MyRouter$$Path$$";

    String GROUP_FILE_NAME = "MyRouter$$Group$$";

    // OrderActivity$$MyRouterParameter
    String PARAMETER_FILE_NAME = "$$IMyRouterParameter";

    String PARAMETER_ROUTER_INTERFACE_NAME = ".IMyRouterParameter";

    String APT_PACKAGE_NAME = "customrouter_apt";
    String PARAMETER_METHOD_NAME = "setParameter";

    String PARAMETER_TARGET_NAME = "targetParameter";

    String PARAMETER_PACKAGE_NAME = ROUTER_API_PACKAGE_NAME + PARAMETER_ROUTER_INTERFACE_NAME;

}
