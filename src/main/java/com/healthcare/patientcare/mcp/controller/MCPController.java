package com.healthcare.patientcare.mcp.controller;

import com.healthcare.patientcare.mcp.MCPServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/mcp")
public class MCPController {

    @Autowired
    private MCPServer mcpServer;

    @PostMapping
    public ResponseEntity<Map<String, Object>> handleMCPRequest(@RequestBody Map<String, Object> request) {
        String method = (String) request.get("method");
        Object id = request.get("id");

        Map<String, Object> response = new HashMap<>();
        response.put("jsonrpc", "2.0");
        response.put("id", id);

        try {
            switch (method) {
                case "initialize":
                    response.put("result", handleInitialize());
                    break;

                case "tools/list":
                    response.put("result", handleToolsList());
                    break;

                case "tools/call":
                    @SuppressWarnings("unchecked")
                    Map<String, Object> params = (Map<String, Object>) request.get("params");
                    response.put("result", handleToolsCall(params));
                    break;

                default:
                    Map<String, Object> error = new HashMap<>();
                    error.put("code", -32601);
                    error.put("message", "Method not found: " + method);
                    response.put("error", error);
            }
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("code", -32603);
            error.put("message", "Internal error: " + e.getMessage());
            response.put("error", error);
        }

        return ResponseEntity.ok(response);
    }

    private Map<String, Object> handleInitialize() {
        Map<String, Object> result = new HashMap<>();
        result.put("protocolVersion", "2024-11-05");
        result.put("serverInfo", Map.of(
                "name", "patient-care-system",
                "version", "1.0.0"
        ));
        result.put("capabilities", Map.of(
                "tools", Map.of()
        ));
        return result;
    }

    private Map<String, Object> handleToolsList() {
        Map<String, Object> result = new HashMap<>();
        result.put("tools", mcpServer.listTools());
        return result;
    }

    private Map<String, Object> handleToolsCall(Map<String, Object> params) {
        String toolName = (String) params.get("name");
        @SuppressWarnings("unchecked")
        Map<String, Object> arguments = (Map<String, Object>) params.get("arguments");

        if (arguments == null) {
            arguments = new HashMap<>();
        }

        String toolResult = mcpServer.callTool(toolName, arguments);

        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> content = new ArrayList<>();

        Map<String, Object> textContent = new HashMap<>();
        textContent.put("type", "text");
        textContent.put("text", toolResult);
        content.add(textContent);

        result.put("content", content);
        return result;
    }
}