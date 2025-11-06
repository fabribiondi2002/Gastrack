package ar.edu.iua.iw3.gastrack.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import ar.edu.iua.iw3.gastrack.model.Camion;
import ar.edu.iua.iw3.gastrack.model.Chofer;
import ar.edu.iua.iw3.gastrack.model.Cliente;
import ar.edu.iua.iw3.gastrack.model.Producto;
import ar.edu.iua.iw3.gastrack.model.business.exception.BusinessException;
import ar.edu.iua.iw3.gastrack.model.business.intefaces.ICamionBusiness;
import ar.edu.iua.iw3.gastrack.model.business.intefaces.IChoferBusiness;
import ar.edu.iua.iw3.gastrack.model.business.intefaces.IClienteBusiness;
import ar.edu.iua.iw3.gastrack.model.business.intefaces.IProductoBusiness;
import static ar.edu.iua.iw3.gastrack.util.JsonAtributesConstants.CAMION_OBJETO;
import static ar.edu.iua.iw3.gastrack.util.JsonAtributesConstants.CHOFER_OBJETO;
import static ar.edu.iua.iw3.gastrack.util.JsonAtributesConstants.CLIENTE_OBJETO;
import static ar.edu.iua.iw3.gastrack.util.JsonAtributesConstants.PRODUCTO_OBJETO;
/* */
/**
 * Clase de utilidad para trabajar con JSON
 */
public final class JsonUtils {
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static ObjectMapper getObjectMapper(Class clazz, StdSerializer ser, String dateFormat) {
		ObjectMapper mapper = new ObjectMapper();
		String defaultFormat = "yyyy-MM-dd'T'HH:mm:ssZ";
		if (dateFormat != null)
			defaultFormat = dateFormat;
		SimpleDateFormat df = new SimpleDateFormat(defaultFormat, Locale.getDefault());
		SimpleModule module = new SimpleModule();
		if (ser != null) {
			module.addSerializer(clazz, ser);
		}
		mapper.setDateFormat(df);
		mapper.registerModule(module);
		return mapper;

	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static ObjectMapper getObjectMapper(Class clazz, StdDeserializer deser, String dateFormat) {
		ObjectMapper mapper = new ObjectMapper();
		String defaultFormat = "yyyy-MM-dd'T'HH:mm:ssZ";
		if (dateFormat != null)
			defaultFormat = dateFormat;
		SimpleDateFormat df = new SimpleDateFormat(defaultFormat, Locale.getDefault());
		SimpleModule module = new SimpleModule();
		if (deser != null) {
			module.addDeserializer(clazz, deser);
		}
		mapper.setDateFormat(df);
		mapper.registerModule(module);
		return mapper;
	}

	/**
	 * Obtiene una cadena con la siguiente lógica:
	 * 1) Busca en cada uno de los atributos definidos en el arreglo "attrs",
	 * el primero que encuentra será el valor retornado.
	 * 2) Si no se encuentra ninguno de los atributos del punto 1), se
	 * retorna "defaultValue".
	 * Ejemplo: supongamos que "node" represente: {"code":"c1, "codigo":"c11",
	 * "stock":true}
	 * getString(node, String[]{"codigo","cod"},"-1") retorna: "cl1"
	 * getString(node, String[]{"cod_prod","c_prod"},"-1") retorna: "-1"
	 * 
	 * @param node
	 * @param attrs
	 * @param defaultValue
	 * @return
	 */

	public static String getString(JsonNode node, String[] attrs, String defaultValue) {
		String r = null;
		for (String attr : attrs) {
			if (node.get(attr) != null) {
				r = node.get(attr).asText();
				break;
			}
		}
		if (r == null)
			r = defaultValue;
		return r;
	}

	public static Long getLong(JsonNode node, String[] attrs, Long defaultValue) {
		Long r = null;
		for (String attr : attrs) {
			if (node.has(attr) && node.get(attr).isNumber()) { // aceptar cualquier número
				r = node.get(attr).asLong();
				break;
			}
		}
		if (r == null)
		{
			if (defaultValue != null) {
				return defaultValue;
			} else {
				throw new IllegalArgumentException("Campo numérico no encontrado: " + String.join(", ", attrs));
			}
		}
		return r;
	}

	public static double getDouble(JsonNode node, String[] attrs, Double defaultValue) {
		Double r = null;
		for (String attr : attrs) {
			if (node.has(attr)) {
				JsonNode val = node.get(attr);
				if (val.isNumber()) { // ✅ Acepta int, float o double
					r = val.asDouble();
					break;
				}
			}
		}
		if (r == null) {
			if (defaultValue != null) {
				return defaultValue;
			} else {
				throw new IllegalArgumentException("Campo numérico no encontrado: " + String.join(", ", attrs));
			}
		}
		return r;
	}

	public static boolean getBoolean(JsonNode node, String[] attrs, boolean defaultValue) {
		Boolean r = null;
		for (String attr : attrs) {
			if (node.get(attr) != null && node.get(attr).isBoolean()) {
				r = node.get(attr).asBoolean();
				break;
			}
		}
		if (r == null)
			r = defaultValue;
		return r;
	}

        @SuppressWarnings("CallToPrintStackTrace")
	public static Date getDate(JsonNode node, String[] attrs, Date defaultValue) {
		Date r = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");

		for (String attr : attrs) {
			if (node.has(attr) && node.get(attr).isTextual()) {
				try {
					r = sdf.parse(node.get(attr).asText());
				} catch (ParseException e) {
					e.printStackTrace();
				}

				break;
			}
		}

		if (r == null)
			r = defaultValue;

		return r;
	}

	public static JsonNode getJsonNode(JsonNode node, String[] attrs) {
		JsonNode r = null;
		for (String attr : attrs) {
			if (node.get(attr) != null) {
				r = node.get(attr);
				break;
			}
		}
		return r;
	}

	public static Cliente getCliente(JsonNode node, String[] attrs, IClienteBusiness clienteBusiness)
			throws BusinessException {
		JsonNode clienteNodo = getJsonNode(node, CLIENTE_OBJETO);
		if (clienteNodo != null) {
			String razonSocial = null;
			for (String attr : attrs) {
				if (clienteNodo.get(attr) != null) {
					razonSocial = clienteNodo.get(attr).asText();
					break;
				}
			}
			if (razonSocial != null) {
				Cliente cliente = EntityBuilder.buildCliente(clienteNodo);
				return clienteBusiness.add(cliente);
			} else {
				throw new IllegalArgumentException("El campo cliente no se recibió correctamente");
			}
		} else {
			throw new IllegalArgumentException("El nodo cliente no se recibió correctamente");
		}

	}

	public static Producto getProducto(JsonNode node, String[] attrs, IProductoBusiness productoBusiness)
			throws BusinessException{
		JsonNode productoNodo = getJsonNode(node, PRODUCTO_OBJETO);
		if (productoNodo != null) {
			String productCode = null;
			for (String attr : attrs) {
				if (productoNodo.get(attr) != null) {
					productCode = productoNodo.get(attr).asText();
					break;
				}
			}
			if (productCode != null) {
				Producto producto = EntityBuilder.buildProducto(productoNodo);
				return productoBusiness.add(producto);
			} else {
				throw new IllegalArgumentException("El campo producto no se recibió correctamente");
			}
		} else {
			throw new IllegalArgumentException("El nodo producto no se recibió correctamente");
		}

	}

	public static Chofer getChofer(JsonNode node, String[] attrs, IChoferBusiness choferBusiness)
        throws BusinessException {
		JsonNode choferNodo = getJsonNode(node, CHOFER_OBJETO);
		if (choferNodo != null) {
			Long documento = null;
			for (String attr : attrs) {
				JsonNode val = choferNodo.get(attr);
				if (val != null) {
					if (val.isNumber()) {
						documento = val.asLong(); 
						break;
					} else if (val.isTextual()) {
						try {
							documento = Long.valueOf(val.asText());
							break;
						} catch (NumberFormatException e) {
							throw new IllegalArgumentException("Documento del chofer no es un número válido");
						}
					}
				}
        }

        if (documento == null) {
            throw new IllegalArgumentException("El campo chofer no se recibió correctamente");
        }

        Chofer chofer = EntityBuilder.buildChofer(choferNodo);
        return choferBusiness.add(chofer);
    } else {
        throw new IllegalArgumentException("El nodo chofer no se recibió correctamente");
    }
}


	public static Camion getCamion(JsonNode node, String[] attrs, ICamionBusiness camionBusiness)
			throws  BusinessException {
		JsonNode camionNodo = getJsonNode(node, CAMION_OBJETO);
		if (camionNodo != null) {
			String patente = getString(camionNodo, attrs, null);
			if (patente != null) {
				JsonNode cisternasNode = camionNodo.get("cisternas");
				return camionBusiness.add(EntityBuilder.buildCamion(camionNodo, cisternasNode));
			} else {
				throw new IllegalArgumentException("El campo camion no se recibió correctamente");
			}
		} else {
			throw new IllegalArgumentException("El nodo camion no se recibió correctamente");
		}

	}

	public static float getValue(JsonNode node, String[] attrs, float defaultValue) {
		Float r = null;
		for (String attr : attrs) {
			if (node.get(attr) != null) {
				if (node.get(attr).isFloat() || node.get(attr).isDouble() || node.get(attr).isInt()) {
					r = node.get(attr).floatValue();
					break;
				}
			}
		}
		if (r == null)
			r = defaultValue;
		return r;
	}

}