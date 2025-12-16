package ar.edu.iua.iw3.gastrack.websocket.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CabeceraOrdenWS {

    private long numeroOrden;
    private double ultimaMasaAcumulada;
    private double ultimaDensidad;
    private double ultimaTemperatura;
    private double ultimoCaudal;
    private Date fechaUltimoMedicion;
}
