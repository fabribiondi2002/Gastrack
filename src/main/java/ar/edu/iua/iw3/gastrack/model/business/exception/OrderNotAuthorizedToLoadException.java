package ar.edu.iua.iw3.gastrack.model.business.exception;

import lombok.Builder;
import lombok.NoArgsConstructor;

/**
 * Excepcion lanzada cuando se intenta registrar un detalle en una orden que no esta habilitada para carga
 * @author Leandro Biondi
 * @author Benjamin Vargas
 * @author Antonella Badami
 * @version 1.0
 * @since 2025-10-2
 */

@NoArgsConstructor
public class OrderNotAuthorizedToLoadException extends Exception {

	@Builder
	public OrderNotAuthorizedToLoadException(String message, Throwable ex) {
		super(message, ex);
	}

	@Builder
	public OrderNotAuthorizedToLoadException(String message) {
		super(message);
	}

	@Builder
	public OrderNotAuthorizedToLoadException(Throwable ex) {
		super(ex);
	}
}