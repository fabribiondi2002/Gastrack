package ar.edu.iua.iw3.gastrack.model.business;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.edu.iua.iw3.gastrack.model.Orden;
import ar.edu.iua.iw3.gastrack.model.business.exception.BusinessException;
import ar.edu.iua.iw3.gastrack.model.business.exception.FoundException;
import ar.edu.iua.iw3.gastrack.model.business.exception.NotFoundException;
import ar.edu.iua.iw3.gastrack.model.business.inteface.IOrdenBusiness;
import ar.edu.iua.iw3.gastrack.model.persistence.OrdenRepository;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class OrdenBusiness implements IOrdenBusiness {

    @Autowired
    private OrdenRepository ordenDAO;


    /*
     * Listar todas las ordenes por estado
     * @param status Estado de las ordenes a listar
     * @return Lista de ordenes
     * @throws BusinessException Si ocurre un error no previsto
     * @throws NotFoundException Si no se encuentran ordenes con el estado especificado
     */
    @Override
    public List<Orden> listByStatus(String status) throws BusinessException, NotFoundException {
        Optional<List<Orden>> o;
        try {
            o = ordenDAO.findAllByStatus(status);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
        if (o.isEmpty()) {
            throw NotFoundException.builder().message("No se encuentran ordenes con estado:" + status).build();
        }
        return o.get();
    }

    /**
    * Busca una orden por su numero
    * 
    * @return orden cagada
    * @throws BusinessException Si ocurre un error no previsto
    * @throws NotFoundException Si no existe una orden con ese numero
    */
    @Override
    public Orden load(long id) throws NotFoundException, BusinessException {
        Optional<Orden> o;
        try {
            o = ordenDAO.findById(id);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
        if (o.isEmpty()) {
            throw NotFoundException.builder().message("No se encuentra la orden de numero:" + id).build();
        }
        return o.get();
    }


    /**
     * Añade una orden
     * 
     * @return orden añadida
     * @throws BusinessException Si ocurre un error no previsto
     * @throws FoundException Si ya existe una orden con ese numero
     */
    @Override
    public Orden add(Orden orden) throws FoundException, BusinessException {
        try {
            load(orden.getId());
            throw FoundException.builder().message("Se encontró la orden de numero" + orden.getId()).build();
        } catch (NotFoundException e) {

        }
        try {
            return ordenDAO.save(orden);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
    }
     /*
     * Obtener un orden por id
     * 
     * @param id Id del orden
     * @return Orden
     * @throws NotFoundException Si no existe la orden a actualizar
     * @throws BusinessException Si ocurre un error no previsto
     */
    @Override
    public Orden update(Orden orden) throws NotFoundException, BusinessException {
        load(orden.getId());
		try {
			return ordenDAO.save(orden);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw BusinessException.builder().ex(e).build();
		}
        
    }

    /*
     * Eliminar una orden por id
     * 
     * @param id Id de la orden
     * @throws NotFoundException Si no existe la orden a eliminar
     * @throws BusinessException Si ocurre un error no previsto
     */
    @Override
    public void delete(long id) throws NotFoundException, BusinessException {
        try {
            load(id);
        } catch (NotFoundException e) {
            throw NotFoundException.builder().message("No se encuentra la orden id=" + id).build();

        }
        try {
            ordenDAO.deleteById(id);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
    }

}
