package ar.edu.iua.iw3.gastrack.integration.cli1.models.persistence;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ar.edu.iua.iw3.gastrack.integration.cli1.models.OrdenCli1;

@Repository
public interface OrdenCli1Repository extends JpaRepository<OrdenCli1, Long> {
    Optional<OrdenCli1> findByNumeroOrden(String numeroOrden);
    Optional<OrdenCli1> findByCodigoExternoCli1(String codigoExternoCli1);
}
