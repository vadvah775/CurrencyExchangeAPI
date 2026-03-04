package com.utils;

import jakarta.servlet.http.HttpServletResponse;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

public class ErrorHandler {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static void sendError(int code, String message, HttpServletResponse resp){
        resp.setContentType("application/json");
        resp.setStatus(code);
        try {
            resp.getWriter().println(MAPPER.createObjectNode().put("message", message));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
