package ar.edu.iua.iw3.gastrack.model.deserializers.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Deprecated
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TaraDTO {
    private Long numeroOrden;
    private Double pesoInicial;
}
