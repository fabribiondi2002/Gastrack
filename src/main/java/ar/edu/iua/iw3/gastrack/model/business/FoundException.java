package ar.edu.iua.iw3.gastrack.model.business;

import lombok.Builder;
import lombok.NoArgsConstructor;

/**
 * Excepcion lanzada cuando se encuentra un recurso que no deberia existir
 * @author Leandro Biondi
 * @author Benjamin Vargas
 * @author Antonella Badami
 * @version 1.0
 * @since 2025-10-16
 */

@NoArgsConstructor
public class FoundException extends Exception {

	@Builder
	public FoundException(String message, Throwable ex) {
		super(message, ex);
	}

	@Builder
	public FoundException(String message) {
		super(message);
	}

	@Builder
	public FoundException(Throwable ex) {
		super(ex);
	}
}