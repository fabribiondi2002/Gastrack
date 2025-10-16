package ar.edu.iua.iw3.gastrack.model.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ar.edu.iua.iw3.gastrack.model.Orden;

@Repository
public interface OrdenRepository extends JpaRepository<Orden,Long>{}
