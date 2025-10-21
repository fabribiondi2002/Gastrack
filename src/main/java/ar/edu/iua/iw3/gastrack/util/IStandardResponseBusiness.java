package ar.edu.iua.iw3.gastrack.util;

import org.springframework.http.HttpStatus;
/*
 * Interfaz para la construcción de respuestas estándar
 * @author Leandro Biondi
 * @author Benjamin Vargas
 * @author Antonella Badami
 * @version 1.0
 * @since 2025-10-21
 */
public interface IStandardResponseBusiness {
	public StandardResponse build(HttpStatus httpStatus, Throwable ex, String message);

}