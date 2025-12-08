package ar.edu.iua.iw3.gastrack.model.business.events;

import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class LoadEventListener implements ApplicationListener<LoadEvent> {

	@Override
	public void onApplicationEvent(LoadEvent event) {
		if (event.getTypeEvent().equals(LoadEvent.TypeEvent.HIGH_TEMP)) {
			handleHighTemp(event);
		}
	}

	private void handleHighTemp(LoadEvent event) {
		log.info("Exceso de temperatura detectada ");
	}
}