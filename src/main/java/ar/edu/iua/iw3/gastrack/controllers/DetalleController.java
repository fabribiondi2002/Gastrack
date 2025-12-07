package ar.edu.iua.iw3.gastrack.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ar.edu.iua.iw3.gastrack.model.Detalle;
import ar.edu.iua.iw3.gastrack.model.business.exception.BusinessException;
import ar.edu.iua.iw3.gastrack.model.business.exception.InvalidDetailException;
import ar.edu.iua.iw3.gastrack.model.business.exception.InvalidDetailFrecuencyException;
import ar.edu.iua.iw3.gastrack.model.business.exception.NotFoundException;
import ar.edu.iua.iw3.gastrack.model.business.exception.OrderInvalidStateException;
import ar.edu.iua.iw3.gastrack.model.business.exception.OrderNotAuthorizedToLoadException;
import ar.edu.iua.iw3.gastrack.model.business.intefaces.IDetalleBusiness;
import ar.edu.iua.iw3.gastrack.util.IStandardResponseBusiness;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

/**
 * Controlador REST para la gestion de detalles
 * @author Leandro Biondi
 * @author Benjamin Vargas
 * @author Antonella Badami
 * @version 1.0
 * @since 2025-10-21
 */

@RestController
@RequestMapping(Constants.URL_DETALLE)
public class DetalleController {
    @Autowired
	private IDetalleBusiness detalleBusiness;

    @Autowired
	private IStandardResponseBusiness response;

	@Operation(
		operationId = "get-detalles-by-orden-id",
		summary = "Obtiene la lista de detalles asociados a una orden específica.",
		description = "Recupera todos los detalles vinculados a la orden identificada por su ID. Devuelve un arreglo JSON con los detalles encontrados."
	)
	@Parameter(
		name = "id",
		description = "ID de la orden para la cual se desean obtener los detalles.",
		required = true,
		example = "1234"
	)
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "200",
			description = "Detalles recuperados correctamente.",
			content = @Content(
				mediaType = "application/json",
				schema = @Schema(implementation = Detalle.class)
			)
		),
		@ApiResponse(
			responseCode = "404",
			description = "No se encontró la orden o no tiene detalles asociados.",
			content = @Content(
				mediaType = "application/json",
				examples = @ExampleObject(
					name = "Orden no encontrada",
					value = """
					{
						"message": "No se encuentra la orden de numero:98776",
						"code": 404,
						"devInfo": "ar.edu.iua.iw3.gastrack.model.business.exception.NotFoundException: No se encuentra la orden de numero:98776"
					}
					"""
				)
			)
		),
		@ApiResponse(
			responseCode = "500",
			description = "Error interno del servidor.",
			content = @Content(
				mediaType = "application/json",
				examples = @ExampleObject(
					name = "Error interno",
					value = """
					{
						"message": null,
						"code": 500,
						"devInfo": "ar.edu.iua.iw3.gastrack.model.business.exception.BusinessException"
					}
					"""
				)
			)
		)
	})
    @GetMapping(value = "/by-orden/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> list(@PathVariable long id) {
		try {
			return new ResponseEntity<>(detalleBusiness.loadByOrdenId(id), HttpStatus.OK);
		} catch (BusinessException e) {
			return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}catch (NotFoundException e) {
			return new ResponseEntity<>(response.build(HttpStatus.NOT_FOUND, e, e.getMessage()), HttpStatus.NOT_FOUND);
		}
	}

	@Operation(
    	operationId = "add-detalle",
    	summary = "Agrega un nuevo detalle.",
    	description = "Permite registrar un nuevo detalle a partir de un cuerpo JSON. Si se crea correctamente, devuelve la cabecera 'location' con la URL del recurso creado."
	)
	@io.swagger.v3.oas.annotations.parameters.RequestBody(
    	required = true,
    	description = "Datos del detalle a crear.",
    	content = @Content(
    	    mediaType = "application/json",
    	    schema = @Schema(implementation = Detalle.class),
    	    examples = {
    	        @ExampleObject(
    	            name = "Ejemplo válido",
    	            summary = "Detalle básico de ejemplo",
    	            description = "Ejemplo de cuerpo válido para crear un nuevo detalle.",
    	            value = """
    	            {
    	                "numero_orden": 1234,
						"masaAcumulada": 20000.0,
    					"densidad": 0.82,
    					"temperatura": 25.5,
    					"caudal": 15.3
    	            }
    	            """
    	        )
    	    }
    	)
	)
	@ApiResponses(value = {
    	@ApiResponse(
        responseCode = "201",
        description = "Detalle creado correctamente. Devuelve la cabecera 'location' con la URL del nuevo recurso."
    	),
    	@ApiResponse(
    	    responseCode = "400",
    	    description = "El detalle enviado es inválido o faltan campos obligatorios.",
    	    content = @Content(
            mediaType = "application/json",
            examples = @ExampleObject(
                name = "Usuario no encontrado",
                value = """
                {
				 "message": "Detalle no valido",
				 "code": 400,
				 "devInfo": "ar.edu.iua.iw3.gastrack.model.business.exception.InvalidDetailException: Detalle no valido"
                }
                """
            ))
    	),
    	@ApiResponse(
    	    responseCode = "403",
    	    description = "El usuario no tiene autorización para agregar detalles. No se habilito la carga en la orden.",
			content = @Content(
            mediaType = "application/json",
            examples = @ExampleObject(
                name = "Usuario no encontrado",
                value = """
                {
					"message": "No se puede agregar detalle a la orden id 7 ya que no esta habilitada para carga",
    				"code": 403,
    				"devInfo": "ar.edu.iua.iw3.gastrack.model.business.exception.OrderNotAuthorizedToLoadException: No se puede agregar detalle a la orden id 7 ya que no esta habilitada para carga"
                }
                """
            ))    	),
    	@ApiResponse(
    	    responseCode = "404",
    	    description = "No se encontró la orden o recurso asociado al detalle.",
			content = @Content(
            mediaType = "application/json",
            examples = @ExampleObject(
                name = "Usuario no encontrado",
                value = """
                {
					"message": "No se encuentra la orden de numero:98776",
    				"code": 404,
					"devInfo": "ar.edu.iua.iw3.gastrack.model.business.exception.NotFoundException: No se encuentra la orden de numero:98776"		
                }
                """
            ))
    	),
    	@ApiResponse(
    	    responseCode = "409",
    	    description = "La orden asociada está en un estado inválido para esta operación.",
			content = @Content(
            mediaType = "application/json",
            examples = @ExampleObject(
                name = "Usuario no encontrado",
                value = """
                {
					"message": "estado de orden ORDEN_CERRADA_PARA_CARGA invalido",
    				"code": 409,
    				"devInfo": "ar.edu.iua.iw3.gastrack.model.business.exception.OrderInvalidStateException: estado de orden ORDEN_CERRADA_PARA_CARGA invalido"
				}
                """
            ))
    	),
    	@ApiResponse(
    	    responseCode = "429",
    	    description = "Se ha excedido la frecuencia permitida para registrar detalles.",
			content = @Content(
            mediaType = "application/json",
            examples = @ExampleObject(
                name = "Usuario no encontrado",
                value = """
                {
					"message": "Detalle recibido fuera de frecuencia de muestreo",
    				"code": 429,
    				"devInfo": "ar.edu.iua.iw3.gastrack.model.business.exception.InvalidDetailFrecuencyException: Detalle recibido fuera de frecuencia de muestreo"}
                """
            ))
    	),
    	@ApiResponse(
    	    responseCode = "500",
    	    description = "Error interno del servidor.",
			content = @Content(
            mediaType = "application/json",
            examples = @ExampleObject(
                name = "Usuario no encontrado",
                value = """
                {
				 	"message": null,
    				"code": 500,
    				"devInfo": "ar.edu.iua.iw3.gastrack.model.business.exception.BusinessException"
				}
                """
            ))
    	)
	})

	@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_CS')")
	@PostMapping(value = "", consumes = MediaType.APPLICATION_JSON_VALUE,    produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> add(HttpEntity<String> httpEntity) {
		try {
			Detalle response = detalleBusiness.add(httpEntity.getBody());
			HttpHeaders responseHeaders = new HttpHeaders();
			responseHeaders.set("location", Constants.URL_DETALLE + "/" + response.getId());
			return new ResponseEntity<>(responseHeaders, HttpStatus.CREATED);
		} catch (BusinessException e) {
			return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (NotFoundException e) {
			return new ResponseEntity<>(response.build(HttpStatus.NOT_FOUND, e, e.getMessage()), HttpStatus.FOUND);
		} catch (InvalidDetailException e) {
			return new ResponseEntity<>(response.build(HttpStatus.BAD_REQUEST, e, e.getMessage()), HttpStatus.BAD_REQUEST);
		}
		catch (InvalidDetailFrecuencyException e) {
			return new ResponseEntity<>(response.build(HttpStatus.TOO_MANY_REQUESTS, e, e.getMessage()), HttpStatus.TOO_MANY_REQUESTS);
		}
		catch (OrderInvalidStateException e) {
			return new ResponseEntity<>(response.build(HttpStatus.CONFLICT, e, e.getMessage()), HttpStatus.CONFLICT);
		}
		catch (OrderNotAuthorizedToLoadException e)
		{
			return new ResponseEntity<>(response.build(HttpStatus.FORBIDDEN, e, e.getMessage()), HttpStatus.FORBIDDEN);
		}
		catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}
}
