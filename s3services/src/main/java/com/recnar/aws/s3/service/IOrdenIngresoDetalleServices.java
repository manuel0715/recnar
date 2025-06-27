package com.recnar.aws.s3.service;

import com.recnar.aws.s3.db.entity.OrdenIngresoDetalle;

import java.util.Optional;

public interface IOrdenIngresoDetalleServices {

    Optional<OrdenIngresoDetalle> obtenerDetallePorId(Long id);
}
