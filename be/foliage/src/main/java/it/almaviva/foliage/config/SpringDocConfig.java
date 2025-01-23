// package it.almaviva.foliage.config;

// import java.util.Map;

// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;

// import io.swagger.v3.oas.models.Components;
// import io.swagger.v3.oas.models.OpenAPI;
// import io.swagger.v3.oas.models.info.Info;
// import io.swagger.v3.oas.models.security.OAuthFlow;
// import io.swagger.v3.oas.models.security.OAuthFlows;
// import io.swagger.v3.oas.models.security.Scopes;
// import io.swagger.v3.oas.models.security.SecurityRequirement;
// import io.swagger.v3.oas.models.security.SecurityScheme;

// @Configuration
// public class SpringDocConfig {                                    
// 	// @Bean
// 	// public GroupedOpenApi publicApi() {
// 	// 	return GroupedOpenApi.builder()
// 	// 			.group("springshop-public")
// 	// 			.pathsToMatch("/public/**")
// 	// 			.build();
// 	// }
	
// 	@Bean
// 	public OpenAPI springShopOpenAPI() {
//         return new OpenAPI()
//                 .info(new Info().title("Foliage API")
// 				.description("Api rest per l'applicazione Life Foliage")
//                 .version("1.0"))
//                 .components(getComponents())
// 				.addSecurityItem(new SecurityRequirement().addList("Access Token"));
// 	}

// 	private Components getComponents() {
// 		SecurityScheme authorizationHeaderSchema = new SecurityScheme()
// 				.name("Authorization")
// 				.type(SecurityScheme.Type.APIKEY)
// 				.in(SecurityScheme.In.HEADER);

// 		return new Components()
// 				.securitySchemes(Map.of("Access Token", authorizationHeaderSchema));
// 	}
// }
