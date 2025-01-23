package it.almaviva.foliage.authorization;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import it.almaviva.foliage.authentication.FoliageGrantedAuthority;
import it.almaviva.foliage.authentication.JwtAuthentication;


public class ProfileAuthorizationFilter implements Filter  {

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		if (request instanceof HttpServletRequest && response instanceof HttpServletResponse) {
			HttpServletRequest httpRequest = (HttpServletRequest)request;
			HttpServletResponse httpResponse = (HttpServletResponse)response;

			String auth = httpRequest.getParameter("authority");
			String authScope = httpRequest.getParameter("authScope");
			if (auth != null || authScope != null) {
				Authentication a = SecurityContextHolder.getContext().getAuthentication();
				JwtAuthentication jwtAuth = (JwtAuthentication)a;
				if (auth != null) {
					if (authScope != null) {
						if (
							jwtAuth.getAuthorities().stream().anyMatch(
								x -> x.getAuthority().equals(auth) && ((FoliageGrantedAuthority)x).getAmbito().equals(authScope)
							)
						) {
							chain.doFilter(request, response);
						}
						else {
							httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "Non si ha la visibilità richiesta per questo ruolo");
						}
					}
					else {
						if (jwtAuth.getAuthorities().stream().anyMatch(x -> x.getAuthority().equals(auth))) {
							chain.doFilter(request, response);
						}
						else {
							httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "Ruolo non corrispondente");
						}
					}
				}
				else {
					if (authScope != null) {
						if (jwtAuth.getAuthorities().stream().anyMatch(x -> ((FoliageGrantedAuthority)x).getAmbito().equals(authScope))) {
							chain.doFilter(request, response);
						}
						else {
							httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "Non si ha la visibilità richiesta");
						}
					}
					else {
						chain.doFilter(request, response);
					}
				}
			}
			else {
				chain.doFilter(request, response);
			}
		}
		else {
			chain.doFilter(request, response);
		}
	}
	
}
