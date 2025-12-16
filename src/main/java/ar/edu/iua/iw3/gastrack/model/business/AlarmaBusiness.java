package ar.edu.iua.iw3.gastrack.model.business;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ar.edu.iua.iw3.gastrack.auth.UserBusiness;
import ar.edu.iua.iw3.gastrack.model.Alarma;
import ar.edu.iua.iw3.gastrack.model.Alarma.TipoAlarma;
import ar.edu.iua.iw3.gastrack.model.business.exception.BusinessException;
import ar.edu.iua.iw3.gastrack.model.business.exception.FoundException;
import ar.edu.iua.iw3.gastrack.model.business.exception.NotFoundException;
import ar.edu.iua.iw3.gastrack.model.business.intefaces.IAlarmaBusiness;
import ar.edu.iua.iw3.gastrack.model.deserializers.AlarmaDeserializer;
import ar.edu.iua.iw3.gastrack.model.deserializers.DTO.AlarmaDTO;
import ar.edu.iua.iw3.gastrack.model.persistence.AlarmaRepository;
import ar.edu.iua.iw3.gastrack.util.JsonUtils;
import ar.edu.iua.iw3.gastrack.websocket.service.AlarmasWebSocketService;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AlarmaBusiness implements IAlarmaBusiness {

    @Autowired
    private AlarmaRepository alarmaDAO;

    @Autowired
    private UserBusiness userBusiness;

    @Autowired
    private AlarmasWebSocketService alarmasWS;

    @Override
    public List<Alarma> list() throws BusinessException {

        try {

            return alarmaDAO.findAll();
        } catch (Exception e) {
            throw BusinessException.builder().ex(e).message(e.getMessage()).build();
        }
    }

    @Override
    public Alarma add(Alarma alarma) throws BusinessException, FoundException {
        try {
            Alarma prev = loadByOrdenAndTipo(alarma.getOrden().getNumeroOrden(), alarma.getTipoAlarma());
            if (!prev.isAceptada()) {
                throw FoundException.builder().message("Ya existe una alarma igual que aún no fue aceptada").build();
            }
            return alarmaDAO.save(alarma);

        } catch (NotFoundException e) {
        }
        try {
            return alarmaDAO.save(alarma);

        } catch (Exception ex) {
            throw BusinessException.builder().ex(ex).message(ex.getMessage()).build();
        }
    }

    @Override
    public Alarma loadByOrdenAndTipo(long numeroOrden, TipoAlarma tipoAlarma)
            throws BusinessException, NotFoundException {
        Optional<Alarma> alarma;
        try {
            alarma = alarmaDAO
                    .findTopByOrden_NumeroOrdenAndTipoAlarmaOrderByFechaEmisionDesc(numeroOrden, tipoAlarma);

        } catch (Exception e) {
            throw BusinessException.builder().ex(e).message(e.getMessage()).build();
        }
        if (alarma.isPresent()) {
            return alarma.get();
        } else {
            throw NotFoundException.builder().message("No hay alarmas de ese tipo registradas").build();
        }
    }

    @Override
    public Alarma aceptarAlarma(String json)
            throws BusinessException, NotFoundException {

        ObjectMapper mapper = JsonUtils.getObjectMapper(AlarmaDTO.class, new AlarmaDeserializer(AlarmaDTO.class), null);
        AlarmaDTO alarmaDTO;

        try {
            alarmaDTO = mapper.readValue(json, AlarmaDTO.class);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().message("El formato JSON es incorrecto").build();
        }

        Alarma alarma = loadByOrdenAndTipo(alarmaDTO.getNumeroOrden(), alarmaDTO.getTipoAlarma());
        alarma.setAceptada(true);
        alarma.setFechaAceptacion(new Date());
        alarma.setObservacion(alarmaDTO.getObservacion());
        alarma.setUsuario(userBusiness.load(alarmaDTO.getUsermail()));
        try {

            return alarmaDAO.save(alarma);
        } catch (Exception e) {
            throw BusinessException.builder().ex(e).message(e.getMessage()).build();
        }
    }

    @Override
    public List<Alarma> loadNoAceptadas() throws BusinessException {
        try {
            List<Alarma> alarmas = alarmaDAO.findByAceptadaFalse();

            return alarmas;
        } catch (Exception e) {
            throw BusinessException.builder().ex(e).message(e.getMessage()).build();
        }

    }

}
