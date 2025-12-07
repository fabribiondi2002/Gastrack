package ar.edu.iua.iw3.gastrack.auth;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
/**
 * Repositorio para la gestión de roles en la base de datos.
 * @author Leandro Biondi
 * @author Benjamin Vargas
 * @author Antonella Badami
 * @version 1.0
 * @since 2025-12-07
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Integer>{ 
}