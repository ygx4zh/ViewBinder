package com.example.binder_tools;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;

import com.example.ity_annotation.IInjecter;

import java.util.HashMap;
import java.util.Map;

public class BinderTools {

    private static Map<Object, IInjecter> sInjecterCache = new HashMap<>();

    private BinderTools() {
    }

    public static void bind(Activity target) {
        bind(target, target.getWindow().getDecorView());
    }

    public static void bind(Dialog dialog) {
        bind(dialog, dialog.getWindow().getDecorView());
    }

    @SuppressWarnings("unchecked")
    public static void bind(Object target, View source) {
        IInjecter objectInjecter = sInjecterCache.get(target.getClass());

        if (objectInjecter == null) {
            String injecterClassName = target.getClass().getName() + "$$Injecter";
            try {
                Class<?> injectClass = Class.forName(injecterClassName);
                objectInjecter = (IInjecter) injectClass.newInstance();
                sInjecterCache.put(target.getClass(), objectInjecter);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        objectInjecter.inject(target, source);
    }
}
