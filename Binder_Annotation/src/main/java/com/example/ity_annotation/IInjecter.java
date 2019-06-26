package com.example.ity_annotation;

public interface IInjecter<Host, Source> {

    void inject(Host host, Source source);
}
