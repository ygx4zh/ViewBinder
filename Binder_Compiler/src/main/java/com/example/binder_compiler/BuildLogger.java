package com.example.binder_compiler;

import java.util.Locale;

import javax.annotation.processing.Messager;
import javax.tools.Diagnostic;

public class BuildLogger {

    private final Messager logMessager;

    public BuildLogger(Messager messager) {
        this.logMessager = messager;
    }

    public void d(String tag, String message, Object... args) {
        printLog(Diagnostic.Kind.NOTE, tag, message, args);
    }

    public void w(String tag, String message, Object... args) {
        printLog(Diagnostic.Kind.WARNING, tag, message, args);
    }

    public void e(String tag, String message, Object... args) {
        printLog(Diagnostic.Kind.ERROR, tag, message, args);
    }

    public void printLog(Diagnostic.Kind kind, String tag, String message, Object... args) {
        if (args.length > 0) message = String.format(Locale.CHINA, message, args);
        // logMessager.printMessage(kind, message);
        if (kind == Diagnostic.Kind.ERROR) {
            System.err.println(tag + ": " + message);
        } else {
            System.out.println(tag + ": " + message);
        }
    }
}
