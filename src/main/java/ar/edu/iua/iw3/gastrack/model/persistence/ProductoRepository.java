package ar.edu.iua.iw3.gastrack.model.persistence;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ar.edu.iua.iw3.gastrack.model.Producto;

@Repository
public interface ProductoRepository  extends JpaRepository<Producto,Long>
{
    /**
     * Busca un producto por su nombre
     * @param nombre
     * @return El producto encontrado o null si no existe
     */
    Optional<Producto> findByNombre(String nombre);

    Optional<Producto> findByNombreAndIdNot(String nombre, Long id);
}