package ar.edu.iua.iw3.gastrack.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import ar.edu.iua.iw3.gastrack.model.Detalle;
import ar.edu.iua.iw3.gastrack.model.Orden;
import ar.edu.iua.iw3.gastrack.model.business.exception.BadActivationPasswordException;
import ar.edu.iua.iw3.gastrack.model.business.exception.BusinessException;
import ar.edu.iua.iw3.gastrack.model.business.exception.FoundException;
import ar.edu.iua.iw3.gastrack.model.business.exception.InvalidOrderAttributeException;
import ar.edu.iua.iw3.gastrack.model.business.exception.NotFoundException;
import ar.edu.iua.iw3.gastrack.model.business.exception.OrderAlreadyAuthorizedToLoadException;
import ar.edu.iua.iw3.gastrack.model.business.exception.OrderAlreadyLockedToLoadException;
import ar.edu.iua.iw3.gastrack.model.business.exception.OrderInvalidStateException;
import ar.edu.iua.iw3.gastrack.model.business.intefaces.IOrdenBusiness;
import ar.edu.iua.iw3.gastrack.model.serializers.ConciliacionSerializer;
import ar.edu.iua.iw3.gastrack.model.serializers.ContrasenaActivacionSerializer;
import ar.edu.iua.iw3.gastrack.model.serializers.NumeroOrdenSerializer;
import ar.edu.iua.iw3.gastrack.model.serializers.OrdenListSerializer;
import ar.edu.iua.iw3.gastrack.model.serializers.PresetSerializer;
import ar.edu.iua.iw3.gastrack.model.serializers.DTO.ConciliacionDTO;
import ar.edu.iua.iw3.gastrack.util.IStandardResponseBusiness;
import ar.edu.iua.iw3.gastrack.util.JsonUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;


/**
 * Controlador REST para la gestion de ordenes
 * 
 * @author Leandro Biondi
 * @author Benjamin Vargas
 * @author Antonella Badami
 * @version 1.0
 * @since 2025-10-21
 */

@RestController
@RequestMapping(Constants.URL_ORDEN)
public class OrdenController {

	@Autowired
	private IOrdenBusiness ordenBusiness;

	@Autowired
	private IStandardResponseBusiness response;

	/**
	 * Listar ordenes por estado
	 *
	 * @param status Estado de la orden
	 * @return Lista de ordenes
	 * @throws NotFoundException Si no se encuentran ordenes con el estado
	 *                           especificado
	 * @throws BusinessException Si ocurre un error no previsto
	 */
	@Operation(operationId = "listar-ordenes-por-estado", summary = "Lista las órdenes por estado.", description = "Permite listar las órdenes filtradas por su estado.")
	@Parameter(description = "Estado de la orden. Ejemplos: PENDIENTE, ENTREGADA, CANCELADA", required = true, example = "PENDIENTE")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Órdenes listadas correctamente.", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "Ordenes por estado", summary = "Lista de órdenes filtradas por estado", description = "Ejemplo de respuesta JSON al listar órdenes por estado.", value = """
					[
					    {
					        "id": 1,
					        "numeroOrden": 1234,
					        "estado": "PENDIENTE",
					        "codigoExterno": "ejemploCodigoExterno"
					    },
					    {
					        "id": 2,
					        "numeroOrden": 5678,
					        "estado": "PENDIENTE",
					        "codigoExterno": "otroCodigoExterno"
					    }
					]
					"""))),
			@ApiResponse(responseCode = "404", description = "No se encontraron órdenes con el estado especificado."),
			@ApiResponse(responseCode = "500", description = "Error interno del servidor.")
	})
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
	@GetMapping(value = "/by-status/{status}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> list(

			@PathVariable Orden.Estado status) {
		try {
			return new ResponseEntity<>(ordenBusiness.listByStatus(status), HttpStatus.OK);
		} catch (BusinessException e) {
			return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (NotFoundException e) {
			return new ResponseEntity<>(response.build(HttpStatus.NOT_FOUND, e, e.getMessage()), HttpStatus.NOT_FOUND);
		}
	}

	/**
	 * Obtener una orden por id
	 * 
	 * @param id Id de la orden
	 * @return Orden cargada
	 * @throws NotFoundException Si no existe una orden con ese id
	 * @throws BusinessException Si ocurre un error no previsto
	 */
	@Operation(operationId = "obtener-orden-por-id", summary = "Obtiene una orden por su ID.", description = "Permite obtener una orden específica utilizando su ID único.")
	@Parameter(description = "ID de la orden a obtener. Ejemplo: 1", required = true, example = "1")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Orden obtenida correctamente.", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "Orden por ID", summary = "Orden obtenida por su ID", description = "Ejemplo de respuesta JSON al obtener una orden por su ID.", value = """
					{
					    "id": 1,
					    "numeroOrden": 1234,
					    "estado": "PENDIENTE",
					    "codigoExterno": "ejemploCodigoExterno",
					    // Otros campos relevantes de la orden
					}
					"""))),
			@ApiResponse(responseCode = "404", description = "No se encontró una orden con el ID especificado.", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "Orden no encontrada", value = """
					{
						"message": "No se encontró una orden con ID: 1",
						"code": 404,
						"devInfo": "ar.edu.iua.iw3.gastrack.model.business.exception.NotFoundException: No se encontró una orden con ID: 1"
					}
					"""))),
			@ApiResponse(responseCode = "500", description = "Error interno del servidor.", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "Error interno del servidor", value = """
					{
					 	"message": null,
						"code": 500,
						"devInfo": "ar.edu.iua.iw3.gastrack.model.business.exception.BusinessException"
					}
					""")))
	})
	@GetMapping(value = "/{id}")
	public ResponseEntity<?> load(@PathVariable long id) {
		try {
			return new ResponseEntity<>(ordenBusiness.load(id), HttpStatus.OK);
		} catch (BusinessException e) {
			return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (NotFoundException e) {
			return new ResponseEntity<>(response.build(HttpStatus.NOT_FOUND, e, e.getMessage()), HttpStatus.NOT_FOUND);
		}
	}

	@GetMapping(value = "")
	public ResponseEntity<?> list() {
		try
		{
			StdSerializer<Orden> ser =  new OrdenListSerializer(Orden.class,false);
			String result = JsonUtils.getObjectMapper(Orden.class, ser, null)
			.writeValueAsString(ordenBusiness.list());
			Object jsonResult = new ObjectMapper().readValue(result, Object.class);
			return new ResponseEntity<>(jsonResult, HttpStatus.OK);
		} catch (BusinessException | JsonProcessingException e) {

			return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);

		}
	}
	/**
	 * Obtener una orden por codigo externo
	 * 
	 * @param codigoExterno Codigo externo de la orden
	 * @return Orden cargada
	 * @throws NotFoundException Si no existe una orden con ese codigo externo
	 * @throws BusinessException Si ocurre un error no previsto
	 */
	@Operation(operationId = "obtener-orden-por-codigo-externo", summary = "Obtiene una orden por su código externo.", description = "Permite obtener una orden específica utilizando su código externo único.")
	@Parameter(description = "Código externo de la orden a obtener. Ejemplo: ejemploCodigoExterno", required = true, example = "ejemploCodigoExterno")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Orden obtenida correctamente.", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "Orden por código externo", summary = "Orden obtenida por su código externo", description = "Ejemplo de respuesta JSON al obtener una orden por su código externo.", value = """
					{
					    "id": 1,
					    "numeroOrden": 1234,
					    "estado": "PENDIENTE",
					    "codigoExterno": "ejemploCodigoExterno",
					    // Otros campos relevantes de la orden
					}
					"""))),
			@ApiResponse(responseCode = "404", description = "No se encontró una orden con el código externo especificado.", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "Orden no encontrada", value = """
					{
						"message": "No se encontró una orden con código externo: ejemploCodigoExterno",
						"code": 404,
						"devInfo": "ar.edu.iua.iw3.gastrack.model.business.exception.NotFoundException: No se encontró una orden con código externo: ejemploCodigoExterno"
					}
					"""))),
			@ApiResponse(responseCode = "500", description = "Error interno del servidor.", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "Error interno del servidor", value = """
					{
					 	"message": null,
						"code": 500,
						"devInfo": "ar.edu.iua.iw3.gastrack.model.business.exception.BusinessException"
					}
					""")))
	})
	@GetMapping(value = "/codigoExterno/{codigoExterno}")
	public ResponseEntity<?> loadByCodigoExterno(@PathVariable String codigoExterno) {
		try {
			return new ResponseEntity<>(ordenBusiness.loadByCodigoExterno(codigoExterno), HttpStatus.OK);
		} catch (BusinessException e) {
			return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (NotFoundException e) {
			return new ResponseEntity<>(response.build(HttpStatus.NOT_FOUND, e, e.getMessage()), HttpStatus.NOT_FOUND);
		}
	}

	/**
	 * Obtener una orden por numero de orden
	 * 
	 * @param numeroOrden Numero de la orden
	 * @return Orden cargada
	 * @throws NotFoundException Si no existe una orden con ese numero de orden
	 * @throws BusinessException Si ocurre un error no previsto
	 */
	@Operation(operationId = "obtener-orden-por-numero-de-orden", summary = "Obtiene una orden por su número de orden.", description = "Permite obtener una orden específica utilizando su número de orden único.")
	@Parameter(description = "Número de orden de la orden a obtener. Ejemplo: 1234", required = true, example = "1234")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Orden obtenida correctamente.", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "Orden por número de orden", summary = "Orden obtenida por su número de orden", description = "Ejemplo de respuesta JSON al obtener una orden por su número de orden.", value = """
					{
					    "id": 1,
					    "numeroOrden": 1234,
					    "estado": "PENDIENTE",
					    "codigoExterno": "ejemploCodigoExterno",
					    // Otros campos relevantes de la orden
					}
					"""))),
			@ApiResponse(responseCode = "404", description = "No se encontró una orden con el número de orden especificado.", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "Orden no encontrada", value = """
					{
						"message": "No se encontró una orden con número de orden: 1234",
						"code": 404,
						"devInfo": "ar.edu.iua.iw3.gastrack.model.business.exception.NotFoundException: No se encontró una orden con número de orden: 1234"
					}
					"""))),
			@ApiResponse(responseCode = "500", description = "Error interno del servidor.", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "Error interno del servidor", value = """
					{
					 	"message": null,
						"code": 500,
						"devInfo": "ar.edu.iua.iw3.gastrack.model.business.exception.BusinessException"
					}
					""")))
	})
	@GetMapping(value = "/numeroOrden/{numeroOrden}")
	public ResponseEntity<?> loadByNumeroOrden(@PathVariable long numeroOrden) {
		try {
			return new ResponseEntity<>(ordenBusiness.loadByNumeroOrden(numeroOrden), HttpStatus.OK);
		} catch (BusinessException e) {
			return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (NotFoundException e) {
			return new ResponseEntity<>(response.build(HttpStatus.NOT_FOUND, e, e.getMessage()), HttpStatus.NOT_FOUND);
		}
	}

	/**
	 * Eliminar una orden por id
	 * 
	 * @param id Id de la orden
	 * @return Respuesta HTTP con el estado de la operacion
	 */
	@Operation(operationId = "eliminar-orden-por-id", summary = "Elimina una orden por su ID.", description = "Permite eliminar una orden específica utilizando su ID único.")
	@Parameter(description = "ID de la orden a eliminar. Ejemplo: 1", required = true, example = "1")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Orden eliminada correctamente.", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "Orden eliminada", summary = "Orden eliminada por su ID", description = "Ejemplo de respuesta JSON al eliminar una orden por su ID.", value = """
					{
					    "message": "Orden eliminada correctamente",
					    "code": 200
					}
					"""))),
			@ApiResponse(responseCode = "404", description = "No se encontró una orden con el ID especificado.", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "Orden no encontrada", value = """
					{
						"message": "No se encontró una orden con ID: 1",
						"code": 404,
						"devInfo": "ar.edu.iua.iw3.gastrack.model.business.exception.NotFoundException: No se encontró una orden con ID: 1"
					}
					"""))),
			@ApiResponse(responseCode = "500", description = "Error interno del servidor.", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "Error interno del servidor", value = """
					{
					 	"message": null,
						"code": 500,
						"devInfo": "ar.edu.iua.iw3.gastrack.model.business.exception.BusinessException"
					}
					""")))
	})
	@DeleteMapping(value = "/{id}")
	public ResponseEntity<?> delete(@PathVariable long id) {
		try {
			ordenBusiness.delete(id);
			return new ResponseEntity<String>(HttpStatus.OK);
		} catch (BusinessException e) {
			return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (NotFoundException e) {
			return new ResponseEntity<>(response.build(HttpStatus.NOT_FOUND, e, e.getMessage()), HttpStatus.NOT_FOUND);
		}
	}

	/**
	 * 
	 * Registrar una orden completa a partir de un JSON
	 * Tiene en cuenta todos los objetos relacionados (chofer, camion, cliente,
	 * producto)
	 * 
	 * @param httpEntity Entidad HTTP que contiene el JSON de la orden
	 * @return Respuesta HTTP con el estado de la operacion
	 */
	@Operation(operationId = "registrar-orden", summary = "Registra una orden completa.", description = "Permite registrar una orden completa a partir de un cuerpo JSON que incluye todos los objetos relacionados (chofer, camion, cliente, producto).")
	@io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, description = "Datos de la orden a registrar junto con el cliente, chofer, camion, cisterna y producto.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Detalle.class), examples = {
			@ExampleObject(name = "Ejemplo válido", summary = "Orden completa", description = "Ejemplo de cuerpo válido para registrar una orden completa", value = """
					    	            {
					  "numero_orden": 12345,
					"fecha_carga_prevista": "2025-10-28T08:30:00.000-03:00",
					  "preset": 15000,
					  "codigo_externo": "ejemploCodigoExterno",

					  "chofer": {
					    "nombre": "Juan",
					    "apellido": "Pérez",
					    "documento": 30123456
					  },

					  "camion": {
					    "patente": "AB123CD",
					    "camion_descripcion": "Camión Scania cisterna 30.000L",
					    "cisternas": [
					      {
					        "numero_cisterna": 1,
					        "volumen_cisterna": 30000
					      },
					      {
					        "numero_cisterna": 2,
					        "volumen_cisterna": 28000
					      }
					    ]
					  },

					  "producto": {
					    "nombre_producto": "Gas Oil Grado 3",
					    "descripcion_producto": "Combustible diésel premium de alta calidad"
					  },

					  "cliente": {
					    "razon_social": "Transporte Biondi SRL",
					    "mail": "contacto@biondi.com"
					  }
					}

					    	            """)
	}))
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "Orden registrada correctamente.", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "Orden registrada", value = """
					{
					 "numeroOrden": 1234
					}
					"""))),
			@ApiResponse(responseCode = "400", description = "El JSON enviado es inválido o faltan campos obligatorios.", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "JSON invalido", value = """
					{
					 "message": "El JSON de la orden es invalido",
					 "code": 400,
					 "devInfo": "ar.edu.iua.iw3.gastrack.model.business.exception.InvalidOrderAttributeException: El JSON de la orden es invalido"
					}
					"""))),
			@ApiResponse(responseCode = "302", description = "Ya existe una orden con el mismo número de orden.", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "Orden duplicada", value = """
					{
						"message": "La orden con numero 12345 ya existe",
						"code": 302,
						"devInfo": "ar.edu.iua.iw3.gastrack.model.business.exception.FoundException: La orden con numero 12345 ya existe"
					}
					"""))),
			@ApiResponse(responseCode = "500", description = "Error interno del servidor.", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "Error interno del servidor", value = """
					{
					 	"message": null,
						"code": 500,
						"devInfo": "ar.edu.iua.iw3.gastrack.model.business.exception.BusinessException"
					}
					""")))
	})
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_SAP')")
	@PostMapping(value = "")
	public ResponseEntity<?> add(HttpEntity<String> httpEntity) {
		try {
			Orden orden = ordenBusiness.addOrdenCompleta(httpEntity.getBody());
			HttpHeaders responseHeaders = new HttpHeaders();
			NumeroOrdenSerializer serializer = new NumeroOrdenSerializer(Orden.class, false);
			String result = JsonUtils.getObjectMapper(Orden.class, serializer, null)
					.writeValueAsString(orden);

			responseHeaders.set("location", Constants.URL_ORDEN + "/" + orden.getId());
			responseHeaders.setContentType(MediaType.APPLICATION_JSON);
			return new ResponseEntity<>(result, responseHeaders, HttpStatus.CREATED);

		} catch (BusinessException | JsonProcessingException e) {
			return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (FoundException e) {
			return new ResponseEntity<>(response.build(HttpStatus.FOUND, e, e.getMessage()), HttpStatus.FOUND);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());

		}
	}

	/**
	 * Habilitar una orden para carga
	 * 
	 * @param httpEntity Entidad HTTP que contiene el JSON con el numero de orden y
	 *                   la contrasena de activacion
	 * @return Respuesta HTTP con el estado de la operacion
	 */

	@Operation(operationId = "habilitar-carga", summary = "Habilita la carga de una orden.", description = "Permite habilitar la carga de una orden a partir de un cuerpo JSON. Si se registra correctamente, devuelve la contraseña de activacion.")
	@io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, description = "Datos de la carga a habilitar.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Detalle.class), examples = {
			@ExampleObject(name = "Ejemplo válido", summary = "Detalle básico de ejemplo", description = "Ejemplo de cuerpo válido para habilitar carga.", value = """
					        {
					            "numero_orden": 123485,
					"contrasenaActivacion": "67689"
					        }
					        """)
	}))
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "Carga habilitada correctamente. Devuelve el preset en formato JSON.", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "Carga habilitada", value = """
					{
					 "message": "preset:15000.0",
					 "code": 201
					}
					"""))),
			@ApiResponse(responseCode = "400", description = "La tara enviada es inválida o faltan campos obligatorios.", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "Tara invalida", value = """
					           {
					"message": "valor de peso inicial invalido",
					"code": 400,
					"devInfo": "ar.edu.iua.iw3.gastrack.model.business.exception.InvalidOrderAttributeException: valor de peso inicial invalido"
					           }
					           """))),
			@ApiResponse(responseCode = "401", description = "La contraseña de activacion enviada es inválida.", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "Contraseña de activacion invalida", value = """
					           {
					"message": "La contrasena de activacion no tiene formato valido",
					"code": 401,
					"devInfo": "ar.edu.iua.iw3.gastrack.model.business.exception.BadActivationPasswordException: La contrasena de activacion no tiene formato valido"
					           }
					           """))),
			@ApiResponse(responseCode = "404", description = "No se encontró la orden", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "Orden no encontrada", value = """
					           {
					"message": "No se encuentra la orden de numero:123475",
								"code": 404,
					"devInfo": "ar.edu.iua.iw3.gastrack.model.business.exception.NotFoundException: No se encuentra la orden de numero:123475"
					           }
					           """))),
			@ApiResponse(responseCode = "409", description = "La orden asociada está en un estado inválido para esta operación.", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "Estado invalido", value = """
					            {
						"message": "La orden numero 123485 ya se encuentra autorizada para carga",
									"code": 409,
									"devInfo": "ar.edu.iua.iw3.gastrack.model.business.exception.OrderAlreadyAuthorizedToLoadException: La orden numero 123485 ya se encuentra autorizada para carga"
					}
					            """))),
			@ApiResponse(responseCode = "500", description = "Error interno del servidor.", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "Error interno del servidor", value = """
					            {
					 	"message": null,
									"code": 500,
									"devInfo": "ar.edu.iua.iw3.gastrack.model.business.exception.BusinessException"
					}
					            """)))
	})
	
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_CS')")
	@PostMapping(value = "/carga/habilitar")
	public ResponseEntity<?> habilitarCarga(HttpEntity<String> httpEntity) {
		try {
			Orden orden = ordenBusiness.habilitarOrdenParaCarga(httpEntity.getBody());

			StdSerializer<Orden> serializer = new PresetSerializer(Orden.class, false);
			String result = JsonUtils.getObjectMapper(Orden.class, serializer, null)
					.writeValueAsString(orden);
			Object jsonResult = new ObjectMapper().readValue(result, Object.class);

			HttpHeaders responseHeaders = new HttpHeaders();
			responseHeaders.set("location", Constants.URL_ORDEN + "/" + orden.getId());

			return new ResponseEntity<>(jsonResult, responseHeaders, HttpStatus.CREATED);
		} catch (BusinessException | JsonProcessingException e) {
			return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (NotFoundException e) {
			return new ResponseEntity<>(response.build(HttpStatus.NOT_FOUND, e, e.getMessage()), HttpStatus.NOT_FOUND);
		} catch (BadActivationPasswordException e) {
			return new ResponseEntity<>(response.build(HttpStatus.UNAUTHORIZED, e, e.getMessage()),
					HttpStatus.UNAUTHORIZED);
		} catch (OrderInvalidStateException | OrderAlreadyAuthorizedToLoadException e) {
			return new ResponseEntity<>(response.build(HttpStatus.CONFLICT, e, e.getMessage()), HttpStatus.CONFLICT);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());

		}
	}

	/**
	 * Registra la tara de una orden.
	 *
	 * @param httpEntity Contiene el JSON con los datos necesarios para el registro.
	 * @return Contraseña de activación si tiene éxito, o un error correspondiente.
	 * @throws InvalidOrderAttributeException Cuando faltan o son inválidos los
	 *                                        atributos de la orden.
	 * @throws NotFoundException              Cuando la orden no se encuentra.
	 * @throws OrderInvalidStateException     Cuando la orden está en un estado no
	 *                                        admitido.
	 * @throws BusinessException              Por errores internos de negocio.
	 */

	@Operation(operationId = "registrar-tara", summary = "Regitra la tara de una orden.", description = "Permite registrar la tara de uns orden a partir de un cuerpo JSON. Si se registra correctamente, devuelve la contraseña de activacion.")
	@io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, description = "Datos de la tara a registrar.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Detalle.class), examples = {
			@ExampleObject(name = "Ejemplo válido", summary = "Detalle básico de ejemplo", description = "Ejemplo de cuerpo válido para registrar una tara.", value = """
					        {
					            "numeroOrden": 1234,
					"pesoInicial": 15000.0
					        }
					        """)
	}))
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "Tara registrada correctamente. Devuelve la contraseña de activacion en formato JSON.", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "Tara registrada", value = """
					{
					 "message": "contrasenaActivacion: 18685",
					 "code": 201
					}
					"""))),
			@ApiResponse(responseCode = "400", description = "La tara enviada es inválida o faltan campos obligatorios.", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "Tara invalida", value = """
					           {
					"message": "valor de peso inicial invalido",
					"code": 400,
					"devInfo": "ar.edu.iua.iw3.gastrack.model.business.exception.InvalidOrderAttributeException: valor de peso inicial invalido"
					           }
					           """))),
			@ApiResponse(responseCode = "404", description = "No se encontró la orden.", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "Orden no encontrada", value = """
					           {
					"message": "No se encuentra la orden de numero:123475",
								"code": 404,
					"devInfo": "ar.edu.iua.iw3.gastrack.model.business.exception.NotFoundException: No se encuentra la orden de numero:123475"
					           }
					           """))),
			@ApiResponse(responseCode = "409", description = "La orden asociada está en un estado inválido para esta operación.", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "Estado invalido", value = """
					            {
						"message": "estado de orden PESAJE_INICIAL_REGISTRADO invalido",
									"code": 409,
									"devInfo": "ar.edu.iua.iw3.gastrack.model.business.exception.OrderInvalidStateException: estado de orden PESAJE_INICIAL_REGISTRADO invalido"
					}
					            """))),
			@ApiResponse(responseCode = "500", description = "Error interno del servidor.", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "Error interno del servidor", value = """
					            {
					 	"message": null,
									"code": 500,
									"devInfo": "ar.edu.iua.iw3.gastrack.model.business.exception.BusinessException"
					}
					            """)))
	})
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_TMS')")
	@PostMapping(value = "/tara")
	public ResponseEntity<?> registrarTara(HttpEntity<String> httpEntity) {
		try {

			Orden orden = ordenBusiness.registrarTara(httpEntity.getBody());

			StdSerializer<Orden> serializer = new ContrasenaActivacionSerializer(Orden.class, false);
			String result = JsonUtils.getObjectMapper(Orden.class, serializer, null)
					.writeValueAsString(orden);

			Object jsonResult = new ObjectMapper().readValue(result, Object.class);

			return ResponseEntity.status(HttpStatus.CREATED).body(jsonResult);

		} catch (BusinessException | JsonProcessingException e) {
			return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (InvalidOrderAttributeException e) {
			return new ResponseEntity<>(response.build(HttpStatus.BAD_REQUEST, e, e.getMessage()),
					HttpStatus.BAD_REQUEST);
		} catch (NotFoundException e) {
			return new ResponseEntity<>(response.build(HttpStatus.NOT_FOUND, e, e.getMessage()), HttpStatus.NOT_FOUND);
		} catch (OrderInvalidStateException e) {
			return new ResponseEntity<>(response.build(HttpStatus.CONFLICT, e, e.getMessage()), HttpStatus.CONFLICT);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	/**
	 * Deshabilitar una orden para carga
	 * 
	 * @param httpEntity Entidad HTTP que contiene el JSON con el numero de orden
	 * @return Respuesta HTTP con el estado de la operacion
	 */
	@Operation(operationId = "deshabilitar-orden-para-carga", summary = "Deshabilita una orden para carga.", description = "Permite deshabilitar una orden para carga a partir de un cuerpo JSON.")
	@io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, description = "Datos de la orden a deshabilitar.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Detalle.class), examples = {
			@ExampleObject(name = "Ejemplo válido", summary = "Número de orden a deshabilitar", description = "Ejemplo de cuerpo válido para deshabilitar una orden para carga.", value = """
					{
					    "numeroOrden": 1234
					}
					""")
	}))
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "Orden deshabilitada correctamente.", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "Orden deshabilitada", value = """
					{
					 "code": 201
					}
					"""))),
			@ApiResponse(responseCode = "404", description = "No se encontró la orden.", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "Orden no encontrada", value = """
					           {
					"message": "No se encuentra la orden de numero:123475",
								"code": 404,
					"devInfo": "ar.edu.iua.iw3.gastrack.model.business.exception.NotFoundException: No se encuentra la orden de numero:123475"
					           }
					           """))),
			@ApiResponse(responseCode = "409", description = "La orden asociada está en un estado inválido para esta operación.", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "Estado invalido", value = """
					            {
						"message": "estado de orden PENDIENTE invalido",
									"code": 409,
									"devInfo": "ar.edu.iua.iw3.gastrack.model.business.exception.OrderInvalidStateException: estado de orden PENDIENTE invalido"
					}
					            """))),
			@ApiResponse(responseCode = "500", description = "Error interno del servidor.", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "Error interno del servidor", value = """
					            {
					 	"message": null,
									"code": 500,
									"devInfo": "ar.edu.iua.iw3.gastrack.model.business.exception.BusinessException"
					}
					            """)))
	})
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_CS')")
	@PostMapping("/carga/deshabilitar")
	public ResponseEntity<?> deshabilitarOrdenParaCarga(HttpEntity<String> httpEntity) {
		try {
			Orden orden = ordenBusiness.deshabilitarOrdenParaCarga(httpEntity.getBody());
			HttpHeaders responseHeaders = new HttpHeaders();
			responseHeaders.set("location", Constants.URL_ORDEN + "/" + orden.getId());
			return new ResponseEntity<>(responseHeaders, HttpStatus.CREATED);
		} catch (BusinessException e) {
			return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (NotFoundException e) {
			return new ResponseEntity<>(response.build(HttpStatus.NOT_FOUND, e, e.getMessage()), HttpStatus.NOT_FOUND);
		} catch (OrderInvalidStateException | OrderAlreadyLockedToLoadException e) {
			return new ResponseEntity<>(response.build(HttpStatus.CONFLICT, e, e.getMessage()), HttpStatus.CONFLICT);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());

		}
	}

	/**
	 * Registrar el cierre de una orden
	 * 
	 * @param httpEntity Entidad HTTP que contiene el JSON con el numero de orden y
	 *                   el peso final
	 * @return Respuesta HTTP con el estado de la operacion
	 */
	@Operation(operationId = "registrar-cierre", summary = "Registra el cierre de una orden.", description = "Permite registrar el cierre de una orden a partir de un cuerpo JSON.")
	@io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, description = "Datos del cierre a registrar.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Detalle.class), examples = {
			@ExampleObject(name = "Ejemplo válido", summary = "Número de orden a cerrar y peso final para cerrar una orden", description = "Ejemplo de cuerpo válido para registrar el cierre de una orden.", value = """
					        {
					            "numeroOrden": 1234,
					"pesoFinal": 28000.0
					        }
					        """)
	}))
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "Cierre registrado correctamente.", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "Cierre registrado", value = """
					{
					 "code": 201
					}
					"""))),
			@ApiResponse(responseCode = "400", description = "El JSON enviado es inválido o faltan campos obligatorios.", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "JSON invalido", value = """
					{
					 "message": "El JSON de la orden es invalido",
					 "code": 400,
					 "devInfo": "ar.edu.iua.iw3.gastrack.model.business.exception.InvalidOrderAttributeException: El JSON de la orden es invalido"
					}
					"""))),
			@ApiResponse(responseCode = "404", description = "No se encontró la orden.", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "Orden no encontrada", value = """
					           {
					"message": "No se encuentra la orden de numero:123475",
								"code": 404,
					"devInfo": "ar.edu.iua.iw3.gastrack.model.business.exception.NotFoundException: No se encuentra la orden de numero:123475"
					           }
					           """))),
			@ApiResponse(responseCode = "409", description = "La orden asociada está en un estado inválido para esta operación.", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "Estado invalido", value = """
					            {
						"message": "estado de orden CERRADA invalido",
									"code": 409,
									"devInfo": "ar.edu.iua.iw3.gastrack.model.business.exception.OrderInvalidStateException: estado de orden CERRADA invalido"
					}
					            """))),
			@ApiResponse(responseCode = "500", description = "Error interno del servidor.", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "Error interno del servidor", value = """
					            {
					 	"message": null,
									"code": 500,
									"devInfo": "ar.edu.iua.iw3.gastrack.model.business.exception.BusinessException"
					}
					            """)))
	})
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_TMS')")
	@PostMapping(value = "/registrar-cierre")
	public ResponseEntity<?> registrarCierreOrden(HttpEntity<String> httpEntity) {
		try {
			Orden orden = ordenBusiness.registrarCierreOrden(httpEntity.getBody());
			HttpHeaders responseHeaders = new HttpHeaders();
			responseHeaders.set("location", Constants.URL_ORDEN + "/" + orden.getId());
			return new ResponseEntity<>(responseHeaders, HttpStatus.CREATED);
		} catch (BusinessException e) {
			return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (NotFoundException e) {
			return new ResponseEntity<>(response.build(HttpStatus.NOT_FOUND, e, e.getMessage()), HttpStatus.NOT_FOUND);
		} catch (OrderInvalidStateException e) {
			return new ResponseEntity<>(response.build(HttpStatus.CONFLICT, e, e.getMessage()),
					HttpStatus.CONFLICT);
		} catch (InvalidOrderAttributeException e) {
			return new ResponseEntity<>(response.build(HttpStatus.BAD_REQUEST, e, e.getMessage()),
					HttpStatus.BAD_REQUEST);

		}
	}

	/*
	 * Obtener la conciliacion de una orden
	 * 
	 * @param numeroOrden Numero de la orden
	 * 
	 * @return ConciliacionDTO serializado en JSON
	 */
	@Operation(operationId = "obtener-conciliacion", summary = "Obtiene la conciliación de una orden.", description = "Permite obtener la conciliación de una orden a partir de su número de orden.")
	@Parameter(description = "Número de orden de la orden cuya conciliación se desea obtener. Ejemplo: 12345", required = true, example = "12345")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Conciliación obtenida correctamente.", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "Conciliación obtenida", value = """
										{
											"Pesaje inicial": 10000.0,
											"Pesaje final": 32000.0,
											"Producto cargado": 20000.0,
											"Neto por balanza": 22000.0,
											"Diferencia entre balanza y caudalímetro": 2000.0,
											"Promedio caudal": 15.3,
											"Promedio temperatura": 25.5,
											"Promedio densidad": 0.82
					}
										}
										"""))),
			@ApiResponse(responseCode = "400", description = "El número de orden proporcionado no es válido.", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "Número de orden inválido", value = """
					{
					 "message": "El numero de orden debe ser un numero valido",
					 "code": 400,
					 "devInfo": "java.lang.NumberFormatException: For input string: \"abc\""
					}
					"""))),
			@ApiResponse(responseCode = "404", description = "No se encontró la orden.", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "Orden no encontrada", value = """
					           {
					"message": "No se encuentra la orden de numero:123475",
								"code": 404,
					"devInfo": "ar.edu.iua.iw3.gastrack.model.business.exception.NotFoundException: No se encuentra la orden de numero:123475"
					           }
					           """))),
			@ApiResponse(responseCode = "409", description = "La orden está en un estado inválido para obtener la conciliación.", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "Estado inválido", value = """
					            {
						"message": "La orden numero 12345 no se encuentra cerrada",
									"code": 409,
									"devInfo": "ar.edu.iua.iw3.gastrack.model.business.exception.OrderInvalidStateException: La orden numero 12345 no se encuentra cerrada"
					}
					            """))),
			@ApiResponse(responseCode = "500", description = "Error interno del servidor.", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "Error interno del servidor", value = """
					            {
					 	"message": null,
									"code": 500,
									"devInfo": "ar.edu.iua.iw3.gastrack.model.business.exception.BusinessException"
					}
					            """)))
	})
	@GetMapping("/conciliacion/{numeroOrden}")
	public ResponseEntity<?> getConciliacion(@PathVariable String numeroOrden) {
		try {
			ConciliacionDTO conciliacion = ordenBusiness.crearConciliacion(Long.parseLong(numeroOrden));
			StdSerializer<ConciliacionDTO> serializer;
			serializer = new ConciliacionSerializer(ConciliacionDTO.class, false);
			String result = JsonUtils.getObjectMapper(ConciliacionDTO.class, serializer, null)
					.writeValueAsString(conciliacion);
			Object jsonResult = new ObjectMapper().readValue(result, Object.class);

			return new ResponseEntity<>(jsonResult, HttpStatus.OK);
		} catch (BusinessException | JsonProcessingException e) {
			return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (NotFoundException e) {
			return new ResponseEntity<>(response.build(HttpStatus.NOT_FOUND, e, e.getMessage()), HttpStatus.NOT_FOUND);
		} catch (NumberFormatException e) {
			return new ResponseEntity<>(
					response.build(HttpStatus.BAD_REQUEST, e, "El numero de orden debe ser un numero valido"),
					HttpStatus.BAD_REQUEST);
		} catch (OrderInvalidStateException e) {
			return new ResponseEntity<>(response.build(HttpStatus.CONFLICT, e, e.getMessage()), HttpStatus.CONFLICT);
		}
	}
}
