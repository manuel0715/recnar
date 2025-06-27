package com.recnar.aws.s3.db.entity;

import javax.persistence.*;

@Entity
@Table(name = "orden_ingreso")
public class OrdenIngreso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String codigoOrdenIngreso;

    private String nitProveedor;

    public String getCodigoOrdenIngreso() {
        return codigoOrdenIngreso;
    }

    public void setCodigoOrdenIngreso(String codigoOrdenIngreso) {
        this.codigoOrdenIngreso = codigoOrdenIngreso;
    }

    public String getNitProveedor() {
        return nitProveedor;
    }

    public void setNitProveedor(String nitProveedor) {
        this.nitProveedor = nitProveedor;
    }
}
