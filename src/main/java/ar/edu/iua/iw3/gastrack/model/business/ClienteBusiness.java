package ar.edu.iua.iw3.gastrack.model.business;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.edu.iua.iw3.gastrack.model.Cliente;
import ar.edu.iua.iw3.gastrack.model.business.exception.BusinessException;
import ar.edu.iua.iw3.gastrack.model.business.exception.FoundException;
import ar.edu.iua.iw3.gastrack.model.business.exception.NotFoundException;
import ar.edu.iua.iw3.gastrack.model.business.inteface.IClienteBusiness;
import ar.edu.iua.iw3.gastrack.model.persistence.ClienteRepository;
import lombok.extern.slf4j.Slf4j;

/**
 * Clase que implementa la logica de negocio para los clientes
 * 
 * @author Leandro Biondi
 * @author Benjamin Vargas
 * @author Antonella Badami
 * @version 1.0
 * @since 2025-10-16
 */

@Service
@Slf4j
public class ClienteBusiness implements IClienteBusiness {
    @Autowired
    private ClienteRepository clienteDAO;

    /**
     * Listar todos los clientes
     * 
     * @return Lista de clientes
     * @throws BusinessException Si ocurre un error no previsto
     */
    @Override
    public List<Cliente> list() throws BusinessException {
        try {
            return clienteDAO.findAll();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).message(e.getMessage()).build();
        }
    }

    /**
     * Obtener un cliente por id
     * 
     * @param id Id del cliente
     * @return Cliente
     * @throws NotFoundException Si no se encuentra el cliente
     * @throws BusinessException Si ocurre un error no previsto
     */
    @Override
    public Cliente load(long id) throws NotFoundException, BusinessException {
        Optional<Cliente> r;
        try {
            r = clienteDAO.findById(id);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
        if (r.isEmpty()) {
            throw NotFoundException.builder().message("No se encuentra el Cliente id=" + id).build();
        }
        return r.get();
    }

    /**
     * Obtener un cliente por razon social
     * 
     * @param razonSocial Razon social del cliente
     * @return Cliente
     * @throws NotFoundException Si no se encuentra el cliente
     * @throws BusinessException Si ocurre un error no previsto
     */
    @Override
    public Cliente load(String razonSocial) throws NotFoundException, BusinessException {
        Optional<Cliente> r;
        try {
            r = clienteDAO.findByRazonSocial(razonSocial);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
        if (r.isEmpty()) {
            throw NotFoundException.builder().message("No se encuentra el Cliente razonSocial=" + razonSocial).build();
        }
        return r.get();
    }

    /**
     * Agregar un cliente
     * 
     * @param cliente Cliente a agregar
     * @return Cliente agregado
     * @throws FoundException    Si ya existe un cliente con el mismo id o razon social
     * @throws BusinessException Si ocurre un error no previsto
     */
    @Override
    public Cliente add(Cliente cliente) throws FoundException, BusinessException {
        try {
            load(cliente.getId());
            throw FoundException.builder().message("Se encontró el Cliente id=" + cliente.getId()).build();
        } catch (NotFoundException e) {

        }
        try {
            load(cliente.getRazonSocial());
            throw FoundException.builder().message("Se encontró el Cliente razonSocial=" + cliente.getRazonSocial())
                    .build();
        } catch (NotFoundException e) {
        }
        try {
            return clienteDAO.save(cliente);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
    }

    /**
     * Actualizar un cliente
     * 
     * @param cliente Cliente a actualizar
     * @return Cliente actualizado
     * @throws NotFoundException Si no se encuentra el cliente
     * @throws FoundException    Si ya existe un cliente con la misma razon social
     *                           que no sea el mismo cliente a actualizar
     * @throws BusinessException Si ocurre un error no previsto
     */
    @Override
    public Cliente update(Cliente cliente) throws NotFoundException, FoundException, BusinessException {
        load(cliente.getId());
        Optional<Cliente> razonSocialExistente = null;
        try {
            razonSocialExistente = clienteDAO.findByRazonSocialAndIdNot(cliente.getRazonSocial(), cliente.getId());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
        if (razonSocialExistente.isPresent()) {
            throw FoundException.builder().message("Se encontró un cliente razonSocial=" + cliente.getRazonSocial())
                    .build();
        }

        try {
            return clienteDAO.save(cliente);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
    }

    /**
     * Eliminar un cliente por id
     * 
     * @param id Id del cliente a eliminar
     * @throws NotFoundException Si no se encuentra el cliente a eliminar
     * @throws BusinessException Si ocurre un error no previsto
     */
    @Override
    public void delete(long id) throws NotFoundException, BusinessException {
        try {
            load(id);
        } catch (NotFoundException e) {
            throw NotFoundException.builder().message("No se encuentra el Cliente id=" + id).build();

        }
        try {
            clienteDAO.deleteById(id);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
    }

}
