package ar.edu.iua.iw3.gastrack.model.deserializers;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import ar.edu.iua.iw3.gastrack.model.Alarma;
import ar.edu.iua.iw3.gastrack.model.Alarma.TipoAlarma;
import ar.edu.iua.iw3.gastrack.model.deserializers.DTO.AlarmaDTO;
import ar.edu.iua.iw3.gastrack.util.JsonAtributesConstants;
import ar.edu.iua.iw3.gastrack.util.JsonUtils;
/**
 * Deserealizador de Alarmas
 * @see Alarma
 */
public class AlarmaDeserializer extends StdDeserializer<AlarmaDTO> {

	public AlarmaDeserializer(Class<?> vc){
		super(vc);
	}
	
	@Override
	public AlarmaDTO deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
		AlarmaDTO r = new AlarmaDTO();
		JsonNode node = jp.getCodec().readTree(jp);

        long numeroOrden = JsonUtils.getLong(node, JsonAtributesConstants.NUMERO_ORDEN, null);
        String tipoAlarma = JsonUtils.getString(node, JsonAtributesConstants.TIPO_ALARMA, null);
        String observacion = JsonUtils.getString(node, JsonAtributesConstants.OBSERVACION, null);
        String usermail = JsonUtils.getString(node, JsonAtributesConstants.USERMAIL, null);

        r.setNumeroOrden(numeroOrden);
        r.setTipoAlarma(TipoAlarma.valueOf(tipoAlarma));
        r.setObservacion(observacion);
        r.setUsermail(usermail);

		return r;

	}
}
    
