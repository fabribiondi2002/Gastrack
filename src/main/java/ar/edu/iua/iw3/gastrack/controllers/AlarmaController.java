package ar.edu.iua.iw3.gastrack.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import ar.edu.iua.iw3.gastrack.model.Alarma;

import ar.edu.iua.iw3.gastrack.model.business.AlarmaBusiness;
import ar.edu.iua.iw3.gastrack.model.business.exception.BusinessException;
import ar.edu.iua.iw3.gastrack.model.serializers.AlarmaListSerializer;

import ar.edu.iua.iw3.gastrack.util.IStandardResponseBusiness;
import ar.edu.iua.iw3.gastrack.util.JsonUtils;


@RestController
@RequestMapping(Constants.URL_ALARMA)
public class AlarmaController {

	@Autowired
	private IStandardResponseBusiness response;
    @Autowired
    private AlarmaBusiness alarmaBusiness;

	@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
	@GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> list() {
		try {
			StdSerializer<Alarma> ser = new AlarmaListSerializer(Alarma.class, false);
			String result = JsonUtils.getObjectMapper(Alarma.class, ser, null)
					.writeValueAsString(alarmaBusiness.list());
			Object jsonResult = new ObjectMapper().readValue(result, Object.class);
			return new ResponseEntity<>(jsonResult, HttpStatus.OK);
		} catch (BusinessException e) {
			return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (JsonProcessingException e) {
			return new ResponseEntity<>(
					response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, "Error procesando el resultado en JSON"),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} 
	}
}