/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.almaviva.foliage.authentication;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import org.apache.logging.log4j.ThreadContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.util.matcher.AnyRequestMatcher;

import it.almaviva.foliage.FoliageException;




/**
 *
 * @author A.Rossi
 */
@Slf4j
public class AccessTokenFilter extends AbstractAuthenticationProcessingFilter {
	
	private JwtAuthenticationDal authenticationDal;
	

	private final JwtTokenValidator tokenVerifier;

	public AccessTokenFilter(
		JwtTokenValidator jwtTokenValidator,
		AuthenticationManager authenticationManager,
		AuthenticationFailureHandler authenticationFailureHandler,
		JwtAuthenticationDal authenticationDal
	) {
		super(AnyRequestMatcher.INSTANCE);
		this.authenticationDal = authenticationDal;
		setAuthenticationManager(authenticationManager);
		setAuthenticationFailureHandler(authenticationFailureHandler);
		this.tokenVerifier = jwtTokenValidator;
	}
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {

		String threadName = Thread.currentThread().getName();
		log.info("threadName = {}", threadName);

		Map<String, String> context = ThreadContext.getContext();
		if (context != null) {
			Iterator<Map.Entry<String, String>> i = context.entrySet().iterator();
			while (i.hasNext()) {
				Map.Entry<String, String> e = i.next();
				log.info("{} = {}", e.getKey(), e.getValue());
			}
		}

		log.info("Attempting to authenticate JWT for request {}", request.getRequestURI());

		String authorizationHeader = extractAuthorizationHeaderAsString(request);
		AccessToken accessToken = tokenVerifier.validateAuthorizationHeader(authorizationHeader);
		try {
			authenticationDal.censimentoUtente(accessToken);
		}
		catch (SQLException e) {
			//e.printStackTrace();
			//error = new FoliageException("Non è stato possibile censire l'utente");
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			String sStackTrace = sw.toString(); // stack trace as a string
			
			log.error(sStackTrace);

			throw new FoliageAuthenticationException("Non è stato possibile censire l'utente", e);
		}
		catch (FoliageException fe) {
			throw new FoliageAuthenticationException(fe.getMessage(), fe);
		}
		AuthenticationManager m = this.getAuthenticationManager();
		JwtAuthentication auth = new JwtAuthentication(accessToken);
		
		auth.setAuthenticated(true);
		if (m == null) {
			return auth;
		}
		else {
			return m.authenticate(auth);   	
		}
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest request,
											HttpServletResponse response,
											FilterChain chain,
											Authentication authResult) throws IOException, ServletException {

		log.info("Successfully JWT authentication for request {}", request.getRequestURI());

		SecurityContextHolder.getContext().setAuthentication(authResult);
		chain.doFilter(request, response);
	}

	private String extractAuthorizationHeaderAsString(HttpServletRequest request) {
		try {
			return request.getHeader("Authorization");
			//return request.getHeader("x-jwt-token");
		} catch (Exception ex){
			throw new FoliageAuthenticationException("There is no Authorization header in a request", ex);
		}
	}

	@Override
	protected void unsuccessfulAuthentication(
		HttpServletRequest request,
		HttpServletResponse response,
		AuthenticationException failed
	) throws IOException, ServletException {
		log.error("Unsuccessfully authentication for request {}", request.getRequestURI());
		super.unsuccessfulAuthentication(request, response, failed);
	}
}