package ar.edu.iua.iw3.gastrack.model.persistence;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ar.edu.iua.iw3.gastrack.model.Camion;

@Repository
public interface CamionRepository extends JpaRepository<Camion, Long> {
    /**
     * Busca un camion por su patente
     * @param patente
     * @return El camion encontrado o null si no existe
     */
    Optional<Camion> findByPatente(String patente);
    /**
     * Busca un camion por su patente, excluyendo un id
     * @param patente
     * @param id
     * @return El camion encontrado o null si no existe
     */
    Optional<Camion> findByPatenteAndIdNot(String patente, Long id);
}
