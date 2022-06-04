package org.swa.collectorsite.model;

import java.util.List;

public class Autore {

    private String nome;
    private String cognome;
    private String nomeArtistico;
    private TipologiaAutore tipologiaAutore;
    private List<Disco> dischi;
    private List<Traccia> tracce;

    public Autore(){
        this.nome = "";
        this.cognome = "";
        this.nomeArtistico = "";
        this.tipologiaAutore = null;
        this.dischi = null;
        this.tracce = null;
    }

    public Autore(String nome, String cognome, String nomeArtistico, TipologiaAutore tipologiaAutore, List<Disco> dischi, List<Traccia> tracce) {
        this.nome = nome;
        this.cognome = cognome;
        this.nomeArtistico = nomeArtistico;
        this.tipologiaAutore = tipologiaAutore;
        this.dischi = dischi;
        this.tracce = tracce;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCognome() {
        return cognome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    public String getNomeArtistico() {
        return nomeArtistico;
    }

    public void setNomeArtistico(String nomeArtistico) {
        this.nomeArtistico = nomeArtistico;
    }

    public TipologiaAutore getTipologia() {
        return tipologiaAutore;
    }

    public void setTipologia(TipologiaAutore tipologiaAutore) {
        this.tipologiaAutore = tipologiaAutore;
    }

    public List<Disco> getDischi() {
        return this.dischi;
    }

    public void setDischi(List<Disco> dischi) {
        this.dischi = dischi;
    }

    public List<Traccia> getTracce() {
        return this.tracce;
    }

    public void setTracce(List<Traccia> tracce) {
        this.tracce = tracce;
    }
}
