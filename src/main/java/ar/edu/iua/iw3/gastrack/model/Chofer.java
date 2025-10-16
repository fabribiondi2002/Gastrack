package ar.edu.iua.iw3.gastrack.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Clase que representa un chofer que conduce un camión en el sistema.
 * Cada chofer tiene un ID único, un nombre, un apellido y un documento.
 * @param nombre El nombre del chofer.
 * @param apellido El apellido del chofer.
 * @param documento El documento del chofer.
 * @author Leandro Biondi
 * @author Benjamin Vargas
 * @author Antonella Badami
 * @version 1.0
 * @since 2025-10-16
 */

@Entity
@Table(name = "choferes")
@Inheritance(strategy = InheritanceType.JOINED)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Chofer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 100,nullable = false)
    private String nombre;
    @Column(length = 100,nullable = false)
    private String apellido;
    @Column(unique = true, nullable = false, length = 8)
    private Long documento;
}
