package com.recnar.aws.s3.db.repository;

import com.recnar.aws.s3.db.entity.ArchivosRecnar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IArchivosRecnar extends JpaRepository<ArchivosRecnar,Long> {

    List<ArchivosRecnar> findByCodigo(String codigo);

    Optional<ArchivosRecnar> findById(Long id);

    List<ArchivosRecnar> findByCodigoPadre(String codigoPadre);
}
