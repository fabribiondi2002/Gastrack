package ar.edu.iua.iw3.gastrack.model.business.intefaces;

import java.util.Date;
import java.util.List;
import java.util.Map;

import ar.edu.iua.iw3.gastrack.model.Detalle;
import ar.edu.iua.iw3.gastrack.model.business.exception.BusinessException;
import ar.edu.iua.iw3.gastrack.model.business.exception.FoundException;
import ar.edu.iua.iw3.gastrack.model.business.exception.InvalidDetailException;
import ar.edu.iua.iw3.gastrack.model.business.exception.InvalidDetailFrecuencyException;
import ar.edu.iua.iw3.gastrack.model.business.exception.NotFoundException;
import ar.edu.iua.iw3.gastrack.model.business.exception.OrderInvalidStateException;
import ar.edu.iua.iw3.gastrack.model.business.exception.OrderNotAuthorizedToLoadException;

/** 
 * Interfaz para la logica de negocio de los detalles 
 * @author Leandro Biondi
 * @author Benjamin Vargas
 * @author Antonella Badami
 * @version 1.0
 * @since 2025-10-16
*/

public interface IDetalleBusiness {
    
    public List<Detalle> list() throws BusinessException;

    public Detalle load(long id) throws NotFoundException, BusinessException;

	public Detalle add(String json, Long ordenNumero) throws NotFoundException, BusinessException, InvalidDetailException,
		InvalidDetailFrecuencyException, OrderInvalidStateException, OrderNotAuthorizedToLoadException;

	public Detalle update(Detalle detalle) throws FoundException, NotFoundException, BusinessException;

	public void delete(long id) throws NotFoundException, BusinessException;

	public List<Detalle> loadByOrdenId(long ordenId) throws NotFoundException, BusinessException;

	public Detalle getLastDetailByOrderId(long ordenId) throws NotFoundException, BusinessException;

	public Detalle getFirstDetailByOrderId(long ordenId) throws NotFoundException, BusinessException;

	public Map<String, Double> loadAverageDetails(long ordenId) throws NotFoundException, BusinessException;

	public Date loadFirstDetailDate(long ordenId) throws NotFoundException, BusinessException;

    public Date loadLastDetailDate(long ordenId) throws NotFoundException, BusinessException;	

}
