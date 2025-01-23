package it.almaviva.foliage;

import java.util.TimeZone;

// import javax.net.ssl.HostnameVerifier;
// import javax.net.ssl.SSLSession;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import it.almaviva.foliage.istanze.CaricatoreIstanza;
import it.almaviva.foliage.istanze.SchedaIstanza;
import lombok.extern.slf4j.Slf4j;

import org.springframework.scheduling.annotation.EnableScheduling;


// class NullHostnameVerifier implements HostnameVerifier {
//     @Override
//     public boolean verify(String hostname, SSLSession session) {
//         return true;
//     }
// }

@SpringBootApplication(/*exclude = {DataSourceAutoConfiguration.class }*/)
@EnableScheduling
public class FoliageApplication {

	
	
	public static void main(String[] args) throws Exception {
		/*
		ApplicationContext applicationContext = new AnnotationConfigApplicationContext(FoliageApplication.class);

		for (String beanName : applicationContext.getBeanDefinitionNames()) {
			log.debug(beanName);
		}
		*/
		
		
		//HostnameVerifier verifier = new NullHostnameVerifier();
		//javax.net.ssl.HttpsURLConnection.setDefaultHostnameVerifier(verifier);
		
		
		SpringApplication.run(FoliageApplication.class, args);

		
	}
//        @Bean
//	public WebMvcConfigurer corsConfigurer() {
//		return new WebMvcConfigurer() {
//			@Override
//			public void addCorsMappings(CorsRegistry registry) {
//				registry.addMapping("/**").allowedOrigins("http://localhost:4200");
//			}
//		};
//	}

		

//    @Bean
//    public FilterRegistrationBean dawsonApiFilter() {
//        FilterRegistrationBean registration = new FilterRegistrationBean();
//        registration.setFilter(new AuthorizationRequestFilter());
//
//        // In case you want the filter to apply to specific URL patterns only
//        registration.addUrlPatterns("/istanze/*");
//        return registration;
//    }
}
