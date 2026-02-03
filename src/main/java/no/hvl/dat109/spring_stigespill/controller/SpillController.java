package no.hvl.dat109.spring_stigespill.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import javax.swing.JFrame;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import no.hvl.dat109.spring_stigespill.model.Spill;
import no.hvl.dat109.spring_stigespill.model.Spiller;
import no.hvl.dat109.spring_stigespill.service.StigespillService;
import no.hvl.dat109.spring_stigespill.view.BrettPanel;

/**
 * Kontroller som styrer flyten i spillet.
 * Inneholder nå en hovedmeny for å velge mellom nytt spill eller historikk.
 */
@Component
public class SpillController implements CommandLineRunner {

    @Autowired
    private StigespillService service;

    private BrettPanel brettPanel;

    @Override
    public void run(String... args) {
        System.setProperty("java.awt.headless", "false");

        // Setter opp vinduet én gang ved start
        JFrame vindu = new JFrame("Stigespill");
        brettPanel = new BrettPanel();
        vindu.add(brettPanel);
        vindu.setSize(620, 650);
        vindu.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        vindu.setResizable(false);
        vindu.setVisible(true);

        Scanner sc = new Scanner(System.in);
        
        while (true) {
            System.out.println("\n=== HOVEDMENY ===");
            System.out.println("1. Start nytt spill");
            System.out.println("2. Hent logg fra gammelt spill (via ID)");
            System.out.println("q. Avslutt");
            System.out.print("Valg: ");
            
            String valg = sc.nextLine();

            if ("q".equals(valg)) {
                System.out.println("Takk for nå!");
                break;
            } else if ("1".equals(valg)) {
                kjoreNyttSpill(sc);
            } else if ("2".equals(valg)) {
                hentGammelLogg(sc);
            } else {
                System.out.println("Ugyldig valg.");
            }
        }
        
        sc.close();
        System.exit(0); 
    }

    /**
     * Kjører logikken for et nytt spill.
     */
    private void kjoreNyttSpill(Scanner sc) {
        System.out.println("\n--- NYTT SPILL ---");
        int antall;
        while (true) {
            System.out.print("Antall spillere (2–4): ");
            try {
                antall = Integer.parseInt(sc.nextLine());

                if (antall < 2 || antall > 4) {
                    System.out.println("Antall spillere må være mellom 2 og 4.");
                } else {
                    break; // gyldig input
                }

            } catch (NumberFormatException e) {
                System.out.println("Ugyldig input. Skriv et tall.");
            }
        }

        List<Spiller> spillere = new ArrayList<>();
        for (int i = 1; i <= antall; i++) {
            System.out.print("Navn spiller " + i + ": ");
            spillere.add(new Spiller(sc.nextLine(), "F" + i));
        }

        Spill spill = service.opprettNyttSpill(spillere);
        brettPanel.oppdaterSpillere(spill.getSpillere());

        System.out.println("Spill opprettet med ID: " + spill.getId());
        System.out.println("Noter deg ID-en hvis du vil se loggen senere!");
        System.out.println("Trykk ENTER for å trille. 'logg' for historikk. 'q' for å gå til meny.");

        
        while (true) {
            String input = sc.nextLine();
            
            if ("q".equals(input)) {
                System.out.println("Avslutter spillet og går til meny...");
                break;
            } else if ("logg".equals(input)) {
                visLogg(spill.getId());
                continue; 
            }

            String melding = service.spillTur(spill.getId());
            System.out.println(melding);

            Spill oppdatert = service.hentSpill(spill.getId());
            brettPanel.oppdaterSpillere(oppdatert.getSpillere());

            if (oppdatert.erFerdig()) {
                System.out.println("--- SPILLET ER SLUTT ---");
                System.out.println("\nTrykk ENTER for å gå til hovedmeny...");
                sc.nextLine();
                break;
            }
        }
    }

    /**
     * Lar brukeren skrive inn en ID for å hente logg.
     */
    private void hentGammelLogg(Scanner sc) {
        System.out.print("Skriv inn Spill-ID: ");
        try {
            Long id = Long.parseLong(sc.nextLine());
            visLogg(id);
        } catch (NumberFormatException e) {
            System.out.println("Ugyldig ID");
        }
        System.out.println("\nTrykk ENTER for å gå tilbake");
        sc.nextLine();
    }
    
    /**
     * Hjelpemetode som printer loggen.
     */
    private void visLogg(Long spillId) {
        List<String> logg = service.hentLogg(spillId);
        
        if (logg.isEmpty()) {
            System.out.println("Fant ingen logg for spill ID " + spillId + " (eller loggen er tom)");
        } else {
            System.out.println("\n LOGG FOR SPILL " + spillId + " ---");
            for (String linje : logg) {
                System.out.println(linje);
            }
        }
    }
}