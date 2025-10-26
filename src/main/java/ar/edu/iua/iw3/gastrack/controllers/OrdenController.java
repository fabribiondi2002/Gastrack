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

import ar.edu.iua.iw3.gastrack.model.Orden;
import ar.edu.iua.iw3.gastrack.model.business.exception.BusinessException;
import ar.edu.iua.iw3.gastrack.model.business.exception.FoundException;
import ar.edu.iua.iw3.gastrack.model.business.exception.NotFoundException;
import ar.edu.iua.iw3.gastrack.model.business.intefaces.IOrdenBusiness;
import ar.edu.iua.iw3.gastrack.util.IStandardResponseBusiness;

/**
 * Controlador REST para la gestion de ordenes
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
	 * @param status Estado de la orden
	 * @return Lista de ordenes
	 * @throws NotFoundException Si no se encuentran ordenes con el estado especificado
	 * @throws BusinessException Si ocurre un error no previsto
	*/
    @GetMapping(value = "/by-status/{status}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> list(@PathVariable Orden.Estado status) {
		try {
			return new ResponseEntity<>(ordenBusiness.listByStatus(status), HttpStatus.OK);
		} catch (BusinessException e) {
			return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}catch (NotFoundException e) {
			return new ResponseEntity<>(response.build(HttpStatus.NOT_FOUND, e, e.getMessage()), HttpStatus.NOT_FOUND);
		}
	}

	/**
	 * Obtener una orden por id
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
<<<<<<< HEAD

	/**
	 * Actualizar una orden
	 * @param orden Orden a actualizar
	 * @throws NotFoundException Si no existe una orden con ese id
	 * @throws BusinessException Si ocurre un error no previsto
	 */
    @PutMapping(value = "")
	public ResponseEntity<?> update(@RequestBody Orden orden) {
=======
	@GetMapping(value = "/codigoExterno/{codigoExterno}")
	public ResponseEntity<?> loadByCodigoExterno(@PathVariable String codigoExterno) {
>>>>>>> feature/crearorden
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
<<<<<<< HEAD
	 * Agregar una nueva orden
	 * @param orden Orden a agregar
	 * @return Orden agregada
	 * @throws FoundException Si ya existe una orden con ese numero
	 * @throws BusinessException Si ocurre un error no previsto
=======
	 * Registrar una orden completa a partir de un JSON
	 * Tiene en cuenta todos los objetos relacionados (chofer, camion, cliente, producto)
	 * @param httpEntity Entidad HTTP que contiene el JSON de la orden
	 * @return Respuesta HTTP con el estado de la operacion
>>>>>>> feature/crearorden
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
	

}
