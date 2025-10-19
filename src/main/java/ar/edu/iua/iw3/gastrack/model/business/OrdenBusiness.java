package ar.edu.iua.iw3.gastrack.model.business;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.edu.iua.iw3.gastrack.model.Orden;
import ar.edu.iua.iw3.gastrack.model.persistence.OrdenRepository;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class OrdenBusiness implements IOrdenBusiness {

    @Autowired
    private OrdenRepository ordenDAO;

    @Override
    public List<Orden> list() throws BusinessException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'list'");
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

    @Override
    public Orden update(Orden orden) throws NotFoundException, FoundException, BusinessException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    @Override
    public void delete(long id) throws NotFoundException, BusinessException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

}
