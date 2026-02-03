package no.hvl.dat109.spring_stigespill.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

/**
 * Representerer et utført trekk (historikk) i spillet.
 */
@Entity
public class Trekk {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	private int terningkast;
	private int fraRute;
	private int tilRute;
	private String spillerNavn;
	private LocalDateTime tidspunkt;
	
	@ManyToOne
	@JoinColumn(name="spill_id")
	private Spill spill;
	
	protected Trekk() {}  //tom konstruktør for jpa
	
	/** Oppretter et trekk med informasjon om kast, flytting og tidspunkt. */
	public Trekk(Spill spill, int terningkast, int fraRute,
			int tilRute, String spillerNavn, 
			LocalDateTime tidspunkt) {
		
		this.spill=spill;
		this.spillerNavn=spillerNavn;
		this.terningkast=terningkast;
		this.fraRute=fraRute;
		this.tilRute=tilRute;
		this.tidspunkt=tidspunkt;
	}
	
	public Long getId() {return id;}
	public String getSpillerNavn() {return spillerNavn;}
	public int getTerningkast() {return terningkast;}
	public int getFraRute() {return fraRute;}
	public int getTilRute() {return tilRute;}
	public LocalDateTime getTidspunkt() {return tidspunkt;}
	
}