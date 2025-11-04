package ar.edu.iua.iw3.gastrack.model.deserializers;
import java.io.IOException;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import ar.edu.iua.iw3.gastrack.model.Orden;
import ar.edu.iua.iw3.gastrack.util.JsonUtils;

public class NOrdenJsonDeserializer extends StdDeserializer<Orden> {


	public NOrdenJsonDeserializer(Class<?> vc) {
		super(vc);
	}

	@Override
	public Orden deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JacksonException {
		Orden r = new Orden();
		JsonNode node = jp.getCodec().readTree(jp);
        long numeroOrden = JsonUtils.getLong(node, "numero_orden,numeroOrden,order_number,orderNumber".split(","), null);
        r.setNumeroOrden(numeroOrden);
		return r;
	}

}