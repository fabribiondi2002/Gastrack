package ar.edu.iua.iw3.gastrack.model.serializers;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import ar.edu.iua.iw3.gastrack.model.serializers.DTO.ConciliacionDTO;

public class ConciliacionSerializer extends StdSerializer<ConciliacionDTO>{

	public ConciliacionSerializer(Class<?> t, boolean dummy) {
		super(t,dummy);
	}

	@Override
	public void serialize(ConciliacionDTO value, JsonGenerator gen, SerializerProvider provider) throws IOException {
		
		gen.writeStartObject(); 
		gen.writeNumberField("Pesaje inicial",value.getPesajeInicial());
		gen.writeNumberField("Pesaje final",value.getPesajeFinal());
		gen.writeNumberField("Producto cargado",value.getProductoCargado());
		gen.writeNumberField("Neto por balanza",value.getNetoBalanza());
		gen.writeNumberField("Diferencia entre balanza y caudalímetro",value.getDifBalanzaCaudalimentro());
		gen.writeNumberField("Promedio caudal",value.getPromedioCaudal());
		gen.writeNumberField("Promedio temperatura",value.getPromedioTemperatura());
		gen.writeNumberField("Promedio densidad",value.getPromedioDensidad());
		gen.writeEndObject();
	}



}