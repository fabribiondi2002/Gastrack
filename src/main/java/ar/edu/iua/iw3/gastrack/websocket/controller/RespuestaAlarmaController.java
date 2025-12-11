package ar.edu.iua.iw3.gastrack.websocket.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import ar.edu.iua.iw3.gastrack.model.business.AlarmaBusiness;
import ar.edu.iua.iw3.gastrack.model.business.exception.BusinessException;
import ar.edu.iua.iw3.gastrack.model.business.exception.NotFoundException;
import ar.edu.iua.iw3.gastrack.websocket.dto.RespuestaAlarma;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class RespuestaAlarmaController {

    @Autowired
    AlarmaBusiness alarmaBusiness;

    @MessageMapping("/aceptar-alarma")
    public void aceptarAlarma(@Payload RespuestaAlarma respuesta) {
        
        try
        {
            alarmaBusiness.aceptarAlarma(respuesta.getNumeroOrden(), respuesta.getTipoAlarma(),
                respuesta.getUsermail(),respuesta.getObservarcion());    
        }
        catch(BusinessException | NotFoundException e)
        {
            log.error("Error en Respuesta web Socket", e);
        }
    }
}