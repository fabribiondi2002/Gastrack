package ar.edu.iua.iw3.gastrack.model.business.exception;

import lombok.Builder;
import lombok.NoArgsConstructor;

/**
 * Excepcion lanzada cuando se envia un atributo invalido para una orden
 * @author Leandro Biondi
 * @author Benjamin Vargas
 * @author Antonella Badami
 * @version 1.0
 * @since 2025-10-24
 */

@NoArgsConstructor
public class InvalidOrderAttributeException extends Exception {

	@Builder
	public InvalidOrderAttributeException(String message, Throwable ex) {
		super(message, ex);
	}

	@Builder
	public InvalidOrderAttributeException(String message) {
		super(message);
	}

	@Builder
	public InvalidOrderAttributeException(Throwable ex) {
		super(ex);
	}
}