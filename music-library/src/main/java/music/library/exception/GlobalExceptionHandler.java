package music.library.exception;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;

/*
 Centralized error handling for the whole API. 
 It turns various exceptions into a uniform {@link ApiError} JSON payload.
 Spring scans the methods in the @RestControllerAdvice class and picks the first matching handler 
 based on exception type specificity, not on declaration order.
 But placing the most specific handlers before the catch-all Exception handler makes the file 
 easier to read and mirrors the logical flow: 
 Specific → Validation → Type-mismatch → JSON parsing → Not-found → Duplicate → Fallback.
*/

@RestControllerAdvice
public class GlobalExceptionHandler {

	private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	/*
	 * --------------------------------------------------------- 
	 * 400 – Bean validation failures (invalid @Valid body)
	 * ---------------------------------------------------------
	 */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiError> handleValidationException(MethodArgumentNotValidException ex,
			HttpServletRequest request) {

		List<String> fieldErrors = ex.getBindingResult().getFieldErrors().stream().map(this::formatFieldError)
				.collect(Collectors.toList());

		ApiError error = ApiError.builder().timestamp(Instant.now()).status(HttpStatus.BAD_REQUEST.value())
				.error(HttpStatus.BAD_REQUEST.getReasonPhrase()).message("Validation failed")
				.path(request.getRequestURI()).validationErrors(fieldErrors).build();

		return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
	}

	private String formatFieldError(FieldError fe) {
		return String.format("%s: %s", fe.getField(), fe.getDefaultMessage());
	}

	/*
	 * --------------------------------------------------------- 
	 * 400 – Constraint violation (e.g. @Positive on @PathVariable)
	 * ---------------------------------------------------------
	 */
	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<ApiError> handleConstraintViolation(ConstraintViolationException ex,
			HttpServletRequest request) {

		List<String> violations = ex.getConstraintViolations().stream()
				.map(cv -> cv.getPropertyPath() + ": " + cv.getMessage()).collect(Collectors.toList());

		ApiError error = ApiError.builder().timestamp(Instant.now()).status(HttpStatus.BAD_REQUEST.value())
				.error(HttpStatus.BAD_REQUEST.getReasonPhrase()).message("Constraint violation")
				.path(request.getRequestURI()).validationErrors(violations).build();

		return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
	}

	/*
	 * --------------------------------------------------------- 
	 * 404 – Resource not found (custom exception)
	 * ---------------------------------------------------------
	 */
	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ApiError> handleNotFound(ResourceNotFoundException ex, HttpServletRequest request) {

		ApiError error = ApiError.builder().timestamp(Instant.now()).status(HttpStatus.NOT_FOUND.value())
				.error(HttpStatus.NOT_FOUND.getReasonPhrase()).message(ex.getMessage()).path(request.getRequestURI())
				.validationErrors(null).build();

		return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
	}

	/*
	 * --------------------------------------------------------- 
	 * 409 – Duplicate resource (custom exception)
	 * ---------------------------------------------------------
	 */
	@ExceptionHandler(DuplicateResourceException.class)
	public ResponseEntity<ApiError> handleDuplicateResource(DuplicateResourceException ex, HttpServletRequest request) {

		ApiError error = ApiError.builder().timestamp(Instant.now()).status(HttpStatus.CONFLICT.value())
				.error(HttpStatus.CONFLICT.getReasonPhrase()).message(ex.getMessage()).path(request.getRequestURI())
				.validationErrors(null).build();

		return new ResponseEntity<>(error, HttpStatus.CONFLICT);
	}

	/*
	 * --------------------------------------------------------- 
	 * 400 – Wrong type for a request parameter or path variable
	 * ---------------------------------------------------------
	 */
	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<ApiError> handleTypeMismatch(MethodArgumentTypeMismatchException ex,
			HttpServletRequest request) {

		String msg = String.format("Parameter '%s' should be of type %s", ex.getName(),
				ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown");

		ApiError error = ApiError.builder().timestamp(Instant.now()).status(HttpStatus.BAD_REQUEST.value())
				.error(HttpStatus.BAD_REQUEST.getReasonPhrase()).message(msg).path(request.getRequestURI())
				.validationErrors(null).build();

		return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
	}

	/*
	 * --------------------------------------------------------- 
	 * 400 – Malformed JSON (cannot be parsed)
	 * ---------------------------------------------------------
	 */
	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ApiError> handleUnreadable(HttpMessageNotReadableException ex, HttpServletRequest request) {

		ApiError error = ApiError.builder().timestamp(Instant.now()).status(HttpStatus.BAD_REQUEST.value())
				.error(HttpStatus.BAD_REQUEST.getReasonPhrase()).message("Malformed JSON request")
				.path(request.getRequestURI()).validationErrors(null).build();

		return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
	}

	/*
	 * --------------------------------------------------------- 
	 * 500 – Any other unexpected exception (fallback)
	 * ---------------------------------------------------------
	 */
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiError> handleAllExceptions(Exception ex, HttpServletRequest request) {

		// Log the stack trace with SLF4J (better than printStackTrace)
		log.error("Unexpected error at {}: {}", request.getRequestURI(), ex.getMessage(), ex);

		ApiError error = ApiError.builder().timestamp(Instant.now())
				.status(HttpStatus.INTERNAL_SERVER_ERROR.value())
				.error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
				.message(ex.getMessage() != null ? ex.getMessage() : "Unexpected error").path(request.getRequestURI())
				.validationErrors(null).build();

		return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
