package ar.edu.iua.iw3.gastrack.model.business;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.edu.iua.iw3.gastrack.auth.UserBusiness;
import ar.edu.iua.iw3.gastrack.model.Alarma;
import ar.edu.iua.iw3.gastrack.model.Alarma.TipoAlarma;
import ar.edu.iua.iw3.gastrack.model.business.exception.BusinessException;
import ar.edu.iua.iw3.gastrack.model.business.exception.FoundException;
import ar.edu.iua.iw3.gastrack.model.business.exception.NotFoundException;
import ar.edu.iua.iw3.gastrack.model.business.intefaces.IAlarmaBusiness;
import ar.edu.iua.iw3.gastrack.model.persistence.AlarmaRepository;
import ar.edu.iua.iw3.gastrack.websocket.service.AlarmasWebSocketService;

@Service
public class AlarmaBusiness implements IAlarmaBusiness {

    @Autowired
    private AlarmaRepository alarmaDAO;

    @Autowired
    private UserBusiness userBusiness;

    @Autowired
    AlarmasWebSocketService alarmasWebSocketService;

    @Override
    public List<Alarma> list() throws BusinessException {

        try
        {
            
            return alarmaDAO.findAll();
        }
        catch (Exception e)
        {
            throw BusinessException.builder().ex(e).message(e.getMessage()).build();
        }
    }


    @Override
    public Alarma add(Alarma alarma) throws BusinessException, FoundException {
        try
        {
            loadByOrdenAndTipo(alarma.getOrden().getNumeroOrden(), alarma.getTipoAlarma());
            if(!alarma.isAceptada())
            {
                throw FoundException.builder().message("Ya existe una alarma igual que aún no fue aceptada").build();
            }
            return alarmaDAO.save(alarma);
            
        }
        catch (NotFoundException e)
        {}
         try
        {
            return alarmaDAO.save(alarma);

        }
        catch (Exception ex)
        {
                throw BusinessException.builder().ex(ex).message(ex.getMessage()).build();
        }
    }

    @Override
    public Alarma loadByOrdenAndTipo(long numeroOrden, TipoAlarma tipoAlarma) throws BusinessException, NotFoundException
    {
        Optional <Alarma> alarma;
        try
        {
            alarma = alarmaDAO
            .findTopByOrden_NumeroOrdenAndTipoAlarmaOrderByFechaEmisionDesc(numeroOrden, tipoAlarma);
            
        }
        catch (Exception e)
        {
            throw BusinessException.builder().ex(e).message(e.getMessage()).build();
        }
        if(alarma.isPresent())
        {
            return alarma.get();
        }
        else
        {
            throw NotFoundException.builder().message("No hay alarmas de ese tipo registradas").build();
        }
    }


    @Override
    public Alarma aceptarAlarma(long numeroOrden, TipoAlarma tipoAlarma, String useremail, String observacion)
            throws BusinessException, NotFoundException {
        Alarma alarma = loadByOrdenAndTipo(numeroOrden, tipoAlarma);
        alarma.setAceptada(true);
        alarma.setFechaAceptacion(new Date());
        alarma.setObservacion(observacion);
        alarma.setUsuario(userBusiness.load(useremail));
        try
        {
            alarmaDAO.save(alarma);
            alarmasWebSocketService.notificarAceptacionAlarma(numeroOrden, tipoAlarma);
            return alarma;
        }
        catch (Exception e)
        {
            throw BusinessException.builder().ex(e).message(e.getMessage()).build();
        }
    }


    
}
