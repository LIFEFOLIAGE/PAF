package it.almaviva.foliage.tracking;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import it.almaviva.foliage.FoliageException;
import it.almaviva.foliage.authentication.AccessToken;
import it.almaviva.foliage.authentication.JwtAuthentication;
import it.almaviva.foliage.bean.FoliageRequest;
import it.almaviva.foliage.services.BaseDal;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class FoliageTrackingInterceptor implements HandlerInterceptor {
	@Autowired
	private BaseDal dal;

	private ContentCachingRequestWrapper requestWrapper;
	private ContentCachingResponseWrapper responseWrapper;

	private FoliageRequest foliageRequest;
	private LocalDateTime oraInizio;

	private final String[] headers = "HOST,Origin,Referer,X-Requested-With,X-Forwarded-For,X-Forwarded-Proto".toUpperCase().split(",");

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		oraInizio = LocalDateTime.now();


		String heads = StreamSupport.stream(
				Spliterators.spliteratorUnknownSize(request.getHeaderNames().asIterator(), Spliterator.DISTINCT
			), false).filter(
					(String headName) -> {
						String name = headName.toUpperCase();
						return Arrays.stream(headers).anyMatch(s -> s.equals(name));
					}
				)
				.map(
					s -> {
						return String.format("%s: %s", s, request.getHeader(s));
					}
				).collect(Collectors.joining("\n"));


		foliageRequest = new FoliageRequest();
		foliageRequest.setOraInizio(oraInizio);
		foliageRequest.setIpAddress(request.getRemoteAddr());
		foliageRequest.setMethod(request.getMethod());
		foliageRequest.setHost(request.getRemoteHost());
		foliageRequest.setPath(request.getServletPath());
		foliageRequest.setQueryString(request.getQueryString());
		foliageRequest.setThreadName(Thread.currentThread().getName());
		foliageRequest.setHeaders(heads);

		
		

		//foliageRequest.requestBody = request.getInputStream().readAllBytes();

		// requestWrapper = new ContentCachingRequestWrapper(request);
        // responseWrapper = new ContentCachingResponseWrapper(response);

		Authentication a = SecurityContextHolder.getContext().getAuthentication();
		if (a == null || !(a instanceof JwtAuthentication)) {
			foliageRequest.setUsername(null);
		}
		else {
			JwtAuthentication jwtAuth = (JwtAuthentication)a;
			AccessToken tok = jwtAuth.getAccessToken();
			foliageRequest.setUsername(tok.getUsername());
		}
		

		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
		// your code
		// log.info("Bodies of {} {}", foliageRequest.getMethod(), foliageRequest.getPath());
		// log.info("Request {}", new String(requestWrapper.getContentAsByteArray()));
        // log.info("Response {}", new String(responseWrapper.getContentAsByteArray()));

		foliageRequest.setDurata(Duration.between(LocalDateTime.now(), oraInizio));
	}

	private static String insSql = """
INSERT INTO foliage2.flgserver_requests_tab(
		thread_name, ip_addres, hostname, username,
		http_method, requested_path, request_query,
		ora_inizio, durata, http_status_code, errore, headers
	)
VALUES(
		:threadName, :ipAddress, :host, :username,
		:method, :path, :queryString,
		:oraInizio, :durata, :status, :errore, :headers
	)""";;
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {

		foliageRequest.setStatus(response.getStatus());
		if (foliageRequest.getDurata() == null) {
			try {
				foliageRequest.setDurata(Duration.between(LocalDateTime.now(), oraInizio));
			}
			catch (Exception e) {
				try {
					foliageRequest.setDurata(null);
				}
				catch(Exception e1) {
				}
			}
		}
		if (ex != null) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			ex.printStackTrace(pw);
			foliageRequest.setErrore(sw.toString());
		}
		else {
			foliageRequest.setErrore(null);
		}
		HashMap<String, Object> pars = foliageRequest.getParsMap();
		if (pars == null || pars.isEmpty()) {
			throw new FoliageException("Error in interceptor");
		}
		else {
			dal.getNamedTemplate().update(insSql, pars);
		}

		foliageRequest = null;
	}
}
