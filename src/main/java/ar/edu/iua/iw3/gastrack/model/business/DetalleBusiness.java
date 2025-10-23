package ar.edu.iua.iw3.gastrack.model.business;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.edu.iua.iw3.gastrack.model.Detalle;
import ar.edu.iua.iw3.gastrack.model.business.exception.BusinessException;
import ar.edu.iua.iw3.gastrack.model.business.exception.FoundException;
import ar.edu.iua.iw3.gastrack.model.business.exception.InvalidDetailException;
import ar.edu.iua.iw3.gastrack.model.business.exception.NotFoundException;
import ar.edu.iua.iw3.gastrack.model.business.intefaces.IDetalleBusiness;
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
    @Autowired
	private OrdenBusiness ordenBusiness;

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
	public Detalle add(Detalle detalle) throws FoundException, BusinessException, InvalidDetailException{
        try
        {
			load(detalle.getId());
			throw FoundException.builder().message("Se encuentró el detalle id: " + detalle.getId()).build();
		} catch (NotFoundException e) {
		}

        Optional<Detalle> detalleFiltrado = filtradoDeDetalles(detalle);
        
        if(detalleFiltrado.isEmpty())
        {
            throw InvalidDetailException.builder().message("El detalle no cumple con los criterios de aceptacion").build();      
        }

		try 
        {
			return detalleDAO.save(detalle);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw BusinessException.builder().ex(e).build();
		}
    }

    
    /**
     * Logica de filtrado de detalles
     * Solo detalles con caudal > 0 y masa acumulada valida son aceptados.
     * @param d detalle a filtrar
     * @return detalle si pasa el filtro, Optional.empty() si no pasa
     * @throws BusinessException Si ocurre un error no previsto
     */
    private Optional<Detalle> filtradoDeDetalles(Detalle d) throws BusinessException
    {
        if(!(d.getCaudal() <=0))
        {
            try
            {
                Optional<Detalle> last = detalleDAO.findFirstByFechaDesc();
                if(!last.isEmpty())
                {
                    if(!(d.getMasaAcumulada() <= 0 || d.getMasaAcumulada()< last.get().getMasaAcumulada()))
                    {
                        return Optional.of(d);
                    }
                }
            }
            catch(Exception e)
            {
                throw BusinessException.builder().ex(e).build();
            }
        }
        
        return Optional.empty();
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

    /*
     * Obtener detalles por id de orden
     * @param ordenId Id de la orden
     * @return Lista de detalles
     * @throws NotFoundException Si no se encuentra la orden o no hay detalles para la orden
     * @throws BusinessException Si ocurre un error no previsto
     */

    @Override
    public List<Detalle> loadByOrdenId(long ordenId) throws NotFoundException, BusinessException {
        Optional<List<Detalle>> r;
        try {
            ordenBusiness.load(ordenId);
        } catch (NotFoundException e) {
            throw NotFoundException.builder().message("No se encuentra la orden id=" + ordenId).build();
        }
        try {
            r = detalleDAO.findAllByOrden_id(ordenId);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
        if (r.isEmpty() || r.get().isEmpty()) {
            throw NotFoundException.builder().message("No se encuentran detalles para la orden id=" + ordenId).build();
        }
        return r.get();
    }

    

}
