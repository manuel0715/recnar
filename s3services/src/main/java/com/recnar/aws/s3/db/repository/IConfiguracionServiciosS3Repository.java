package com.recnar.aws.s3.db.repository;

import com.recnar.aws.s3.db.entity.ConfiguracionServiciosS3;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IConfiguracionServiciosS3Repository extends JpaRepository<ConfiguracionServiciosS3,Long> {
}
