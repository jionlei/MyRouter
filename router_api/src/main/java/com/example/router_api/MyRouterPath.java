package com.example.router_api;


import com.example.annotation.bean.RouterBean;

import java.util.Map;

// 这个是需要让注解处理器自动实现生成的接口
public interface MyRouterPath {

    //Map <path, routerBean>
    Map<String, RouterBean> getPathMap();

}

/**
 * 例如：Personal Path：
 * // 这就是要用 APT 动态生成的代码
 * public class ARouter$$Path$$personal implements ARouterPath {
 *
 *     @Override
 *     public Map<String, RouterBean> getPathMap() {
 *         Map<String, RouterBean> pathMap = new HashMap<>();
 *         pathMap.put("/personal/PersonalMainActivity",
 *                 RouterBean.create(RouterBean.TypeEnum.ACTIVITY,
 *                                   Order_MainActivity.class,
 *                            "/personal/PersonalMainActivity",
 *                           "personal"));
 *         return pathMap;
 *     }
 * }
 */

