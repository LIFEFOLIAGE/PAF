/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.almaviva.foliage.authentication;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import org.springframework.http.HttpStatus;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.access.AccessDeniedException;

/**
 *
 * @author A.Rossi
 */
public class AuthorizationAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException  {

        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(createErrorBody(accessDeniedException));
    }

    private String createErrorBody(AccessDeniedException exception) {
        JsonObject exceptionMessage = new JsonObject();
        exceptionMessage.addProperty("code", HttpStatus.FORBIDDEN.value());
        exceptionMessage.addProperty("reason", HttpStatus.FORBIDDEN.getReasonPhrase());
        exceptionMessage.addProperty("timestamp", Instant.now().toString());
        exceptionMessage.addProperty("message", exception.getMessage());
        return new Gson().toJson(exceptionMessage);
    }

}
