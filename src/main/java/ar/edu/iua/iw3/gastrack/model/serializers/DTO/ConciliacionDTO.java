package ar.edu.iua.iw3.gastrack.model.serializers.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/*
 * DTO para la conciliacion de una orden
 * Contiene los campos necesarios para la conciliacion
 * numeroOrden, codigoExterno, pesajeInicial, pesajeFinal, productoCargado, netoBalanza,
 * difBalanzaCaudalimetro, promedioCaudal, promedioTemperatura, promedioDensidad
 * @author Leandro Biondi
 * @author Antonella Badami
 * @author Benjamin Vargas
 * @since 1/11/2025
 */

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ConciliacionDTO {
    private long numeroOrden;
    private String codigoExterno;
    private double pesajeInicial;
    private double pesajeFinal;
    private double productoCargado;
    private double netoBalanza;
    private double difBalanzaCaudalimentro;
    private double promedioCaudal;
    private double promedioTemperatura;
    private double promedioDensidad;
}
