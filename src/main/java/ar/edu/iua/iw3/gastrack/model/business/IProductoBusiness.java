package ar.edu.iua.iw3.gastrack.model.business;

import ar.edu.iua.iw3.gastrack.model.Producto;

/** 
 * Interfaz para la logica de negocio de los productos 
 * @author Leandro Biondi
 * @author Benjamin Vargas
 * @author Antonella Badami
 * @version 1.0
 * @since 2025-10-16
*/

public interface IProductoBusiness {

    public Producto add(Producto producto) throws BusinessException, FoundException;

    public Producto load(long id) throws NotFoundException, BusinessException;

    public Producto load(String nombre) throws NotFoundException, BusinessException;

}
