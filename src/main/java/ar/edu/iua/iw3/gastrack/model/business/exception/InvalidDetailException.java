package ar.edu.iua.iw3.gastrack.model.business.exception;

import lombok.Builder;
import lombok.NoArgsConstructor;

/**
 * Excepcion lanzada cuando se envia un detalle invalido
 * @author Leandro Biondi
 * @author Benjamin Vargas
 * @author Antonella Badami
 * @version 1.0
 * @since 2025-10-23
 */

@NoArgsConstructor
public class InvalidDetailException extends Exception {

	@Builder
	public InvalidDetailException(String message, Throwable ex) {
		super(message, ex);
	}

	@Builder
	public InvalidDetailException(String message) {
		super(message);
	}

	@Builder
	public InvalidDetailException(Throwable ex) {
		super(ex);
	}
}