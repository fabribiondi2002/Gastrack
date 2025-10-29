package ar.edu.iua.iw3.gastrack.model.serializers.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ConciliacionDTO {
    private double pesajeInicial;
    private double pesajeFinal;
    private double productoCargado;
    private double netoBalanza;
    private double difBalanzaCaudalimentro;
    private double promedioCaudal;
    private double promedioTemperatura;
    private double promedioDensidad;
}
