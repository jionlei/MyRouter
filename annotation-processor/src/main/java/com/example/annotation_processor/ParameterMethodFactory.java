package com.example.annotation_processor;

import com.example.annotation.MyRouterParameter;
import com.example.annotation_processor.utils.ProcessorUtils;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;

import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;

public class ParameterMethodFactory {
    private MethodSpec.Builder method;

    private ParameterSpec parameterSpec;

    private ClassName className;

    private Messager messager;

    private final String VAR_ACTIVITY = "activity";

    private ParameterMethodFactory(Builder builder) {
        className = builder.className;
        messager = builder.messager;
        parameterSpec = builder.parameterSpec;
        method = MethodSpec.methodBuilder(builder.methodName)
                .addAnnotation(ClassName.get(Override.class))
                .addModifiers(Modifier.PUBLIC)
                .addParameter(builder.parameterSpec)
                .returns(builder.returnType);
    }

//    public void addFirstLineCode(){
//        method.addStatement("$T t = ($T) "+ ProcessorConfig.PARAMETER_TARGET_NAME, className, className);
//    }

    public void addFirstState() {
        //  PersonalActivity activity = (PersonalActivity) targetParameter;
        // $T $S = = ($T) $L;

        method.addStatement(" $T $N = ($T) $N", className, VAR_ACTIVITY, className, parameterSpec.name);

    }

    public void addBuildState(Element element) {
        VariableElement variableElement = (VariableElement) element;
        TypeMirror typeMirror = element.asType();
      //  ClassName.get(typeMirror);
        TypeKind kind = typeMirror.getKind();
        String fieldName = element.getSimpleName().toString();// 获取变量名 对于类是类名 这里是变量
        MyRouterParameter elementAnnotation = element.getAnnotation(MyRouterParameter.class);
        String field = elementAnnotation.key(); // 拿到注解的key
        String intentKey = (field != null) && !field.equals("") ? field : fieldName;

        // left state
        String leftCode = "$N" + "." + variableElement.getSimpleName() + " = ";
        StringBuilder rightCode = new StringBuilder(parameterSpec.name + ".getIntent()");
        switch (kind) {
            case INT:
                int valueInt = elementAnnotation.defInt();
                rightCode.append(".getIntExtra(").append("$S").append(",").append(valueInt).append(")");
                break;
            case BOOLEAN:
                boolean valueBoolean = elementAnnotation.defBoolean();
                rightCode.append(".getBooleanExtra(").append("$S").append(",").append(valueBoolean).append(")");
                break;
            default: {
                messager.printMessage(Diagnostic.Kind.ERROR, "被注解的类型 kind 是什么呢？" + kind);
                throw new IllegalArgumentException(kind+"类型目前不支持   " + "index = " + kind.ordinal());
            }
        }
        method.addStatement(leftCode + rightCode, VAR_ACTIVITY, intentKey);
    }

    public MethodSpec.Builder getMethodSpecBuild() {
        return method;
    }

    public static class Builder {

        private Messager messager;

        private ClassName className;

        private String methodName;

        private ParameterSpec parameterSpec;

        private TypeName returnType;

        public Builder setParameterSpec(ParameterSpec parameterSpec) {
            this.parameterSpec = parameterSpec;
            return this;
        }

        public Builder setMethodName(String methodName) {
            this.methodName = methodName;
            return this;
        }

        public Builder setClassName(ClassName className) {
            // 这里已经传进来了真实的acitivity类了
            this.className = className;
            return this;
        }

        public Builder setReturn(TypeName returnType) {
            this.returnType = returnType;
            return this;
        }

        public Builder setMessager(Messager messager) {
            this.messager = messager;
            return this;
        }

        public ParameterMethodFactory build() {
            if (parameterSpec == null) {
                throw new IllegalArgumentException("parameterSpec方法参数体为空");
            }

            if (className == null) {
                throw new IllegalArgumentException("方法中的className为空");
            }

            if (messager == null) {
                throw new IllegalArgumentException("message为空，messager用于错误报告，不能为空");
            }

            if (ProcessorUtils.isEmpty(methodName)) {
                throw new IllegalArgumentException("methodName不能为空");
            }

            if (returnType == null) {
                throw new IllegalArgumentException("returnType不能为空");
            }
            return new ParameterMethodFactory(this);
        }
    }
}
