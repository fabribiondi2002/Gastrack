package ar.edu.iua.iw3.gastrack.websocket.service;


import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import ar.edu.iua.iw3.gastrack.model.Orden;
import ar.edu.iua.iw3.gastrack.websocket.dto.AlarmaTemperatura;

@Service
public class AlarmasWebSocketService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;


    public void enviarAlarmaTemperatura(Orden orden, Date timestamp, double temperatura)
    {
        AlarmaTemperatura dto = new AlarmaTemperatura();
        dto.setNumeroOrden(orden.getNumeroOrden());
        dto.setTemperatura(temperatura);
        dto.setFecha(timestamp);
        dto.setPatente(orden.getCamion().getPatente());

        messagingTemplate.convertAndSend("/topic/alarma/temperatura", dto);   
    }
}
