package ar.edu.iua.iw3.gastrack.model.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ar.edu.iua.iw3.gastrack.model.Alarma;
/**
 * Repositorio para la entidad Camion
 * @author Leandro Biondi
 * @author Benjamin Vargas
 * @author Antonella Badami
 * @version 1.0
 * @since 2025-12-11
 */

@Repository
public interface AlarmaRepository extends JpaRepository<Alarma, Long> {

    // Buscar la alarma más reciente por número de orden y tipo de alarma
    Optional<Alarma> findTopByOrden_NumeroOrdenAndTipoAlarmaOrderByFechaEmisionDesc(long numeroOrden,Alarma.TipoAlarma tipoAlarma);
    List<Alarma> findByAceptadaFalse();
}
