package com.recnar.aws.s3.db.repository;

import com.recnar.aws.s3.db.entity.OrdenIngresoDetalle;
import com.recnar.aws.s3.dto.DetalleOrdenIngresoDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface IOrdenIngresoDetalleRepository extends JpaRepository<OrdenIngresoDetalle, Long> {
    Optional<OrdenIngresoDetalle> findById(Long id);

    @Query(value =
            "SELECT " +
                    " id.id AS idDetalleOrdenIngreso, " +
                    " id.codigo_orden_ingreso AS codigoOrdenIngreso, " +
                    " ird.codigo_ingreso_remoto AS codigoIngresoRemoto, " +
                    " ild.codigo_ingreso_local AS codigoIngresoLocal, " +
                    " d.codigo_desintegracion AS codigoDesintegracion " +
                    "FROM orden_ingreso_detalle id " +
                    "LEFT JOIN orden_ingreso oi ON oi.codigo_orden_ingreso = id.codigo_orden_ingreso " +
                    "LEFT JOIN ingreso_remoto_detalle ird ON id.id = ird.id_detalle_orden_ingreso " +
                    "LEFT JOIN ingreso_local_detalle ild ON ild.id_detalle_orden_ingreso = id.id " +
                    "LEFT JOIN desintegracion d ON d.id_detalle_orden_ingreso = id.id " +
                    "WHERE id.id = :idDetalle",
            nativeQuery = true)
    Optional<DetalleOrdenIngresoDTO> findDetalleOrdenIngresoById(@Param("idDetalle") Long idDetalle);

}
