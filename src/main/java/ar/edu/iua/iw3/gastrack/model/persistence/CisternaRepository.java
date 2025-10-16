package ar.edu.iua.iw3.gastrack.model.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ar.edu.iua.iw3.gastrack.model.Cisterna;

@Repository
public interface CisternaRepository extends JpaRepository<Cisterna, Long> {
    
}
