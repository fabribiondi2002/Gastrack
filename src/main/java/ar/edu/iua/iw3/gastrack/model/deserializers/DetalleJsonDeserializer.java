package ar.edu.iua.iw3.gastrack.model.deserializers;
import static ar.edu.iua.iw3.gastrack.util.JsonAtributesConstants.CAUDAL;
import static ar.edu.iua.iw3.gastrack.util.JsonAtributesConstants.DENSIDAD;
import static ar.edu.iua.iw3.gastrack.util.JsonAtributesConstants.MASA_ACUMULADA;
import static ar.edu.iua.iw3.gastrack.util.JsonAtributesConstants.NUMERO_ORDEN;
import static ar.edu.iua.iw3.gastrack.util.JsonAtributesConstants.TEMPERATURA;

import java.io.IOException;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import ar.edu.iua.iw3.gastrack.model.Detalle;
import ar.edu.iua.iw3.gastrack.model.Orden;
import ar.edu.iua.iw3.gastrack.util.JsonUtils;
public class DetalleJsonDeserializer extends StdDeserializer<Detalle> {


	public DetalleJsonDeserializer(Class<?> vc) {
		super(vc);
	}

	@Override
	public Detalle deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JacksonException {
		Detalle r = new Detalle();
		JsonNode node = jp.getCodec().readTree(jp);

        Double masaAcumulada = JsonUtils.getDouble(node, MASA_ACUMULADA, null);
        Double densidad = JsonUtils.getDouble(node, DENSIDAD, null);
        Double temperatura = JsonUtils.getDouble(node, TEMPERATURA, null);
        Double caudal = JsonUtils.getDouble(node, CAUDAL, null);
        Long numeroOrden = JsonUtils.getLong(node, NUMERO_ORDEN, null);
        
        r.setMasaAcumulada(masaAcumulada);
        r.setDensidad(densidad);
        r.setTemperatura(temperatura);
        r.setCaudal(caudal);
        Orden dummyOrden = new Orden();
        dummyOrden.setNumeroOrden(numeroOrden);
        r.setOrden(dummyOrden);
		return r;
	}

}