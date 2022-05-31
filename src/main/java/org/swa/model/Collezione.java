package org.swa.model;

import java.time.LocalDate;
import java.util.List;

public class Collezione {
     private String titolo;
     private String privacy;
     private LocalDate dataCreazione;
     private Utente utente;
     private List<Disco> dischi;
     private List<Utente> utentiCondivisione;

     public Collezione() {
          this.titolo = "";
          this.privacy = "";
          this.dataCreazione = LocalDate.now();
          this.utente = null;
          this.dischi = null;
          this.utentiCondivisione = null;
     }

     public Collezione(String titolo, String privacy, Utente utente, List<Disco> dischi, List<Utente> utentiCondivisione) {
          this.titolo = titolo;
          this.privacy = privacy;
          this.dataCreazione = LocalDate.now();
          this.utente = utente;
          this.dischi = dischi;
          this.utentiCondivisione = utentiCondivisione;
     }

     
     public String getTitolo() {
          return titolo;
     }

     
     public void setTitolo(String titolo) {
          this.titolo = titolo;
     }

     
     public String getPrivacy() {
          return privacy;
     }

     
     public void setPrivacy(String privacy) {
          this.privacy = privacy;
     }

     
     public Utente getUtente() {
          return utente;
     }

     
     public void setUtente(Utente utente) {
          this.utente = utente;
     }

     
     public LocalDate getDataCreazione() {
          return this.dataCreazione;
     }

     
     public void setDataCreazione(LocalDate dataCreazione) {
          this.dataCreazione = dataCreazione;
     }

     
     public List<Disco> getDischi() {
          return dischi;
     }

     
     public void setDischi(List<Disco> dischi) {
          this.dischi = dischi;
     }

     
     public List<Utente> getUtentiCondivisi() {
          return this.utentiCondivisione;
     }

     
     public void setUtentiCondivisi(List<Utente> utentiCondivisione) {
          this.utentiCondivisione = utentiCondivisione;
     }

     
     public void addUtenteCondiviso(Utente utente) {
          if (this.utentiCondivisione == null) {
               this.utentiCondivisione = new java.util.ArrayList<>();
          }
          this.utentiCondivisione.add(utente);
     }

     
     public void removeUtenteCondiviso(Utente utente) {
          if (this.utentiCondivisione != null) {
               this.utentiCondivisione.remove(utente);
          }
     }
}
