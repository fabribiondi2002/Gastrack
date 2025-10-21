package ar.edu.iua.iw3.gastrack.model.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ar.edu.iua.iw3.gastrack.model.Orden;

/*
 * Repositorio para la entidad Orden
 * @author Leandro Biondi
 * @author Benjamin Vargas
 * @author Antonella Badami
 * @version 1.0
 * @since 2025-10-21
 */

@Repository
public interface OrdenRepository extends JpaRepository<Orden,Long>{
    /*
     * Buscar ordenes por estado
     * @param status Estado de la orden
     * @return Lista de ordenes con el estado especificado
     */
    Optional <List<Orden>> findAllByEstado(Orden.Estado status);

    /*
     * Buscar una orden por su numero de orden
     * @param numeroOrden Numero de la orden
     * @return Orden con el numero especificado
     */
    Optional<Orden> findByNumeroOrden(String numeroOrden);
}
