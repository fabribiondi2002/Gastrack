package ar.edu.iua.iw3.gastrack.model.deserializers;
import java.io.IOException;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import ar.edu.iua.iw3.gastrack.model.deserializers.DTO.NOrdenPassDTO;
import ar.edu.iua.iw3.gastrack.util.JsonUtils;

public class NOrdenPassJsonDeserializer extends StdDeserializer<NOrdenPassDTO> {


	public NOrdenPassJsonDeserializer(Class<?> vc) {
		super(vc);
	}

	@Override
	public NOrdenPassDTO deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JacksonException {
		NOrdenPassDTO r = new NOrdenPassDTO();
		JsonNode node = jp.getCodec().readTree(jp);
        long numeroOrden = JsonUtils.getLong(node, "numero_orden,numeroOrden,order_number,orderNumber".split(","), null);
        String contrasenaActivacion = JsonUtils.getString(node, "contrasena_activacion,contrasenaActivacion,activation_password".split(","), "");
        r.setNumeroOrden(numeroOrden);
        r.setContrasenaActivacion(contrasenaActivacion);
		return r;
	}

}