package com.healthcare.patientcare.mcp;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * Standalone Java wrapper that acts as an MCP server bridge
 * Reads JSON-RPC requests from stdin and forwards to Spring Boot HTTP endpoint
 *
 * Compile: javac MCPServerWrapper.java
 * Run: java MCPServerWrapper
 */
public class MCPServerWrapper {

    private static final String MCP_URL = "http://localhost:8080/mcp";

    public static void main(String[] args) {
        System.err.println("Patient Care MCP Server Bridge started");
        System.err.println("Forwarding requests to: " + MCP_URL);

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(System.in, StandardCharsets.UTF_8))) {

            String line;
            while ((line = reader.readLine()) != null) {
                try {
                    String response = sendRequest(line);
                    System.out.println(response);
                    System.out.flush();
                } catch (Exception e) {
                    System.err.println("Error processing request: " + e.getMessage());
                    e.printStackTrace(System.err);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading from stdin: " + e.getMessage());
            e.printStackTrace(System.err);
        }
    }

    private static String sendRequest(String jsonRequest) throws IOException {
        URL url = new URL(MCP_URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        try {
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            // Send request
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonRequest.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            // Read response
            int responseCode = conn.getResponseCode();
            InputStream is = (responseCode >= 200 && responseCode < 300)
                    ? conn.getInputStream()
                    : conn.getErrorStream();

            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(is, StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }
                return response.toString();
            }
        } finally {
            conn.disconnect();
        }
    }
}