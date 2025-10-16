package ar.edu.iua.iw3.gastrack.model;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Clase que representa un camión con cisternas.
 * Cada camión tiene un ID único, una patente y una descripción.
 * @param patente La patente del camión.
 * @param descripcion La descripción adicional del camión.
 * @param cisternas Las cisternas que lleva el camión.
 * @author Leandro Biondi
 * @author Benjamin Vargas
 * @author Antonella Badami
 * @version 1.0
 * @since 2025-10-16
*/

@Entity
@Table(name = "camiones")
@Inheritance(strategy = InheritanceType.JOINED)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Camion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false, length = 7)
    private String patente;
    @Column(length = 100)
    private String descripcion;

    @OneToMany(mappedBy = "camion",cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Cisterna> cisternas = new HashSet<>();
}
