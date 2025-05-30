package it.almaviva.foliage.authentication;

import java.io.IOException;
import java.security.KeyStore.Entry;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationConverter;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.util.matcher.AnyRequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import it.almaviva.foliage.FoliageException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MonitoraggioFilter extends OncePerRequestFilter  {
	private static ResponseEntity<String> responseHostKo = new ResponseEntity<>(
			"La richiesta non proviene dall'host definito per il monitoraggio",
			HttpStatus.UNAUTHORIZED
		);
	private static ResponseEntity<String> responsePasswordKo = new ResponseEntity<>(
			"Autenticazione fallita",
			HttpStatus.UNAUTHORIZED
		);
	private static ResponseEntity<String> responseAuthenitcationKo = new ResponseEntity<>(
			"È necessaria un'autenticazione di tipo basic per effettuare la richiesta",
			HttpStatus.UNAUTHORIZED
		);

	private String monitoraggioHost;
	private String user;
	private String password;

	private SecurityContextHolderStrategy securityContextHolderStrategy = SecurityContextHolder.getContextHolderStrategy();
	private BasicAuthenticationEntryPoint entryPoint = new BasicAuthenticationEntryPoint();
	private BasicAuthenticationConverter authenticationConverter = new BasicAuthenticationConverter();
	private SecurityContextRepository securityContextRepository = new RequestAttributeSecurityContextRepository();
	public MonitoraggioFilter(
		String monitoraggioHost,
		String user,
		String password
	) {
		this.monitoraggioHost = monitoraggioHost;
		this.user = user;
		this.password = password;
		this.entryPoint.setRealmName("lifefoliage");
	}

	// @Override
	// public Authentication attemptAuthentication(
	// 	HttpServletRequest request,
	// 	HttpServletResponse response
	// ) throws AuthenticationException, IOException, ServletException {
		
	// }

	// @Override
	// protected void successfulAuthentication(HttpServletRequest request,
	// 										HttpServletResponse response,
	// 										FilterChain chain,
	// 										Authentication authResult) throws IOException, ServletException {

	// 	log.info("Successfully JWT authentication for request {}", request.getRequestURI());

	// 	SecurityContextHolder.getContext().setAuthentication(authResult);
	// 	chain.doFilter(request, response);
	// }

	// @Override
	// protected void unsuccessfulAuthentication(
	// 	HttpServletRequest request,
	// 	HttpServletResponse response,
	// 	AuthenticationException failed
	// ) throws IOException, ServletException {
	// 	log.error("Unsuccessfully authentication for request {}", request.getRequestURI());
	// 	super.unsuccessfulAuthentication(request, response, failed);
		
	// }

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
		String host = request.getRemoteAddr();
		if (monitoraggioHost.equals(host)) {
			try {
				UsernamePasswordAuthenticationToken authRequest = this.authenticationConverter.convert(request);
				
				if (authRequest == null) {
					this.logger.warn("Did not process authentication request since failed to find username and password in Basic Authorization header");
					throw new FoliageAuthenticationException("È necessaria un'autenticazione di tipo basic per effettuare la richiesta");
					//chain.doFilter(request, response);
				}
				else {
					String username = authRequest.getName();
					String password = authRequest.getCredentials().toString();
					if (username.equals(this.user) && password.equals(this.password)) {
						SimpleGrantedAuthority auth = new SimpleGrantedAuthority("monitoraggio");
						LinkedList<SimpleGrantedAuthority> auths = new LinkedList<>();
						auths.add(auth);
	
						authRequest = new UsernamePasswordAuthenticationToken(username, password, auths);
						
						SecurityContext context = this.securityContextHolderStrategy.createEmptyContext();
						context.setAuthentication(authRequest);
						this.securityContextRepository.saveContext(context, request, response);
						chain.doFilter(request, response);
					}
					else {
						saveResponse(response, responsePasswordKo);
						throw new FoliageAuthenticationException("Autenticazione fallita");
					}
				}
			}
			catch (AuthenticationException var8) {
				log.error(FoliageException.GetExceptionStackTrace(var8));
				this.securityContextHolderStrategy.clearContext();
				this.logger.debug("Failed to process authentication request", var8);
				this.entryPoint.commence(request, response, var8);
			}
		}
		else {
			saveResponse(response, responseHostKo);
		}
	}

	private static void saveResponse(HttpServletResponse response, ResponseEntity<String> responseEntity) {
		response.setStatus(responseEntity.getStatusCode().value());
		responseEntity.getHeaders().entrySet().stream().forEach(
			(java.util.Map.Entry<String, List<String>> e) -> response.setHeader(e.getKey(), e.getValue().stream().collect(Collectors.joining()))
		);
		try {
			response.getWriter().print(responseEntity.getBody());
		}
		catch (IOException e){
		}
	}
}
