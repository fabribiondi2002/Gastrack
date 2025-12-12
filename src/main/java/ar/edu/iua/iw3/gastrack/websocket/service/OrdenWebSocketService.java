package ar.edu.iua.iw3.gastrack.websocket.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import ar.edu.iua.iw3.gastrack.model.Orden;
@Service
public class OrdenWebSocketService {
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public void enviarOrden(Orden orden) {
        messagingTemplate.convertAndSend("/topic/orden", orden);
    }
}

