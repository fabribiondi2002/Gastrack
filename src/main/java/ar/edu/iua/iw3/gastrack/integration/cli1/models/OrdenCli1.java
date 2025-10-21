package ar.edu.iua.iw3.gastrack.integration.cli1.models;

import ar.edu.iua.iw3.gastrack.model.Orden;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/*
 * Clase que representa una orden para la integracion CLI1
 * Hereda de la clase Orden
 * Se le añade numero de orden unico para la integracion CLI1
 * @author Leandro Biondi
 * @author Benjamin Vargas
 * @author Antonella Badami
 * @version 1.0
 * @since 2025-10-21
 */

@Entity
@Table(name="cli1_ordenes")
@PrimaryKeyJoinColumn(name="id_orden")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class OrdenCli1 extends Orden {
    
    @Column(nullable = false, unique = true)
    private String codigoExternoCli1;
}
