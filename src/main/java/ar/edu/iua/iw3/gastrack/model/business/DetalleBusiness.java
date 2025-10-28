package ar.edu.iua.iw3.gastrack.model.business;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import ar.edu.iua.iw3.gastrack.model.Detalle;
import ar.edu.iua.iw3.gastrack.model.Orden;
import ar.edu.iua.iw3.gastrack.model.business.exception.BusinessException;
import ar.edu.iua.iw3.gastrack.model.business.exception.FoundException;
import ar.edu.iua.iw3.gastrack.model.business.exception.InvalidDetailException;
import ar.edu.iua.iw3.gastrack.model.business.exception.InvalidDetailFrecuencyException;
import ar.edu.iua.iw3.gastrack.model.business.exception.NotFoundException;
import ar.edu.iua.iw3.gastrack.model.business.exception.OrderInvalidStateException;
import ar.edu.iua.iw3.gastrack.model.business.exception.OrderNotAuthorizedToLoadException;
import ar.edu.iua.iw3.gastrack.model.business.intefaces.IDetalleBusiness;
import ar.edu.iua.iw3.gastrack.model.deserializers.DetalleJsonDeserializer;
import ar.edu.iua.iw3.gastrack.model.persistence.DetalleRepository;
import ar.edu.iua.iw3.gastrack.util.DetalleManager;
import ar.edu.iua.iw3.gastrack.util.JsonUtiles;
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

    @Value("${detalle.frecuencia.muestreo.milis:10000}")
    private long frecuenciaMuestreoMilis; // Frecuencia de muestreo en milisegundos

     /**
     * Agregar un detalle
     * Se implementa deserializador personalizado para validar el JSON de entrada
     * @see DetalleJsonDeserializer
     * @param detalle detalle a agregar
     * @return detalle agregado
     * @throws FoundException    Si ya existe un detalle con el mismo id 
     * @throws BusinessException Si ocurre un error no previsto
     * @throws InvalidDetailException Si el detalle no cumple los criterios de aceptacion
     * @throws InvalidDetailFrecuencyException Si el detalle no cumple con la frecuencia de muestreo
     * @throws OrderInvalidStateException Si la orden no se encuentra en estado valido para agregar detalles
     */

    @Override
	public Detalle add(String json)
        throws NotFoundException, BusinessException, InvalidDetailException,InvalidDetailFrecuencyException,
        OrderInvalidStateException, OrderNotAuthorizedToLoadException
    {
        //deserializador
        ObjectMapper mapper = JsonUtiles.getObjectMapper(Detalle.class, new DetalleJsonDeserializer(Detalle.class), null);
        Detalle detalle = null;
        try
        {
            detalle = mapper.readValue(json, Detalle.class);
        }
        catch (Exception e)
        {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        } 
        Orden ord = ordenBusiness.loadByNumeroOrden(detalle.getOrden().getNumeroOrden());

        if (!ord.getCargaHabilitada())
        {
            log.warn("Se intento registrar detalles en una operacion no habilitada para carga: " + ord.getId());
            throw OrderNotAuthorizedToLoadException.builder()
                .message("No se puede agregar detalle a la orden id "+ord.getId()+" ya que no esta habilitada para carga")
                .build();
        }

        if(!ord.getEstado().equals(Orden.Estado.PESAJE_INICIAL_REGISTRADO))
        {
            log.warn("Se intento registrar detalles en una operacion con estado: " + ord.getEstado());
            throw OrderInvalidStateException.builder()
                .message("No se puede agregar detalle a la orden id="+ord.getId()+" en estado "+ord.getEstado())
                .build();
        }

        detalle.setOrden(ord);
        detalle.setFecha(new Date());
        DetalleManager.manage(detalleDAO, detalle,frecuenciaMuestreoMilis);
		return detalleDAO.save(detalle);
    }


    /**
     * Actualizar un detalle si la orden se encuentra en estado valido
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

    /**
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
