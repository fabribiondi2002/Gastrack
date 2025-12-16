package ar.edu.iua.iw3.gastrack.model.serializers;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import ar.edu.iua.iw3.gastrack.model.Alarma;
/**
 * Serializador personalizado para listado de ordenes
 * @author Leandro Biondi
 * @author Antonella Badami
 * @author Benjamin Vargas
 * @since 11/12/2025
 */

public class AlarmaListSerializer extends StdSerializer<Alarma>{
    
	public AlarmaListSerializer(Class<?> t, boolean dummy) {
		super(t, dummy);
	}

	@Override
	public void serialize(Alarma value, JsonGenerator gen, SerializerProvider provider) throws IOException {
            
        gen.writeStartObject(); 
        gen.writeStringField("fecha",value.getFechaEmision().toString());
        gen.writeStringField("fechaAceptacion",value.getFechaAceptacion() != null ? value.getFechaAceptacion().toString() : null);
        gen.writeStringField("tipoAlarma",value.getTipoAlarma().toString().replace("_", " "));
        gen.writeNumberField("numeroOrden", value.getOrden().getNumeroOrden());
        gen.writeBooleanField("aceptada", value.isAceptada());
        gen.writeStringField("observacion", value.getObservacion());
        gen.writeStringField("usuario", value.getUsuario() != null ? value.getUsuario().getEmail() : null);
        gen.writeEndObject();	

	}
}

