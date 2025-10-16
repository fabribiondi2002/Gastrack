package ar.edu.iua.iw3.gastrack.model.business;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.edu.iua.iw3.gastrack.model.Cisterna;
import ar.edu.iua.iw3.gastrack.model.persistence.CisternaRepository;
import lombok.extern.slf4j.Slf4j;

/**
 * Clase que implementa la logica de negocio para las cisternas
 * 
 * @author Leandro Biondi
 * @author Benjamin Vargas
 * @author Antonella Badami
 * @version 1.0
 * @since 2025-10-16
 */

@Service
@Slf4j
public class CisternaBusiness implements ICisternaBusiness{
    
    @Autowired
	private CisternaRepository cisternaDAO;

    /**
     * Listar todos las cisternas
     * 
     * @return Lista de cisternas
     * @throws BusinessException Si ocurre un error no previsto
     */
    @Override
    public List<Cisterna> list() throws BusinessException {
        try {
			return cisternaDAO.findAll();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw BusinessException.builder().ex(e).message(e.getMessage()).build();
		}
    }

    /**
     * Obtener una cisterna por id
     * 
     * @param id Id de la cisterna
     * @return cisterna
     * @throws NotFoundException Si no se encuentra la cisterna
     * @throws BusinessException Si ocurre un error no previsto
     */
    @Override
    public Cisterna load(long id) throws NotFoundException, BusinessException{
        Optional<Cisterna> r;
		try {
			r = cisternaDAO.findById(id);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw BusinessException.builder().ex(e).build();
		}
		if (r.isEmpty()) {
			throw NotFoundException.builder().message("No se encuentra la cisterna id=" + id).build();
		}
		return r.get();
    }

     /**
     * Agregar una cisterna
     * 
     * @param cisterna cisterna a agregar
     * @return cisterna agregado
     * @throws FoundException    Si ya existe una cisterna con el mismo id 
     * @throws BusinessException Si ocurre un error no previsto
     */
    @Override
	public Cisterna add(Cisterna cisterna) throws FoundException, BusinessException{
        try {
			load(cisterna.getId());
			throw FoundException.builder().message("Se encuentró la cisterna id=" + cisterna.getId()).build();
		} catch (NotFoundException e) {
		}

		try {
			return cisternaDAO.save(cisterna);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw BusinessException.builder().ex(e).build();
		}
    }

    /**
     * Actualizar una cisterna
     * 
     * @param cisterna cisterna a actualizar
     * @return cisterna actualizado
     * @throws NotFoundException Si no se encuentra la cisterna
     * @throws FoundException    Si ya existe una cisterna con el mismo id
     *                           que no sea la misma cisterna actualizar
     * @throws BusinessException Si ocurre un error no previsto
     */
    @Override
	public Cisterna update(Cisterna cisterna) throws FoundException, NotFoundException, BusinessException{
        load(cisterna.getId());
		Optional<Cisterna> idExistente=null;
		try {
			idExistente=cisternaDAO.findById(cisterna.getId());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw BusinessException.builder().ex(e).build();
		}
		if(idExistente.isPresent()) {
			throw FoundException.builder().message("Se encontró una cisterna="+cisterna.getId()).build();
		}

		try {
			return cisternaDAO.save(cisterna);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw BusinessException.builder().ex(e).build();
		}
    }

    /**
     * Eliminar un cisterna por id
     * 
     * @param id Id de la cisterna a eliminar
     * @throws NotFoundException Si no se encuentra la cisterna a eliminar
     * @throws BusinessException Si ocurre un error no previsto
     */
    @Override
	public void delete(long id) throws NotFoundException, BusinessException{
       load(id);
		try {
			 cisternaDAO.deleteById(id);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw BusinessException.builder().ex(e).build();
		}
    }

}
