package ar.edu.iua.iw3.gastrack.model.deserializers.DTO;

import ar.edu.iua.iw3.gastrack.model.Alarma.TipoAlarma;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AlarmaDTO {

    private long numeroOrden;
    private TipoAlarma tipoAlarma;
    private String observacion;
    private String usermail;
}
