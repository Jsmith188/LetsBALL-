package com.jeffreysmith.letsball.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.jeffreysmith.letsball.models.Game;
import com.jeffreysmith.letsball.models.LoggedInUser;
import com.jeffreysmith.letsball.models.User;
import com.jeffreysmith.letsball.service.GameService;
import com.jeffreysmith.letsball.service.UserService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class HomeController {
	@Autowired
	UserService users;
	@Autowired
	GameService games;
	
	
	@GetMapping("/home")
	public String homepage(HttpSession session, Model model) {
		Long userId = (Long) session.getAttribute("userId");
		if (userId == null) {
			return "redirect:/";
		} else {
			model.addAttribute("user", users.getLoggedInUser(userId));
			model.addAttribute("games", games.getAllByNameAsc());
			model.addAttribute("newUser", new User());
		    model.addAttribute("newLogin", new LoggedInUser());
			return "home.jsp";
		}
	}
	@GetMapping("/new/game")
	public String gameForm(@ModelAttribute("game") Game game, HttpSession session, RedirectAttributes redirectAttributes ) {
		Long userId = (Long) session.getAttribute("userId");
		if (userId == null) {
			redirectAttributes.addFlashAttribute("error", "No fields can be blank");
			return "redirect:/register";
		} else {
			return "addNew.jsp";
		}
	}

	@PostMapping("/create/game")
	public String createGame(@Valid @ModelAttribute("game") Game game, BindingResult result, Model model,
			HttpSession session) {
		Long userId = (Long) session.getAttribute("userId");
		if (userId == null) {
			return "redirect:/";
		}
	
		if (result.hasErrors()) {
			return "addNew.jsp";
		} else {
			User commissioner = users.getLoggedInUser(userId);
			game.setUser(commissioner);
			games.createGame(game);
			return "redirect:/home";
		}
	}
	
	@GetMapping("/view/game/{id}")
	public String viewGame(@PathVariable("id") Long id, Model model, HttpSession session,  RedirectAttributes redirectAttributes) {
		Long userId = (Long) session.getAttribute("userId");
		if (userId == null) {
			redirectAttributes.addFlashAttribute("error", "No fields can be blank");
			return "redirect:/register";
		}else {
			model.addAttribute("game", games.getOneGame(id));
			return "viewGame.jsp";
		}
		}


	@GetMapping("/edit/game/{id}")
	public String editForm(@PathVariable("id") Long id, Model model, HttpSession session) {
		Long userId = (Long) session.getAttribute("userId");
		if (userId == null) {
			return "redirect:/";
		}
		model.addAttribute("game", games.getOneGame(id));
		return "editGame.jsp";
	}

	@PutMapping("/update/game/{id}")
	public String updateGame(@Valid @ModelAttribute("game") Game game, BindingResult result, Model model,HttpSession session, RedirectAttributes redirectAttributes) {
		Long userId = (Long) session.getAttribute("userId");
		if (userId == null) {
			return "redirect:/";
		}
		else if (result.hasErrors()) {
			redirectAttributes.addFlashAttribute("error", "No fields can be blank");
			model.addAttribute("game", game);
			return "editGame.jsp" ;
		}
		else {
			game.setUser(users.getLoggedInUser(userId));
			games.update(game);
			return "redirect:/home";
		}
	}
	
	@GetMapping("/join")
	public String joinForm(@ModelAttribute("join") Game game, HttpSession session, RedirectAttributes redirectAttribute, Model model ) {
		Long userId = (Long) session.getAttribute("userId");
		if (userId == null) {
			return "redirect:/register";
		} else {
			model.addAttribute("user", users.getLoggedInUser(userId));
			model.addAttribute("games", games.getAllByNameAsc());
			return "joinGame.jsp";
		}
	}
	
	@PostMapping("/join/game")
	public String joinGame(@Valid @ModelAttribute("game") Game game, BindingResult result, Model model,
			HttpSession session) {
		Long userId = (Long) session.getAttribute("userId");
		if (userId == null) {
			return "redirect:/join";
		}
		if (result.hasErrors()) {
			return "joinGame.jsp";
		} else {
			User commissioner = users.getLoggedInUser(userId);
			game.setUser(commissioner);
			return "redirect:/home";
		}
	}
	
	@DeleteMapping("/delete/game/{gameId}")
	public String deleteGame(@PathVariable("gameId") Long gameId, HttpSession session) {
		Long userId = (Long) session.getAttribute("userId");
		if (userId == null) {
			return "redirect:/";
		}
		games.deleteGame(gameId);
		return "redirect:/home";
	}
}


	

