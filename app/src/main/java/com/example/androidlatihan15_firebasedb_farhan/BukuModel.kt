package com.example.androidlatihan15_firebasedb_farhan

class BukuModel {
    private var namaPenulis: String? = null
    private var tanggal: String? = null
    private var judulBuku: String? = null
    private var id: String? = null
    private var description: String? = null

    constructor()
    constructor(nama: String, tanggal: String, judul: String) {
        this.namaPenulis = nama
        this.tanggal = tanggal
        this.judulBuku = judul
    }

    fun getNamaPenulis(): String {
        return namaPenulis!!
    }

    fun getTanggal(): String {
        return tanggal!!
    }

    fun getJudulBuku(): String {
        return judulBuku!!
    }

    fun getId(): String {
        return id!!
    }

    fun getDesc(): String {
        return description!!
    }

    fun setNama(nama: String) {
        this.namaPenulis = nama
    }

    fun setTanggal(tanggal: String) {
        this.tanggal = tanggal
    }

    fun setJudul(judul: String) {
        this.judulBuku = judul
    }

    fun setId(id: String) {
        this.id = id
    }

    fun setDesc(description: String) {
        this.description = description
    }

}