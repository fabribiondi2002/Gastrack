package ar.edu.iua.iw3.gastrack.model.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ar.edu.iua.iw3.gastrack.model.Detalle;

/*
 * Repositorio para la entidad Detalle
 * @author Leandro Biondi
 * @author Benjamin Vargas
 * @author Antonella Badami
 * @version 1.0
 * @since 2025-10-21
 */
@Repository
public interface DetalleRepository extends JpaRepository<Detalle, Long> {
    /*
     * Buscar detalles por id de orden
     * @param orden Id de la orden
     * @return Lista de detalles
     * @throws NotFoundException Si no se encuentran detalles para la orden
     */
    Optional <List<Detalle>> findAllByOrden_id(Long orden);

    Optional<Detalle> findFirstByOrderByFechaDesc();
}
