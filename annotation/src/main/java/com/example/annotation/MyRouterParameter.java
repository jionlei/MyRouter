package com.example.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.SOURCE)
public @interface MyRouterParameter {
    String key() default "";  // 传递参数时的key 相当于 getIntExtra 和 putIntExtra 的key
        // 默认为""表示使用 变量名作为key
    int defInt() default 0;
    boolean defBoolean() default false;
}
