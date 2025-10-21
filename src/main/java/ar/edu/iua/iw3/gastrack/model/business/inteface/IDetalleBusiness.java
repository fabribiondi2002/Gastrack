package ar.edu.iua.iw3.gastrack.model.business.inteface;

import java.util.List;

import ar.edu.iua.iw3.gastrack.model.Detalle;
import ar.edu.iua.iw3.gastrack.model.business.exception.BusinessException;
import ar.edu.iua.iw3.gastrack.model.business.exception.FoundException;
import ar.edu.iua.iw3.gastrack.model.business.exception.NotFoundException;

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

	public Detalle add(Detalle detalle) throws FoundException, BusinessException;

	public Detalle update(Detalle detalle) throws FoundException, NotFoundException, BusinessException;

	public void delete(long id) throws NotFoundException, BusinessException;

	public List<Detalle> loadByOrdenId(long ordenId) throws NotFoundException, BusinessException;

}
