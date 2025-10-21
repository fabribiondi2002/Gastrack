package ar.edu.iua.iw3.gastrack.model.business;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import ar.edu.iua.iw3.gastrack.model.Producto;
import ar.edu.iua.iw3.gastrack.model.business.exception.BusinessException;
import ar.edu.iua.iw3.gastrack.model.business.exception.FoundException;
import ar.edu.iua.iw3.gastrack.model.business.exception.NotFoundException;
import ar.edu.iua.iw3.gastrack.model.business.intefaces.IProductoBusiness;
import ar.edu.iua.iw3.gastrack.model.persistence.ProductoRepository;
import lombok.extern.slf4j.Slf4j;

/**
 * Clase que implementa la logica de negocio para los productos
 * 
 * @author Leandro Biondi
 * @author Benjamin Vargas
 * @author Antonella Badami
 * @version 1.0
 * @since 2025-10-16
 */

@Service
@Slf4j
public class ProductoBusiness implements IProductoBusiness {

    private ProductoRepository productoDAO;
    

    /**
     * Agregar un producto
     * 
     * @param producto Producto a agregar
     * @return Producto agregado
     * @throws FoundException    Si ya existe un producto con el mismo id o nombre
     * @throws BusinessException Si ocurre un error no previsto
     */

    @Override
    public Producto add(Producto producto) throws FoundException, BusinessException {
        try {
            load(producto.getId());
            throw FoundException.builder().message("Se encontró el producto con id: " + producto.getId()).build();
        } catch (NotFoundException e) {

        }
        try {
            load(producto.getNombre());
            throw FoundException.builder().message("Se encontró el producto de nombre: " + producto.getNombre())
                    .build();
        } catch (NotFoundException e) {
        }
        try {
            return productoDAO.save(producto);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
    }

    /**
     * Obtener un producto por id
     * 
     * @param id Id del producto
     * @return Producto
     * @throws NotFoundException Si no se encuentra el producto
     * @throws BusinessException Si ocurre un error no previsto
     */
    @Override
    public Producto load(long id) throws NotFoundException, BusinessException {
        Optional<Producto> p;
        try {
            p = productoDAO.findById(id);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
        if (p.isEmpty()) {
            throw NotFoundException.builder().message("No se encuentra el Producto id: " + id).build();
        }
        return p.get();
    }

    /**
     * Obtener un producto por nombre
     * 
     * @param nombre Nombre del producto
     * @return Producto
     * @throws NotFoundException Si no se encuentra el producto
     * @throws BusinessException Si ocurre un error no previsto
     */
    @Override
    public Producto load(String nombre) throws NotFoundException, BusinessException {
        Optional<Producto> p;
        try {
            p = productoDAO.findByNombre(nombre);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
        if (p.isEmpty()) {
            throw NotFoundException.builder().message("No se encuentra el producto de nombre: " + nombre).build();
        }
        return p.get();
    }

    @Override
    public Producto update(Producto producto) throws NotFoundException, FoundException, BusinessException {
        load(producto.getId());
		Optional<Producto> nombreExistente=null;
		try {
			nombreExistente=productoDAO.findByNombreAndIdNot(producto.getNombre(), producto.getId());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw BusinessException.builder().ex(e).build();
		}
		if(nombreExistente.isPresent()) {
			throw FoundException.builder().message("Se encontró un producto con nombre="+producto.getNombre()).build();
		}

		try {
			return productoDAO.save(producto);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw BusinessException.builder().ex(e).build();
		}
    }

    @Override
    public void delete(long id) throws NotFoundException, BusinessException {
        load(id);
        try {
            productoDAO.deleteById(id);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
    }

    @Override
    public List<Producto> list() throws BusinessException, NotFoundException {
        try {
            return productoDAO.findAll();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
    }

}
