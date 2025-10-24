package ar.edu.iua.iw3.gastrack.model.business.exception;

import lombok.Builder;
import lombok.NoArgsConstructor;

/**
 * Excepcion lanzada cuando una orden se encuentra en un estado invalido
 * @author Leandro Biondi
 * @author Benjamin Vargas
 * @author Antonella Badami
 * @version 1.0
 * @since 2025-10-22
 */

@NoArgsConstructor
public class OrderInvalidStateException extends Exception {

	@Builder
	public OrderInvalidStateException(String message, Throwable ex) {
		super(message, ex);
	}

	@Builder
	public OrderInvalidStateException(String message) {
		super(message);
	}

	@Builder
	public OrderInvalidStateException(Throwable ex) {
		super(ex);
	}
}