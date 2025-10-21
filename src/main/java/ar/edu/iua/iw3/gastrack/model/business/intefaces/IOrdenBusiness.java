package ar.edu.iua.iw3.gastrack.model.business.intefaces;

import java.util.List;

import ar.edu.iua.iw3.gastrack.model.Orden;
import ar.edu.iua.iw3.gastrack.model.business.exception.BusinessException;
import ar.edu.iua.iw3.gastrack.model.business.exception.FoundException;
import ar.edu.iua.iw3.gastrack.model.business.exception.NotFoundException;

/** 
 * Interfaz para la logica de negocio de ordenes 
 * @author Leandro Biondi
 * @author Benjamin Vargas
 * @author Antonella Badami
 * @version 1.0
 * @since 2025-10-16
*/
public interface IOrdenBusiness {

    public List<Orden> listByStatus(Orden.Estado status) throws BusinessException, NotFoundException;

    public Orden load(long id) throws NotFoundException, BusinessException;

    public Orden add(Orden orden) throws FoundException, BusinessException;

    public Orden update(Orden orden) throws NotFoundException, BusinessException;

    public void delete(long id) throws NotFoundException, BusinessException;

    public Orden loadByNumeroOrden(long numeroOrden) throws NotFoundException, BusinessException;

    public Orden loadByCodigoExterno(String codigoExterno) throws NotFoundException, BusinessException;
    
    public Orden addExternal(String json) throws FoundException, BusinessException;

}
