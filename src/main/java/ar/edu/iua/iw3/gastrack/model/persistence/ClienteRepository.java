package ar.edu.iua.iw3.gastrack.model.persistence;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ar.edu.iua.iw3.gastrack.model.Cliente;
/*
 * Repositorio para la entidad Cliente
 * @author Leandro Biondi
 * @author Benjamin Vargas
 * @author Antonella Badami
 * @version 1.0
 * @since 2025-10-21
 */
@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    /**
     * Busca un cliente por su razon social
     * @param razonSocial
     * @return El cliente encontrado o null si no existe
     */
    Optional<Cliente> findByRazonSocial(String razonSocial);
    /**
     * Busca un cliente por su razon social, excluyendo un id
     * @param razonSocial
     * @param id
     * @return El cliente encontrado o null si no existe
     */
    Optional<Cliente> findByRazonSocialAndIdNot(String razonSocial, Long id);

    
}
