package no.hvl.dat109.spring_stigespill.view;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.JPanel;
import org.springframework.core.io.ClassPathResource;
import no.hvl.dat109.spring_stigespill.model.Spiller;

/**
 * Ansvarlig for å tegne selve spillbrettet og brikkene grafisk.
 * Klassen arver fra JPanel og tegnes på nytt hver gang spilltilstanden endres.
 */
public class BrettPanel extends JPanel {
	
    private static final int RUTE_STR = 60;
    private static final int ANTALL_RUTER = 10;
    private static final int BRETT_STR = RUTE_STR * ANTALL_RUTER;
    private List<Spiller> spillere = new ArrayList<>();
    private Image bakgrunnsBilde;

    /**
     * Oppretter panelet og laster inn bakgrunnsbildet fra ressurser.
     */
    public BrettPanel() {
        try {
            // Laster bildet sikkert via Spring sin ClassPathResource
            bakgrunnsBilde = ImageIO.read(new ClassPathResource("Brett.png").getInputStream());
        } catch (IOException e) {
            System.err.println("Feil: Fant ikke Brett.png i src/main/resources!");
            e.printStackTrace();
        }
    }

    /**
     * Oppdaterer listen over spillere og tvinger frem en ny opptegning av brettet.
     *
     * @param nyListe Den oppdaterte listen med spillere fra logikken.
     */
    public void oppdaterSpillere(List<Spiller> nyListe) {
        this.spillere = new ArrayList<>(nyListe);
        repaint(); 
    }

    /**
     * Beregner x,y koordinater i piksler basert på rutenummer (1-100).
     * Tar hensyn til "slange-mønsteret" på brettet.
     *
     * @param posisjon Rutenummeret spilleren står på.
     * @return Et Point-objekt med x og y koordinater for øvre venstre hjørne av ruten.
     */
    private Point getKoordinater(int posisjon) {
        if (posisjon <= 0) return new Point(20, 560); 

        int p = posisjon - 1; 
        int radFraBunn = p / 10;
        int skjermRad = 9 - radFraBunn; 
        int kolonne = p % 10;

        if (radFraBunn % 2 != 0) {
            kolonne = 9 - kolonne;
        }
        return new Point(kolonne * RUTE_STR, skjermRad * RUTE_STR);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (bakgrunnsBilde != null) {
            g.drawImage(bakgrunnsBilde, 0, 0, BRETT_STR, BRETT_STR, this);
        }
        g.setColor(Color.BLACK);
        for (int rad = 0; rad < ANTALL_RUTER; rad++) {
            for (int kol = 0; kol < ANTALL_RUTER; kol++) {
                g.drawRect(kol * RUTE_STR, rad * RUTE_STR, RUTE_STR, RUTE_STR);
            }
        }
        for (Spiller s : spillere) {
            tegneSpiller(g, s);
        }
    }

    /**
     * Hjelpemetode for å tegne en enkelt spiller.
     */
    private void tegneSpiller(Graphics g, Spiller s) {
        Point p = getKoordinater(s.getPosisjon());
        
        String farge = s.getFarge();
        if ("F1".equals(farge)) g.setColor(Color.RED);
        else if ("F2".equals(farge)) g.setColor(Color.BLUE);
        else if ("F3".equals(farge)) g.setColor(Color.GREEN);
        else if ("F4".equals(farge)) g.setColor(Color.YELLOW);
        else g.setColor(Color.MAGENTA);
        
        g.fillOval(p.x + 10, p.y + 10, 40, 40);
        g.setColor(Color.BLACK);
        g.drawOval(p.x + 10, p.y + 10, 40, 40);
        g.drawString(s.getNavn(), p.x + 5, p.y + 55); 
    }
}