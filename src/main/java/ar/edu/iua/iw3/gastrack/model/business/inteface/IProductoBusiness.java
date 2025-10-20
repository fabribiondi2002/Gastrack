package ar.edu.iua.iw3.gastrack.model.business.inteface;

import java.util.List;

import ar.edu.iua.iw3.gastrack.model.Producto;
import ar.edu.iua.iw3.gastrack.model.business.exception.BusinessException;
import ar.edu.iua.iw3.gastrack.model.business.exception.FoundException;
import ar.edu.iua.iw3.gastrack.model.business.exception.NotFoundException;

/**
 * Interfaz para la logica de negocio de los productos
 * 
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

    public Producto update(Producto producto) throws NotFoundException, FoundException, BusinessException;

    public void delete(long id) throws NotFoundException, BusinessException;

    public List<Producto> list() throws BusinessException, NotFoundException;

}
