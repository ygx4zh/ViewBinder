package com.example.binder_compiler;

import com.example.ity_annotation.BindView;
import com.example.ity_annotation.OnClick;
import com.google.auto.service.AutoService;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

@AutoService(Processor.class)
public class BinderProcessor extends AbstractProcessor {
    private static final String TAG = "BinderProcessor";
    private Messager envMessager;
    private Elements envElementUtils;
    private Filer envFiler;
    private BuildLogger buildLogger;

    private Map<String, GenerateInfo> mCacheMap = new HashMap<>();

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new HashSet<>();

        types.add(BindView.class.getCanonicalName());
        types.add(OnClick.class.getCanonicalName());

        return types;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        // 获取日志数据管理器
        envMessager = processingEnv.getMessager();

        // 获取操作元素的工具类
        envElementUtils = processingEnv.getElementUtils();

        // 获取写入文件的类
        envFiler = processingEnv.getFiler();

        buildLogger = new BuildLogger(envMessager);
    }

    private void logd(String tag, String message, Object... args) {
        if (args.length > 0) message = String.format(message, args);
        envMessager.printMessage(Diagnostic.Kind.NOTE, tag + ": " + message);
    }

    private void loge(String tag, String message, Object... args) {
        if (args.length > 0) message = String.format(message, args);
        envMessager.printMessage(Diagnostic.Kind.NOTE, tag + ": " + message);
    }


    /**
     * 处理注解时
     */
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        mCacheMap.clear();
        handleBindViewAnnotation(roundEnv);
        handleOnClickAnnotation(roundEnv);
        Collection<GenerateInfo> values = mCacheMap.values();
        values.forEach(new Consumer<GenerateInfo>() {
            @Override
            public void accept(GenerateInfo generateInfo) {
                Writer writer = null;
                try {
                    JavaFileObject sourceFile = envFiler.createSourceFile(generateInfo.getGenerateClassName(), generateInfo.getTypeElement());
                    writer = sourceFile.openWriter();
                    String str = generateInfo.generateJavaCode();
                    buildLogger.e(TAG, "\n%s", str);
                    writer.write(str);
                    writer.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (writer != null) {
                        try {
                            writer.close();
                        } catch (IOException e) {
                        }
                    }
                }
            }
        });
        return true;
    }

    private void handleOnClickAnnotation(RoundEnvironment roundEnv) {
        Set<? extends Element> onClickElement = roundEnv.getElementsAnnotatedWith(OnClick.class);
        for (Element element : onClickElement) {
            if (element.getKind() != ElementKind.METHOD) {
                buildLogger.w(TAG,
                        "element annotation by @OnClick %s should be a method",
                        element.getSimpleName());
                continue;
            }
            // make sure the element's modifiers not contains PRIVATE
            Set<Modifier> modifiers = element.getModifiers();
            if (modifiers.contains(Modifier.PRIVATE)) {
                buildLogger.w(TAG,
                        "element annotation by @OnClick %s should not private",
                        element.getSimpleName());
                continue;
            }

            // 转换为对象
            ExecutableElement executableElement = (ExecutableElement) element;
            List<? extends VariableElement> parameters = executableElement.getParameters();
            if (parameters == null || parameters.size() != 1) {
                buildLogger.w(TAG,
                        "element annotation by @OnClick %s should only one parameter",
                        element.getSimpleName());
                continue;
            }
            VariableElement parameter = parameters.get(0);
            String s = parameter.asType().toString();
            // method: test parameter type: android.view.View
            // buildLogger.d(TAG, "method: " + executableElement.getSimpleName() + " parameter type: " + s);
            if (!s.equals("android.view.View")) {
                buildLogger.w(TAG,
                        "element annotation by @OnClick %s parameter onle can be android.view.View",
                        element.getSimpleName());
                continue;
            }
            TypeElement enclosingElement = (TypeElement) executableElement.getEnclosingElement();
            String enclosingElementName = enclosingElement.getQualifiedName().toString();
            GenerateInfo generateInfo = mCacheMap.get(enclosingElementName);
            if (generateInfo == null) {
                generateInfo = new GenerateInfo(envElementUtils, enclosingElement);
                mCacheMap.put(enclosingElementName, generateInfo);
            }
            OnClick onClick = executableElement.getAnnotation(OnClick.class);
            int[] value = onClick.value();
            generateInfo.addOnClickMap(value, executableElement);
        }
    }

    private void handleBindViewAnnotation(RoundEnvironment roundEnv) {
        // find element annotated by BindView.class
        Set<? extends Element> bindViewElement = roundEnv.getElementsAnnotatedWith(BindView.class);
        // traversal set
        for (Element element : bindViewElement) {
            // make sure the element is field
            if (element.getKind() != ElementKind.FIELD) {
                buildLogger.w(TAG,
                        "element annotation by @BindView %s should be a field",
                        element.getSimpleName());
                continue;
            }

            // make sure the element's modifiers not contains PRIVATE
            Set<Modifier> modifiers = element.getModifiers();
            if (modifiers.contains(Modifier.PRIVATE)) {
                buildLogger.w(TAG,
                        "element annotation by @BindView %s should not ptivate",
                        element.getSimpleName());
                continue;
            }

            // cast element 2 VariableElement, in fact, we were already judge this element is an Filed
            VariableElement fieldElement = (VariableElement) element;
            // get the source TypeElement,
            // For example, there are some filed annotation by BindBiew in MainActivity.java, then here will get TypeElement(MainActivity)
            TypeElement enclosingElement = (TypeElement) fieldElement.getEnclosingElement();

            String enclosingElementName = enclosingElement.getQualifiedName().toString();
            GenerateInfo generateInfo = mCacheMap.get(enclosingElementName);
            if (generateInfo == null) {
                generateInfo = new GenerateInfo(envElementUtils, enclosingElement);
                mCacheMap.put(enclosingElementName, generateInfo);
                buildLogger.d(TAG, "enclosingElementName-generateInfo: %s", generateInfo.toString());
            }

            // com.example.sample.MainActivity, tv, android.widget.TextView
            buildLogger.d(TAG, "enclosingElementNamewtf: %s, %s, %s",
                    enclosingElementName,
                    fieldElement.getSimpleName().toString(), fieldElement.asType().toString());

            BindView bindViewInstance = fieldElement.getAnnotation(BindView.class);
            int value = bindViewInstance.value();
            buildLogger.d(TAG, "BindView value: %d", value);
            generateInfo.addBindViewMap(value, fieldElement);
        }
    }
}
