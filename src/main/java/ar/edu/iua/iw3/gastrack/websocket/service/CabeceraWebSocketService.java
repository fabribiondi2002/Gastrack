package ar.edu.iua.iw3.gastrack.websocket.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import ar.edu.iua.iw3.gastrack.model.Orden;
import ar.edu.iua.iw3.gastrack.websocket.dto.CabeceraOrdenWS;

@Service
public class CabeceraWebSocketService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public void enviarCabecera(Orden orden) {

        CabeceraOrdenWS dto = new CabeceraOrdenWS();
        dto.setNumeroOrden(orden.getNumeroOrden());
        dto.setUltimaMasaAcumulada(orden.getUltimaMasaAcumulada());
        dto.setUltimaDensidad(orden.getUltimaDensidad());
        dto.setUltimaTemperatura(orden.getUltimaTemperatura());
        dto.setUltimoCaudal(orden.getUltimoCaudal());
        dto.setFechaUltimoMedicion(orden.getFechaUltimoMedicion());

        // Emitir a todos los clientes suscriptos
        messagingTemplate.convertAndSend("/topic/cabecera", dto);
    }
}
