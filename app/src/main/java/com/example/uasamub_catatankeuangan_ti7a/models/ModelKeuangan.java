package com.example.uasamub_catatankeuangan_ti7a.models;

public class ModelKeuangan {

    private String key;
    private String kategori;
    private String nominal;
    private String keterangan;
    private String tanggal;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getKategori() {
        return kategori;
    }

    public void setKategori(String kategori) {
        this.kategori = kategori;
    }

    public String getNominal() {
        return nominal;
    }

    public void setNominal(String nominal) {
        this.nominal = nominal;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }


    //Membuat Konstuktor kosong untuk membaca data snapshot
    public ModelKeuangan(){
    }

    //Konstruktor dengan beberapa parameter, untuk mendapatkan Input Data dari User
    public ModelKeuangan(String kategori, String nominal, String keterangan, String tanggal) {
        this.kategori = kategori;
        this.nominal = nominal;
        this.keterangan = keterangan;
        this.tanggal = tanggal;
    }
}
