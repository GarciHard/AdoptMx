package com.garcihard.adoptmx;

/**
 * Created by garcihard on 10/04/17.
 */

public class UserObj {

    private String nombre;
    private String apellido;
    private String correo;
    private String imagen;

    public UserObj(){}

    public UserObj(String nombre, String apellido, String correo, String imagen) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.correo = correo;
        this.setImagen(imagen);
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }
}
