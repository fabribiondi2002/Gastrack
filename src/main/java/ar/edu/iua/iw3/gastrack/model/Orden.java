package ar.edu.iua.iw3.gastrack.model;


import java.beans.Transient;
import java.util.Date;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Clase que representa una orden de carga de una cisterna.
 * Cada orden tiene un ID único, un estado, un peso inicial, un peso final, fechas de pesaje y carga, y mediciones de la última carga.
 * @param camion El camión asociado a la orden.
 * @param chofer El chofer asociado a la orden.
 * @param producto El producto asociado a la orden.
 * @param cliente El cliente asociado a la orden.
 * @param detalles Arreglo de los detalles de la orden.
 * @param id Identificador único de la orden.
 * @param numeroOrden Número único de la orden.
 * @param estado Estado actual de la orden.
 * @param contrasenaActivacion Contraseña de activación de la orden.
 * @param codigoExterno Código externo de la orden.
 * @param preset Volumen a cargar de la orden.
 * @param pesoInicial Peso inicial del camión.
 * @param pesoFinal Peso final del camión.
 * @param fechaPesajeInicial Fecha del pesaje inicial.
 * @param fechaPesajeFinal Fecha del pesaje final.
 * @param fechaCargaPrevista Fecha prevista para la carga.
 * @param fechaInicioCarga Fecha de inicio de la carga.
 * @param fechaFinCarga Fecha de fin de la carga.
 * @param ultimaMasaAcumulada Última masa acumulada registrada.
 * @param ultimaDensidad Última densidad registrada.
 * @param ultimaTemperatura Última temperatura registrada.
 * @param ultimoCaudal Último caudal registrado.
 * @param fechaUltimoMedicion Fecha de la última medición registrada.
 * @author Leandro Biondi
 * @author Benjamin Vargas
 * @author Antonella Badami
 * @version 1.0
 * @since 2025-10-16
 */

@Entity
@Table(name = "ordenes")
@Inheritance(strategy = InheritanceType.JOINED)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Orden {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, unique = true)
    private long numeroOrden;


    @Column(nullable = true, unique = true)
    private String codigoExterno;

    @Enumerated(EnumType.STRING)
    @Column()
    private Estado estado;

    public enum Estado {
        PENDIENTE_PESAJE_INICIAL,
        PESAJE_INICIAL_REGISTRADO,
        ORDEN_CERRADA_PARA_CARGA,
        PESAJE_FINAL_REGISTRADO,
        ORDEN_CANCELADA
    }


    @Column(length = 5)
    private String contrasenaActivacion;

    private Boolean cargaHabilitada=false;

    
    @Column(nullable = false)
    private double preset;

    private double pesoInicial;
    private double pesoFinal;
    private Date fechaPesajeInicial;
    private Date fechaPesajeFinal;

    private Date fechaCargaPrevista;
    private Date fechaInicioCarga;
    private Date fechaFinCarga;

    private double ultimaMasaAcumulada;
    private double ultimaDensidad;
    private double ultimaTemperatura;
    private double ultimoCaudal;
    private Date fechaUltimoMedicion;

    @ManyToOne
    @JoinColumn(name = "id_camion")
    private Camion camion;

    @ManyToOne
    @JoinColumn(name = "id_chofer")
    private Chofer chofer;

    @ManyToOne
    @JoinColumn(name = "id_producto")
    private Producto producto;

    @ManyToOne
    @JoinColumn(name = "id_cliente")
    private Cliente cliente;

    @OneToMany(mappedBy = "orden")
    private Set<Detalle> detalles;
    
    @Transient
    public void siguienteEstado() {
        switch (this.estado) {
            case PENDIENTE_PESAJE_INICIAL:
                this.estado = Estado.PESAJE_INICIAL_REGISTRADO;
                break;
            case PESAJE_INICIAL_REGISTRADO:
                this.estado = Estado.ORDEN_CERRADA_PARA_CARGA;
                break;
            case ORDEN_CERRADA_PARA_CARGA:
                this.estado = Estado.PESAJE_FINAL_REGISTRADO;
                break;
            case PESAJE_FINAL_REGISTRADO:
                this.estado = Estado.ORDEN_CANCELADA;
                break;
            default:
                break;
        }
    }
}