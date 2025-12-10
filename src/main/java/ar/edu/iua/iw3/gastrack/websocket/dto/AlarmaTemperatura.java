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
public class AlarmaTemperatura {

    private long numeroOrden;
    private double temperatura;
    private Date fecha;
    private String patente;
}
