package com.example.annotation_processor;

import com.example.annotation.MyRouterParameter;
import com.example.annotation_processor.config.ProcessorConfig;
import com.example.annotation_processor.utils.ProcessorUtils;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

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
import javax.annotation.processing.SupportedOptions;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;


@AutoService(Processor.class)
@SupportedOptions({ProcessorConfig.MODULE_NAME, ProcessorConfig.KEY_APT_PACKAGE_NAME})
//@SupportedAnnotationTypes(value = {}) // 表示我要处理那个注解
public class MyRouterParameterProcessor extends AbstractProcessor {

    private Filer mFiler;
    private Messager messager;

    private Elements elementTools;

    private Types typeTools;

    private String moduleName;


    // 全类名下 parameter
    private final Map<TypeElement, List<Element>> parameterMap = new HashMap<>();

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        typeTools = processingEnv.getTypeUtils();
        elementTools = processingEnv.getElementUtils();
        messager = processingEnv.getMessager();
        mFiler = processingEnv.getFiler();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotationSet = new LinkedHashSet<>();
        annotationSet.add(MyRouterParameter.class.getCanonicalName());
        return annotationSet;
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        if (!ProcessorUtils.isEmpty(set)) {
            Set<? extends Element> elementsAnnotatedWith = roundEnvironment.getElementsAnnotatedWith(MyRouterParameter.class);
            for (Element element : elementsAnnotatedWith) {
                // 找对应的包裹类
                if (element.getKind() == ElementKind.FIELD) {
                    TypeElement enclosingElement = (TypeElement) element.getEnclosingElement(); // 找到包裹类

                    if (parameterMap.containsKey(enclosingElement)) {
                        parameterMap.get(enclosingElement).add(element);
                    } else {
                        List<Element> list = new ArrayList<>();
                        list.add(element);
                        parameterMap.put(enclosingElement, list);
                    }

                } else {
                    messager.printMessage(Diagnostic.Kind.ERROR, "MyRouterParameter需要注解在属性上");
                }

            }
            if (ProcessorUtils.isEmpty(parameterMap)) return true;
            TypeElement activityElement = elementTools.getTypeElement(ProcessorConfig.ACTIVITY_PACKAGE_NAME); // activity 类名
            TypeElement parameterElement = elementTools.getTypeElement(ProcessorConfig.PARAMETER_PACKAGE_NAME);  // IMyRouterParameter 接口


//            @Override
//            public void getParameter(Context targetParameter)
            TypeElement contextElement = elementTools.getTypeElement(ProcessorConfig.PARAMETER_CONTEXT_PACKAGE_NAME);
            if(activityElement == null) {
                messager.printMessage(Diagnostic.Kind.ERROR, ProcessorConfig.PARAMETER_CONTEXT_PACKAGE_NAME + "is not found");
                return false;
            }

            // TODO 这里可以优化， 这样就不用在脚本里做类型强转了
            ParameterSpec getParameterSpec = ParameterSpec.
                    builder(TypeName.get(activityElement.asType()), ProcessorConfig.PARAMETER_TARGET_NAME)
                    .build();

            Set<Map.Entry<TypeElement, List<Element>>> entries = parameterMap.entrySet();
            for (Map.Entry<TypeElement, List<Element>> entry : entries) {
                TypeElement typeElement = entry.getKey();
                String realActivityClass = typeElement.getQualifiedName().toString();
                if (!typeTools.isSubtype(typeElement.asType(), activityElement.asType())) { // 判断是否是activity的子类
                    throw new RuntimeException("@MyRouterParameter注解目前只支持Activity");
                }
                String parameterClassName = ProcessorUtils.handleClassName(realActivityClass);
                // Com_xxx_activity$$MyRouterParameter
//                String parameterClassName = className + ;
                messager.printMessage(Diagnostic.Kind.NOTE, "生成的APT参数类文件: " + parameterClassName);

                // 1. 获取activity的类型
                ClassName activityName = ClassName.get(typeElement);

                ParameterMethodFactory parameterMethodFactory = new ParameterMethodFactory.Builder()
                        .setParameterSpec(getParameterSpec)
                        .setMessager(messager)
                        .setClassName(activityName)
                        .setMethodName(ProcessorConfig.PARAMETER_METHOD_NAME)
                        .setReturn(TypeName.VOID)
                        .build();

                parameterMethodFactory.addFirstState();

                for (Element fieldElement : entry.getValue()) {
                        parameterMethodFactory.addBuildState(fieldElement);
                }
                try {
                    JavaFile.builder(activityName.packageName(),
                            TypeSpec.classBuilder(parameterClassName)
                                    .addSuperinterface(ClassName.get(parameterElement))
                                    .addModifiers(Modifier.PUBLIC)
                                    .addMethod(parameterMethodFactory.getMethodSpecBuild().build())
                                    .build())
                            .build().writeTo(mFiler);
                } catch (IOException e) {
                     e.printStackTrace();
                }
            }
        }
        return false;
    }
}
