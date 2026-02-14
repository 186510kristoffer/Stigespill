package no.hvl.dat109.spring_stigespill.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import no.hvl.dat109.spring_stigespill.dto.SpillOppsett;
import no.hvl.dat109.spring_stigespill.model.Spill;
import no.hvl.dat109.spring_stigespill.model.Spiller;
import no.hvl.dat109.spring_stigespill.service.OppsettService;
import no.hvl.dat109.spring_stigespill.service.StigespillService;

@Controller
public class StartSpillController {
	@Autowired StigespillService stigespillService;
	@Autowired OppsettService oppsettService;
	
	
	@GetMapping("/oppsett")
	public String visNyttSpillMeny(Model model) {
		
		model.addAttribute("oppsett", new SpillOppsett());
		return "oppsett";
	}
	
	@PostMapping("/oppsett")
	public String settOppNyttSpill(Model model, RedirectAttributes ra,
			@ModelAttribute SpillOppsett spillOppsett) {
		
		List <Spiller> spillere = oppsettService.lagSpillerListe(spillOppsett);
		Spill nyttSpill = stigespillService.opprettNyttSpill(spillere);
		
		
		return "redirect:/spill/" + nyttSpill.getId();
	}
	
}
