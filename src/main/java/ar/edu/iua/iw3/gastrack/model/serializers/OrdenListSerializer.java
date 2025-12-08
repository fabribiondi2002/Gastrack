package ar.edu.iua.iw3.gastrack.model.serializers;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import ar.edu.iua.iw3.gastrack.model.Orden;
/**
 * Serializador personalizado para listado de ordenes
 * @author Leandro Biondi
 * @author Antonella Badami
 * @author Benjamin Vargas
 * @since 7/12/2025
 */

public class OrdenListSerializer extends StdSerializer<Orden>{
    
	public OrdenListSerializer(Class<?> t, boolean dummy) {
		super(t, dummy);
	}

	@Override
	public void serialize(Orden value, JsonGenerator gen, SerializerProvider provider) throws IOException {
            
        gen.writeStartObject(); 
        gen.writeNumberField("numero-orden",value.getNumeroOrden());
        gen.writeStringField("estado",value.getEstado().toString().replace("_", " "));
        gen.writeStringField("camion", value.getCamion().getPatente());
        gen.writeStringField("preset", value.getCamion().getPatente());
        gen.writeNumberField("carga", value.getUltimaMasaAcumulada());
        gen.writeNumberField("temperatura", value.getUltimaTemperatura());
        gen.writeNumberField("densidad", value.getUltimaDensidad());
        gen.writeNumberField("caudal", value.getUltimoCaudal());
        gen.writeEndObject();	

	}
}

