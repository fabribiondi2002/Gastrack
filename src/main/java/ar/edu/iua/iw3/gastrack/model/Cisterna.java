package ar.edu.iua.iw3.gastrack.model;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Clase que representa una cisterna de un camión.
 * Cada cisterna tiene un ID único, un número y un volumen.
 * @param numero El número de la cisterna.
 * @param volumen El volumen de la cisterna.
 * @param camion El camión al que pertenece la cisterna.
 * @author Leandro Biondi
 * @author Benjamin Vargas
 * @author Antonella Badami
 * @version 1.0
 * @since 2025-10-16
 */

@Entity
@Table(name = "cisternas")
@Inheritance(strategy = InheritanceType.JOINED)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Cisterna {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long numero;

    @Column(nullable = false)
    private Double volumen;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_camion", nullable = false)
    private Camion camion;
}
