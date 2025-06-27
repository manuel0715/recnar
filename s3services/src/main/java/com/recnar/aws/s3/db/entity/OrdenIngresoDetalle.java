package com.recnar.aws.s3.db.entity;

import javax.persistence.*;

@Entity
@Table(name = "orden_ingreso_detalle")
public class OrdenIngresoDetalle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "codigo_orden_ingreso")
    private OrdenIngreso ordenIngreso;

    private String certificadoGenerado;

    private String aprobado;

    private int idCategoria;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public OrdenIngreso getOrdenIngreso() {
        return ordenIngreso;
    }

    public void setOrdenIngreso(OrdenIngreso ordenIngreso) {
        this.ordenIngreso = ordenIngreso;
    }

    public String getCertificadoGenerado() {
        return certificadoGenerado;
    }

    public void setCertificadoGenerado(String certificadoGenerado) {
        this.certificadoGenerado = certificadoGenerado;
    }

    public String getAprobado() {
        return aprobado;
    }

    public void setAprobado(String aprobado) {
        this.aprobado = aprobado;
    }

    public int getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(int idCategoria) {
        this.idCategoria = idCategoria;
    }
}
