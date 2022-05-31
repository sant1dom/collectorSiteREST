package org.swa.model;

import java.util.List;

public class Utente {
    private String nome;
    private String cognome;
    private String email;
    private String password;
    private String username;
    private List<Collezione> collezioni;
    private List<Disco> dischi;

    public Utente(String nome, String cognome, String email, String password, String username, List<Collezione> collezioni, List<Disco> dischi) {
        super();
        this.nome = nome;
        this.cognome = cognome;
        this.email = email;
        this.password = password;
        this.username = username;
        this.collezioni = collezioni;
        this.dischi = dischi;
    }

    public Utente(){
        super();
        this.nome = "";
        this.cognome = "";
        this.email = "";
        this.password = "";
        this.username = "";
        this.collezioni = null;
        this.dischi = null;
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

    
    public String getEmail() {
        return email;
    }

    
    public void setEmail(String email) {
        this.email = email;
    }

    
    public String getPassword() {
        return this.password;
    }

    
    public void setPassword(String password) {
        this.password = password;
    }

    
    public String getUsername() {
        return username;
    }

    
    public void setUsername(String username) {
        this.username = username;
    }

    
    public List<Collezione> getCollezioni() {
        return collezioni;
    }

    
    public void setCollezioni(List<Collezione> collezioni) {
        this.collezioni = collezioni;
    }

    
    public void addCollezione(Collezione collezione) {
        this.collezioni.add(collezione);
    }

    
    public void removeCollezione(Collezione collezione) {
        this.collezioni.remove(collezione);
    }

    
    public List<Disco> getDischi() {
        return dischi;
    }

    
    public void setDischi(List<Disco> dischi) {
        this.dischi = dischi;
    }


    
    public void addDisco(Disco disco) {
        this.dischi.add(disco);
    }

    
    public void removeDisco(Disco disco) {
        this.dischi.remove(disco);
    }
}