package com.pawslab.toolsuc;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.pawslab.toolsuc.service.UserRegister;

 
@Controller
public class MainController{
	
	@Autowired
	private UserRegister ur;
	
	//for the main portal page
	@RequestMapping(value = { "/", "/home" }, method = RequestMethod.GET)
	public ModelAndView defaultPage(){
		ModelAndView model = new ModelAndView();
		UserDetails userDetails = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		int hash = userDetails.hashCode();
		ur.aggUserComplete(userDetails.getUsername());
		model.addObject("hashVal", hash);
		model.setViewName("home");
		return model;
	}
	
	//for login page
	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public ModelAndView login(@RequestParam(value = "error", required = false) String error,
		@RequestParam(value = "logout", required = false) String logout){
		ModelAndView model = new ModelAndView();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if(!(auth instanceof AnonymousAuthenticationToken))
			model.addObject("goback", "window.history.back();");
		if(error != null)
			model.addObject("error", "Invalid username and password!");
		if(logout != null)
			model.addObject("msg", "You've been logged out successfully.");
		model.setViewName("login");
		return model;
	}
	
	//for other sites' request
	@RequestMapping(value = "/centralCheck", method = RequestMethod.GET)
	public @ResponseBody HashMap<String, String> centralCheck(@RequestParam(value = "username", required = true) String username,
			@RequestParam(value = "hashVal", required = true) String hash){
		HashMap<String, String> rs = new HashMap<String, String>();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if(auth instanceof AnonymousAuthenticationToken){
			rs.put("status", "0");
			rs.put("reason", "not signed in");
			return rs;
		}else{
			UserDetails userDetails = (UserDetails)auth.getPrincipal();
			int realHash = userDetails.hashCode();
			String realUsername = userDetails.getUsername();
			if(hash.equals(String.valueOf(realHash)) && username.equals(realUsername)){
				rs.put("status", "1");
				return rs;
			}else{
				rs.put("status", "0");
				rs.put("reason", "wrong attempt");
				return rs;
			}
		}
	}
}