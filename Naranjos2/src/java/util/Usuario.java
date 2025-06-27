/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.util.ArrayList;

/**
 *
 * @author mcamargo
 */
public class Usuario {
    
    private String id;
    private String status;
    private String identificacion;
    private String nombre;
    private String email;
    private String celular;
    private String id_usuario;
    private String clave;
    private String usuario_creador;
    private String fecha_creacion;
    private String usuario_modifica;
    private String fecha_modifica;    
    private boolean cambio_clave;
    
    
    public Usuario() {
    }

    public Usuario(String id, String status, String identificacion,String nombre, String email, String celular, String id_usuario
                   ,String clave,String usuario_creador, String fecha_creacion, String usuario_modifica, String fecha_modifica, boolean cambio_clave) {
        
        
        this.id=id;
        this.status=status;
        this.identificacion=identificacion;
        this.nombre=nombre;
        this.email=email;
        this.celular=celular;
        this.id_usuario=id_usuario;
        this.clave=clave;
        this.usuario_creador=usuario_creador;
        this.fecha_creacion=fecha_creacion;
        this.usuario_modifica=usuario_modifica;
        this.fecha_modifica=fecha_modifica;  
        this.cambio_clave=cambio_clave;
        
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getIdentificacion() {
        return identificacion;
    }

    public void setIdentificacion(String identificacion) {
        this.identificacion = identificacion;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }

    public String getId_usuario() {
        return id_usuario;
    }

    public void setId_usuario(String id_usuario) {
        this.id_usuario = id_usuario;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public String getUsuario_creador() {
        return usuario_creador;
    }

    public void setUsuario_creador(String usuario_creador) {
        this.usuario_creador = usuario_creador;
    }

    public String getFecha_creacion() {
        return fecha_creacion;
    }

    public void setFecha_creacion(String fecha_creacion) {
        this.fecha_creacion = fecha_creacion;
    }

    public String getUsuario_modifica() {
        return usuario_modifica;
    }

    public void setUsuario_modifica(String usuario_modifica) {
        this.usuario_modifica = usuario_modifica;
    }

    public String getFecha_modifica() {
        return fecha_modifica;
    }

    public void setFecha_modifica(String fecha_modifica) {
        this.fecha_modifica = fecha_modifica;
    }

    public boolean getCambio_clave() {
        return cambio_clave;
    }

    public void setCambio_clave(boolean cambio_clave) {
        this.cambio_clave = cambio_clave;
    }
    
}
