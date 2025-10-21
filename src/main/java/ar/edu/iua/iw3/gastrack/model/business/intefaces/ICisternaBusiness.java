package ar.edu.iua.iw3.gastrack.model.business.intefaces;

import java.util.List;

import ar.edu.iua.iw3.gastrack.model.Cisterna;
import ar.edu.iua.iw3.gastrack.model.business.exception.BusinessException;
import ar.edu.iua.iw3.gastrack.model.business.exception.FoundException;
import ar.edu.iua.iw3.gastrack.model.business.exception.NotFoundException;

/** 
 * Interfaz para la logica de negocio de las ciscernas 
 * @author Leandro Biondi
 * @author Benjamin Vargas
 * @author Antonella Badami
 * @version 1.0
 * @since 2025-10-16
*/

public interface ICisternaBusiness {
    
    public List<Cisterna> list() throws BusinessException;

    public Cisterna load(long id) throws NotFoundException, BusinessException;

	public Cisterna add(Cisterna cisterna) throws FoundException, BusinessException;

	public Cisterna update(Cisterna cisterna) throws FoundException, NotFoundException, BusinessException;

	public void delete(long id) throws NotFoundException, BusinessException;

}
