package ar.edu.iua.iw3.gastrack.websocket.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import ar.edu.iua.iw3.gastrack.model.Alarma;
import ar.edu.iua.iw3.gastrack.model.business.AlarmaBusiness;
import ar.edu.iua.iw3.gastrack.model.business.exception.BusinessException;
import ar.edu.iua.iw3.gastrack.model.business.exception.NotFoundException;
import ar.edu.iua.iw3.gastrack.websocket.dto.AlarmaDTO;
import ar.edu.iua.iw3.gastrack.websocket.dto.RespuestaAlarma;
import ar.edu.iua.iw3.gastrack.websocket.service.AlarmasWebSocketService;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class RespuestaAlarmaController {

    @Autowired
    AlarmaBusiness alarmaBusiness;

    @Autowired
    AlarmasWebSocketService alarmasWebSocketService;

    @MessageMapping("/aceptar-alarma")
    public void aceptarAlarma(@Payload RespuestaAlarma respuesta,Principal usuario) {
        
        try
        {
            Alarma alarma = alarmaBusiness.aceptarAlarma(respuesta.getNumeroOrden(), respuesta.getTipoAlarma(),
                respuesta.getUsermail(),respuesta.getObservarcion());

            AlarmaDTO alarmaDto = new AlarmaDTO();
            alarmaDto.setNumeroOrden(alarma.getOrden().getNumeroOrden());
            alarmaDto.setTipoAlarma(alarma.getTipoAlarma());
            alarmaDto.setFecha(alarma.getFechaAceptacion());
            alarmasWebSocketService.notificarCorrectaAceptacionAlarma(alarmaDto);
            
        }
        catch(BusinessException | NotFoundException e)
        {
            log.error("Error en Respuesta web Socket", e);

        }
    }
}