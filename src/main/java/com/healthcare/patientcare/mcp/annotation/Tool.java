package com.healthcare.patientcare.mcp.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to mark methods as MCP tools
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Tool {
    /**
     * Name of the tool (used in MCP tools/list and tools/call)
     */
    String name();

    /**
     * Description of what the tool does
     */
    String description();
}