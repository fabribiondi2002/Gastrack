package ar.edu.iua.iw3.gastrack.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Clase que representa un detalle de carga de una cisterna.
 * Cada detalle tiene un ID único, una masa acumulada, una densidad, una temperatura, un caudal y una fecha.
 * @param orden La orden asociada al detalle.
 * @param cisterna La cisterna asociada al detalle.
 * @param masaAcumulada Masa acumulada registrada en el detalle.
 * @param densidad Densidad registrada en el detalle.
 * @param temperatura Temperatura registrada en el detalle.
 * @param caudal Caudal registrado en el detalle.
 * @param fecha Fecha del registro del detalle.
 * @author Leandro Biondi
 * @author Benjamin Vargas
 * @author Antonella Badami
 * @version 1.0
 * @since 2025-10-16
 */

@Entity
@Table(name = "detalles")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Detalle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Double masaAcumulada;

    @Column(nullable = false)
    private Double densidad;
    
    @Column(nullable = false)
    private Double temperatura;
    
    @Column(nullable = false)
    private Double caudal;
    
    @Column(nullable = false)
    private Date fecha;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_orden", nullable = false)
    @JsonIgnore
    private Orden orden;

}