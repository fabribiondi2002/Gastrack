package ar.edu.iua.iw3.gastrack.model.business;

import java.util.List;

import ar.edu.iua.iw3.gastrack.model.Cliente;

/** 
 * Interfaz para la logica de negocio de los clientes 
 * @author Leandro Biondi
 * @author Benjamin Vargas
 * @author Antonella Badami
 * @version 1.0
 * @since 2025-10-16
*/

public interface IClienteBusiness {
    public List<Cliente> list() throws BusinessException;

    public Cliente load(long id) throws NotFoundException, BusinessException;

    public Cliente load(String razonSocial) throws NotFoundException, BusinessException;

    public Cliente add(Cliente cliente) throws FoundException, BusinessException;

    public Cliente update(Cliente cliente) throws NotFoundException, FoundException, BusinessException;

    public void delete(long id) throws NotFoundException, BusinessException;
}
