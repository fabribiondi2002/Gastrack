package ar.edu.iua.iw3.gastrack.model.business;

import java.util.List;
import ar.edu.iua.iw3.gastrack.model.Cisterna;

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

	public Cisterna add(Cisterna camion) throws FoundException, BusinessException;

	public Cisterna update(Cisterna camion) throws FoundException, NotFoundException, BusinessException;

	public void delete(long id) throws NotFoundException, BusinessException;

}
