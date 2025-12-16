package ar.edu.iua.iw3.gastrack.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import ar.edu.iua.iw3.gastrack.model.Alarma;
import ar.edu.iua.iw3.gastrack.model.business.AlarmaBusiness;
import ar.edu.iua.iw3.gastrack.model.business.exception.BusinessException;
import ar.edu.iua.iw3.gastrack.model.business.exception.NotFoundException;
import ar.edu.iua.iw3.gastrack.model.serializers.AlarmaListSerializer;
import ar.edu.iua.iw3.gastrack.util.IStandardResponseBusiness;
import ar.edu.iua.iw3.gastrack.util.JsonUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;


@RestController
@RequestMapping(Constants.URL_ALARMA)
public class AlarmaController {

	@Autowired
	private IStandardResponseBusiness response;
    @Autowired
    private AlarmaBusiness alarmaBusiness;

	
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
	@Operation(operationId = "listar-alarmas", summary = "Lista todas las alarmas.", description = "Permite listar todas las alarmas registradas en el sistema.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Alarmas listadas correctamente.", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "Lista de alarmas", summary = "Lista completa de alarmas", description = "Ejemplo de respuesta JSON al listar alarmas.", value = """
					[
					    {
					        "fecha": "2025-12-11T10:30:00",
					        "tipo": "TEMPERATURA ALTA",
					        "numero-orden": 12345,
					        "aceptada": false
					    },
					    {
					        "fecha": "2025-12-11T12:45:00",
					        "tipo": "PRESION BAJA",
					        "numero-orden": 12346,
					        "aceptada": true
					    }
					]
					"""))),
			@ApiResponse(responseCode = "500", description = "Error interno del servidor.", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "Error interno del servidor", value = """
					{
						"message": "Error procesando el resultado en JSON",
						"code": 500,
						"devInfo": "com.fasterxml.jackson.core.JsonProcessingException"
					}
					""")))
	})
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


	@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
	@Operation(operationId = "aceptar-alarma", summary = "Acepta una alarma.", description = "Permite aceptar una alarma registrada en el sistema especificando el número de orden, tipo de alarma, observación y correo del usuario.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Alarma aceptada correctamente.", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "Alarma aceptada", summary = "Alarma aceptada exitosamente", description = "Ejemplo de respuesta JSON al aceptar una alarma.", value = """
					{
					    "fecha": "2025-12-11T10:30:00",
					    "tipo": "TEMPERATURA ALTA",
					    "numero-orden": 12345,
					    "aceptada": true
					}
					"""))),
			@ApiResponse(responseCode = "400", description = "Solicitud inválida.", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "Error de solicitud", value = """
					{
						"message": "Datos inválidos en la solicitud",
						"code": 400,
						"devInfo": "java.lang.IllegalArgumentException"
					}
					"""))),
			@ApiResponse(responseCode = "404", description = "Alarma no encontrada.", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "Alarma no encontrada", value = """
					{
						"message": "Alarma no encontrada",
						"code": 404,
						"devInfo": "ar.edu.iua.iw3.gastrack.model.business.exception.NotFoundException"
					}
					"""))),
			@ApiResponse(responseCode = "500", description = "Error interno del servidor.", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "Error interno del servidor", value = """
					{
						"message": "Error procesando el resultado en JSON",
						"code": 500,
						"devInfo": "com.fasterxml.jackson.core.JsonProcessingException"
					}
					""")))
	})
	@io.swagger.v3.oas.annotations.parameters.RequestBody(
			description = "Datos de la alarma a aceptar",
			required = true,
			content = @Content(
					mediaType = "application/json",
					examples = @ExampleObject(
							name = "Ejemplo de aceptación de alarma",
							summary = "Datos necesarios para aceptar una alarma",
							description = "JSON con el número de orden, tipo de alarma, observación y correo del usuario",
							value = """
									{
									    "numero-orden": 12345,
									    "tipo-alarma": "TEMPERATURA_ALTA",
									    "observacion": "Alarma revisada y aceptada",
									    "usermail": "usuario@ejemplo.com"
									}
									"""
					)
			)
	)
    @PutMapping(value = "/aceptar", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> aceptarAlarma(HttpEntity<String> httpEntity) {
        try {
            
			StdSerializer<Alarma> ser = new AlarmaListSerializer(Alarma.class, false);
			String result = JsonUtils.getObjectMapper(Alarma.class, ser, null)
					.writeValueAsString(alarmaBusiness.aceptarAlarma(httpEntity.getBody()));
			Object jsonResult = new ObjectMapper().readValue(result, Object.class);
			return new ResponseEntity<>(jsonResult, HttpStatus.OK);


        }catch (BusinessException e) {
			return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (NotFoundException e) {
			return new ResponseEntity<>(
					response.build(HttpStatus.NOT_FOUND, e, e.getMessage()),
					HttpStatus.NOT_FOUND);
		} catch (IllegalArgumentException e) {
			return new ResponseEntity<>(
					response.build(HttpStatus.BAD_REQUEST, e, e.getMessage()),
					HttpStatus.BAD_REQUEST);
		}
		catch (JsonProcessingException e) {
			return new ResponseEntity<>(
					response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, "Error procesando el resultado en JSON"),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
    }
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
	@Operation(operationId = "listar-alarmas-no-aceptadas", summary = "Lista todas las alarmas no aceptadas.", description = "Permite listar todas las alarmas no aceptadas registradas en el sistema.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Alarmas no aceptadas listadas correctamente.", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "Lista de alarmas no aceptadas", summary = "Lista completa de alarmas no aceptadas", description = "Ejemplo de respuesta JSON al listar alarmas no aceptadas.", value = """
					[
					    {
					        "fecha": "2025-12-11T10:30:00",
					        "tipo": "TEMPERATURA ALTA",
					        "numero-orden": 12345,
					        "aceptada": false
					    },
					    {
					        "fecha": "2025-12-11T12:45:00",
					        "tipo": "PRESION BAJA",
					        "numero-orden": 12346,
					        "aceptada": false
					    }
					]
					"""))),
			@ApiResponse(responseCode = "500", description = "Error interno del servidor.", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "Error interno del servidor", value = """
					{
						"message": "Error procesando el resultado en JSON",
						"code": 500,
						"devInfo": "com.fasterxml.jackson.core.JsonProcessingException"
					}
					""")))
	})
	@GetMapping(value = "/noAceptadas", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> listNoAceptadas() {
		try {
			StdSerializer<Alarma> ser = new AlarmaListSerializer(Alarma.class, false);
			String result = JsonUtils.getObjectMapper(Alarma.class, ser, null)
					.writeValueAsString(alarmaBusiness.loadNoAceptadas());
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