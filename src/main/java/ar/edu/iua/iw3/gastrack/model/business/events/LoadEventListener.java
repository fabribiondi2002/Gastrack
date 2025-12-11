package ar.edu.iua.iw3.gastrack.model.business.events;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import ar.edu.iua.iw3.gastrack.model.Alarma;
import ar.edu.iua.iw3.gastrack.model.Detalle;
import ar.edu.iua.iw3.gastrack.model.business.AlarmaBusiness;
import ar.edu.iua.iw3.gastrack.model.business.exception.FoundException;
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

	@Autowired
	private AlarmaBusiness alarmaBusiness;

	@Override
	public void onApplicationEvent(LoadEvent event) {
		if (event.getTypeEvent().equals(LoadEvent.TypeEvent.HIGH_TEMP)) {
			handleHighTemp(event);
		}
	}

	private void handleHighTemp(LoadEvent event) {
		log.info("Exceso de temperatura detectada en la orden nro: " + event.getOrden().getNumeroOrden());
		try {

			Alarma alarma = new Alarma();
			alarma.setOrden(event.getOrden());
			alarma.setTipoAlarma(Alarma.TipoAlarma.ALTA_TEMPERATURA);
			alarma.setFechaEmision(((Detalle) event.getSource()).getFecha());
			alarmaBusiness.add(alarma);
			
			alarmasWebSocketService.enviarAlarmaTemperatura(event.getOrden(), ((Detalle) event.getSource()).getFecha());
			
			
			emailBusiness.sendHighTempAlert(
				event.getContacts(),
	    		"Alerta de alta temperatura en la carga",
	    		"Temperatura excesiva de " + ((Detalle) event.getSource()).getTemperatura()+ "°C registrada.",
	    		String.valueOf(event.getOrden().getNumeroOrden()),
	    		event.getOrden().getCamion().getPatente(),
	    		((Detalle)event.getSource()).getFecha().toString(),
	    		"www.google.com"//"http://gastrack.com/ordenes/" + event.getOrden().getId()
	    	);
		}
		catch (FoundException e)
		{
			log.info("No se envio alerta de alta temperatura: " + e.getMessage(), e);
		} 
		catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}
}