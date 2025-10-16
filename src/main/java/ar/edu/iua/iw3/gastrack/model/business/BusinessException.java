package ar.edu.iua.iw3.gastrack.model.business;

import lombok.Builder;
import lombok.NoArgsConstructor;

/**
 * Excepcion lanzada cuando ocurre un error no previsto en la logica de negocio
 * @author Leandro Biondi
 * @author Benjamin Vargas
 * @author Antonella Badami
 * @version 1.0
 * @since 2025-10-16
 */

@NoArgsConstructor
public class BusinessException extends Exception {

    @Builder
	public BusinessException(String message, Throwable ex) {
		super(message, ex);
	}

	@Builder
	public BusinessException(String message) {
		super(message);
	}

	@Builder
	public BusinessException(Throwable ex) {
		super(ex);
	}
}