package ar.edu.iua.iw3.gastrack.model.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ar.edu.iua.iw3.gastrack.model.Orden;

@Repository
public interface OrdenRepository extends JpaRepository<Orden,Long>{
    Optional <List<Orden>> findAllByStatus(String status);
}
