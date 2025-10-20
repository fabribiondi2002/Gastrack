package ar.edu.iua.iw3.gastrack.model.business.inteface;

import java.util.List;

import ar.edu.iua.iw3.gastrack.model.Camion;
import ar.edu.iua.iw3.gastrack.model.business.exception.BusinessException;
import ar.edu.iua.iw3.gastrack.model.business.exception.FoundException;
import ar.edu.iua.iw3.gastrack.model.business.exception.NotFoundException;

/** 
 * Interfaz para la logica de negocio de los camiones 
 * @author Leandro Biondi
 * @author Benjamin Vargas
 * @author Antonella Badami
 * @version 1.0
 * @since 2025-10-16
*/

public interface ICamionBusiness {
    
    public List<Camion> list() throws BusinessException;

    public Camion load(long id) throws NotFoundException, BusinessException;

    public Camion load(String patente) throws NotFoundException, BusinessException;

	public Camion add(Camion camion) throws FoundException, BusinessException;

	public Camion update(Camion camion) throws FoundException, NotFoundException, BusinessException;

	public void delete(long id) throws NotFoundException, BusinessException;

}
