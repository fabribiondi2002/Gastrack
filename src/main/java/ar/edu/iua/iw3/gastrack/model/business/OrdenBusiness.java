package ar.edu.iua.iw3.gastrack.model.business;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.edu.iua.iw3.gastrack.model.Orden;
import ar.edu.iua.iw3.gastrack.model.Orden.Estado;
import ar.edu.iua.iw3.gastrack.model.business.exception.BusinessException;
import ar.edu.iua.iw3.gastrack.model.business.exception.FoundException;
import ar.edu.iua.iw3.gastrack.model.business.exception.NotFoundException;
import ar.edu.iua.iw3.gastrack.model.business.intefaces.IOrdenBusiness;
import ar.edu.iua.iw3.gastrack.model.persistence.OrdenRepository;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.concurrent.ThreadLocalRandom;

@Service
@Slf4j
public class OrdenBusiness implements IOrdenBusiness {

    @Autowired
    private OrdenRepository ordenDAO;


    /**
     * Listar todas las ordenes por estado
     * @param status Estado de las ordenes a listar
     * @return Lista de ordenes
     * @throws BusinessException Si ocurre un error no previsto
     * @throws NotFoundException Si no se encuentran ordenes con el estado especificado
     */
    @Override
    public List<Orden> listByStatus(Orden.Estado status) throws BusinessException, NotFoundException {
        Optional<List<Orden>> o;
        try {
            o = ordenDAO.findAllByEstado(status);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
        if (o.isEmpty()) {
            throw NotFoundException.builder().message("No se encuentran ordenes con estado:" + status).build();
        }
        return o.get();
    }

    /**
    * Busca una orden por su numero
    * 
    * @return orden cagada
    * @throws BusinessException Si ocurre un error no previsto
    * @throws NotFoundException Si no existe una orden con ese numero
    */
    @Override
    public Orden load(long id) throws NotFoundException, BusinessException {
        Optional<Orden> o;
        try {
            o = ordenDAO.findById(id);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
        if (o.isEmpty()) {
            throw NotFoundException.builder().message("No se encuentra la orden de numero:" + id).build();
        }
        return o.get();
    }

    @Override
    public Orden loadByNumeroOrden(long numeroOrden) throws NotFoundException, BusinessException {
        Optional<Orden> o;
        try {
            o = ordenDAO.findByNumeroOrden(numeroOrden);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
        if (o.isEmpty()) {
            throw NotFoundException.builder().message("No se encuentra la orden de numero:" + numeroOrden).build();
        }
        return o.get();
    }

    /**
     * Añade una orden
     * 
     * @return orden añadida
     * @throws BusinessException Si ocurre un error no previsto
     * @throws FoundException Si ya existe una orden con ese numero, uno con el id o uno con el codigo externo
     */
    @Override
    public Orden add(Orden orden) throws FoundException, BusinessException {
        
        try {
            load(orden.getId());
            throw FoundException.builder().message("Se encontró la orden de numero" + orden.getId()).build();
        } catch (NotFoundException e) {

        }
        try {
            loadByNumeroOrden(orden.getNumeroOrden());
            throw FoundException.builder().message("Se encontró la orden de numero" + orden.getNumeroOrden()).build();
        } catch (NotFoundException e) {
            
        }
        try {
            loadByCodigoExterno(orden.getCodigoExterno());
            throw FoundException.builder().message("Se encontró la orden de codigo externo" + orden.getCodigoExterno()).build();
        } catch (NotFoundException e) {

        }
        try {
            return ordenDAO.save(orden);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
    }
    
    
     /**
     * Obtener un orden por id
     * 
     * @param id Id del orden
     * @return Orden
     * @throws NotFoundException Si no existe la orden a actualizar
     * @throws BusinessException Si ocurre un error no previsto
     */
    @Override
    public Orden update(Orden orden) throws NotFoundException, BusinessException {
        load(orden.getId());
		try {
			return ordenDAO.save(orden);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw BusinessException.builder().ex(e).build();
		}
        
    }

    /**
     * Eliminar una orden por id
     * 
     * @param id Id de la orden
     * @throws NotFoundException Si no existe la orden a eliminar
     * @throws BusinessException Si ocurre un error no previsto
     */
    @Override
    public void delete(long id) throws NotFoundException, BusinessException {
        try {
            load(id);
        } catch (NotFoundException e) {
            throw NotFoundException.builder().message("No se encuentra la orden id=" + id).build();

        }
        try {
            ordenDAO.deleteById(id);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
    }

    /**
     * Obtener una orden por su codigo externo
     * @param codigoExterno Codigo externo de la orden
     * @return Orden cargada
     * @throws NotFoundException Si no existe una orden con ese codigo externo
     * @throws BusinessException Si ocurre un error no previsto
     */

    @Override
    public Orden loadByCodigoExterno(String codigoExterno) throws NotFoundException, BusinessException {
        Optional<Orden> r;
		try {
			r = ordenDAO.findByCodigoExterno(codigoExterno);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw BusinessException.builder().ex(e).build();
		}
		if (r.isEmpty()) {
			throw NotFoundException.builder().message("No se encuentra la Orden con codigo=" + codigoExterno).build();
		}
		return r.get();
    }

    /*
     * Registra los objetos mandados en el Json al registrar las ordenes
     * 
     */

    @Override
    public Orden addExternal(String json) throws FoundException, BusinessException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'addExternal'");
    }

    /**
     * Registra el pesaje inicial (tara) para una orden identificada por su id.
     *
     * @param id identificador interno de la orden.
     * @param pesoInicial peso del camion vacio (tara)
     * @return orden actualizada luego del registro de la tara.
     * @throws NotFoundException si no existe una orden con el id indicado.
     * @throws BusinessException si la orden no esta en el estado esperado o ocurre
     *         algun error no previsto
     */
    @Override
    public Orden registrarTara(long numeroOrden, double pesoInicial) throws NotFoundException, BusinessException {
        try {
            Optional<Orden> or = ordenDAO.findByNumeroOrden(numeroOrden);
            if (!or.isPresent()) {
                throw NotFoundException.builder().message("No se encontró la orden numero " + numeroOrden ).build();
            }

            Orden orden = or.get();

            // Valida estado actual
            validarEstado(orden, Estado.PENDIENTE_PESAJE_INICIAL);

            // Registra valores de pesaje inicial
            orden.setPesoInicial(pesoInicial);
            orden.setFechaPesajeInicial(new Date());

            orden.setContrasenaActivacion(generarContrasenaActivacion());

            // Cambia estado
            orden.siguienteEstado();
            return ordenDAO.save(orden);

        } catch (NotFoundException | BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).message("Error al registrar la tara").build();
        }
    }

    /*
     * Valida el estado actual de la orden
     */
    private void validarEstado(Orden orden, Estado... estadosValidos) throws BusinessException {
        for (Estado e : estadosValidos) {
            if (orden.getEstado() == e) return;
        }
        StringBuilder sb = new StringBuilder();
        for (Estado e : estadosValidos) sb.append(e.name()).append(" ");
        throw BusinessException.builder()
                .message("Estado inválido. Estado actual: "
                        + (orden.getEstado() != null ? orden.getEstado().name() : "NULL")
                        + ". Estados válidos: " + sb.toString().trim())
                .build();
    }

    /*
     * genera una contraseña de activacion de 5 digitos
     * @return contraseña como entero
     */
    private int generarContrasenaActivacion() {
        return ThreadLocalRandom.current().nextInt(10000, 100000);
    }

}
