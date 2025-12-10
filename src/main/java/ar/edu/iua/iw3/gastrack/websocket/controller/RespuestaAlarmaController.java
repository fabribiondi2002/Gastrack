package ar.edu.iua.iw3.gastrack.websocket.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import ar.edu.iua.iw3.gastrack.websocket.dto.RespuestaAlarma;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class RespuestaAlarmaController {

    @MessageMapping("/aceptar-alarma")
    public void recibirRespuesta(@Payload RespuestaAlarma respuesta) {
        log.info("Respuesta de alarma recibida el " + respuesta.getFechaAceptacion() +
                 " para la orden nro: " + respuesta.getNumeroOrden() +
                 " con la observacion: " + respuesta.getObservarcion());

        // TODO: Procesar la respuesta de la alarma (e.g., actualizar el estado en la base de datos)
    
    }
}