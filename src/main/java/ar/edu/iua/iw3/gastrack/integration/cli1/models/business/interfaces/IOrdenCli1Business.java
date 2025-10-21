package ar.edu.iua.iw3.gastrack.integration.cli1.models.business.interfaces;

import ar.edu.iua.iw3.gastrack.integration.cli1.models.OrdenCli1;
import ar.edu.iua.iw3.gastrack.model.business.exception.BusinessException;
import ar.edu.iua.iw3.gastrack.model.business.exception.FoundException;
import ar.edu.iua.iw3.gastrack.model.business.exception.NotFoundException;

public interface IOrdenCli1Business {
    public OrdenCli1 add(OrdenCli1 orden) throws FoundException, BusinessException;
    public OrdenCli1 loadByCodigoExternoCli1(String codigoExterno) throws NotFoundException, BusinessException;
    public OrdenCli1 loadByNumeroOrden(String numeroOrden) throws NotFoundException, BusinessException;
    public OrdenCli1 addExternal(String json) throws FoundException, BusinessException;
}
