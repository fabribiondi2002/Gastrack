package ar.edu.iua.iw3.gastrack.model.business;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ar.edu.iua.iw3.gastrack.model.Chofer;
import ar.edu.iua.iw3.gastrack.model.persistence.ChoferRepository;
import lombok.extern.slf4j.Slf4j;

/**
 * Clase que implementa la logica de negocio para los choferes
 * 
 * @author Leandro Biondi
 * @author Benjamin Vargas
 * @author Antonella Badami
 * @version 1.0
 * @since 2025-10-16
 */

@Service
@Slf4j
public class ChoferBusiness implements IChoferBusiness {
    @Autowired
    private ChoferRepository choferDAO;

    /**
     * Listar todos los choferes
     * 
     * @return Lista de choferes
     * @throws BusinessException Si ocurre un error no previsto
     */
    @Override
    public List<Chofer> list() throws BusinessException {
        try {
            return choferDAO.findAll();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).message(e.getMessage()).build();
        }
    }

    /**
     * Obtener un chofer por id
     * 
     * @param id Id del chofer
     * @return Chofer
     * @throws NotFoundException Si no se encuentra el chofer
     * @throws BusinessException Si ocurre un error no previsto
     */
    @Override
    public Chofer load(long id) throws NotFoundException, BusinessException {
        Optional<Chofer> r;
        try {
            r = choferDAO.findById(id);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
        if (r.isEmpty()) {
            throw NotFoundException.builder().message("No se encuentra el Chofer id=" + id).build();
        }
        return r.get();
    }

    /**
     * Obtener un chofer por documento
     * 
     * @param documento Documento del chofer
     * @return Chofer
     * @throws NotFoundException Si no se encuentra el chofer
     * @throws BusinessException Si ocurre un error no previsto
     */
    @Override
    public Chofer load(Long documento) throws NotFoundException, BusinessException {
        Optional<Chofer> r;
        try {
            r = choferDAO.findByDocumento(documento);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
        if (r.isEmpty()) {
            throw NotFoundException.builder().message("No se encuentra el Chofer documento=" + documento).build();
        }
        return r.get();
    }

    /**
     * Agregar un chofer
     * 
     * @param chofer Chofer a agregar
     * @return Chofer agregado
     * @throws FoundException    Si ya existe un chofer con el mismo id o documento
     * @throws BusinessException Si ocurre un error no previsto
     */
    @Override
    public Chofer add(Chofer chofer) throws FoundException, BusinessException {
        try {
            load(chofer.getId());
            throw FoundException.builder().message("Se encontró el Chofer id=" + chofer.getId()).build();
        } catch (NotFoundException e) {

        }
        try {
            load(chofer.getDocumento());
            throw FoundException.builder().message("Se encontró el Chofer documento=" + chofer.getDocumento()).build();
        } catch (NotFoundException e) {
        }
        try {
            return choferDAO.save(chofer);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
    }

    /**
     * Actualizar informacion de un chofer sin poder repetir documento
     * 
     * @param chofer Chofer a actualizar
     * @return Chofer actualizado
     * @throws NotFoundException Si no se encuentra el chofer a actualizar
     * @throws FoundException    Si ya existe un chofer con el mismo documento
     * @throws BusinessException Si ocurre un error no previsto
     */
    @Override
    public Chofer update(Chofer chofer) throws NotFoundException, FoundException, BusinessException {
        load(chofer.getId());
        Optional<Chofer> documentoExistente = null;
        try {
            documentoExistente = choferDAO.findByDocumentoAndIdNot(chofer.getDocumento(), chofer.getId());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
        if (documentoExistente.isPresent()) {
            throw FoundException.builder().message("Se encontró un chofer documento=" + chofer.getDocumento()).build();
        }

        try {
            return choferDAO.save(chofer);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
    }

    /**
     * Eliminar un chofer
     * 
     * @param id Id del chofer a eliminar
     * @throws NotFoundException Si no se encuentra el chofer a eliminar
     * @throws BusinessException Si ocurre un error no previsto
     */
    @Override
    public void delete(long id) throws NotFoundException, BusinessException {
        try {
            load(id);
        } catch (NotFoundException e) {
            throw NotFoundException.builder().message("No se encuentra el Chofer id=" + id).build();

        }
        try {
            choferDAO.deleteById(id);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
    }

}
