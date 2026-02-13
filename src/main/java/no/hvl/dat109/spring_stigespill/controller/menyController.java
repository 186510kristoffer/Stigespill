package no.hvl.dat109.spring_stigespill.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import no.hvl.dat109.spring_stigespill.service.StigespillService;

@Controller
public class menyController {
	@Autowired StigespillService stigespillService;
	
	@GetMapping("/")
	public String visMeny() {
		return "/meny";
	}
}
