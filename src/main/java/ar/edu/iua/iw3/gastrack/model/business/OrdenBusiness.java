package ar.edu.iua.iw3.gastrack.model.business;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ar.edu.iua.iw3.gastrack.model.Orden;
import ar.edu.iua.iw3.gastrack.model.business.exception.BadActivationPasswordException;
import ar.edu.iua.iw3.gastrack.model.business.exception.BusinessException;
import ar.edu.iua.iw3.gastrack.model.business.exception.FoundException;
import ar.edu.iua.iw3.gastrack.model.business.exception.NotFoundException;
import ar.edu.iua.iw3.gastrack.model.business.exception.OrderAlreadyAuthorizedToLoadException;
import ar.edu.iua.iw3.gastrack.model.business.exception.OrderInvalidStateException;
import ar.edu.iua.iw3.gastrack.model.business.intefaces.ICamionBusiness;
import ar.edu.iua.iw3.gastrack.model.business.intefaces.IChoferBusiness;
import ar.edu.iua.iw3.gastrack.model.business.intefaces.IClienteBusiness;
import ar.edu.iua.iw3.gastrack.model.business.intefaces.IOrdenBusiness;
import ar.edu.iua.iw3.gastrack.model.business.intefaces.IProductoBusiness;
import ar.edu.iua.iw3.gastrack.model.deserealizers.OrdenDeserealizer;
import ar.edu.iua.iw3.gastrack.model.deserializers.NOrdenPassJsonDeserializer;
import ar.edu.iua.iw3.gastrack.model.deserializers.DTO.NOrdenPassDTO;
import ar.edu.iua.iw3.gastrack.model.persistence.OrdenRepository;
import ar.edu.iua.iw3.gastrack.util.ContrasenaActivacionUtiles;
import ar.edu.iua.iw3.gastrack.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class OrdenBusiness implements IOrdenBusiness {

    @Autowired
    private OrdenRepository ordenDAO;

    @Autowired
    private IClienteBusiness clienteBusiness;

    @Autowired
    private IChoferBusiness choferBusiness;
    @Autowired
    private ICamionBusiness camionBusiness;
    @Autowired
    private IProductoBusiness productoBusiness;

    /**
     * Listar todas las ordenes por estado
     * 
     * @param status Estado de las ordenes a listar
     * 
     * @return Lista de ordenes
     * 
     * @throws BusinessException Si ocurre un error no previsto
     * 
     * @throws NotFoundException Si no se encuentran ordenes con el estado
     * especificado
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
     * @throws FoundException    Si ya existe una orden con ese numero, uno con el
     *                           id o uno con el codigo externo
     */

@Transactional(rollbackFor = Exception.class)
    @Override
    public Orden add(Orden orden) throws FoundException, BusinessException {
        if (orden.getCodigoExterno() != null) {
            Optional<Orden> existente = ordenDAO.findByCodigoExterno(orden.getCodigoExterno());
            if (existente.isPresent()) {
                throw FoundException.builder()
                        .message("Se encontró la orden de codigo externo " + orden.getCodigoExterno())
                        .build();
            }
        }
        try {
            loadByNumeroOrden(orden.getNumeroOrden());
            throw FoundException.builder().message("Se encontró la orden de numero" + orden.getNumeroOrden()).build();
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
     * 
     * @return Orden
     * 
     * @throws NotFoundException Si no existe la orden a actualizar
     * 
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
     * 
     * @throws NotFoundException Si no existe la orden a eliminar
     * 
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
     * 
     * @param codigoExterno Codigo externo de la orden
     * 
     * @return Orden cargada
     * 
     * @throws NotFoundException Si no existe una orden con ese codigo externo
     * 
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
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Orden addOrdenCompleta(String json) throws FoundException, BusinessException {
        ObjectMapper mapper = JsonUtils.getObjectMapper(Orden.class, new OrdenDeserealizer(
                Orden.class, choferBusiness, clienteBusiness, camionBusiness, productoBusiness), null);
        Orden orden;

        try {
            orden = mapper.readValue(json, Orden.class);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().message("El formato JSON es incorrecto").build();
        }

        return add(orden);
    }

    @Override
    public Orden habilitarOrdenParaCarga(String json)
            throws NotFoundException, BusinessException, BadActivationPasswordException, OrderInvalidStateException, OrderAlreadyAuthorizedToLoadException {

        ObjectMapper mapper = JsonUtils.getObjectMapper(NOrdenPassDTO.class, new NOrdenPassJsonDeserializer(
                NOrdenPassDTO.class), null);

        NOrdenPassDTO nOrdenPassDTO;
        try {
            nOrdenPassDTO = mapper.readValue(json, NOrdenPassDTO.class);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().build();
        }

        Orden orden = loadByNumeroOrden(nOrdenPassDTO.getNumeroOrden());

        if(orden.getCargaHabilitada().equals(true)) {
            throw OrderAlreadyAuthorizedToLoadException.builder()
                    .message("La orden numero " + orden.getNumeroOrden() + " ya se encuentra autorizada para carga")
                    .build();
        }
        
        if (!orden.getEstado().equals(Orden.Estado.PESAJE_INICIAL_REGISTRADO)) {
            throw OrderInvalidStateException.builder()
                    .message("La orden numero " + orden.getNumeroOrden() + " no se encuentra en estado PESAJE_INICIAL_REGISTRADO")
                    .build();
        }

        if (!ContrasenaActivacionUtiles.formatoDeConstrasenaValido(nOrdenPassDTO.getContrasenaActivacion()))
        {
            throw BadActivationPasswordException.builder().message("La contrasena de activacion no tiene formato valido").build();
        }

        if (!orden.getContrasenaActivacion().equals(nOrdenPassDTO.getContrasenaActivacion()))
        {
            throw BadActivationPasswordException.builder().message("La contrasena de activacion es incorrecta").build();
        }

        orden.setCargaHabilitada(true);
        
        return update(orden);
    }
}
