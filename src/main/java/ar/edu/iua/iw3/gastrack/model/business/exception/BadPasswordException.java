package ar.edu.iua.iw3.gastrack.model.business.exception;

import lombok.Builder;
import lombok.NoArgsConstructor;

/**
 * Excepcion lanzada cuando la contraseña ingresada es erronea
 * @author Leandro Biondi
 * @author Benjamin Vargas
 * @author Antonella Badami
 * @version 1.0
 * @since 2025-10-22
 */

@NoArgsConstructor
public class BadPasswordException extends Exception {

    @Builder
	public BadPasswordException(String message, Throwable ex) {
		super(message, ex);
	}

	@Builder
	public BadPasswordException(String message) {
		super(message);
	}

	@Builder
	public BadPasswordException(Throwable ex) {
		super(ex);
	}
}