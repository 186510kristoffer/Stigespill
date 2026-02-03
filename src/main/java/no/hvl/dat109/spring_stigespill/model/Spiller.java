package no.hvl.dat109.spring_stigespill.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

/**
 * Representerer en deltaker i stigespillet.
 */
@Entity
public class Spiller {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String navn;
	private String farge;
	private int posisjon;
	private int antallSekserePaaRad;
	
	protected Spiller() { //tom konstruktør for jpa
	}
	
	/**
	 * Oppretter en ny spiller med navn og farge, startposisjon settes til 1.
	 */
	public Spiller(String navn, String farge) {
		this.navn=navn;
		this.farge=farge;
		this.posisjon=1; 
		this.antallSekserePaaRad=0;
	}
	
	public Long getId() {
		return this.id;
	}
	
	public String getNavn() {
		return this.navn;
	}
	
	public void setNavn(String nyttNavn) {
		this.navn=nyttNavn;
	}
	
	public String getFarge() {
        return farge;
    }

    public void setFarge(String farge) {
        this.farge = farge;
    }
	
	public int getPosisjon() {
		return this.posisjon;
	}

	public void setPosisjon(int nyPosisjon) {
		this.posisjon=nyPosisjon;
	}
	
	public int getAntallSekserePaaRad() {
		return this.antallSekserePaaRad;
	}
	
	public void setAntallSekserePaaRad(int nyttAntall) {
		this.antallSekserePaaRad=nyttAntall;
	}
}