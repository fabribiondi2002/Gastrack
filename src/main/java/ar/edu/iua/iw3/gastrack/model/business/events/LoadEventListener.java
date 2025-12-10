package ar.edu.iua.iw3.gastrack.model.business.events;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import ar.edu.iua.iw3.gastrack.model.Detalle;
import ar.edu.iua.iw3.gastrack.util.EmailBusiness;
import ar.edu.iua.iw3.gastrack.websocket.service.AlarmasWebSocketService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class LoadEventListener implements ApplicationListener<LoadEvent> {

	@Autowired
	private EmailBusiness emailBusiness;

	@Autowired
	private AlarmasWebSocketService alarmasWebSocketService;

	@Override
	public void onApplicationEvent(LoadEvent event) {
		if (event.getTypeEvent().equals(LoadEvent.TypeEvent.HIGH_TEMP)) {
			handleHighTemp(event);
		}
	}

	private void handleHighTemp(LoadEvent event) {
		log.info("Exceso de temperatura detectada en la orden nro: " + event.getOrden().getNumeroOrden());
		try {
			alarmasWebSocketService.enviarAlarmaTemperatura(event.getOrden(), ((Detalle) event.getSource()).getFecha(), ((Detalle) event.getSource()).getTemperatura());
			emailBusiness.sendHighTempAlert(
				event.getContacts(),
	    		"Alerta de alta temperatura en la carga",
	    		"Temperatura excesiva de " + ((Detalle) event.getSource()).getTemperatura()+ "°C registrada.",
	    		String.valueOf(event.getOrden().getNumeroOrden()),
	    		event.getOrden().getCamion().getPatente(),
	    		((Detalle)event.getSource()).getFecha().toString(),
	    		"www.google.com"//"http://gastrack.com/ordenes/" + event.getOrden().getId()
	    	);
		} catch (Exception e) {
			log.error("Error enviando mail de alerta de alta temperatura: " + e.getMessage(), e);
		}
	}
}