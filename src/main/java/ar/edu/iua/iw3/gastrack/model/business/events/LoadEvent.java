package ar.edu.iua.iw3.gastrack.model.business.events;

import java.util.List;

import org.springframework.context.ApplicationEvent;

import ar.edu.iua.iw3.gastrack.model.Orden;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoadEvent extends ApplicationEvent {
	public enum TypeEvent {
		HIGH_TEMP,
	}
	public LoadEvent(Object source, List<String> contacts,Orden orden, TypeEvent typeEvent) {
		super(source);
		this.typeEvent = typeEvent;
		this.contacts=contacts;
		this.orden=orden;
	}
	private TypeEvent typeEvent;
	private List<String> contacts;
	private Orden orden;
}