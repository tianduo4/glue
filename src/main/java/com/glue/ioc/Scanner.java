package com.glue.ioc;

import lombok.Builder;
import lombok.Data;

import java.lang.annotation.Annotation;

@Data
@Builder
public class Scanner {

    private String                      packageName;
    private boolean                     recursive;
    private Class<?>                    parent;
    private Class<? extends Annotation> annotation;
}