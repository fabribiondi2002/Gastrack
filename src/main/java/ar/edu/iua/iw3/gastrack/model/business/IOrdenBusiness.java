package ar.edu.iua.iw3.gastrack.model.business;

import java.util.List;

import ar.edu.iua.iw3.gastrack.model.Orden;

/** 
 * Interfaz para la logica de negocio de ordenes 
 * @author Leandro Biondi
 * @author Benjamin Vargas
 * @author Antonella Badami
 * @version 1.0
 * @since 2025-10-16
*/
public interface IOrdenBusiness {

    public List<Orden> list() throws BusinessException;

    public Orden load(long id) throws NotFoundException, BusinessException;

    public Orden add(Orden orden) throws FoundException, BusinessException;

    public Orden update(Orden orden) throws NotFoundException, FoundException, BusinessException;

    public void delete(long id) throws NotFoundException, BusinessException;
    
}
