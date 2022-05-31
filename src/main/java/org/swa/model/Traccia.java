package org.swa.model;

import java.time.LocalDate;
import java.util.List;

public class Traccia{
    private String titolo;
    private int durata;
    private String iswc;
    private LocalDate dataInserimento;
    private Traccia padre;
    private List<Autore> autori;
    private List<Traccia> figli;
    private List<Disco> dischi;

    public Traccia(String titolo, int durata, String iswc, List<Autore> autori) {
        this.titolo = titolo;
        this.durata = durata;
        this.iswc = iswc;
        this.autori = autori;
    }

    public Traccia() {
        this.titolo = "";
        this.durata = 0;
        this.iswc = "";
        this.autori = null;
    }

    
    public String getTitolo() {
        return titolo;
    }

    
    public void setTitolo(String titolo) {
        this.titolo = titolo;
    }

    
    public int getDurata() {
        return durata;
    }

    
    public void setDurata(int durata) {
        this.durata = durata;
    }

    
    public String getISWC() {
        return this.iswc;
    }

    
    public void setISWC(String iswc) {
        this.iswc = iswc;
    }

    
    public LocalDate getDataInserimento() {
        return this.dataInserimento;
    }

    
    public void setDataInserimento(LocalDate dataInserimento) {
        this.dataInserimento = dataInserimento;
    }


    
    public List<Autore> getAutori() {
        return autori;
    }

    
    public void setAutori(List<Autore> autori) {
        this.autori = autori;
    }

    
    public void addAutore(Autore autore) {
        if (this.autori == null) {
            this.autori = new java.util.ArrayList<>();
        }
        this.autori.add(autore);
    }

    
    public void removeAutore(Autore autore) {
        if (this.autori != null) {
            this.autori.remove(autore);
        }
    }

    
    public List<Traccia> getFigli() {
        return this.figli;
    }

    
    public void setFigli(List<Traccia> figli) {
        this.figli = figli;
    }

    
    public void addFiglio(Traccia figlio) {
        if (this.figli == null) {
            this.figli = new java.util.ArrayList<>();
        }
        this.figli.add(figlio);
    }

    
    public void removeFiglio(Traccia figlio) {
        if (this.figli != null) {
            this.figli.remove(figlio);
        }
    }

    
    public List<Disco> getDischi() {
        return this.dischi;
    }

    
    public void setDischi(List<Disco> dischi) {
        this.dischi = dischi;
    }

    
    public void setPadre(Traccia padre) {
        this.padre = padre;
    }

    
    public Traccia getPadre() {
        return this.padre;
    }
}
