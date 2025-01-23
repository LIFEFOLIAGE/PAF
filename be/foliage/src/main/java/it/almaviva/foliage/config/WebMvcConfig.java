// /*
//  * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
//  * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
//  */
// package it.almaviva.foliage.config;

// import java.io.IOException;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.core.io.ClassPathResource;
// import org.springframework.core.io.Resource;
// import org.springframework.core.io.UrlResource;
// import org.springframework.web.servlet.config.annotation.CorsRegistry;
// import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
// import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
// import org.springframework.web.servlet.resource.PathResourceResolver;

// /**
//  *
//  * @author A.Rossi
//  */
// @Configuration
// //@EnableWebMvc
// public class WebMvcConfig implements WebMvcConfigurer {
// //    @Override
// //    public void addViewControllers(ViewControllerRegistry registry) {
// //        registry.addViewController("/").setViewName("forward:/index.html");
// //    }
	
	
//     @Override
//     public void addResourceHandlers(ResourceHandlerRegistry registry) {
//     /// Provvede a servire i file statici per il front end
		
//         registry
//             .addResourceHandler("/**")
//             //.addResourceLocations("classpath:/resources/build")
// 			.addResourceLocations("file:///C:/temp/web/")
//             .setCachePeriod(3600)
//             .resourceChain(true)
//             .addResolver(new PathResourceResolver() {
//                 @Override
//                 protected Resource getResource(String resourcePath, Resource location) throws IOException {
//                     Resource requestedResource = location.createRelative(resourcePath);
//                     //return requestedResource.exists() && requestedResource.isReadable() ? requestedResource : new ClassPathResource("/resources/build/index.html");
// 					return requestedResource.exists() && requestedResource.isReadable() ? requestedResource : new UrlResource("file:///C:/temp/web/index.html");
//                 }
//             });
		
// //        registry
// //            .addResourceHandler(
// //                    "/assets/**"
// //            )
// //            .addResourceLocations("/resources/assets/")
// //            .resourceChain(true)
// //            .addResolver(new PathResourceResolver() {
// //                @Override
// //                protected Resource getResource(String resourcePath, Resource location) throws IOException {
// //                    log.debug(resourcePath);
// //                    Resource requestedResource = location.createRelative(resourcePath);
// //                    return requestedResource;
// //                }
// //            }
// //        );
// //        registry
// //            .addResourceHandler(
// //                    "/login/**", 
// //                    "/cittadino/**", 
// //                    "/unimplemented", 
// //                    "/unauthorized",
// //                    "/utente",
// //                    "/edit/**",
// //                    "/view/**",
// //                    "/index.html",
// //                    "/"
// //            )
// //            .addResourceLocations("/resources/")
// //            .resourceChain(true)
// //            .addResolver(new PathResourceResolver() {
// //                @Override
// //                protected Resource getResource(String resourcePath, Resource location) throws IOException {
// //                    log.debug(resourcePath);
// //                    /*
// //                    if (resourcePath.startsWith("assets/")) {
// //                        //Resource requestedResource = location.createRelative(resourcePath);
// //                        //return requestedResource;
// //                        return new ClassPathResource("/resources/" + resourcePath);
// //                    }
// //                    else {
// //                        return new ClassPathResource("/resources/index.html");
// //                    }
// //                    */
// //                    Resource requestedResource = location.createRelative(resourcePath);
// //                    return requestedResource.exists() 
// //                            && requestedResource.isReadable() ? requestedResource 
// //                                : new ClassPathResource("/resources/index.html");
// //                }
// //            }
// //        );
		
		
// //        registry
// //            .addResourceHandler(
// //                    "/assets/**"
// //            )
// //            .addResourceLocations("classpath:/static/")
// //            .resourceChain(true)
// //            .addResolver(new PathResourceResolver() {
// //                @Override
// //                protected Resource getResource(String resourcePath, Resource location) throws IOException {
// //                    log.debug(resourcePath);
// //                    Resource requestedResource = location.createRelative(resourcePath);
// //                    return requestedResource.exists() 
// //                            && requestedResource.isReadable() ? requestedResource 
// //                                : new ClassPathResource("/resources/assets/"+resourcePath);
// //                }
// //            }
// //        );
//     }
// }
package it.almaviva.foliage.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import it.almaviva.foliage.tracking.FoliageTrackingInterceptor;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
	
	// @Bean
	// @Autowired
	// public WebDal getWebDal(
	// 	JdbcTemplate jdbcTemplate,
	// 	TransactionTemplate transactionTemplate,
	// 	PlatformTransactionManager platformTransactionManager
	// ) throws Exception{
	// 	return new WebDal(jdbcTemplate, transactionTemplate, platformTransactionManager);
	// }
	
	// @Bean
	// @Autowired
	// public LegacyDal getLegacyDal(
	// 	JdbcTemplate jdbcTemplate,
	// 	TransactionTemplate transactionTemplate,
	// 	PlatformTransactionManager platformTransactionManager
	// ) throws Exception{
	// 	return new LegacyDal(jdbcTemplate, transactionTemplate, platformTransactionManager);
	// }

	// @Bean
	// @Autowired
	// public BaseDal getBaseDal(
	// 	JdbcTemplate jdbcTemplate,
	// 	TransactionTemplate transactionTemplate,
	// 	PlatformTransactionManager platformTransactionManager
	// ) throws Exception{
	// 	return new BaseDal(jdbcTemplate, transactionTemplate, platformTransactionManager);
	// }
	@Bean
	@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
	FoliageTrackingInterceptor getFoliageTrackingInterceptor() {
		return new FoliageTrackingInterceptor();
	}


	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(getFoliageTrackingInterceptor());
	}


	// @Bean
	// public CommonsRequestLoggingFilter requestLoggingFilter() {
	// 	CommonsRequestLoggingFilter loggingFilter = new CommonsRequestLoggingFilter();
	// 	loggingFilter.setIncludeClientInfo(true);
	// 	loggingFilter.setIncludeQueryString(true);
	// 	loggingFilter.setIncludePayload(true);
	// 	loggingFilter.setIncludeHeaders(true);
		
	// 	loggingFilter.setMaxPayloadLength(64000);
	// 	return loggingFilter;
	// }
}