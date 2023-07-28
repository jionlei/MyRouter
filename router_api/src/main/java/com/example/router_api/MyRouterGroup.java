package com.example.router_api;

import java.util.Map;

// 注解处理器生成自动实现

// 每个module下会通过注解生成一个ARouter$$Path$$xxx 的类，
public interface MyRouterGroup {
    Map<String, Class<? extends MyRouterPath>> getGroupMap();
}

/**
 * 例如：Personal Group：
 * // 这就是要用 APT 动态生成的代码
 * public class ARouter$$Group$$personal implements ARouterGroup {
 *
 *     @Override
 *     public Map<String, Class<? extends ARouterPath>> getGroupMap() {
 *         Map<String, Class<? extends ARouterPath>> groupMap = new HashMap<>();
 *         groupMap.put("personal", ARouter$$Path$$personal.class);
 *         return groupMap;
 *     }
 * }
 */
