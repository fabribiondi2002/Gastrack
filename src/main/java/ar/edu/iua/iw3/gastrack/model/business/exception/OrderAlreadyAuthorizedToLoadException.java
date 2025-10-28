package ar.edu.iua.iw3.gastrack.model.business.exception;

import lombok.Builder;
import lombok.NoArgsConstructor;

/**
 * Excepcion lanzada cuando la orden ya se encuentra autorizada para carga y se intenta autorizar nuevamente
 * @author Leandro Biondi
 * @author Benjamin Vargas
 * @author Antonella Badami
 * @version 1.0
 * @since 2025-10-28
 */

@NoArgsConstructor
public class OrderAlreadyAuthorizedToLoadException extends Exception {
    @Builder
	public OrderAlreadyAuthorizedToLoadException(String message, Throwable ex) {
		super(message, ex);
	}

	@Builder
	public OrderAlreadyAuthorizedToLoadException(String message) {
		super(message);
	}

	@Builder
	public OrderAlreadyAuthorizedToLoadException(Throwable ex) {
		super(ex);
	}
    
}
