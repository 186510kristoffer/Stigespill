package no.hvl.dat109.spring_stigespill.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;

/**
 * Representerer et spill som pågår.
 * Holder styr på spillere, historikk (logg), hvem sin tur det er, og status.
 */
@Entity
public class Spill {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
    
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name="spill_id")
    private List<Spiller> spillere = new ArrayList<>();
    
    @OneToMany(mappedBy = "spill", cascade = CascadeType.ALL)
    private List<Trekk> trekkLogg = new ArrayList<>();
    
    private int nesteSpillerIndex;
    private boolean ferdig;
    
    protected Spill() {} 
    
    /**
     * Oppretter et nytt spill med gitte spillere.
     * @param spillere Listen med spillere som deltar.
     */
    public Spill(List<Spiller> spillere){
        this.spillere = spillere;
        this.nesteSpillerIndex = 0;
        this.ferdig = false;
    }
    
    /**
     * Legger til et trekk i loggen.
     * @param trekk Trekket som skal lagres.
     */
    public void leggTilTrekk (Trekk trekk) {
        this.trekkLogg.add(trekk);
    }
    
    /**
     * Henter spilleren som har neste tur.
     * @return Spiller-objektet.
     */
    public Spiller nesteSpiller() {
        return spillere.get(nesteSpillerIndex);
    }
    
    public Long getId() {
        return id;
    }

    /**
     * Setter spillets ID manuelt.
     * Brukes kun ved testing.
     * @param id Ny ID.
     */
    public void setId(Long id) {
        this.id = id;
    }
    
    public List<Spiller> getSpillere() { return spillere; }
    
    public void setSpillere(List<Spiller> spillere) { 
        this.spillere = spillere; 
    }
    
    public List<Trekk> getTrekkLogg() {
        return trekkLogg; }
        
    public int getNesteSpillerIndex() { 
        return nesteSpillerIndex; 
    }
    
    public void setNesteSpillerIndex(int nesteSpillerIndex) { 
        this.nesteSpillerIndex = nesteSpillerIndex; 
    }

    public boolean erFerdig() { 
        return ferdig;
    }
    
    public void setFerdig(boolean ferdig) {
        this.ferdig = ferdig; 
    }
}