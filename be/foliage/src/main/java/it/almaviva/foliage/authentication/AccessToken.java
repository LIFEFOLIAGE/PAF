/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.almaviva.foliage.authentication;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import it.almaviva.foliage.FoliageException;
import it.almaviva.foliage.function.VoidFunction;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.Base64;
import java.util.Collection;
import static java.util.Objects.isNull;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.core.GrantedAuthority;


/**
 *
 * @author A.Rossi
 */
//@RequiredArgsConstructor
@Slf4j
public class AccessToken {

	public static final String BEARER = "Bearer ";

	private final String value;
	private DecodedJWT decodedJWT;
	private JsonObject payloadAsJson;

	@Getter
	@Setter
	private Integer idUtente;

	@Getter
	@Setter
	private String username;

	@Getter
	@Setter
	private String name;

	@Getter
	@Setter
	private String surname;

	@Getter
	@Setter
	private String address;

	@Getter
	@Setter
	private String city;
	
	@Getter
	@Setter
	private String cap;

	@Getter
	@Setter
	private String codiceFiscale;

	@Getter
	@Setter
	private String birthPlace;
	
	@Getter
	@Setter
	private LocalDate birthDate;

	// @Getter
	// @Setter
	// private String phoneNumber;

	@Getter
	@Setter
	private String gender;

	// @Getter
	// @Setter
	// private String email;

	// @Getter
	// @Setter
	// private String pec;

	@Getter
	@Setter
	private FoliageGrantedAuthority profiloDefault;

	//@Getter
	private Collection<GrantedAuthority> authorities;
	public Collection<GrantedAuthority> getAuthorities(){
		return authorities;
	}

	public AccessToken(
		String tokValue,
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
		String dateFormat
	) throws FoliageAuthenticationException {
		this.value = tokValue;
		this.authorities = new LinkedList<GrantedAuthority>();
		decodedJWT = decodeToken(value);
		payloadAsJson = decodeTokenPayloadToJsonObject(decodedJWT);
		
		if (payloadAsJson != null) {
			username = fromPayload(usernameMember);
			name = fromPayload(nameMember);
			surname = fromPayload(surnameMember);
			address = fromPayload(addressMember);
			city = fromPayload(cityMember);
			cap = fromPayload(capMember);
			codiceFiscale = fromPayload(codiceFiscaleMember);
			if (username == null || "".equals(username)) {
				if (
					!(codiceFiscale == null || "".equals(codiceFiscale))
				) {
					username = codiceFiscale;
				}
				else {
					throw new FoliageAuthenticationException(String.format("Token senza username(%s) né codice fiscale(%s)", usernameMember, codiceFiscaleMember));
				}
			}
			String birthDateStr = fromPayload(birthDateMember);
			if (birthDateStr != null) {
				try {
					birthDate = LocalDate.parse(birthDateStr, DateTimeFormatter.ofPattern(dateFormat));
				}
				catch (Exception e) {
					log.error(String.format("Stringa birtDate(%s) con formato data non corrispondente a quello indicatao per il token", birthDateStr, dateFormat));
				}
			}
			birthPlace = fromPayload(birthPlaceMember);
			//phoneNumber = fromPayload(phoneNumberMember);
			gender = fromPayload(genderMember);
			//email = fromPayload(emailMember);
			//pec = fromPayload(pecMember);
		}
	}
	private String fromPayload(String member) {
		JsonElement e = payloadAsJson.get(member);
		if (e == null) {
			return null;
		}
		else {
			if (e.isJsonPrimitive()) {
				return e.getAsString();
			}
			else {
				return null;
			}
		}
	}

	public String getValueAsString() {
		return value;
	}

	private DecodedJWT decodeToken(String value) {
		if (isNull(value)){
			throw new FoliageAuthenticationException("Token has not been provided");
		}
		return JWT.decode(value);
	}

	private JsonObject decodeTokenPayloadToJsonObject(DecodedJWT decodedJWT) {
		try {
			String payloadAsString = decodedJWT.getPayload();
			String decodedString = new String(Base64.getDecoder().decode(payloadAsString), StandardCharsets.UTF_8);

			return new Gson().fromJson(
					decodedString,
					JsonObject.class
				);
		}
		catch (RuntimeException exception) {
			throw new FoliageAuthenticationException("Invalid JWT or JSON format of each of the jwt parts", exception);
		}
	}

	public Object authorizeExecution(
		String requestAuthority,
		VoidFunction<Object> authOk,
		VoidFunction<Object> authKo
	) throws Exception {
		GrantedAuthority userProf = authorities.stream()
  			.filter(authority -> requestAuthority.equals(authority.getAuthority()))
			.findAny()
  			.orElse(null);
		if (userProf != null) {
			return authOk.get();
		}
		else {
			return authKo.get();
		}
	}
}