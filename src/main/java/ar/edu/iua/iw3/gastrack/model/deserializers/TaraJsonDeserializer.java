package ar.edu.iua.iw3.gastrack.model.deserializers;
import java.io.IOException;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import ar.edu.iua.iw3.gastrack.model.deserializers.DTO.TaraDTO;
import ar.edu.iua.iw3.gastrack.util.JsonUtils;

public class TaraJsonDeserializer extends StdDeserializer<TaraDTO> {

	public TaraJsonDeserializer(Class<?> vc) {
		super(vc);
	}

	@Override
	public TaraDTO deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JacksonException {
		TaraDTO r = new TaraDTO();
		JsonNode node = jp.getCodec().readTree(jp);

        long numeroOrden = JsonUtils.getLong(node, "orden_numero,ordenNumero,order_number,orderNumber,numeroOrden".split(","), null);
        double tara = JsonUtils.getDouble(node, "tara,pesoInicial".split(","), 0.0);
        r.setNumeroOrden(numeroOrden);
        r.setPesoInicial(tara);

		return r;
	}

}