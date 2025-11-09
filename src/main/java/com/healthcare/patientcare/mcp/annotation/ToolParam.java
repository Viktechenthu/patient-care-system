package com.healthcare.patientcare.mcp.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to describe tool parameters
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface ToolParam {
    /**
     * Name of the parameter
     */
    String name();

    /**
     * Description of the parameter
     */
    String description();

    /**
     * Whether the parameter is required
     */
    boolean required() default true;
}