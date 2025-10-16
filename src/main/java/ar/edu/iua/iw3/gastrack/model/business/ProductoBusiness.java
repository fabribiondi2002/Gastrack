package ar.edu.iua.iw3.gastrack.model.business;

import java.util.Optional;

import org.springframework.stereotype.Service;

import ar.edu.iua.iw3.gastrack.model.Producto;
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
public class ProductoBusiness implements IProductoBusiness{
    
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

    
}
