package ar.edu.iua.iw3.gastrack.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
/*
 * Clase para la construcción de respuestas estándar
 * @author Leandro Biondi
 * @author Benjamin Vargas
 * @author Antonella Badami
 * @version 1.0
 * @since 2025-10-21
 */
@Service
public class StandardResponseBusiness implements IStandardResponseBusiness {

	@Value("${dev.info.enabled:false}")
	private boolean devInfoEnabled;

	@Override
	public StandardResponse build(HttpStatus httpStatus, Throwable ex, String message) {
		StandardResponse sr=new StandardResponse();
		sr.setDevInfoEnabled(devInfoEnabled);
		sr.setMessage(message);
		sr.setHttpStatus(httpStatus);
		sr.setEx(ex);
		return sr;
	}


}