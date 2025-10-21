package ar.edu.iua.iw3.gastrack.integration.cli1.models.business;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.edu.iua.iw3.gastrack.integration.cli1.models.OrdenCli1;
import ar.edu.iua.iw3.gastrack.integration.cli1.models.business.interfaces.IOrdenCli1Business;
import ar.edu.iua.iw3.gastrack.integration.cli1.models.persistence.OrdenCli1Repository;
import ar.edu.iua.iw3.gastrack.model.business.exception.BusinessException;
import ar.edu.iua.iw3.gastrack.model.business.exception.FoundException;
import ar.edu.iua.iw3.gastrack.model.business.exception.NotFoundException;
import lombok.extern.slf4j.Slf4j;

/*
 * Clase que implementa la logica de negocio para las ordenes en la integracion CLI1
 * Debe crear las ordenes recibidas desde la CLI1
 * @author Leandro Biondi
 * @author Benjamin Vargas
 * @author Antonella Badami
 * @version 1.0
 * @since 2025-10-21
 */


@Service
@Slf4j
public class OrdenCli1Business implements IOrdenCli1Business {
    @Autowired
    private OrdenCli1Repository ordenCli1DAO;
    
    @Override
    public OrdenCli1 add(OrdenCli1 orden) throws FoundException, BusinessException {
        try {
            loadByNumeroOrden(orden.getNumeroOrden());
            throw FoundException.builder().message("Se encontró la orden de numero" + orden.getNumeroOrden()).build();
        } catch (NotFoundException e) {

        }
        try {
            loadByCodigoExternoCli1(orden.getCodigoExternoCli1());
            throw FoundException.builder().message("Se encontró la orden de codigo externo" + orden.getCodigoExternoCli1()).build();
        } catch (NotFoundException e) {

        }
        try {
            return ordenCli1DAO.save(orden);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
    }




    @Override
    public OrdenCli1 loadByCodigoExternoCli1(String codigoExterno) throws NotFoundException, BusinessException {
		Optional<OrdenCli1> r;
		try {
			r = ordenCli1DAO.findByCodigoExternoCli1(codigoExterno);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw BusinessException.builder().ex(e).build();
		}
		if (r.isEmpty()) {
			throw NotFoundException.builder().message("No se encuentra la Orden con codigo=" + codigoExterno).build();
		}
		return r.get();
	}
    @Override
    public OrdenCli1 loadByNumeroOrden(String numeroOrden) throws NotFoundException, BusinessException {
		Optional<OrdenCli1> r;
		try {
			r = ordenCli1DAO.findByNumeroOrden(numeroOrden);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw BusinessException.builder().ex(e).build();
		}
		if (r.isEmpty()) {
			throw NotFoundException.builder().message("No se encuentra la Orden numeroOrden=" + numeroOrden).build();
		}
		return r.get();
	}




    @Override
    public OrdenCli1 addExternal(String json) throws FoundException, BusinessException{
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'addExternal'");
    }


    
    
}
 