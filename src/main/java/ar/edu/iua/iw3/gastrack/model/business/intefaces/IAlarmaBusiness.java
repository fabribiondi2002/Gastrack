package ar.edu.iua.iw3.gastrack.model.business.intefaces;

import java.util.List;

import ar.edu.iua.iw3.gastrack.model.Alarma;
import ar.edu.iua.iw3.gastrack.model.Alarma.TipoAlarma;
import ar.edu.iua.iw3.gastrack.model.business.exception.BusinessException;
import ar.edu.iua.iw3.gastrack.model.business.exception.FoundException;
import ar.edu.iua.iw3.gastrack.model.business.exception.NotFoundException;


public interface IAlarmaBusiness {

    public List<Alarma> list() throws BusinessException;
    public Alarma aceptarAlarma(String json) throws BusinessException, NotFoundException;
    public Alarma add(Alarma alarma) throws BusinessException, FoundException;
    public Alarma loadByOrdenAndTipo(long numeroOrden, TipoAlarma tipoAlarma) throws BusinessException, NotFoundException;
    public List<Alarma> loadNoAceptadas() throws BusinessException;
}