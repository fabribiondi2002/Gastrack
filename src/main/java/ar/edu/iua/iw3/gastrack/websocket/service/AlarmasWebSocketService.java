package ar.edu.iua.iw3.gastrack.websocket.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import ar.edu.iua.iw3.gastrack.model.Alarma;
import ar.edu.iua.iw3.gastrack.websocket.dto.AlarmaWSDTO;
import lombok.extern.slf4j.Slf4j;
@Slf4j
@Service
public class AlarmasWebSocketService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public void enviarAlarmaNueva(Alarma alarma) {
        AlarmaWSDTO dto = new AlarmaWSDTO();
        dto.setId(alarma.getId());
        dto.setNumeroOrden(alarma.getOrden().getNumeroOrden());
        dto.setTipoAlarma(alarma.getTipoAlarma());
        dto.setFecha(alarma.getFechaEmision());
        // Emitir a todos los clientes suscriptos
        messagingTemplate.convertAndSend("/topic/alarma/alarmaNueva", dto);
    }
    
}

