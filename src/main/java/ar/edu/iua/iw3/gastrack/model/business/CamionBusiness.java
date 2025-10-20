package ar.edu.iua.iw3.gastrack.model.business;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.edu.iua.iw3.gastrack.model.Camion;
import ar.edu.iua.iw3.gastrack.model.business.exception.BusinessException;
import ar.edu.iua.iw3.gastrack.model.business.exception.FoundException;
import ar.edu.iua.iw3.gastrack.model.business.exception.NotFoundException;
import ar.edu.iua.iw3.gastrack.model.business.inteface.ICamionBusiness;
import ar.edu.iua.iw3.gastrack.model.persistence.CamionRepository;
import lombok.extern.slf4j.Slf4j;

/**
 * Clase que implementa la logica de negocio para los camiones
 * 
 * @author Leandro Biondi
 * @author Benjamin Vargas
 * @author Antonella Badami
 * @version 1.0
 * @since 2025-10-16
 */

@Service
@Slf4j
public class CamionBusiness implements ICamionBusiness{
    
    @Autowired
	private CamionRepository camionDAO;

    /**
     * Listar todos los camiones
     * 
     * @return Lista de camiones
     * @throws BusinessException Si ocurre un error no previsto
     */
    @Override
    public List<Camion> list() throws BusinessException {
        try {
			return camionDAO.findAll();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw BusinessException.builder().ex(e).message(e.getMessage()).build();
		}
    }

    /**
     * Obtener un camion por id
     * 
     * @param id Id del camion
     * @return camion
     * @throws NotFoundException Si no se encuentra el camion
     * @throws BusinessException Si ocurre un error no previsto
     */
    @Override
    public Camion load(long id) throws NotFoundException, BusinessException{
        Optional<Camion> r;
		try {
			r = camionDAO.findById(id);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw BusinessException.builder().ex(e).build();
		}
		if (r.isEmpty()) {
			throw NotFoundException.builder().message("No se encuentra el camion id=" + id).build();
		}
		return r.get();
    }

    /**
     * Obtener un camion por patente
     * 
     * @param patente patente del camion
     * @return camion
     * @throws NotFoundException Si no se encuentra el camion
     * @throws BusinessException Si ocurre un error no previsto
     */
    @Override
    public Camion load(String patente) throws NotFoundException, BusinessException {
        Optional<Camion> r;
		try {
			r = camionDAO.findByPatente(patente);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw BusinessException.builder().ex(e).build();
		}
		if (r.isEmpty()) {
			throw NotFoundException.builder().message("No se encuentra la patente '"+patente+"'").build();
		}
		return r.get();
    }

     /**
     * Agregar un camion
     * 
     * @param camion camion a agregar
     * @return camion agregado
     * @throws FoundException    Si ya existe un camion con el mismo id o patente
     * @throws BusinessException Si ocurre un error no previsto
     */
    @Override
	public Camion add(Camion camion) throws FoundException, BusinessException{
        try {
			load(camion.getId());
			throw FoundException.builder().message("Se encuentró el camion id=" + camion.getId()).build();
		} catch (NotFoundException e) {
		}
		try {
			load(camion.getPatente());
			throw FoundException.builder().message("Se encuentró el camion '" + camion.getPatente() +"'").build();
		} catch (NotFoundException e) {
		}

		try {
			return camionDAO.save(camion);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw BusinessException.builder().ex(e).build();
		}
    }

    /**
     * Actualizar un camion
     * 
     * @param camion camion a actualizar
     * @return camion actualizado
     * @throws NotFoundException Si no se encuentra el camion
     * @throws FoundException    Si ya existe un camion con la misma patente
     *                           que no sea el mismo camion actualizar
     * @throws BusinessException Si ocurre un error no previsto
     */
    @Override
	public Camion update(Camion camion) throws FoundException, NotFoundException, BusinessException{
        load(camion.getId());
		Optional<Camion> patenteExistente=null;
		try {
			patenteExistente=camionDAO.findByPatenteAndIdNot(camion.getPatente(), camion.getId());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw BusinessException.builder().ex(e).build();
		}
		if(patenteExistente.isPresent()) {
			throw FoundException.builder().message("Se encontró un camion="+camion.getPatente()).build();
		}

		try {
			return camionDAO.save(camion);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw BusinessException.builder().ex(e).build();
		}
    }

    /**
     * Eliminar un camion por id
     * 
     * @param id Id del camion a eliminar
     * @throws NotFoundException Si no se encuentra el camion a eliminar
     * @throws BusinessException Si ocurre un error no previsto
     */
    @Override
	public void delete(long id) throws NotFoundException, BusinessException{
       load(id);
		try {
			 camionDAO.deleteById(id);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw BusinessException.builder().ex(e).build();
		}
    }

}
