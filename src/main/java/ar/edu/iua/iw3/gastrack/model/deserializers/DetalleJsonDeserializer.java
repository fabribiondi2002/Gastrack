package ar.edu.iua.iw3.gastrack.model.deserializers;
import java.io.IOException;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import ar.edu.iua.iw3.gastrack.model.Detalle;
import ar.edu.iua.iw3.gastrack.model.Orden;
import ar.edu.iua.iw3.gastrack.util.JsonUtiles;
public class DetalleJsonDeserializer extends StdDeserializer<Detalle> {


	public DetalleJsonDeserializer(Class<?> vc) {
		super(vc);
	}

	@Override
	public Detalle deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JacksonException {
		Detalle r = new Detalle();
		JsonNode node = jp.getCodec().readTree(jp);

        double masaAcumulada = JsonUtiles.getDouble(node, "masa_acumulada,masaAcumulada,masa".split(","), 0);
        double densidad = JsonUtiles.getDouble(node, "densidad,density".split(","), 0);
        double temperatura = JsonUtiles.getDouble(node, "temperatura,temperature,temp".split(","), 0);
        double caudal = JsonUtiles.getDouble(node, "caudal,flow_rate,flowrate".split(","), 0);
        long numeroOrden = JsonUtiles.getLong(node, "orden_numero,ordenNumero,order_number,orderNumber".split(","), -1);
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