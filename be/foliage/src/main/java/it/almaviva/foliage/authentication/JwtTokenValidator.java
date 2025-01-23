/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.almaviva.foliage.authentication;

import com.auth0.jwk.Jwk;
import com.auth0.jwk.JwkException;
import com.auth0.jwk.JwkProvider;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.nio.charset.StandardCharsets;
import java.security.interfaces.RSAPublicKey;
import java.time.Instant;
import java.util.Base64;
import static java.util.Objects.isNull;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author A.Rossi
 */
@Slf4j
public class JwtTokenValidator {
	private String usernameMember;
	private String nameMember;
	private String surnameMember;
	private String addressMember;
	private String cityMember;
	private String capMember;
	private String codiceFiscaleMember;
	private String birthDateMember;
	private String birthPlaceMember;
	// private String phoneNumberMember;
	private String genderMember;
	// private String emailMember;
	// private String pecMember;
	private String dateFormat;

	private boolean isDevelopment;

	private final JwkProvider jwkProvider;
	public JwtTokenValidator(
		JwkProvider jwkProvider,
		String usernameMember,
		String nameMember,
		String surnameMember,
		String addressMember,
		String cityMember,
		String capMember,
		String codiceFiscaleMember,
		String birthDateMember,
		String birthPlaceMember,
		//String phoneNumberMember,
		String genderMember,
		//String emailMember,
		//String pecMember,
		String dateFormat,
		boolean isDevelopment
	) {
		this.jwkProvider = jwkProvider;
		this.usernameMember = usernameMember;
		this.nameMember = nameMember;
		this.surnameMember = surnameMember;
		this.addressMember = addressMember;
		this.cityMember = cityMember;
		this.capMember = capMember;
		this.codiceFiscaleMember = codiceFiscaleMember;
		this.birthDateMember = birthDateMember;
		this.birthPlaceMember = birthPlaceMember;
		//this.phoneNumberMember = phoneNumberMember;
		this.genderMember = genderMember;
		//this.emailMember = emailMember;
		//this.pecMember = pecMember;
		this.dateFormat = dateFormat;
		this.isDevelopment = isDevelopment;
	}

	public AccessToken validateAuthorizationHeader(
		String authorizationHeader
	) throws FoliageAuthenticationException {
		String tokenValue = subStringBearer(authorizationHeader);
		if (!isDevelopment) {
			validateToken(tokenValue);
		}
		AccessToken vOut = new AccessToken(
			tokenValue,
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
			dateFormat
		);
		return vOut;
	}

	private void validateToken(String value) {
		DecodedJWT decodedJWT = decodeToken(value);
		verifyTokenHeader(decodedJWT);
		verifySignature(decodedJWT);
		verifyPayload(decodedJWT);
	}

	private DecodedJWT decodeToken(String value) {
		if (isNull(value)){
			throw new FoliageAuthenticationException("Token has not been provided");
		}
		DecodedJWT decodedJWT = JWT.decode(value);
		log.debug("Token decoded successfully");
		return decodedJWT;
	}

	private void verifyTokenHeader(DecodedJWT decodedJWT) {
		try {
			String jwtType = decodedJWT.getType();
			if (jwtType != null) {
				jwtType = jwtType.toUpperCase();
				Preconditions.checkArgument(
					jwtType.equals("JWT")
					|| jwtType.contains("+JWT+")
					|| jwtType.startsWith("JWT+")
					|| jwtType.endsWith("+JWT")
				);   
			}
			log.debug("Token's header is correct");
		} catch (IllegalArgumentException ex) {
			throw new FoliageAuthenticationException("Token is not JWT type", ex);
		}
	}

	private void verifySignature(DecodedJWT decodedJWT) {
		try {
			String keyId = decodedJWT.getKeyId();
			Jwk jwk = jwkProvider.get(keyId);
			Algorithm algorithm = Algorithm.RSA256((RSAPublicKey) jwk.getPublicKey(), null);
			algorithm.verify(decodedJWT);
			log.debug("Token's signature is correct");
		} catch (JwkException | SignatureVerificationException ex) {
			log.error(ex.toString());
			throw new FoliageAuthenticationException("Token has invalid signature", ex);
		}
	}

	private void verifyPayload(DecodedJWT decodedJWT) {
		JsonObject payloadAsJson = decodeTokenPayloadToJsonObject(decodedJWT);
		if (hasTokenExpired(payloadAsJson)) {
			throw new FoliageAuthenticationException("Token has expired");
		}
		log.debug("Token has not expired");

		//if (!hasTokenRealmRolesClaim(payloadAsJson)) {
		//    throw new InvalidTokenException("Token doesn't contain claims with realm roles");
		//}
		//log.debug("Token's payload contain claims with realm roles");

		// if (!hasTokenScopeInfo(payloadAsJson)) {
		// 	throw new InvalidTokenException("Token doesn't contain scope information");
		// }
		// log.debug("Token's payload contain scope information");
	}

	private JsonObject decodeTokenPayloadToJsonObject(DecodedJWT decodedJWT) {
		try {
			String payloadAsString = decodedJWT.getPayload();
			return new Gson().fromJson(
					new String(Base64.getDecoder().decode(payloadAsString), StandardCharsets.UTF_8),
					JsonObject.class);
		}   catch (RuntimeException exception){
			throw new FoliageAuthenticationException("Invalid JWT or JSON format of each of the jwt parts", exception);
		}
	}

	private boolean hasTokenExpired(JsonObject payloadAsJson) {
		Instant expirationDatetime = extractExpirationDate(payloadAsJson);
		return Instant.now().isAfter(expirationDatetime);
	}

	private Instant extractExpirationDate(JsonObject payloadAsJson) {
		try {
			return Instant.ofEpochSecond(payloadAsJson.get("exp").getAsLong());
		} catch (NullPointerException ex) {
			throw new FoliageAuthenticationException("There is no 'exp' claim in the token payload");
		}
	}

	private boolean hasTokenRealmRolesClaim(JsonObject payloadAsJson) {
		try {
			return payloadAsJson.getAsJsonObject("realm_access").getAsJsonArray("roles").size() > 0;
		} catch (NullPointerException ex) {
			return false;
		}
	}

	private boolean hasTokenScopeInfo(JsonObject payloadAsJson) {
		return payloadAsJson.has("scope");
	}

	private String subStringBearer(String authorizationHeader) {
		try {
			//return authorizationHeader;
			return authorizationHeader.substring(AccessToken.BEARER.length());
		} catch (Exception ex) {
			throw new FoliageAuthenticationException("There is no AccessToken in a request header");
		}
	}
}