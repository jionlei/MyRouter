package com.example.annotation_processor;

import static com.example.annotation_processor.config.ProcessorConfig.GROUP_MAP;
import static com.example.annotation_processor.config.ProcessorConfig.GROUP_METHOD_NAME;
import static com.example.annotation_processor.config.ProcessorConfig.ROUTER_PACKAGE_NAME;

import com.example.annotation.MyRouter;
import com.example.annotation.bean.RouterBean;
import com.example.annotation_processor.config.ProcessorConfig;
import com.example.annotation_processor.utils.ProcessUtils;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.WildcardTypeName;
import com.sun.org.apache.bcel.internal.classfile.ClassFormatException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;

import javax.annotation.processing.SupportedOptions;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;


@AutoService(Processor.class)
@SupportedAnnotationTypes(value = {ROUTER_PACKAGE_NAME}) // 表示我要处理那个注解
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedOptions({ProcessorConfig.MODULE_NAME, ProcessorConfig.APT_PACKAGE_NAME})
public class MyRouterProcessor extends AbstractProcessor {

    private Filer mFiler;

    // 操作Element的工具类，函数、类、属性都是Element
    private Elements elementsTool;


    // 编译期打印日志
    private Messager messager;

    // 类信息的工具类
    private Types typesTool;
    private String moduleName;
    private String aptPackageName;

    // key group; value = activity list
    private Map<String, List<RouterBean>> groupPathMap;

    // key group, value 对应 每个module下注解MyRouter
    private Map<String, String> groupClassMap;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        typesTool = processingEnv.getTypeUtils();
        messager = processingEnv.getMessager();
        elementsTool = processingEnv.getElementUtils();
        mFiler = processingEnv.getFiler();
        Map<String, String> options = processingEnv.getOptions();
        groupPathMap = new HashMap<>();
        groupClassMap = new HashMap<>();
        // 这两个是在对用的module下gradle脚本上生成的
        // javaCompileOptions {
        //            annotationProcessorOptions{
        //                arguments = [moduleName: this.project.getName(), packageNameForAPT: packageNameForAPT]
        //            }
        //        }
        moduleName = options.get(ProcessorConfig.MODULE_NAME);
        aptPackageName = options.get(ProcessorConfig.APT_PACKAGE_NAME);
        if (moduleName == null || aptPackageName == null) {
            messager.printMessage(Diagnostic.Kind.ERROR, "APT环境出错");
        } else {
            messager.printMessage(Diagnostic.Kind.NOTE, "APT ENV OK, moduleName = " + moduleName + ", aptPackageName = " + aptPackageName);
        }
    }


    @Override
    public Set<String> getSupportedAnnotationTypes() {
        // 也可以在这里
        Set<String> annotationSet = new LinkedHashSet<>();
        annotationSet.add(MyRouter.class.getCanonicalName());
        return annotationSet;
    }


    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnvironment) {
        //没有对应的注解需要处理
        if (annotations.isEmpty()) {
            messager.printMessage(Diagnostic.Kind.NOTE, "没有发现被ARouter注解的类");
            return false; // 标注注解处理器没有工作
        }

        // activity类，用于判断注解时候使用正确
        TypeElement activityTypeElement = elementsTool.getTypeElement(ProcessorConfig.ACTIVITY_PACKAGE_NAME);

        // 获得类对象可以操作
        TypeMirror activityTypeMirror = activityTypeElement.asType();

        // 获取被MyRouter注解的类
        Set<? extends Element> elementsAnnotatedWith = roundEnvironment.getElementsAnnotatedWith(MyRouter.class);

        for (Element element : elementsAnnotatedWith) {
            String className = element.getSimpleName().toString();
            messager.printMessage(Diagnostic.Kind.NOTE, "className = " + className);

            // 获取被注解类的 ARouter group和 path
            MyRouter myRouter = element.getAnnotation(MyRouter.class);
            RouterBean routerBean = new RouterBean(myRouter.path(), myRouter.group());
            routerBean.setElement(element);

            // 判断当前被注解的类是Activity
            TypeMirror type = element.asType();
            // 判断是否是子类
            if (typesTool.isSubtype(type, activityTypeMirror)) {
                routerBean.setType(RouterBean.ROUTER_TYPE.ACTIVITY);
            } else {
                throw new ClassFormatException("@MyRouter is not support current Type");
            }

            // 校验 path 和group
            if (handleRouterBean(routerBean)) {
                List<RouterBean> list = groupPathMap.get(routerBean.getGroup());
                if (ProcessUtils.isEmpty(groupPathMap)) {
                    List<RouterBean> routerBeanList = new ArrayList<>();
                    routerBeanList.add(routerBean);
                    groupPathMap.put(routerBean.getGroup(), routerBeanList);
                } else {
                    list.add(routerBean);
                }
            }
        }
        // 获取MyRouterGroup类
        TypeElement groupType = elementsTool.getTypeElement(ProcessorConfig.ROUTER_API_GROUP);
        // 获取MyRouterPath类
        TypeElement pathType = elementsTool.getTypeElement(ProcessorConfig.ROUTER_API_PATH);

        try {
            createPathFile(pathType);
            createGroupPath(groupType, pathType);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return false;
    }

    private void createGroupPath(TypeElement groupType, TypeElement pathType) throws IOException {
        if (ProcessUtils.isEmpty(groupPathMap) || ProcessUtils.isEmpty(groupClassMap)) {
            return;
        }
        // Class<? extends MyRouterPath>
        ParameterizedTypeName secondTypeName = ParameterizedTypeName.get(ClassName.get(Class.class), WildcardTypeName.subtypeOf(ClassName.get(pathType)));
        //Map<>String,Class<? extends MyRouterPath>
        ParameterizedTypeName topTypeName = ParameterizedTypeName.get(ClassName.get(Map.class), ClassName.get(String.class), secondTypeName);

        // Method
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(GROUP_METHOD_NAME)
                .addAnnotation(ClassName.get(Override.class))
                .addModifiers(Modifier.PUBLIC)
                .returns(topTypeName);

        /*
         * Map<String, Class<? extends MyRouterPath>> groupMap = new HashMap<>();
         */

        methodBuilder.addStatement("$T<$T, $T<? extends $T>> $N = new $T<>()",
                ClassName.get(Map.class),
                ClassName.get(String.class),
                ClassName.get(Class.class),
                ClassName.get(pathType),
                GROUP_MAP,
                ClassName.get(HashMap.class));

        for (Map.Entry<String, String> entry : groupClassMap.entrySet()) {
            methodBuilder.addStatement("$N.put($S, $T.class)",
                    GROUP_MAP,
                    entry.getKey(),
                    ClassName.get(aptPackageName, entry.getValue())
            );

        }
            /*
             * return groupMap;
             */
            methodBuilder.addStatement("return $N", GROUP_MAP);
            String finalClassName = ProcessorConfig.GROUP_FILE_NAME + moduleName;
            messager.printMessage(Diagnostic.Kind.NOTE, "Group:最终生成的文件名称是：" + aptPackageName + "." + finalClassName);

            JavaFile.builder(aptPackageName,
                            TypeSpec.classBuilder(finalClassName)
                                    .addSuperinterface(ClassName.get(groupType))
                                    .addModifiers(Modifier.PUBLIC)
                                    .addMethod(methodBuilder.build())
                                    .build())
                    .build()
                    .writeTo(mFiler);
    }

    private void createPathFile(TypeElement pathType) throws IOException {
        if (ProcessUtils.isEmpty(groupPathMap)) {
            return;
        }
        /*
         * public Map<String, RouterBean> getPathMap() {
         */
        // Map<String, RouterBean>

        // 1. Map<String, RouterBean> 可以共用
        ParameterizedTypeName methodReturn = ParameterizedTypeName.get(ClassName.get(Map.class),
                ClassName.get(String.class),
                ClassName.get(RouterBean.class));

        Set<Map.Entry<String, List<RouterBean>>> entries = groupPathMap.entrySet();

        for (Map.Entry<String, List<RouterBean>> entry : entries) {

            /*
             * @Override
             * public Map<String, RouterBean> getPathMap() {
             */
            // Map<String, RouterBean>
            MethodSpec.Builder builder = MethodSpec
                    .methodBuilder(ProcessorConfig.PATH_METHOD_NAME)
                    .addAnnotation(ClassName.get(Override.class))
                    .addModifiers(Modifier.PUBLIC)
                    .returns(methodReturn);

            /*
             * Map<String, RouterBean> pathMap = new HashMap<>();
             */
            builder.addStatement("$T<$T,$T> $N = new $T<>()",
                    ClassName.get(Map.class),
                    ClassName.get(String.class),
                    ClassName.get(RouterBean.class),
                    ProcessorConfig.PATH_MAP,
                    ClassName.get(HashMap.class));

            for (RouterBean routerBean : entry.getValue()) {
                /*
                 * pathMap.put("/personal/PersonalMainActivity",
                 *                 RouterBean.create(RouterBean.TypeEnum.ACTIVITY,
                 *                                   Order_MainActivity.class,
                 *                            "/personal/PersonalMainActivity",
                 *                           "personal"));
                 */
                // 这里新创建一个 保留骨干信息

                builder.addStatement("$N.put($S,$T.create($T.$L,$T.class,$S,$S))",
                        ProcessorConfig.PATH_MAP,
                        routerBean.getPath(),
                        ClassName.get(RouterBean.class),
                        ClassName.get(RouterBean.ROUTER_TYPE.class),
                        routerBean.getType(),
                        ClassName.get((TypeElement) routerBean.getElement()),
                        routerBean.getPath(),
                        routerBean.getGroup()
                );
            }
            // return pathMap;
            builder.addStatement("return $N", ProcessorConfig.PATH_MAP);
            // public class ARouter$$Path$$groupName() implement ARouterPath {}
            String routerClassName = ProcessorConfig.PATH_FILE_NAME + entry.getKey();
            messager.printMessage(Diagnostic.Kind.NOTE, "Path:最终生成的文件名称是：" + aptPackageName + "." + routerClassName);

            TypeSpec typeSpec = TypeSpec.classBuilder(routerClassName)
                    .addSuperinterface(ClassName.get(pathType))
                    .addModifiers(Modifier.PUBLIC)
                    .addMethod(builder.build())
                    .build();

            JavaFile.builder(aptPackageName, typeSpec)
                    .build()
                    .writeTo(mFiler);

            // 将生成的类和group 打包成map 方便后面查找
            groupClassMap.put(entry.getKey(), routerClassName);
        }


    }

    private boolean handleRouterBean(RouterBean routerBean) {
        String path = routerBean.getPath();
        String group = routerBean.getGroup();

//        if (ProcessUtils.isEmpty(path) && !path.startsWith("/")) {
//            messager.printMessage(Diagnostic.Kind.ERROR, "@MyRouter path is illegal");
//            return false;
//        }
//        // path 只有一个并且是 /
//        if (0 == path.lastIndexOf("/")) {
//            messager.printMessage(Diagnostic.Kind.ERROR, "@MyRouter path is illegal");
//            return false;
//        }
//
//         校验group Name 原则是 moduleName == groupName == path的首个/ ... / 中间的要一致
//         考虑到group 可能为默认空参 因此在此条件下校验  moduleName == path的首个/ ... / 中间的要一致

        String tmpGroup = ProcessUtils.getGroupFromPath(path);

        return ProcessUtils.handleGroupName(group, tmpGroup, moduleName, routerBean);
    }
}
