package ar.edu.iua.iw3.gastrack.model.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ar.edu.iua.iw3.gastrack.model.Cisterna;
/*
 * Repositorio para la entidad Cisterna
 * @author Leandro Biondi
 * @author Benjamin Vargas
 * @author Antonella Badami
 * @version 1.0
 * @since 2025-10-21
 */
@Repository
public interface CisternaRepository extends JpaRepository<Cisterna, Long> {
    
}
