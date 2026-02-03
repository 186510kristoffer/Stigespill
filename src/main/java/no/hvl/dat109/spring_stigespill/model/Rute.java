package no.hvl.dat109.spring_stigespill.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

/**
 * Representerer en rute på brettet, som kan være en vanlig rute, 
 * en stige (opp) eller en slange (ned).
 */
@Entity
public class Rute {
    @Id
    private int id;
    private int flyttTil;
    
    protected Rute() { //tom konstruktør for database
    }
    
    /**
     * Oppretter en rute med en destinasjon. 
     * flyttTil = 0 betyr vanlig rute uten hopping.
     */
    public Rute(int id, int flyttTil){
    	this.id=id;
    	this.flyttTil=flyttTil;
    }
    
    /** Sjekker om ruten er en stige (flytter spilleren fremover). */
    public boolean erStige() {
    	return flyttTil > id;
    }
    
    /** Sjekker om ruten er en slange (flytter spilleren bakover). */
    public boolean erSlange() {
    	return flyttTil < id && flyttTil !=0;
    }
    
    /** Sjekker om ruten inneholder en flytting (stige eller slange). */
    public boolean erSpesialRute() {
    	return flyttTil != 0 && flyttTil !=id;
    }
    
    public int getId() {
    	return this.id;
    }
    
    public int getFlyttTil() {
    	return this.flyttTil;
    }
    
    public void setFlyttTil(int flyttTil) {
        this.flyttTil = flyttTil;
    }
    
}