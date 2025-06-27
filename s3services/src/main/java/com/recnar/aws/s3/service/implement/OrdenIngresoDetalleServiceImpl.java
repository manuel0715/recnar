package com.recnar.aws.s3.service.implement;

import com.recnar.aws.s3.db.entity.OrdenIngresoDetalle;
import com.recnar.aws.s3.db.repository.IOrdenIngresoDetalleRepository;
import com.recnar.aws.s3.service.IOrdenIngresoDetalleServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class OrdenIngresoDetalleServiceImpl implements IOrdenIngresoDetalleServices {

    @Autowired
    IOrdenIngresoDetalleRepository iOrdenIngresoDetalleRepository;

    public Optional<OrdenIngresoDetalle> obtenerDetallePorId(Long id) {
        return iOrdenIngresoDetalleRepository.findById(id);
    }
}



