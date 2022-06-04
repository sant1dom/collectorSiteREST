package org.swa.collectorsite.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Disco {
    private String titolo;
    private String anno;
    private String etichetta;
    private String barcode;
    private Genere genere;
    private StatoConservazione statoConservazione;
    private Formato formato;
    private LocalDate dataInserimento;
    private Utente utente;
    private List<Autore> autori;
    private List<Image> immagini;
    private List<Traccia> tracce;
    private Disco padre;
    private List<Disco> figli;

    public Disco() {
        this.titolo = "";
        this.anno = "";
        this.etichetta = "";
        this.barcode = "";
        this.genere = null;
        this.statoConservazione = null;
        this.formato = null;
        this.dataInserimento = null;
        this.utente = null;
        this.autori = null;
        this.immagini = null;
        this.tracce = null;
    }

    public Disco(String titolo, String anno, String etichetta, String barcode, Genere genere, StatoConservazione statoConservazione, Formato formato, LocalDate dataInserimento,Utente utente, List<Autore> autori, List<Image> immagini, List<Traccia> tracce) {
        this.titolo = titolo;
        this.anno = anno;
        this.etichetta = etichetta;
        this.barcode = barcode;
        this.genere = genere;
        this.statoConservazione = statoConservazione;
        this.formato = formato;
        this.dataInserimento = dataInserimento;
        this.utente = utente;
        this.autori = autori;
        this.immagini = immagini;
        this.tracce = tracce;
    }

    
    public String getTitolo() {
        return titolo;
    }

    
    public void setTitolo(String titolo) {
        this.titolo = titolo;
    }

    
    public String getAnno() {
        return anno;
    }

    
    public void setAnno(String anno) {
        this.anno = anno;
    }

    
    public String getEtichetta() {
        return etichetta;
    }

    
    public void setEtichetta(String etichetta) {
        this.etichetta = etichetta;
    }

    
    public String getBarCode() {
        return barcode;
    }

    
    public void setBarCode(String barCode) {
        this.barcode = barCode;
    }


    
    public Genere getGenere() {
        return genere;
    }

    
    public void setGenere(Genere genere) {
        this.genere = genere;
    }

    
    public StatoConservazione getStatoConservazione() {
        return statoConservazione;
    }

    
    public void setStatoConservazione(StatoConservazione statoConservazione) {
        this.statoConservazione = statoConservazione;
    }

    
    public Formato getFormato() {
        return formato;
    }

    
    public void setFormato(Formato formato) {
        this.formato = formato;
    }

    
    public LocalDate getDataInserimento() {
        return this.dataInserimento;
    }

    
    public void setDataInserimento(LocalDate dataInserimento) {
        this.dataInserimento = dataInserimento;
    }

    
    public Disco getPadre() {
        return this.padre;
    }

    
    public void setPadre(Disco disco) {
        this.padre = disco;
    }

    
    public List<Disco> getFigli() {
        return this.figli;
    }

    
    public void setFigli(List<Disco> figli) {
        this.figli = figli;
    }

    
    public void addFiglio(Disco disco) {
        if (this.figli == null) {
            this.figli = new ArrayList<>();
        }
        this.figli.add(disco);
    }

    
    public void removeFiglio(Disco disco) {
        if (this.figli != null) {
            this.figli.remove(disco);
        }
    }

    
    public Utente getUtente() {
        return utente;
    }

    
    public void setUtente(Utente utente) {
        this.utente = utente;
    }

    
    public List<Autore> getAutori() {
        return autori;
    }

    
    public void setAutori(List<Autore> autori) {
        this.autori = autori;
    }

    
    public void addAutore(Autore autore) {
        if (this.autori == null) {
            this.autori = new ArrayList<>();
        }
        this.autori.add(autore);
    }

    
    public void removeAutore(Autore autore) {
        if (this.autori != null) {
            this.autori.remove(autore);
        }
    }

    
    public List<Image> getImmagini() {
        return immagini;
    }

    
    public void setImmagini(List<Image> immagini) {
        this.immagini = immagini;
    }

    
    public void addImmagine(Image immagine) {
        if (this.immagini == null) {
            this.immagini = new ArrayList<>();
        }
        this.immagini.add(immagine);
    }

    
    public void removeImmagine(Image immagine) {
        if (this.immagini != null) {
            this.immagini.remove(immagine);
        }
    }

    
    public List<Traccia> getTracce() {
        return tracce;
    }

    
    public void setTracce(List<Traccia> tracce) {
        this.tracce = tracce;
    }

    
    public void addTraccia(Traccia traccia) {
        if(this.tracce == null) {
            this.tracce = new ArrayList<>();
        }
        this.tracce.add(traccia);
    }

    
    public void removeTraccia(Traccia traccia) {
        if (this.tracce != null) {
            this.tracce.remove(traccia);
        }
    }
}
