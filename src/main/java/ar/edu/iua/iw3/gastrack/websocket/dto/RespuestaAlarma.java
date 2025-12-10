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
public class RespuestaAlarma {

    private long numeroOrden;
    private Date fechaAceptacion;
    private String observarcion;
}
