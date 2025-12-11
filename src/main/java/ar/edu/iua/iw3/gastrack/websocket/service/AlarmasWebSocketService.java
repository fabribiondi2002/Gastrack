package ar.edu.iua.iw3.gastrack.websocket.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import ar.edu.iua.iw3.gastrack.model.Orden;
import ar.edu.iua.iw3.gastrack.model.Alarma.TipoAlarma;
import ar.edu.iua.iw3.gastrack.websocket.dto.AlarmaDTO;

@Service
public class AlarmasWebSocketService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;


    public void enviarAlarmaTemperatura(Orden orden, Date timestamp)
    {
        AlarmaDTO dto = new AlarmaDTO();
        dto.setNumeroOrden(orden.getNumeroOrden());
        dto.setTipoAlarma(TipoAlarma.ALTA_TEMPERATURA);;
        dto.setFecha(timestamp);

        messagingTemplate.convertAndSend("/topic/alarma/temperatura", dto);   
    }

    public void notificarCorrectaAceptacionAlarma(AlarmaDTO alarma)
    {
        messagingTemplate.convertAndSend("/topic/alarma/aceptada", alarma);
    }
}
