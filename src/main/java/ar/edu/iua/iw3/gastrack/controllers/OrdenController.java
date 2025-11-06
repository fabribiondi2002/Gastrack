package ar.edu.iua.iw3.gastrack.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

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
import ar.edu.iua.iw3.gastrack.model.serializers.PresetSerializer;
import ar.edu.iua.iw3.gastrack.model.serializers.DTO.ConciliacionDTO;
import ar.edu.iua.iw3.gastrack.util.IStandardResponseBusiness;
import ar.edu.iua.iw3.gastrack.util.JsonUtils;

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
	@GetMapping(value = "/by-status/{status}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> list(@PathVariable Orden.Estado status) {
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

	/**
	 * Obtener una orden por codigo externo
	 * 
	 * @param codigoExterno Codigo externo de la orden
	 * @return Orden cargada
	 * @throws NotFoundException Si no existe una orden con ese codigo externo
	 * @throws BusinessException Si ocurre un error no previsto
	 */
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
	@PostMapping(value = "")
	public ResponseEntity<?> add(HttpEntity<String> httpEntity) {
		try {
			Orden orden = ordenBusiness.addOrdenCompleta(httpEntity.getBody());
			HttpHeaders responseHeaders = new HttpHeaders();
			responseHeaders.set("location", Constants.URL_ORDEN + "/" + orden.getId());
			return new ResponseEntity<>(responseHeaders, HttpStatus.CREATED);
		} catch (BusinessException e) {
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
	@PostMapping(value = "/carga/habilitar")
	public ResponseEntity<?> habilitarCarga(HttpEntity<String> httpEntity) {
		try {
			Orden orden = ordenBusiness.habilitarOrdenParaCarga(httpEntity.getBody());

			StdSerializer<Orden> serializer = new PresetSerializer(Orden.class, false);
			String result = JsonUtils.getObjectMapper(Orden.class, serializer, null)
					.writeValueAsString(orden);
					
			HttpHeaders responseHeaders = new HttpHeaders();
			responseHeaders.set("location", Constants.URL_ORDEN + "/" + orden.getId());
			
			return new ResponseEntity<>(result,responseHeaders, HttpStatus.CREATED);
		} catch (BusinessException e) {
			return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (NotFoundException e) {
			return new ResponseEntity<>(response.build(HttpStatus.NOT_FOUND, e, e.getMessage()), HttpStatus.NOT_FOUND);
		} catch (BadActivationPasswordException e) {
			return new ResponseEntity<>(response.build(HttpStatus.UNAUTHORIZED, e, e.getMessage()),
					HttpStatus.UNAUTHORIZED);
		} catch (OrderInvalidStateException | OrderAlreadyAuthorizedToLoadException e) {
			return new ResponseEntity<>(response.build(HttpStatus.CONFLICT, e, e.getMessage()), HttpStatus.CONFLICT);
		} catch (JsonProcessingException e) {
			return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
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
	@PostMapping(value = "/tara")
	public ResponseEntity<?> registrarTara(HttpEntity<String> httpEntity) {
		try {

			String contrasenaActivacion = ordenBusiness.registrarTara(httpEntity.getBody());
			return new ResponseEntity<>(contrasenaActivacion, HttpStatus.OK);

		} catch (BusinessException e) {
			return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (InvalidOrderAttributeException e) {
			return new ResponseEntity<>(response.build(HttpStatus.BAD_REQUEST, e, e.getMessage()),
					HttpStatus.BAD_REQUEST);
		} catch (NotFoundException e) {
			return new ResponseEntity<>(response.build(HttpStatus.NOT_FOUND, e, e.getMessage()), HttpStatus.NOT_FOUND);
		} catch (OrderInvalidStateException e) {
			return new ResponseEntity<>(response.build(HttpStatus.CONFLICT, e, e.getMessage()), HttpStatus.CONFLICT);
		}
	}

	/**
	 * Deshabilitar una orden para carga
	 * @param httpEntity Entidad HTTP que contiene el JSON con el numero de orden
	 * @return Respuesta HTTP con el estado de la operacion
	 */
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
		}
		catch (IllegalArgumentException e) {
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
	@GetMapping("/conciliacion/{numeroOrden}")
	public ResponseEntity<?> getConciliacion(@PathVariable String numeroOrden) {
		try {
			ConciliacionDTO conciliacion = ordenBusiness.crearConciliacion(Long.parseLong(numeroOrden));
			StdSerializer<ConciliacionDTO> serializer;
			serializer = new ConciliacionSerializer(ConciliacionDTO.class, false);
			String result = JsonUtils.getObjectMapper(ConciliacionDTO.class, serializer, null)
					.writeValueAsString(conciliacion);

			return new ResponseEntity<>(result, HttpStatus.OK);
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
