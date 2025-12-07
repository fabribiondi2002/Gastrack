package ar.edu.iua.iw3.gastrack.auth;

import lombok.Builder;
import lombok.NoArgsConstructor;
/**
 * Excepción lanzada cuando una contraseña no cumple con los requisitos establecidos.
 * @author Leandro Biondi
 * @author Benjamin Vargas
 * @author Antonella Badami
 * @version 1.0
 * @since 2025-12-07
 */
@NoArgsConstructor
public class BadPasswordException extends Exception {

	private static final long serialVersionUID = -8582277206660722157L;

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
		super(ex.getMessage(), ex);
	}
}