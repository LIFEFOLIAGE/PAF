// package it.almaviva.foliage.authentication;

// import java.io.IOException;

// import org.springframework.http.ResponseEntity;
// import org.springframework.security.core.AuthenticationException;
// import org.springframework.security.web.authentication.AuthenticationFailureHandler;
// import org.springframework.stereotype.Component;

// import com.fasterxml.jackson.databind.ObjectMapper;

// import jakarta.servlet.ServletException;
// import jakarta.servlet.http.HttpServletRequest;
// import jakarta.servlet.http.HttpServletResponse;

// @Component
// public class FoliageAuthenticationFailureHandler implements AuthenticationFailureHandler {
// 	private static final ObjectMapper om = new ObjectMapper();
//     @Override
//     public void onAuthenticationFailure(
// 		HttpServletRequest request,
// 		HttpServletResponse response,
//         AuthenticationException exception
// 	) throws IOException, ServletException {
// 		//response.setStatus();
		
// 		String errStream = om.writeValueAsString(exception);
// 		response.sendError(
// 			HttpServletResponse.SC_FORBIDDEN, 
// 			errStream
// 		);
//     }
// }
