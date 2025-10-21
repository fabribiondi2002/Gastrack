package ar.edu.iua.iw3.gastrack.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ar.edu.iua.iw3.gastrack.model.Orden;
import ar.edu.iua.iw3.gastrack.model.business.exception.BusinessException;
import ar.edu.iua.iw3.gastrack.model.business.exception.FoundException;
import ar.edu.iua.iw3.gastrack.model.business.exception.NotFoundException;
import ar.edu.iua.iw3.gastrack.model.business.intefaces.IOrdenBusiness;
import ar.edu.iua.iw3.gastrack.util.IStandardResponseBusiness;

import java.util.Map;

/*
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


	/* Listar ordenes por estado 
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

	/*
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

	/*
	 * Actualizar una orden
	 * @param orden Orden a actualizar
	 * @throws NotFoundException Si no existe una orden con ese id
	 * @throws BusinessException Si ocurre un error no previsto
	 */
    @PutMapping(value = "")
	public ResponseEntity<?> update(@RequestBody Orden orden) {
		try {
			ordenBusiness.update(orden);
			return new ResponseEntity<>(HttpStatus.OK);
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
/*
	 * Agregar una nueva orden
	 * @param orden Orden a agregar
	 * @return Orden agregada
	 * @throws FoundException Si ya existe una orden con ese numero
	 * @throws BusinessException Si ocurre un error no previsto
	 */
	@PostMapping(value = "")
	public ResponseEntity<?> add(@RequestBody Orden orden) {
		try {
			Orden response = ordenBusiness.add(orden);
			HttpHeaders responseHeaders = new HttpHeaders();
			responseHeaders.set("location", Constants.URL_ORDEN + "/" + response.getId());
			return new ResponseEntity<>(responseHeaders, HttpStatus.CREATED);
		} catch (BusinessException e) {
			return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (FoundException e) {
			return new ResponseEntity<>(response.build(HttpStatus.FOUND, e, e.getMessage()), HttpStatus.FOUND);
		}
	}
	@GetMapping(value = "/{codCli1}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> loadByCode(@PathVariable("codCli1") String codCli1) {
		try {
			return new ResponseEntity<>(ordenBusiness.loadByCodigoExterno(codCli1), HttpStatus.OK);
		} catch (BusinessException e) {
			return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (NotFoundException e) {
			return new ResponseEntity<>(response.build(HttpStatus.NOT_FOUND, e, e.getMessage()), HttpStatus.NOT_FOUND);
		}
	}

	/**
	 * Registra el pesaje inicial (tara) de una orden.
	 * 
	 * @param id id de la orden.
	 * @param pesoInicial peso de la tara 
	 * @return la orden persistida con los campos actualizados (peso inicial, fecha, contraseña, estado)
	 * @throws NotFoundException si no existe la orden con el id dado.
	 * @throws BusinessException si la orden no está en el estado esperado o si ocurre un error no previsto
	 */
	@PostMapping(value = "/{id}/registrar-tara", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> registrarTara(@PathVariable("id") long id, @RequestBody Map<String, Object> body) {
		try {
			if (body == null || !body.containsKey("pesoInicial")) {
				return new ResponseEntity<>(response.build(HttpStatus.BAD_REQUEST, null, "Debe enviar 'pesoInicial' en el cuerpo"), HttpStatus.BAD_REQUEST);
			}

			double pesoInicial;
			try {
				pesoInicial = Double.parseDouble(body.get("pesoInicial").toString());
			} catch (Exception e) {
				return new ResponseEntity<>(response.build(HttpStatus.BAD_REQUEST, e, "pesoInicial inválido"), HttpStatus.BAD_REQUEST);
			}

			Orden orden = ordenBusiness.registrarTara(id, pesoInicial);
			return new ResponseEntity<>(orden, HttpStatus.OK);

		} catch (BusinessException e) {
			return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (NotFoundException e) {
			return new ResponseEntity<>(response.build(HttpStatus.NOT_FOUND, e, e.getMessage()), HttpStatus.NOT_FOUND);
		}
	}

}
