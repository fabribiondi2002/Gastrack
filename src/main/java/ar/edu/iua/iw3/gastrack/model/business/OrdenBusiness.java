package ar.edu.iua.iw3.gastrack.model.business;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ar.edu.iua.iw3.gastrack.model.Detalle;
import ar.edu.iua.iw3.gastrack.model.Orden;
import ar.edu.iua.iw3.gastrack.model.Orden.Estado;
import ar.edu.iua.iw3.gastrack.model.business.exception.BadActivationPasswordException;
import ar.edu.iua.iw3.gastrack.model.business.exception.BusinessException;
import ar.edu.iua.iw3.gastrack.model.business.exception.FoundException;
import ar.edu.iua.iw3.gastrack.model.business.exception.InvalidOrderAttributeException;
import ar.edu.iua.iw3.gastrack.model.business.exception.NotFoundException;
import ar.edu.iua.iw3.gastrack.model.business.exception.OrderAlreadyAuthorizedToLoadException;
import ar.edu.iua.iw3.gastrack.model.business.exception.OrderAlreadyLockedToLoadException;
import ar.edu.iua.iw3.gastrack.model.business.exception.OrderInvalidStateException;
import ar.edu.iua.iw3.gastrack.model.business.intefaces.ICamionBusiness;
import ar.edu.iua.iw3.gastrack.model.business.intefaces.IChoferBusiness;
import ar.edu.iua.iw3.gastrack.model.business.intefaces.IClienteBusiness;
import ar.edu.iua.iw3.gastrack.model.business.intefaces.IDetalleBusiness;
import ar.edu.iua.iw3.gastrack.model.business.intefaces.IOrdenBusiness;
import ar.edu.iua.iw3.gastrack.model.business.intefaces.IProductoBusiness;
import ar.edu.iua.iw3.gastrack.model.deserializers.CierreOrdenDeserializer;
import ar.edu.iua.iw3.gastrack.model.deserializers.NOrdenJsonDeserializer;
import ar.edu.iua.iw3.gastrack.model.deserializers.NOrdenPassJsonDeserializer;
import ar.edu.iua.iw3.gastrack.model.deserializers.OrdenDeserializer;
import ar.edu.iua.iw3.gastrack.model.deserializers.TaraJsonDeserializer;
import ar.edu.iua.iw3.gastrack.model.persistence.OrdenRepository;
import ar.edu.iua.iw3.gastrack.model.serializers.DTO.ConciliacionDTO;
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

    @Autowired
    @Lazy
    private IDetalleBusiness detalleBusiness;

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
     *                           especificado
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
        ObjectMapper mapper = JsonUtils.getObjectMapper(Orden.class, new OrdenDeserializer(
                Orden.class, choferBusiness, clienteBusiness, camionBusiness, productoBusiness), null);
        Orden orden;

        try {
            orden = mapper.readValue(json, Orden.class);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().message("El formato JSON es incorrecto").build();
        }
        orden.setFechaRecepcionInicial(new Date());
        orden.setEstado(Estado.PENDIENTE_PESAJE_INICIAL);
        log.info("Estado cambiado a PENDIENTE_PESAJE_INICIAL");
        return add(orden);
    }

    /**
     * Habilita una orden para carga si la contrasena de activacion es correcta
     * 
     * @throws BadActivationPasswordException Si la contrasena de activacion es incorrecta o no tiene el formato valido
     * @throws NotFoundException Si no existe una orden con ese numero
     * @throws BusinessException Si ocurre un error no previsto
     * @throws OrderInvalidStateException Si la orden no se encuentra en el estado PESAJE_INICIAL_REGISTRADO
     * @throws OrderAlreadyAuthorizedToLoadException Si la orden ya se encuentra autorizada para carga
     */
    @Override
    public Orden habilitarOrdenParaCarga(String json)
            throws NotFoundException, BusinessException, BadActivationPasswordException, OrderInvalidStateException,
            OrderAlreadyAuthorizedToLoadException {

        ObjectMapper mapper = JsonUtils.getObjectMapper(Orden.class, new NOrdenPassJsonDeserializer(
                Orden.class), null);

        Orden nOrdenPassDTO;
        try {
            nOrdenPassDTO = mapper.readValue(json, Orden.class);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().build();
        }

        if (!ContrasenaActivacionUtiles.formatoDeConstrasenaValido(nOrdenPassDTO.getContrasenaActivacion()))
        {
            throw BadActivationPasswordException.builder().message("La contrasena de activacion no tiene formato valido").build();
        }
        
        
        Orden orden = loadByNumeroOrden(nOrdenPassDTO.getNumeroOrden());
        
        
        if (!orden.getEstado().equals(Orden.Estado.PESAJE_INICIAL_REGISTRADO)) {
            throw OrderInvalidStateException.builder()
                    .message("La orden numero " + orden.getNumeroOrden()
                            + " no se encuentra en estado PESAJE_INICIAL_REGISTRADO")
                    .build();
        }

        if(orden.getCargaHabilitada().equals(true)) {
            throw OrderAlreadyAuthorizedToLoadException.builder()
                    .message("La orden numero " + orden.getNumeroOrden() + " ya se encuentra autorizada para carga")
                    .build();
        }
        


        if (!orden.getContrasenaActivacion().equals(nOrdenPassDTO.getContrasenaActivacion())) {
            throw BadActivationPasswordException.builder().message("La contrasena de activacion es incorrecta").build();
        }

        orden.setCargaHabilitada(true);

        return update(orden);
    }

    /**
     * Registra la tara (peso inicial) de una orden a partir de un JSON recibido.
     *
     * @param json Cadena JSON con los datos de la tara.
     * @return Contraseña de activación generada para la orden.
     * @throws NotFoundException Si la orden no existe.
     * @throws InvalidOrderAttributeException Si los datos del JSON son inválidos.
     * @throws OrderInvalidStateException Si la orden no está en un estado válido para registrar la tara.
     * @throws BusinessException Si ocurre un error interno durante el proceso.
     */
    @Override
    public String registrarTara(String json) 
        throws NotFoundException, BusinessException, InvalidOrderAttributeException, OrderInvalidStateException {

        ObjectMapper mapper = JsonUtils.getObjectMapper(Orden.class, new TaraJsonDeserializer(Orden.class), null);
        Orden tara = null;

        try {
            tara = mapper.readValue(json, Orden.class);
        }catch(Exception e){
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }

        if (tara.getPesoInicial() == 0.0) {
            throw InvalidOrderAttributeException.builder().message("valor de peso inicial invalido").build();
        }

        Orden orden = loadByNumeroOrden(tara.getNumeroOrden());
        
        if (!orden.getEstado().equals(Estado.PENDIENTE_PESAJE_INICIAL)) {
            throw OrderInvalidStateException.builder().message("estado de orden "+ orden.getEstado()+" invalido").build();
        }

        // Registra valores de pesaje inicial
        orden.setPesoInicial(tara.getPesoInicial());
        orden.setFechaPesajeInicial(new Date());
        
        orden.setContrasenaActivacion(ContrasenaActivacionUtiles.generarContrasena());

        // Cambia estado
        orden.siguienteEstado();

        update(orden);
        return orden.getContrasenaActivacion();
    }

    /**
     * Deshabilita la carga de una orden
     * @throws NotFoundException Si no existe una orden con ese numero
     * @throws BusinessException Si ocurre un error no previsto
     * @throws OrderInvalidStateException Si la orden no se encuentra en el estado PESAJE_INICIAL_REGISTRADO
     * @throws OrderAlreadyLockedToLoadException Si la orden ya se encuentra bloqueada para carga
     */
    @Override
    public Orden deshabilitarOrdenParaCarga(String json) throws NotFoundException, BusinessException, OrderInvalidStateException, OrderAlreadyLockedToLoadException {
         
        ObjectMapper mapper = JsonUtils.getObjectMapper(Orden.class, new NOrdenJsonDeserializer(
                Orden.class), null);

        Orden orden;
        try {
            orden = mapper.readValue(json, Orden.class);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().build();
        }
        
        orden = loadByNumeroOrden(orden.getNumeroOrden());
        
        
        if (!orden.getEstado().equals(Orden.Estado.PESAJE_INICIAL_REGISTRADO)) {
            throw OrderInvalidStateException.builder()
                    .message("La orden numero " + orden.getNumeroOrden() + " no se encuentra en estado PESAJE_INICIAL_REGISTRADO")
                    .build();
        }

        if(orden.getCargaHabilitada().equals(false)) {
            throw OrderAlreadyLockedToLoadException.builder()
                    .message("La orden numero " + orden.getNumeroOrden() + " ya se encuentra bloqueada para carga")
                    .build();
        }
        

        orden.setCargaHabilitada(false);
        orden.siguienteEstado();
        orden.setFechaCierreOrdenParaCarga(new Date());
        return update(orden);

    }
    /*
     * Registrar el cierre de una orden
     * @param json Json con el numero de orden y el peso final
     * @return Orden actualizada
     * @throws NotFoundException Si no se encuentra la orden
     * @throws BusinessException Si ocurre un error no previsto
     * @throws OrderInvalidStateException Si la orden no se encuentra en estado
     *         ORDEN_CERRADA_PARA_CARGA
     * @throws InvalidOrderAttributeException Si el peso final es menor o igual al
     *         peso inicial
    */
    @Override
    public Orden registrarCierreOrden(String json)
            throws NotFoundException, BusinessException, OrderInvalidStateException, InvalidOrderAttributeException {

        ObjectMapper mapper = JsonUtils.getObjectMapper(Orden.class, new CierreOrdenDeserializer(Orden.class), null);
        Orden pesajeFinal = null;
        try {
            pesajeFinal = mapper.readValue(json, Orden.class);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
        if (pesajeFinal.getPesoFinal() <= 0.0) {
            throw InvalidOrderAttributeException.builder().message("valor de peso final invalido").build();
        }
        Orden orden = loadByNumeroOrden(pesajeFinal.getNumeroOrden());
        if (orden.getEstado() != Estado.ORDEN_CERRADA_PARA_CARGA) {
            throw OrderInvalidStateException.builder()
                    .message("La orden numero " + orden.getNumeroOrden()
                            + " no se encuentra en estado ORDEN_CERRADA_PARA_CARGA")
                    .build();
        }
        if (pesajeFinal.getPesoFinal() <= orden.getPesoInicial()) {
            throw InvalidOrderAttributeException.builder().message("valor de peso final es menor al del peso inicial").build();
        }
        Detalle primerDetalle;
        try {
            primerDetalle = detalleBusiness.getFirstDetailByOrderId(orden.getId());
        } catch (NotFoundException e) {
            log.error(e.getMessage(), e);
            throw NotFoundException.builder()
                    .message("No se encontraron detalles para la orden de numero:" + orden.getNumeroOrden()).build();
        }
        orden.setFechaPrimerMedicion(primerDetalle.getFecha());

        Detalle ultimoDetalle;
        try {
            ultimoDetalle = detalleBusiness.getLastDetailByOrderId(orden.getId());
        } catch (NotFoundException e) {
            log.error(e.getMessage(), e);
            throw NotFoundException.builder()
                    .message("No se encontraron detalles para la orden de numero:" + orden.getNumeroOrden()).build();
        }
        double pesoFinal = pesajeFinal.getPesoFinal();
        orden.setPesoFinal(pesoFinal);
        orden.setFechaPesajeFinal(new java.util.Date());

        Map<String, Double> promedios;
        promedios = detalleBusiness.loadAverageDetails(orden.getId());
        orden.setPromedioCaudal(promedios.get("promedioCaudal"));
        orden.setPromedioDensidad(promedios.get("promedioDensidad"));
        orden.setPromedioTemperatura(promedios.get("promedioTemperatura"));

        orden.setUltimaMasaAcumulada(ultimoDetalle.getMasaAcumulada());
        orden.setUltimaDensidad(ultimoDetalle.getDensidad());
        orden.setUltimaTemperatura(ultimoDetalle.getTemperatura());
        orden.setUltimoCaudal(ultimoDetalle.getCaudal());
        orden.setFechaUltimoMedicion(ultimoDetalle.getFecha());
        orden.siguienteEstado();
        return ordenDAO.save(orden);
    }
    /*
     * Crear la conciliacion de una orden
     * @param numeroOrden Numero de la orden
     * @return ConciliacionDTO
     * @throws NotFoundException Si no se encuentra la orden
     * @throws BusinessException Si ocurre un error no previsto
     * @throws OrderInvalidStateException Si la orden no se encuentra en estado FINALIZADO
     */
    @Override
    public ConciliacionDTO crearConciliacion(long numeroOrden)
            throws NotFoundException, BusinessException, OrderInvalidStateException {
        ConciliacionDTO conciliacionDTO = new ConciliacionDTO();
        Orden orden;
        
        try {
            orden = loadByNumeroOrden(numeroOrden);
        } catch (NotFoundException e) {
            log.error(e.getMessage(), e);
            throw NotFoundException.builder().message("No se encuentra la orden de numero:" + numeroOrden).build();
        }
        if (orden.getEstado() != Estado.FINALIZADO) {
            throw OrderInvalidStateException.builder()
                    .message("La orden numero " + orden.getNumeroOrden() + " no se encuentra en estado FINALIZADO")
                    .build();
        }
        conciliacionDTO.setPesajeInicial(orden.getPesoInicial());
        conciliacionDTO.setPesajeFinal(orden.getPesoFinal());
        conciliacionDTO.setProductoCargado(orden.getUltimaMasaAcumulada());
        conciliacionDTO.setNetoBalanza(orden.getPesoFinal() - orden.getPesoInicial());
        conciliacionDTO.setDifBalanzaCaudalimentro(conciliacionDTO.getNetoBalanza() - conciliacionDTO.getProductoCargado());
        conciliacionDTO.setPromedioCaudal(orden.getPromedioCaudal());
        conciliacionDTO.setPromedioTemperatura(orden.getPromedioTemperatura());
        conciliacionDTO.setPromedioDensidad(orden.getPromedioDensidad());

        return conciliacionDTO;
    }

}
