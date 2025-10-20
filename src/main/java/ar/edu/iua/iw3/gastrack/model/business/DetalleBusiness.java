package ar.edu.iua.iw3.gastrack.model.business;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.edu.iua.iw3.gastrack.model.Detalle;
import ar.edu.iua.iw3.gastrack.model.business.exception.BusinessException;
import ar.edu.iua.iw3.gastrack.model.business.exception.FoundException;
import ar.edu.iua.iw3.gastrack.model.business.exception.NotFoundException;
import ar.edu.iua.iw3.gastrack.model.persistence.DetalleRepository;
import lombok.extern.slf4j.Slf4j;

/**
 * Clase que implementa la logica de negocio para los detalles
 * 
 * @author Leandro Biondi
 * @author Benjamin Vargas
 * @author Antonella Badami
 * @version 1.0
 * @since 2025-10-16
 */

@Service
@Slf4j
public class DetalleBusiness implements IDetalleBusiness{
    
    @Autowired
	private DetalleRepository detalleDAO;

    /**
     * Listar todos los detalles
     * 
     * @return Lista de detalles
     * @throws BusinessException Si ocurre un error no previsto
     */
    @Override
    public List<Detalle> list() throws BusinessException {
        try {
			return detalleDAO.findAll();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw BusinessException.builder().ex(e).message(e.getMessage()).build();
		}
    }

    /**
     * Obtener un detalle por id
     * 
     * @param id Id del detalles
     * @return detalle
     * @throws NotFoundException Si no se encuentra el detalle
     * @throws BusinessException Si ocurre un error no previsto
     */
    @Override
    public Detalle load(long id) throws NotFoundException, BusinessException{
        Optional<Detalle> r;
		try {
			r = detalleDAO.findById(id);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw BusinessException.builder().ex(e).build();
		}
		if (r.isEmpty()) {
			throw NotFoundException.builder().message("No se encuentra el detalle id=" + id).build();
		}
		return r.get();
    }

     /**
     * Agregar un detalle
     * 
     * @param detalle detalle a agregar
     * @return detalle agregado
     * @throws FoundException    Si ya existe un detalle con el mismo id 
     * @throws BusinessException Si ocurre un error no previsto
     */
    @Override
	public Detalle add(Detalle detalle) throws FoundException, BusinessException{
        try {
			load(detalle.getId());
			throw FoundException.builder().message("Se encuentró el detalle id=" + detalle.getId()).build();
		} catch (NotFoundException e) {
		}

		try {
			return detalleDAO.save(detalle);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw BusinessException.builder().ex(e).build();
		}
    }

    /**
     * Actualizar un detalle
     * 
     * @param detalle detalle a actualizar
     * @return detalle actualizado
     * @throws NotFoundException Si no se encuentra el detalle
     * @throws FoundException    Si ya existe un detalle con el mismo id
     *                           que no sea el mismo detalle actualizar
     * @throws BusinessException Si ocurre un error no previsto
     */
    @Override
	public Detalle update(Detalle detalle) throws FoundException, NotFoundException, BusinessException{
        load(detalle.getId());
		Optional<Detalle> idExistente=null;
		try {
			idExistente=detalleDAO.findById(detalle.getId());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw BusinessException.builder().ex(e).build();
		}
		if(idExistente.isPresent()) {
			throw FoundException.builder().message("Se encontró detalle="+detalle.getId()).build();
		}

		try {
			return detalleDAO.save(detalle);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw BusinessException.builder().ex(e).build();
		}
    }

    /**
     * Eliminar un detalle por id
     * 
     * @param id Id del detalle a eliminar
     * @throws NotFoundException Si no se encuentra el detalle a eliminar
     * @throws BusinessException Si ocurre un error no previsto
     */
    @Override
	public void delete(long id) throws NotFoundException, BusinessException{
       load(id);
		try {
			 detalleDAO.deleteById(id);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw BusinessException.builder().ex(e).build();
		}
    }

}
