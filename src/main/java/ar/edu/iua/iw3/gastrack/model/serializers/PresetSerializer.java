package ar.edu.iua.iw3.gastrack.model.serializers;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import ar.edu.iua.iw3.gastrack.model.Orden;
/**
 * Serializador personalizado para preset de Orden
 * @author Leandro Biondi
 * @author Antonella Badami
 * @author Benjamin Vargas
 * @since 1/11/2025
 */

public class PresetSerializer extends StdSerializer<Orden>{

	public PresetSerializer(Class<?> t, boolean dummy) {
		super(t, dummy);
	}

	@Override
	public void serialize(Orden value, JsonGenerator gen, SerializerProvider provider) throws IOException {
		
		gen.writeStartObject(); 
		gen.writeNumberField("preset",value.getPreset());
		gen.writeEndObject();
	}



}