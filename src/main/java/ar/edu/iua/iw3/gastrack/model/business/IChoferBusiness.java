package ar.edu.iua.iw3.gastrack.model.business;

import java.util.List;
import ar.edu.iua.iw3.gastrack.model.Chofer;
/** 
 * Interfaz para la logica de negocio de los choferes 
 * @author Leandro Biondi
 * @author Benjamin Vargas
 * @author Antonella Badami
 * @version 1.0
 * @since 2025-10-16
*/

public interface IChoferBusiness {

    public List<Chofer> list() throws BusinessException;

    public Chofer load(long id) throws NotFoundException, BusinessException;

    public Chofer load(Long documento) throws NotFoundException, BusinessException;

    public Chofer add(Chofer chofer) throws FoundException, BusinessException;

    public Chofer update(Chofer chofer) throws NotFoundException, FoundException, BusinessException;

    public void delete(long id) throws NotFoundException, BusinessException;
}