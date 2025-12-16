package ar.edu.iua.iw3.gastrack.model;

import java.util.Date;

import ar.edu.iua.iw3.gastrack.auth.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

@Entity
@Table(name = "alarmas")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Alarma {


    public enum TipoAlarma {
        ALTA_TEMPERATURA
    }


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private  Date fechaEmision;
    
    @Enumerated(EnumType.STRING)
    @Column(length = 30, nullable = false)
    private TipoAlarma tipoAlarma;

    @ManyToOne
    @JoinColumn(name = "orden_id", nullable = false)
    private Orden orden;

    @Column(nullable = false)
    private boolean aceptada = false;
    
    @Column(nullable = true)
    private Date fechaAceptacion;

    @Column(length = 255, nullable = true)
    private String observacion;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = true)
    private User usuario;
}
