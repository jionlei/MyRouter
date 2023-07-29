package com.example.router_api;


import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.util.LruCache;

import com.example.annotation.bean.RouterBean;
import com.example.annotation_processor.utils.ProcessorUtils;

import java.util.Map;


public class RouterManger {
    private static volatile RouterManger routerManger = null;
    private static final String TAG = "RouterManger";

    private static final int mMaxCacheSize = 128;
    private final LruCache<String, MyRouterGroup> mGroupClassCache = new LruCache<>(mMaxCacheSize);
    private final LruCache<String, MyRouterPath> mPathClassCache = new LruCache<>(mMaxCacheSize);

    // 一级缓存直接找对应path下的routerBean
    private final LruCache<String, RouterBean> mRouterBeanCache = new LruCache<>(mMaxCacheSize);
    private Router myRouter;

    public static RouterManger getInstance() {
        if (routerManger == null) {
            synchronized (RouterManger.class) {
                if (routerManger == null) {
                    routerManger = new RouterManger();
                }
            }
        }
        return routerManger;
    }

    public static void setGroupCacheSize(int mGroupCacheSize) {
        getInstance().mGroupClassCache.resize(mGroupCacheSize);
    }

    public static void setPathCacheSize(int mPathCacheSize) {
        getInstance().mPathClassCache.resize(mPathCacheSize);
    }

    public static void setRouterBeanCache(int mRouterBeanCacheSize) {
        getInstance().mRouterBeanCache.resize(mRouterBeanCacheSize);
    }

    private RouterManger() {
        myRouter = new Router();
    }

    public void navigation(Context context, BundleManager bundleManager) {
        // ARouter$$Group$$order
        // 全类名
        String group = getInstance().myRouter.getGroup();
        // 这个是已经缓存的routerBena
        String path = getInstance().myRouter.getPath();

        String groupClassName = Router.APT_PACKAGE_NAME + "." + Router.FILE_GROUP_NAME + group;
        Log.i(TAG, "navigation: groupClassName = " + groupClassName);

        RouterBean routerBean = mRouterBeanCache.get(path);
        // 一级缓存里有routerBean，直接用
        if (routerBean == null) {
            // 已经缓存的了RouterPath 不用再通过 groupMap查找了
            MyRouterPath myRouterPath = mPathClassCache.get(path);
            if (myRouterPath != null) {
                Map<String, RouterBean> pathMap = myRouterPath.getPathMap();
                // 拿到routerBean
                routerBean = pathMap.get(path);
            } else {
                // 先找到 group 保存的类
                MyRouterGroup groupClass = mGroupClassCache.get(groupClassName);
                if (groupClass == null) {
                    try {
                        groupClass = (MyRouterGroup) Class.forName(groupClassName).newInstance();
                        mGroupClassCache.put(group, groupClass);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                // 找到对应group下path类 --- 》 去拿 map 匹配path --- 》 routerbean
                Class<? extends MyRouterPath> routerPathClass = groupClass.getGroupMap().get(group);
                if (routerPathClass == null) {
                    throw new RuntimeException("找不到routerPathClass");
                }
                try {
                    MyRouterPath routerPath = routerPathClass.newInstance();
                    routerBean = routerPath.getPathMap().get(path);
                    mPathClassCache.put(path, routerPath);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        // 还要在做一次判空
        if (routerBean != null) {
            switch (routerBean.getType()){
                case ACTIVITY:
                    Intent intent = new Intent(context, routerBean.getMyClass());
                    intent.putExtras(bundleManager.getBundle());
                    context.startActivity(intent);
                    break;
                default:
                    Log.e(TAG, "navigation: 其他类型暂不支持");
                    break;
            }
        }
    }

    public static BundleManager build(String path) {
        String groupName = ProcessorUtils.getGroupFromPath(path);
        //
        if (groupName.isEmpty()) throw new IllegalArgumentException("Group 参数不合法");
        getInstance().myRouter.setPath(path);
        getInstance().myRouter.setGroup(groupName);
        return new BundleManager();
    }

    public Router getMyRouter() {
        return myRouter;
    }

    public void setMyRouter(Router myRouter) {
        this.myRouter = myRouter;
    }

}
