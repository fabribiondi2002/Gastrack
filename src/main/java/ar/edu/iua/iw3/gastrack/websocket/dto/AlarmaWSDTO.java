package ar.edu.iua.iw3.gastrack.websocket.dto;



import java.util.Date;

import ar.edu.iua.iw3.gastrack.model.Alarma.TipoAlarma;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AlarmaWSDTO {
    private long id;
    private long numeroOrden;
    private Date fecha;
    private TipoAlarma tipoAlarma;
}
