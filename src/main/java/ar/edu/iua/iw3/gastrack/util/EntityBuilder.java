package ar.edu.iua.iw3.gastrack.util;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.databind.JsonNode;

import ar.edu.iua.iw3.gastrack.model.Camion;
import ar.edu.iua.iw3.gastrack.model.Chofer;
import ar.edu.iua.iw3.gastrack.model.Cisterna;
import ar.edu.iua.iw3.gastrack.model.Cliente;
import ar.edu.iua.iw3.gastrack.model.Producto;
import static ar.edu.iua.iw3.gastrack.util.JsonAtributesConstants.MAIL_CLIENTE;
import static ar.edu.iua.iw3.gastrack.util.JsonAtributesConstants.RAZON_SOCIAL_CLIENTE;
/**
 * Clase constructora de entidades a partir de nodos JSON
 * 
 * @author Leandro Biondi
 * @author Benjamin Vargas
 * @author Antonella Badami
 * @version 1.0
 * @since 2025-10-26
 */
public class EntityBuilder {
    public static Cliente buildCliente(JsonNode nodoCliente) throws IllegalArgumentException {
        Cliente cliente = new Cliente();

        String razonSocial = JsonUtils.getString(nodoCliente, RAZON_SOCIAL_CLIENTE, "");
        if (razonSocial != null && !razonSocial.isEmpty()) {
            cliente.setRazonSocial(razonSocial);
        } else {
            throw new IllegalArgumentException("El nombre del cliente no puede ser nulo o vacío");
        }

        String email = JsonUtils.getString(nodoCliente, MAIL_CLIENTE, "");
        cliente.setEmail(email);

        return cliente;
    }

    public static Camion buildCamion(JsonNode nodoCamion, JsonNode nodoCisterna) throws IllegalArgumentException {
        Camion camion = new Camion();

        String patente = JsonUtils.getString(nodoCamion, JsonAtributesConstants.PATENTE_CAMION, "");
        if (patente != null && !patente.isEmpty()) {
            camion.setPatente(patente);
        } else {
            throw new IllegalArgumentException("La patente del camión no puede ser nula o vacía");
        }

        String descripcion = JsonUtils.getString(nodoCamion, JsonAtributesConstants.DESCRIPCION_CAMION, "");
        camion.setDescripcion(descripcion);

        Set<Cisterna> cisternas = new HashSet<>();
        if (nodoCisterna !=null && nodoCisterna.isArray()) {
            for (JsonNode cisternaNode : nodoCisterna) {
                Cisterna cisterna = new Cisterna();

                Long numero = JsonUtils.getLong(cisternaNode, JsonAtributesConstants.NUMERO_CISTERNA, null);
                if (numero != null && numero > 0) {
                    cisterna.setNumero(numero);
                } else {
                    throw new IllegalArgumentException("El número de la cisterna no puede ser nulo o negativo");
                }

                double volumen = JsonUtils.getValue(cisternaNode, JsonAtributesConstants.VOLUMEN_CISTERNA, 0);
                if ( volumen > 0) {
                    cisterna.setVolumen(volumen);
                } else {
                    throw new IllegalArgumentException("El volumen de la cisterna no puede ser nulo o negativo");
                }
                cisterna.setCamion(camion);
                cisternas.add(cisterna);
            }
        }

        camion.setCisternas(cisternas);
        return camion;
    }

    public static Producto buildProducto(JsonNode nodoProducto) throws IllegalArgumentException {
        Producto producto = new Producto();

        String nombre = JsonUtils.getString(nodoProducto, JsonAtributesConstants.NOMBRE_PRODUCTO, "");
        if (nombre != null && !nombre.isEmpty()) {
            producto.setNombre(nombre);
        } else {
            throw new IllegalArgumentException("El nombre del producto no puede ser nulo o vacío");
        }

        String descripcion = JsonUtils.getString(nodoProducto, JsonAtributesConstants.DESCRIPCION_PRODUCTO, "");
        producto.setDescripcion(descripcion);

        return producto;
    }
    public static Chofer buildChofer(JsonNode nodoChofer) throws IllegalArgumentException {
        Chofer chofer = new Chofer();

        String nombre = JsonUtils.getString(nodoChofer, JsonAtributesConstants.NOMBRE_CHOFER, "");
        if (nombre != null && !nombre.isEmpty()) {
            chofer.setNombre(nombre);
        } else {
            throw new IllegalArgumentException("El nombre del chofer no puede ser nulo o vacío");
        }

        String apellido = JsonUtils.getString(nodoChofer, JsonAtributesConstants.APELLIDO_CHOFER, "");
        if (apellido != null && !apellido.isEmpty()) {
            chofer.setApellido(apellido);
        } else {
            throw new IllegalArgumentException("El apellido del chofer no puede ser nulo o vacío");
        }

        Long documento = JsonUtils.getLong(nodoChofer, JsonAtributesConstants.DOCUMENTO_CHOFER, null);
        if (documento != null) {
            chofer.setDocumento(documento);
        } else {
            throw new IllegalArgumentException("El documento del chofer no puede ser nulo");
        }

        return chofer;
    }
}
