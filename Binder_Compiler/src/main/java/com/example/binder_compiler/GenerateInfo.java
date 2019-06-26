package com.example.binder_compiler;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;

/**
 * 生成的信息
 *
 * @author YGX
 */
public class GenerateInfo {

    /**
     * 需要自动生成代码的类, 例如要根据MainActivity中的注解自动生成代码; 这里就是MainActivity的元素
     */
    private TypeElement mSourceElement;

    /**
     * 需要自动生成代码的类, 例如要根据MainActivity中的注解自动生成代码; 这里就是MainActivity的类名
     */
    private String mSourceClassName;

    /**
     * 包名
     */
    private String packageName;

    /**
     * 自动生成代码的集合
     */
    private Map<Integer, VariableElement> mGenerateFieldMaps = new HashMap<>();

    private Map<int[], ExecutableElement> mGenerateOnClickMaps = new HashMap<>();

    public GenerateInfo(Elements elementUtils, TypeElement source) {
        mSourceElement = source;
        mSourceClassName = mSourceElement.getSimpleName().toString();
        PackageElement packageInstance = elementUtils.getPackageOf(source);
        packageName = packageInstance.getQualifiedName().toString();
    }

    public void addBindViewMap(Integer key, VariableElement value) {
        mGenerateFieldMaps.put(key, value);
    }

    public void addOnClickMap(int[] key, ExecutableElement value) {
        mGenerateOnClickMaps.put(key, value);
    }

    public String getGenerateClassName() {
        return packageName + "." + mSourceClassName + "$$Injecter";
    }

    public String generateJavaCode() {
        StringBuilder sBuilder = new StringBuilder();

        sBuilder.append("package ").append(packageName).append(";\n\n");
        sBuilder.append("import com.example.ity_annotation.IInjecter;\n");
        sBuilder.append("import android.view.View;\n");
        sBuilder.append("\n");
        sBuilder.append("// Generate By Annotation Processor");
        sBuilder.append("\n\n");

        sBuilder.append("public class ").append(mSourceClassName)
                .append("$$Injecter implements IInjecter<").append(mSourceClassName).append(", View").append("> {");

        sBuilder.append("\n\n");

        sBuilder.append("\t @Override public void inject(final ").append(mSourceClassName).append(" host, final View source){\n");

        Map<Integer, String> findedViewMap = new HashMap<>();
        mGenerateFieldMaps.forEach(new BiConsumer<Integer, VariableElement>() {
            @Override
            public void accept(Integer integer, VariableElement variableElement) {
                String fieldName = variableElement.getSimpleName().toString();
                String fieldType = variableElement.asType().toString();
                sBuilder.append("\t\t").append("host.").append(fieldName)
                        .append(" = (").append(fieldType).append(")").append("source.findViewById(").append(integer).append(");");
                sBuilder.append("\n");
                findedViewMap.put(integer, "host." + fieldName);
            }
        });
        sBuilder.append("\n");
        mGenerateOnClickMaps.forEach(new BiConsumer<int[], ExecutableElement>() {
            @Override
            public void accept(int[] ids, ExecutableElement executableElement) {
                String methodName = executableElement.getSimpleName().toString();
                if (ids.length == 0) {
                    return;
                }

                /*
                    View.OnClickeListener clickListener$xx = new View.OnClickListener(){
                        @Override public void onClick(View view){
                            host.ooo();
                        }
                 */
                int arrayHash = Arrays.hashCode(ids);
                sBuilder.append("\t\t").append("View.OnClickListener clickListener$").append(arrayHash >= 0 ? "0" : "1").append(Math.abs(arrayHash))
                        .append(" = new View.OnClickListener(){\n")
                        .append("\t\t\t").append("@Override public void onClick(View view){\n")
                        .append("\t\t\t\t").append("host.").append(methodName).append("(view);\n")
                        .append("\t\t\t").append("}\n")
                        .append("\t\t").append("};\n");

                for (int id : ids) {
                    String fieldName = findedViewMap.get(id);
                    if (fieldName != null) {
                        sBuilder.append("\t\t").append(fieldName).append(".setOnClickListener(clickListener$").append(arrayHash >= 0 ? "0" : "1").append(Math.abs(arrayHash)).append(");\n");
                    } else {
                        sBuilder.append("\t\tsource.findViewById(").append(id).append(").setOnClickListener(clickListener$").append(arrayHash >= 0 ? "0" : "1").append(Math.abs(arrayHash)).append(");\n");
                    }
                }
            }
        });
        sBuilder.append("\t}\n");
        sBuilder.append("}");
        return sBuilder.toString();
    }

    @Override
    public String toString() {
        return "GenerateInfo{" +
                "mSourceElement=" + mSourceElement +
                ", mSourceClassName='" + mSourceClassName + '\'' +
                ", packageName='" + packageName + '\'' +
                '}';
    }

    public TypeElement getTypeElement() {
        return mSourceElement;
    }
}
