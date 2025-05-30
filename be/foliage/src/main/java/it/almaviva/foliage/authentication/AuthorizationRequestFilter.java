///*
// * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
// * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
// */
//package it.almaviva.foliage.authentication;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.ServletRequest;
//import jakarta.servlet.ServletResponse;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import jakarta.ws.rs.container.ContainerRequestContext;
//import jakarta.ws.rs.container.ContainerRequestFilter;
//import jakarta.ws.rs.core.Response;
//import java.io.IOException;
//import java.net.URI;
//import java.net.URISyntaxException;
//import java.net.URLEncoder;
//import java.net.http.HttpClient;
//import java.net.http.HttpRequest;
//import java.net.http.HttpResponse;
//import java.net.http.HttpResponse.BodyHandlers;
//import java.util.Base64;
//import java.util.Map;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//import org.springframework.core.annotation.Order;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.GenericFilterBean;
//import org.springframework.web.filter.OncePerRequestFilter;
//
///**
// *
// * @author A.Rossi
// */
////@Component
////@Order(1)
//public class AuthorizationRequestFilter extends OncePerRequestFilter {
//    public static final String AUTHENTICATION_HEADER = "OAuth-Token";
//    
//    private static String getBasicAuthenticationHeader(String username, String password) {
//        String valueToEncode = username + ":" + password;
//        return "Basic " + Base64.getEncoder().encodeToString(valueToEncode.getBytes());
//    }
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//        String authHeaderVal = request.getHeader(AUTHENTICATION_HEADER);
//        if (authHeaderVal != null)  {
//            URI uri = null;
//            try {
//                uri = new URI("https://10.206.193.173:9443/oauth2/introspect");
//            } catch (URISyntaxException ex) {
//                Logger.getLogger(AuthorizationRequestFilter.class.getName()).log(Level.SEVERE, null, ex);
//            }
//            if (uri != null) {
//
//                HttpRequest validateRequest = HttpRequest.newBuilder()
//                    .uri(uri)
//                    .header("Content-Type", "application/x-www-form-urlencoded")
//                    .header("Authorization", getBasicAuthenticationHeader("admin", "admin"))
//                    .POST(HttpRequest.BodyPublishers.ofString("token="+URLEncoder.encode(authHeaderVal, "UTF-8")))
//                    .build();
//                HttpClient client = HttpClient.newHttpClient();
//
//                HttpResponse<String> validateResponse = null;
//                try {   
//                    validateResponse = client.send(validateRequest, BodyHandlers.ofString());
//                }
//                catch (InterruptedException ex) {
//                    Logger.getLogger(AuthorizationRequestFilter.class.getName()).log(Level.SEVERE, null, ex);
//                }
//                if (validateResponse != null) {
//                    if (validateResponse.statusCode() == 200) {
//                        String responseBody = validateResponse.body();
//                        ObjectMapper mapper = new ObjectMapper();
//                        Map<String, Object> oauth2Response = mapper.readValue((responseBody), Map.class);
//
//                        Boolean active = (Boolean)oauth2Response.get("active");
//
//                        if (active == null || !active) {
//                            filterChain.doFilter(request, response);
//                            return;
//                        }    
//                    }
//                    else {
//                        String responseBody = validateResponse.body();
//                        Logger.getLogger(AuthorizationRequestFilter.class.getName()).log(Level.SEVERE, "Errore nella validazione del token: " + responseBody);
//                    }
//                }
//            }
//        }
//        
//        //if (response != null) {
//        ////final SecurityContext securityContext = requestContext.getSecurityContext();
//        ////if (securityContext == null || !securityContext.isUserInRole("privileged")) {
//        //
//        //    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User cannot access the resource");
//        ////}   
//        //}
//        //else {
//        //    filterChain.doFilter(request, response);    
//        //}
//        filterChain.doFilter(request, response);
//    }
//}
//