/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.almaviva.foliage.authentication;

import com.auth0.jwk.Jwk;
import com.auth0.jwk.JwkException;
import com.auth0.jwk.JwkProvider;
import com.auth0.jwk.NetworkException;
import com.auth0.jwk.SigningKeyNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.google.common.collect.Lists;

import it.almaviva.foliage.FoliageException;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;

/**
 *
 * @author A.Rossi
 */
@Slf4j
public class FoliageJwkProvider implements JwkProvider {

	private final URI uri;
	private final ObjectReader reader;

	public FoliageJwkProvider(String jwksUrl) {
		try {
			this.uri = new URI(jwksUrl).normalize();
		} catch (URISyntaxException e) {
			throw new IllegalArgumentException("Invalid jwks uri", e);
		}
		this.reader = new ObjectMapper().readerFor(Map.class);
	}

	@Override
	public Jwk get(String keyId) throws JwkException {
		final List<Jwk> jwks = getAll();
		if (keyId == null && jwks.size() == 1) {
			return jwks.get(0);
		}
		if (keyId != null) {
			for (Jwk jwk : jwks) {
				String id = jwk.getId();
				if (keyId.equals(id)) {
					return jwk;
				}
			}
		}
		throw new SigningKeyNotFoundException("No key found in " + uri.toString() + " with kid " + keyId, null);
	}

	private List<Jwk> getAll() throws SigningKeyNotFoundException {
		List<Jwk> jwks = Lists.newArrayList();
		final List<Map<String, Object>> keys = (List<Map<String, Object>>) getJwks().get("keys");
		if (keys == null || keys.isEmpty()) {
			throw new SigningKeyNotFoundException("No keys found in " + uri.toString(), null);
		}
		try {
			for (Map<String, Object> values : keys) {
				jwks.add(Jwk.fromValues(values));
			}
		} catch (IllegalArgumentException e) {
			throw new SigningKeyNotFoundException("Failed to parse jwk from json", e);
		}
		return jwks;
	}

	private Map<String, Object> getJwks() throws SigningKeyNotFoundException {
		try {
			log.debug(String.format("Tentativo di connessione in corso a %s", this.uri));
			HttpClient client = HttpClient.newHttpClient();
			HttpRequest request = HttpRequest.newBuilder()
					.uri(this.uri)
					.headers("Accept", "application/json")
					.GET()
					.build();

			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
			log.debug(String.format("Connessione a %s riscita", this.uri));

			return reader.readValue(response.body());

		} catch (IOException | InterruptedException e) {
			log.error(e.toString());
			throw new NetworkException("Cannot obtain jwks from url " + uri.toString(), e);
		}
	}
}
