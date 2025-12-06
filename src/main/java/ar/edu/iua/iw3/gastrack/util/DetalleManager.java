package ar.edu.iua.iw3.gastrack.util;

import java.util.Date;
import java.util.Optional;

import ar.edu.iua.iw3.gastrack.model.Detalle;
import ar.edu.iua.iw3.gastrack.model.business.exception.BusinessException;
import ar.edu.iua.iw3.gastrack.model.business.exception.InvalidDetailException;
import ar.edu.iua.iw3.gastrack.model.business.exception.InvalidDetailFrecuencyException;
import ar.edu.iua.iw3.gastrack.model.persistence.DetalleRepository;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Clase utilitaria para la gestion de detalles
 * 
 * @author Leandro Biondi
 * @author Benjamin Vargas
 * @author Antonella Badami
 * @version 1.0
 * @since 2025-10-24
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DetalleManager {

    /**
     * Gestiona la logica de filtrado y control de frecuencia de detalles
     * @param dDAO DAO de entidad Detalle
     * @param d Detalle actual
     * @param frecuenciaMilis Frecuencia de muestreo en milisegundos
     * @throws BusinessException si ocurre un error en la capa de negocio
     * @throws InvalidDetailException si el detalle no cumple los criterios de aceptacion
     * @throws InvalidDetailFrecuencyException si el detalle no cumple con la frecuencia de muestreo
     */
    public static void manage(DetalleRepository dDAO, Detalle d, long frecuenciaMilis)
        throws BusinessException,InvalidDetailException,InvalidDetailFrecuencyException
    {
        Optional<Detalle> last;
        try
        {
            last = dDAO.findTopByOrdenIdOrderByFechaDesc(d.getOrden().getId());
            
        }
        catch(Exception e)
        {
            throw BusinessException.builder().ex(e).build();
        }
        
        if(last.isEmpty()) // hay mediciones previas
        {
            filtradoDeDetalles(d,null);
        }
        else
        {
            filtradoDeDetalles(d,last.get());
            controlFrecuencia(d.getFecha(), last.get().getFecha(), frecuenciaMilis);
        }   
        
    }
    
    /**
     * Logica de filtrado de detalles
     * Solo detalles con caudal > 0 y masa acumulada valida son aceptados.
     * @param d Detalle actual
     * @param last Detalle previo
     * @throws InvalidDetailException si el detalle no cumple los criterios de aceptacion
     */
    private static void filtradoDeDetalles(Detalle d,Detalle last)
        throws InvalidDetailException
    {
        if(d.getCaudal() <=0 || d.getMasaAcumulada() <= 0 || (last != null && d.getMasaAcumulada() < last.getMasaAcumulada()))
        {
            log.trace("Detalle no paso el filtrado: Caudal: "+d.getCaudal()+", MasaAcumulada="+d.getMasaAcumulada());
            throw InvalidDetailException.builder().message("Detalle no valido").build();
        }
        
    }

    /**
     * Control de frecuencia de detalles
     * @param current Fecha del detalle actual
     * @param prev Fecha del detalle previo
     * @param fMilis Frecuencia en milisegundos
     * @throws InvalidDetailFrecuencyException si el detalle no cumple con la frecuencia de muestreo
     */
    private static void controlFrecuencia(Date current, Date prev,long fMilis)
        throws InvalidDetailFrecuencyException
    {
        long diff = current.getTime() - prev.getTime();
        if(diff < fMilis)
        {
            log.trace("Detalle no paso el control de frecuencia: Diferencia de tiempo= " + diff +"ms, Frecuencia= "+ fMilis+"ms");
            throw InvalidDetailFrecuencyException.builder().message("Detalle recibido fuera de frecuencia de muestreo").build();
        }
    }
}
