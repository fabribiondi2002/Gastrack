package ar.edu.iua.iw3.gastrack.model.business.events;

import org.springframework.context.ApplicationEvent;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoadEvent extends ApplicationEvent {
	public enum TypeEvent {
		HIGH_TEMP,
	}
	public LoadEvent(Object source, Object extraData, TypeEvent typeEvent) {
		super(source);
		this.typeEvent = typeEvent;
		this.extraData=extraData;
	}
	private TypeEvent typeEvent;
	private Object extraData;
}