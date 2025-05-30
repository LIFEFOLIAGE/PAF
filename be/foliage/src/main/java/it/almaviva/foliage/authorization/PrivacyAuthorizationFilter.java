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

import it.almaviva.foliage.authentication.AccessToken;
import it.almaviva.foliage.authentication.JwtAuthentication;


public class PrivacyAuthorizationFilter implements Filter  {
	private final String accettazionePrivacyPath;
	private final String userDataPath;
	private final String csrsPath;
	private final String mobilePath;

	private final static String webControllerPathStat = "api/web/";
	private final static String appControllerPathStat = "api/app/";

	private final static String accettazionePrivacyPathStat = "corrente/accettazione-privacy";
	private final static String userDataPathStat = "corrente";
	private final static String csrsPathStat = "csrs";
	private final static String accettazionePrivacyMethod = "POST";
	private final static String userDataMethod = "GET";
	private final static String csrsMethod = "GET";

	public boolean toBeIgnored(String reqPath, String reqMethod) {
		boolean isAccettazione = (
				reqPath != null && reqPath.equals(accettazionePrivacyPath)
				&&
				reqMethod != null && reqMethod.equals(accettazionePrivacyMethod)
			);
		if (isAccettazione)	{
			return true;
		}
		else {
			boolean isUserData = (	
				reqPath != null && reqPath.equals(userDataPath)
				&&
				reqMethod != null && reqMethod.equals(userDataMethod)
			);
			if (isUserData) {
				return true;
			}
			else {
				boolean isCsrs = (	
					reqPath != null && reqPath.equals(csrsPath)
					&&
					reqMethod != null && reqMethod.equals(csrsMethod)
				);
				if (isCsrs) {
					return true;
				}
				else {
					boolean isMobile = reqPath != null && reqPath.startsWith(mobilePath);
					return isMobile;
				}
			}
		}
	}

	public PrivacyAuthorizationFilter(String basePath) {
		mobilePath = basePath + appControllerPathStat;
		accettazionePrivacyPath = basePath + webControllerPathStat + accettazionePrivacyPathStat;
		userDataPath = basePath + webControllerPathStat + userDataPathStat;
		csrsPath = basePath + csrsPathStat;
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		if (request instanceof HttpServletRequest && response instanceof HttpServletResponse) {
			HttpServletRequest httpRequest = (HttpServletRequest)request;
			HttpServletResponse httpResponse = (HttpServletResponse)response;
			String reqPath = httpRequest.getServletPath();
			String reqMethod = httpRequest.getMethod();
			if (
				toBeIgnored(reqPath, reqMethod)
			) {
				chain.doFilter(request, response);
			}
			else {
				Authentication a = SecurityContextHolder.getContext().getAuthentication();
				JwtAuthentication jwtAuth = (JwtAuthentication)a;
				if (jwtAuth == null) {
					httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "Dati di autenticazione mancanti");
				}
				else {
					AccessToken token = jwtAuth.getAccessToken();
					if (token == null) {
						httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "Token di autenticazione mancante");
					}
					else {
						Boolean flagAccettazione = token.getFlagAccettazione();
						if (flagAccettazione == null) {
							httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Il sistema sarà disponibile agli utenti dopo che sarà completato il processo di raggiungimento della conformità alle normative vigenti (GDPR)");
						}
						else {
							if (flagAccettazione.booleanValue()) {
								chain.doFilter(request, response);
							}
							else {

								httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Occorre visionare l'informativa sulla privacy (GDPR)");
							}
						}
					}
				}
			}
		}
		else {
			chain.doFilter(request, response);
		}
	}

}
