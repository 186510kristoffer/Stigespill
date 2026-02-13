package no.hvl.dat109.spring_stigespill.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import javax.swing.JFrame;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import no.hvl.dat109.spring_stigespill.model.Spill;
import no.hvl.dat109.spring_stigespill.model.Spiller;
import no.hvl.dat109.spring_stigespill.service.StigespillService;
import no.hvl.dat109.spring_stigespill.view.BrettPanel;

/**
 * Kontroller som styrer flyten i spillet.
 * Inneholder nå en hovedmeny for å velge mellom nytt spill eller historikk.
 */
@Controller
public class SpillController {
	
	@Autowired StigespillService stigespillService;

	@GetMapping("/{id}")
	public String visStigespill(@PathVariable("id") Long id, Model model) {
		
		Spill spill = stigespillService.hentSpill(id);
		model.addAttribute("spill", spill);
		return "/stigespill";
		}
	
	@PostMapping("/{id}/trill")
	public String trillTerning(@PathVariable("id") Long id, RedirectAttributes ra) {
		stigespillService.spillTur(id);
		ra.addAttribute("id", id);
		return "redirect:/spill{id}";
	}	
}