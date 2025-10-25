package ar.edu.iua.iw3.gastrack.model.business.exception;

import lombok.Builder;
import lombok.NoArgsConstructor;

/**
 * Excepcion lanzada cuando se envia un detalle fuera de la frecuencia permitida
 * @author Leandro Biondi
 * @author Benjamin Vargas
 * @author Antonella Badami
 * @version 1.0
 * @since 2025-10-23
 */

@NoArgsConstructor
public class InvalidDetailFrecuencyException extends Exception {

	@Builder
	public InvalidDetailFrecuencyException(String message, Throwable ex) {
		super(message, ex);
	}

	@Builder
	public InvalidDetailFrecuencyException(String message) {
		super(message);
	}

	@Builder
	public InvalidDetailFrecuencyException(Throwable ex) {
		super(ex);
	}
}