package ar.edu.iua.iw3.gastrack.model.persistence;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ar.edu.iua.iw3.gastrack.model.Chofer;

/** 
 * Repositorio para la entidad Chofer 
 * @author Leandro Biondi
 * @author Benjamin Vargas
 * @author Antonella Badami
 * @version 1.0
 * @since 2025-10-16
*/

@Repository
public interface ChoferRepository extends JpaRepository<Chofer, Long> {
    /**
     * Busca un chofer por su documento
     * @param documento
     * @return El chofer encontrado o null si no existe
     */
    Optional<Chofer> findByDocumento(Long documento);
    /**
     * Busca un chofer por su documento, excluyendo un id
     * @param documento
     * @param id
     * @return El chofer encontrado o null si no existe
     */
    Optional<Chofer> findByDocumentoAndIdNot(Long documento, Long id);
}
