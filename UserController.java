package com.jeffreysmith.letsball.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.jeffreysmith.letsball.models.LoggedInUser;
import com.jeffreysmith.letsball.models.User;
import com.jeffreysmith.letsball.service.GameService;
import com.jeffreysmith.letsball.service.UserService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class UserController {
		@Autowired
		UserService users;
		@Autowired
		GameService games;


	@GetMapping("/")
    public String index(Model model) {
		model.addAttribute("games", games.getAllByNameAsc());
        return "home.jsp";

    }
	@GetMapping("/register")
	 public String register(Model model) {
	        model.addAttribute("newUser", new User());
	        model.addAttribute("newLogin", new LoggedInUser());
	        return "register.jsp";
	    }

    @PostMapping("/register/user")
    public String registerUser(@Valid @ModelAttribute("newUser") User newUser, BindingResult result, RedirectAttributes redirectAttribute, HttpSession session, Model model) {
    	users.register(newUser, result);
    	if(result.hasErrors()) {
    		model.addAttribute("newLogin", new LoggedInUser());
    		return "register.jsp";
    	}else {
    		session.setAttribute("userId", newUser.getId());
    		return "redirect:/home";
    	}
    }

	@GetMapping("/login")
	 public String login(Model model) {
	        model.addAttribute("newUser", new User());
	        model.addAttribute("newLogin", new LoggedInUser());
	        return "login.jsp";
	    }

    @PostMapping("/login/user")
    public String loginUser(@Valid @ModelAttribute("newLogin") LoggedInUser newLogin, BindingResult result,Model model, HttpSession session ) {
    	User user = users.login(newLogin, result);
    	if(result.hasErrors()) {
    		model.addAttribute("newUser", new User());
    		return "login.jsp";
    	}else {
    		session.setAttribute("userId", user.getId());
    		System.out.println(user.getId());
    		return "redirect:/home";
    	}
    }


    @GetMapping("/logout")
    public String logout(HttpSession session) {
    	session.invalidate();
    	return "redirect:/";
    }
}
