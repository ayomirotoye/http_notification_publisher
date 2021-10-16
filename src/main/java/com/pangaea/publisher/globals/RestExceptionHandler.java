package com.pangaea.publisher.globals;

import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.ServiceUnavailableException;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.UnexpectedTypeException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Order(1000)
@RestControllerAdvice
@Slf4j
public class RestExceptionHandler {
	@Autowired
	ObjectMapper objectMapper;

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler({ MethodArgumentNotValidException.class })
	public Errors validationExceptionHandler(MethodArgumentNotValidException ex) {
		Map<String, String> responseBody = new HashMap<>();

		Errors errors = new Errors();
		try {
			ex.getBindingResult().getAllErrors().forEach(error -> {
				String fieldName = ((FieldError) error).getField();
				String errorMessage = error.getDefaultMessage();
				responseBody.put(fieldName, errorMessage);
			});
			errors.setError(responseBody);
		} catch (Exception e) {
			if (ex.getBindingResult() != null) {
				errors = processFieldErrors(ex.getBindingResult().getAllErrors(), errors, responseBody);
			}
		}
		return errors;
	}

	private Errors processFieldErrors(List<ObjectError> fieldErrors, Errors errors, Map<String, String> responseBody) {

		if (fieldErrors != null) {
			fieldErrors.stream().forEach(error -> {
				String fieldName = error.getCode();
				String errorMessage = error.getDefaultMessage();
				responseBody.put(fieldName, errorMessage);
			});
			errors.setError(responseBody != null ? responseBody.values() : "");
		}

		return errors;
	}

	@ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
	@ExceptionHandler({ SocketTimeoutException.class })
	public ResponseEntity<Map<String, Object>> validationExceptionHandler(SocketTimeoutException ex) {
		Map<String, Object> errors = new HashMap<>();
		log.debug("==================== ERROR OCCURRED =================" + ex);
		return new ResponseEntity<>(errors, HttpStatus.SERVICE_UNAVAILABLE);
	}

	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler({ RuntimeException.class })
	public ResponseEntity<Map<String, Object>> RunTimeException(RuntimeException ex) {
		Map<String, Object> errors = new HashMap<>();
		log.debug("==================== ERROR OCCURRED =================" + ex);
		return new ResponseEntity<>(errors, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler({ HttpMessageConversionException.class })
	public ResponseEntity<Map<String, Object>> handleConversionEception(HttpMessageConversionException ex) {
		log.debug("==================== ERROR OCCURRED =================" + ex);
		return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler({ UnexpectedTypeException.class })
	public ResponseEntity<Map<String, Object>> handleUnexpectedTypeException(UnexpectedTypeException ex) {
		log.debug("==================== ERROR OCCURRED =================" + ex);
		return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler({ ConstraintViolationException.class })
	public Errors handleConstraintViolationException(ConstraintViolationException ex) {
		Map<String, String> responseBody = new HashMap<>();

		Errors errors = new Errors();
		try {
			for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
				responseBody.put(violation.getPropertyPath().toString(), violation.getMessage());
			}

			errors.setError(responseBody);
		} catch (Exception e) {

		}
		return errors;
	}

	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler({ NullPointerException.class })
	public ResponseEntity<Map<String, Object>> handleNullPointerException(NullPointerException ex) {
		log.error("==================== ERROR OCCURRED =================" + ex);
		return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
	@ExceptionHandler({ HttpRequestMethodNotSupportedException.class })
	public ResponseEntity<Map<String, Object>> handleMethodNotSupportedException(
			HttpRequestMethodNotSupportedException ex) {
		return new ResponseEntity<>(null, HttpStatus.METHOD_NOT_ALLOWED);
	}

	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler({ InternalServerErrorException.class })
	public ResponseEntity<?> handleInternalServerErrorException(InternalServerErrorException ex) {
		return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
	@ExceptionHandler({ HttpMediaTypeNotSupportedException.class })
	public ResponseEntity<?> handleHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException ex) {
		return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler({ HttpClientErrorException.class })
	public ResponseEntity<?> handleHttpClientErrorException(HttpClientErrorException ex) {
		return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
	@ExceptionHandler({ ServiceUnavailableException.class })
	public ResponseEntity<?> handleServiceUnavailableException(ServiceUnavailableException ex) {
		return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
	@ExceptionHandler({ ResourceAccessException.class })
	public ResponseEntity<?> handleResourceAccessException(ResourceAccessException ex) {
		return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler({ HttpMessageNotReadableException.class })
	public ResponseEntity<?> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
		return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
	@ExceptionHandler({ HttpServerErrorException.class })
	public ResponseEntity<?> handleHttpServerErrorException(HttpServerErrorException ex) {
		return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
	}

}
