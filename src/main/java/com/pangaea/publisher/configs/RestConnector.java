package com.pangaea.publisher.configs;

import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLContext;

import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.TrustAllStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author VFDNBK-079
 */
@Configuration
@Slf4j
public class RestConnector {

	@Autowired
	ObjectMapper objectMapper;

	@Bean
	public RestTemplate getRestTemplate() throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
		RestTemplate restTemplate = new RestTemplate(getClientHttpRequestFactory());
		restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));
		return restTemplate;
	}

	private ClientHttpRequestFactory getClientHttpRequestFactory()
			throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException {

		SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(null, new TrustAllStrategy()).build();
		SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext, new NoopHostnameVerifier());

		CloseableHttpClient httpClient = HttpClientBuilder.create().setSSLSocketFactory(csf)
				.setDefaultRequestConfig(RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD).build())
				.setConnectionReuseStrategy((response, context) -> false).build();

		return new HttpComponentsClientHttpRequestFactory(httpClient);
	}

	public ResponseEntity<String> exchange(String requestBody, String urlToCall, HttpHeaders headers,
			HttpMethod httpMethod) throws JsonProcessingException {
		try {
			log.info("CALLING URL:::" + urlToCall);
			if (headers == null)
				headers = new HttpHeaders();
			HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
			log.info("REQUEST BODY:::" + entity.getBody());
			HttpMethod exchangeMethod = (httpMethod != null) ? httpMethod : HttpMethod.POST;
			ResponseEntity<String> response = getRestTemplate().exchange(urlToCall, exchangeMethod, entity,
					String.class, new Object[0]);
			log.info("RESPONSE FROM SERVICE:::" + response);
			return response;
		} catch (HttpClientErrorException e) {
			log.error("SERVICE RETURNED CLIENT ERROR :::" + e.getStatusCode());
			log.error("SERVICE RETURNED CLIENT BODY :::" + e.getResponseBodyAsString());
			return (e.getResponseBodyAsString() != null) ? ResponseEntity.badRequest().body(e.getResponseBodyAsString())
					: null;
		} catch (HttpServerErrorException e) {
			log.error("SERVICE RETURNED SERVER ERROR :::" + e.getStatusCode());
			log.error("SERVICE RETURNED SERVER BODY :::" + e.getResponseBodyAsString());
			return (e.getResponseBodyAsString() != null)
					? ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(e.getResponseBodyAsString())
					: null;
		} catch (ResourceAccessException ex) {
			log.info("========= SERVICE ERROR ======== CONNECTION TIME OUT : " + ex.getMessage());
			return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(objectMapper.writeValueAsString(null));
		} catch (Exception e) {
			log.error("ERROR OCCURRED ON CALLING SERVICE:::", e);
			return (e.getMessage() != null) ? ResponseEntity.badRequest().body(e.getMessage()) : null;
		}
	}

	public ResponseEntity<String> exchange(String urlToCall, HttpEntity<Object> entity, HttpMethod httpMethod)
			throws JsonProcessingException {
		try {
			log.info("CALLING URL:::" + urlToCall);

			log.info("REQUEST BODY:::" + entity.getBody());
			HttpMethod exchangeMethod = (httpMethod != null) ? httpMethod : HttpMethod.POST;
			ResponseEntity<String> response = getRestTemplate().exchange(urlToCall, exchangeMethod, entity,
					String.class, new Object[0]);
			log.info("RESPONSE FROM SERVICE:::" + response);
			return response;
		} catch (HttpClientErrorException e) {
			log.error("SERVICE RETURNED CLIENT ERROR :::" + e.getStatusCode());
			log.error("SERVICE RETURNED CLIENT BODY :::" + e.getResponseBodyAsString());
			return (e.getResponseBodyAsString() != null) ? ResponseEntity.badRequest().body(e.getResponseBodyAsString())
					: null;
		} catch (HttpServerErrorException e) {
			log.error("SERVICE RETURNED SERVER ERROR :::" + e.getStatusCode());
			log.error("SERVICE RETURNED SERVER BODY :::" + e.getResponseBodyAsString());
			return (e.getResponseBodyAsString() != null)
					? ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(e.getResponseBodyAsString())
					: null;
		} catch (ResourceAccessException ex) {
			log.info("========= SERVICE ERROR ======== CONNECTION TIME OUT : " + ex.getMessage());

			return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(null);
		} catch (Exception e) {
			log.error("ERROR OCCURRED ON CALLING SERVICE:::", e);
			return (e.getMessage() != null) ? ResponseEntity.badRequest().body(e.getMessage()) : null;
		}
	}

}
