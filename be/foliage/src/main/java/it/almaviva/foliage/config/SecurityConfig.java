/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.almaviva.foliage.config;


import it.almaviva.foliage.authentication.AuthorizationAccessDeniedHandler;
import it.almaviva.foliage.authentication.FoliageAuthenticationProvider;
import it.almaviva.foliage.authentication.AccessTokenFilter;
import it.almaviva.foliage.authentication.AccessTokenAuthenticationFailureHandler;
import it.almaviva.foliage.authentication.JwtTokenValidator;
import it.almaviva.foliage.authorization.ProfileAuthorizationFilter;
import it.almaviva.foliage.authentication.FoliageJwkProvider;
import it.almaviva.foliage.authentication.JwtAuthenticationDal;

import com.auth0.jwk.JwkProvider;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.Filter;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//import io.wkrzywiec.keycloak.backend.infra.security.JwtTokenValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.util.matcher.RegexRequestMatcher;
/**
 *
 * @author A.Rossi
 */

@Order(1)
@Configuration
@EnableMethodSecurity
@EnableWebSecurity(/*debug = true*/)
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig  {

	@Value("${foliage.jwk-jwksurl}")
	private String jwksUrl;


	@Value("${foliage.jwt.username}")
	private String usernameMember;

	@Value("${foliage.jwt.name}")
	private String nameMember;
	
	@Value("${foliage.jwt.surname}")
	private String surnameMember;

	@Value("${foliage.jwt.address}")
	private String addressMember;

	@Value("${foliage.jwt.city}")
	private String cityMember;

	@Value("${foliage.jwt.cap}")
	private String capMember;

	@Value("${foliage.jwt.codiceFiscale}")
	private String codiceFiscaleMember;

	@Value("${foliage.jwt.birthDate}")
	private String birthDateMember;

	@Value("${foliage.jwt.birthPlace}")
	private String birthPlaceMember;

	// @Value("${foliage.jwt.phoneNumber}")
	// private String phoneNumberMember;

	@Value("${foliage.jwt.gender}")
	private String genderMember;
	
	// @Value("${foliage.jwt.email}")
	// private String emailMember;
	
	// @Value("${foliage.jwt.pec}")
	// private String pecMember;

	@Value("${foliage.jwt.dateFormat}")
	private String dateFormat;

	@Value("${foliage.is_development:false}")
	protected boolean isDevelopment;

	
	@Value("${foliage.base-path}")
	protected String basePath;

	@Autowired
	private JwtAuthenticationDal authenticationDal;

	@Bean
	@Order(1)
	public SecurityFilterChain basicFilterChainMonitoraggio(HttpSecurity http)  throws Exception {
		// JwtTokenValidator tokValidator = jwtTokenValidator(foliageJwkProvider());
		// AuthenticationManager authManager = http.getSharedObject(AuthenticationManager.class);
		// AccessTokenFilter filter = new AccessTokenFilter(
		// 	tokValidator,
		// 	authManager,
		// 	authenticationFailureHandler(),
		// 	authenticationDal
		// );
		// ProfileAuthorizationFilter authProfFilter = new ProfileAuthorizationFilter();

		String monitoraggioRegex = String.format(
			"^%sapi/monitoraggio/.*",
			basePath
		);
		
		// String othersRegex = String.format(
		// 	"^%sapi/(?!monitoraggio/).*",
		// 	basePath
		// );
		log.info(String.format("Il pattern di monitoraggio è: %s", monitoraggioRegex));
		// log.info(String.format("Il pattern delle altre richieste è: %s", othersRegex));

		HttpSecurity lev1 = http.csrf(AbstractHttpConfigurer::disable);
		
		// HttpSecurity underMonitoraggio = lev1.authorizeHttpRequests(
		// 	(auth) -> {
		// 		auth.requestMatchers(RegexRequestMatcher.regexMatcher(monitoraggioRegex)).permitAll();
		// 	}
		// );
		HttpSecurity underMonitoraggio = lev1.securityMatcher(
			RegexRequestMatcher.regexMatcher(monitoraggioRegex)
		);
		
		
		// underOthers.
		// 	addFilterBefore(
		// 		filter,
		// 		BasicAuthenticationFilter.class
		// 	).
		// 	addFilterAfter(
		// 		authProfFilter,
		// 		BasicAuthenticationFilter.class
		// 	);
		return underMonitoraggio.build();
	}

	
	@Bean
	@Order(2)
	public SecurityFilterChain jwtFilterChainOhers(HttpSecurity http)  throws Exception {
		JwtTokenValidator tokValidator = jwtTokenValidator(foliageJwkProvider());
		AuthenticationManager authManager = http.getSharedObject(AuthenticationManager.class);
		AccessTokenFilter filter = new AccessTokenFilter(
			tokValidator,
			authManager,
			authenticationFailureHandler(),
			authenticationDal
		);
		ProfileAuthorizationFilter authProfFilter = new ProfileAuthorizationFilter();

		// String monitoraggioRegex = String.format(
		// 	"^%sapi/monitoraggio/.*",
		// 	basePath
		// );
		
		String othersRegex = String.format(
			"^%sapi/(?!monitoraggio/).*",
			basePath
		);
		// log.info(String.format("Il pattern di monitoraggio è: %s", monitoraggioRegex));
		log.info(String.format("Il pattern delle altre richieste è: %s", othersRegex));

		HttpSecurity lev1 = http.csrf(AbstractHttpConfigurer::disable);
		
		// HttpSecurity underMonitoraggio = lev1.authorizeHttpRequests(
		// 	(auth) -> {
		// 		auth.requestMatchers(RegexRequestMatcher.regexMatcher(monitoraggioRegex)).permitAll();
		// 	}
		// );
		HttpSecurity underOthers = lev1.securityMatcher(
			RegexRequestMatcher.regexMatcher(othersRegex)
		);

		// HttpSecurity underOthers = lev1.authorizeHttpRequests(
		// 	(auth) -> {
		// 		auth.requestMatchers(RegexRequestMatcher.regexMatcher(othersRegex)).permitAll();
		// 	}
		// );
		
		underOthers.
			addFilterBefore(
				filter,
				BasicAuthenticationFilter.class
			).
			addFilterAfter(
				authProfFilter,
				BasicAuthenticationFilter.class
			);
		return underOthers.build();
	}

	/*
	@Bean
	CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000/"));
		configuration.setAllowedMethods(Arrays.asList("GET","POST"));
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}
*/


	// @Bean
	// public WebSecurityCustomizer webSecurityCustomizer() {
	//     //log.debug(nonSecureUrl); 
	//     //log.debug("/actuator/**"); 
		
	//     String[] ignoredPaths = {
	//         //"/{tipo:((cittadino)|(utente)|(istruttore))}*",
	//         //"/{tipo:((cittadino)|(utente)|(istruttore))}**/*",
	//         //"/actuator/**",
	//         "/",
	//         "/**/*.*",
	//         "/{tipo:((cittadino)|(utente)|(istruttore))}/**"
	//     };
		

	//     WebSecurityCustomizer c = (WebSecurity web) -> {
	//         //web.debug(true);
	//         WebSecurity.IgnoredRequestConfigurer ignoring = web.ignoring();
					
	//         ignoring.requestMatchers(HttpMethod.OPTIONS);
	//         ignoring.requestMatchers(
	//             new RequestMatcher() {
	//                 @Override
	//                 public boolean matches(HttpServletRequest request) {
	//                     return request.getLocalPort() == 9090;
	//                     //return true;
	//                 }
	//             }  
	//         );
	//         //
	//         for (String ignoredPath : ignoredPaths) {
	//             log.debug(String.format("will ignore %s", ignoredPath));
	//             ignoring.requestMatchers(
	//                 new AntPathRequestMatcher(ignoredPath)
	//             );
	//         }
	//     };
	//     //return (web) -> web.ignoring().requestMatchers(nonSecureUrl);
	//     return c;
	// }
	

//    public void configure(AuthenticationManagerBuilder auth) {
//        auth.authenticationProvider(authenticationProvider());
//    }

	// @Bean
	// public AuthenticationFailureHandler authenticationFailureHandler() {
	//     return new FoliageAuthenticationFailureHandler();
	// }

	@Bean
	public AuthenticationProvider authenticationProvider() {
		return new FoliageAuthenticationProvider();
	}

	@Bean
	public AuthenticationFailureHandler authenticationFailureHandler() {
		return new AccessTokenAuthenticationFailureHandler();
	}

	@Bean
	public JwtTokenValidator jwtTokenValidator(JwkProvider jwkProvider) {
		return new JwtTokenValidator(
			jwkProvider,
			usernameMember,
			nameMember,
			surnameMember,
			addressMember,
			cityMember,
			capMember,
			codiceFiscaleMember,
			birthDateMember,
			birthPlaceMember,
			//phoneNumberMember,
			genderMember,
			//emailMember,
			//pecMember,
			dateFormat,
			isDevelopment
		);
	}

	@Bean
	public JwkProvider foliageJwkProvider() {
		return new FoliageJwkProvider(jwksUrl);
	}

	@Bean
	public AccessDeniedHandler accessDeniedHandler() {
		return new AuthorizationAccessDeniedHandler();
	}

	
}