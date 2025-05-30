package it.almaviva.foliage;

import java.util.TimeZone;

// import javax.net.ssl.HostnameVerifier;
// import javax.net.ssl.SSLSession;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


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
		SpringApplication.run(FoliageApplication.class, args);
	}
}
