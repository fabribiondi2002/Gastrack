package ar.edu.iua.iw3.gastrack.model.business.exception;

import lombok.Builder;
import lombok.NoArgsConstructor;

/**
 * Excepcion lanzada cuando se intenta bloquear la carga de una orden que ya esta bloqueada
 * @author Leandro Biondi
 * @author Benjamin Vargas
 * @author Antonella Badami
 * @version 1.0
 * @since 2025-10-23
 */

@NoArgsConstructor
public class OrderAlreadyLockedToLoadException extends Exception {

	@Builder
	public OrderAlreadyLockedToLoadException(String message, Throwable ex) {
		super(message, ex);
	}

	@Builder
	public OrderAlreadyLockedToLoadException(String message) {
		super(message);
	}

	@Builder
	public OrderAlreadyLockedToLoadException(Throwable ex) {
		super(ex);
	}
}