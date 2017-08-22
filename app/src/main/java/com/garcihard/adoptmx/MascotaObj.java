package com.garcihard.adoptmx;

/**
 * Created by garcihard on 13/04/17.
 */

public class MascotaObj {

    private String idUserM;
    private String especieM;
    private String tamañoM;
    private String edadM;
    private String nombreM;
    private String razaM;
    private String sexoM;
    private String descripcionM;
    private String urlMascota;

    public MascotaObj() {}

    public MascotaObj(String idUserM, String especieM, String tamañoM, String edadM, String nombreM, String razaM, String sexoM, String descripcionM, String urlMascota) {
        this.idUserM = idUserM;
        this.especieM = especieM;
        this.tamañoM = tamañoM;
        this.edadM = edadM;
        this.nombreM = nombreM;
        this.razaM = razaM;
        this.sexoM = sexoM;
        this.descripcionM = descripcionM;
        this.urlMascota = urlMascota;
    }

    public String getIdUserM() {
        return idUserM;
    }

    public void setIdUserM(String idUserM) {
        this.idUserM = idUserM;
    }

    public String getEspecieM() {
        return especieM;
    }

    public void setEspecieM(String especieM) {
        this.especieM = especieM;
    }

    public String getTamañoM() {
        return tamañoM;
    }

    public void setTamañoM(String tamañoM) {
        this.tamañoM = tamañoM;
    }

    public String getEdadM() {
        return edadM;
    }

    public void setEdadM(String edadM) {
        this.edadM = edadM;
    }

    public String getNombreM() {
        return nombreM;
    }

    public void setNombreM(String nombreM) {
        this.nombreM = nombreM;
    }

    public String getRazaM() {
        return razaM;
    }

    public void setRazaM(String razaM) {
        this.razaM = razaM;
    }

    public String getSexoM() {
        return sexoM;
    }

    public void setSexoM(String sexoM) {
        this.sexoM = sexoM;
    }

    public String getDescripcionM() {
        return descripcionM;
    }

    public void setDescripcionM(String descripcionM) {
        this.descripcionM = descripcionM;
    }

    public String getUrlMascota() {
        return urlMascota;
    }

    public void setUrlMascota(String urlMascota) {
        this.urlMascota = urlMascota;
    }
}