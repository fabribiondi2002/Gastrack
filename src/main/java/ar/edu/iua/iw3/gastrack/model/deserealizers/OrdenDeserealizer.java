package ar.edu.iua.iw3.gastrack.model.deserealizers;

import java.io.IOException;
import java.util.Date;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import ar.edu.iua.iw3.gastrack.model.Camion;
import ar.edu.iua.iw3.gastrack.model.Chofer;
import ar.edu.iua.iw3.gastrack.model.Cliente;
import ar.edu.iua.iw3.gastrack.model.Orden;
import ar.edu.iua.iw3.gastrack.model.Orden.Estado;
import ar.edu.iua.iw3.gastrack.model.Producto;
import ar.edu.iua.iw3.gastrack.model.business.exception.BusinessException;
import ar.edu.iua.iw3.gastrack.model.business.intefaces.ICamionBusiness;
import ar.edu.iua.iw3.gastrack.model.business.intefaces.IChoferBusiness;
import ar.edu.iua.iw3.gastrack.model.business.intefaces.IClienteBusiness;
import ar.edu.iua.iw3.gastrack.model.business.intefaces.IProductoBusiness;
import static ar.edu.iua.iw3.gastrack.util.JsonAtributesConstants.CODIGO_EXTERNO_ORDEN;
import static ar.edu.iua.iw3.gastrack.util.JsonAtributesConstants.DOCUMENTO_CHOFER;
import static ar.edu.iua.iw3.gastrack.util.JsonAtributesConstants.FECHA_CARGA_PREVISTA_ORDEN;
import static ar.edu.iua.iw3.gastrack.util.JsonAtributesConstants.NOMBRE_PRODUCTO;
import static ar.edu.iua.iw3.gastrack.util.JsonAtributesConstants.NUMERO_ORDEN;
import static ar.edu.iua.iw3.gastrack.util.JsonAtributesConstants.PATENTE_CAMION;
import static ar.edu.iua.iw3.gastrack.util.JsonAtributesConstants.PRESET_ORDEN;
import static ar.edu.iua.iw3.gastrack.util.JsonAtributesConstants.RAZON_SOCIAL_CLIENTE;
import ar.edu.iua.iw3.gastrack.util.JsonUtils;
/**
 * Deserealizador de Ordenes
 * @see Orden
 */
public class OrdenDeserealizer extends StdDeserializer<Orden> {

	protected OrdenDeserealizer(Class<?> vc) {
		super(vc);
	}

	private IChoferBusiness choferBusiness;
	private IClienteBusiness clienteBusiness;
	private ICamionBusiness camionBusiness;
	private IProductoBusiness productoBusiness;

	public OrdenDeserealizer(Class<?> vc, IChoferBusiness choferBusiness, IClienteBusiness clienteBusiness,
			ICamionBusiness camionBusiness, IProductoBusiness productoBusiness) {
		super(vc);
		this.choferBusiness = choferBusiness;
		this.clienteBusiness = clienteBusiness;
		this.camionBusiness = camionBusiness;
		this.productoBusiness = productoBusiness;
	}
	
	@Override
	public Orden deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
		Orden r = new Orden();
		JsonNode node = jp.getCodec().readTree(jp);
		Long numeroOrden = JsonUtils.getLong(node, NUMERO_ORDEN, null);
		Double preset = JsonUtils.getDouble(node, PRESET_ORDEN, null);
		Date fechaCargaPrevista = JsonUtils.getDate(node, FECHA_CARGA_PREVISTA_ORDEN, null);
		String codigoExterno = JsonUtils.getString(node, CODIGO_EXTERNO_ORDEN, null);

		if (fechaCargaPrevista == null) {
			throw new IllegalArgumentException(
					"El campo 'fecha_carga_prevista' es obligatorio y no puede estar vacío.");
		}
		if (numeroOrden == null) {
			throw new IllegalArgumentException("El campo 'numero_orden' es obligatorio y no puede estar vacío.");
		}
		if (preset <= 0) {
			throw new IllegalArgumentException("El campo 'preset' es obligatorio y debe ser mayor que cero.");
		}

		r.setNumeroOrden(numeroOrden);
		r.setPreset(preset);
		r.setFechaCargaPrevista(fechaCargaPrevista);
		r.setCodigoExterno(codigoExterno);
		r.setEstado(Estado.PENDIENTE_PESAJE_INICIAL);

		Cliente cliente;
		Producto producto;
		Camion camion;
		Chofer chofer;
		try {
			cliente = JsonUtils.getCliente(node, RAZON_SOCIAL_CLIENTE, clienteBusiness);
			producto = JsonUtils.getProducto(node, NOMBRE_PRODUCTO, productoBusiness);
			camion = JsonUtils.getCamion(node, PATENTE_CAMION, camionBusiness);
			chofer = JsonUtils.getChofer(node, DOCUMENTO_CHOFER, choferBusiness);
		} catch (BusinessException e) {
			throw new IOException("Error resolving related entities", e);
		}

		
		r.setCamion(camion);
		r.setChofer(chofer);
		r.setCliente(cliente);
		r.setProducto(producto);

		return r;

	}
}