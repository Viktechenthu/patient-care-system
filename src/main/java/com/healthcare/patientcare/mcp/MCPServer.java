package com.healthcare.patientcare.mcp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.healthcare.patientcare.mcp.annotation.Tool;
import com.healthcare.patientcare.mcp.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

@Component
public class MCPServer {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    private Map<String, ToolMetadata> tools;

    public void initializeTools() {
        if (tools != null) {
            return;
        }

        tools = new HashMap<>();

        // Scan all beans for @Tool annotated methods
        String[] beanNames = applicationContext.getBeanDefinitionNames();
        for (String beanName : beanNames) {
            Object bean = applicationContext.getBean(beanName);
            Class<?> beanClass = bean.getClass();

            for (Method method : beanClass.getDeclaredMethods()) {
                if (method.isAnnotationPresent(Tool.class)) {
                    Tool toolAnnotation = method.getAnnotation(Tool.class);
                    String toolName = toolAnnotation.name();

                    ToolMetadata metadata = new ToolMetadata();
                    metadata.name = toolName;
                    metadata.description = toolAnnotation.description();
                    metadata.method = method;
                    metadata.bean = bean;
                    metadata.parameters = new ArrayList<>();

                    Parameter[] params = method.getParameters();
                    for (Parameter param : params) {
                        if (param.isAnnotationPresent(ToolParam.class)) {
                            ToolParam toolParam = param.getAnnotation(ToolParam.class);
                            ParameterMetadata paramMetadata = new ParameterMetadata();
                            paramMetadata.name = toolParam.name();
                            paramMetadata.description = toolParam.description();
                            paramMetadata.required = toolParam.required();
                            paramMetadata.type = param.getType();
                            metadata.parameters.add(paramMetadata);
                        }
                    }

                    tools.put(toolName, metadata);
                }
            }
        }
    }

    public List<Map<String, Object>> listTools() {
        initializeTools();

        List<Map<String, Object>> toolsList = new ArrayList<>();
        for (ToolMetadata metadata : tools.values()) {
            Map<String, Object> tool = new HashMap<>();
            tool.put("name", metadata.name);
            tool.put("description", metadata.description);

            Map<String, Object> inputSchema = new HashMap<>();
            inputSchema.put("type", "object");

            Map<String, Object> properties = new HashMap<>();
            List<String> required = new ArrayList<>();

            for (ParameterMetadata param : metadata.parameters) {
                Map<String, Object> paramSchema = new HashMap<>();
                paramSchema.put("type", getJsonType(param.type));
                paramSchema.put("description", param.description);
                properties.put(param.name, paramSchema);

                if (param.required) {
                    required.add(param.name);
                }
            }

            inputSchema.put("properties", properties);
            if (!required.isEmpty()) {
                inputSchema.put("required", required);
            }

            tool.put("inputSchema", inputSchema);
            toolsList.add(tool);
        }

        return toolsList;
    }

    public String callTool(String toolName, Map<String, Object> arguments) {
        initializeTools();

        ToolMetadata metadata = tools.get(toolName);
        if (metadata == null) {
            return "{\"error\": \"Tool not found: " + toolName + "\"}";
        }

        try {
            Object[] args = new Object[metadata.parameters.size()];
            for (int i = 0; i < metadata.parameters.size(); i++) {
                ParameterMetadata param = metadata.parameters.get(i);
                Object value = arguments.get(param.name);

                if (value == null && param.required) {
                    return "{\"error\": \"Missing required parameter: " + param.name + "\"}";
                }

                args[i] = convertValue(value, param.type);
            }

            Object result = metadata.method.invoke(metadata.bean, args);
            return result != null ? result.toString() : "{}";

        } catch (Exception e) {
            return "{\"error\": \"Error calling tool: " + e.getMessage() + "\"}";
        }
    }

    private String getJsonType(Class<?> type) {
        if (type == String.class) return "string";
        if (type == Integer.class || type == int.class || type == Long.class || type == long.class) return "integer";
        if (type == Boolean.class || type == boolean.class) return "boolean";
        if (type == Double.class || type == double.class || type == Float.class || type == float.class) return "number";
        return "string";
    }

    private Object convertValue(Object value, Class<?> targetType) {
        if (value == null) return null;

        if (targetType.isInstance(value)) {
            return value;
        }

        String strValue = value.toString();

        if (targetType == Long.class || targetType == long.class) {
            return Long.parseLong(strValue);
        }
        if (targetType == Integer.class || targetType == int.class) {
            return Integer.parseInt(strValue);
        }
        if (targetType == Boolean.class || targetType == boolean.class) {
            return Boolean.parseBoolean(strValue);
        }
        if (targetType == Double.class || targetType == double.class) {
            return Double.parseDouble(strValue);
        }

        return strValue;
    }

    static class ToolMetadata {
        String name;
        String description;
        Method method;
        Object bean;
        List<ParameterMetadata> parameters;
    }

    static class ParameterMetadata {
        String name;
        String description;
        boolean required;
        Class<?> type;
    }
}