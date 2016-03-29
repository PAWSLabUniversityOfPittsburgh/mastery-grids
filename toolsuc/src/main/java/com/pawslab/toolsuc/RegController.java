package com.pawslab.toolsuc;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.pawslab.toolsuc.service.UserRegister;
import com.pawslab.toolsuc.user.UserModel;

@Controller
public class RegController {

	@Autowired
	private UserRegister userRegisterService;
	
	//for registration form
	@RequestMapping(value = "/register", method = RequestMethod.GET)
	public ModelAndView regForm(){
		ModelAndView model = new ModelAndView();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if(!(auth instanceof AnonymousAuthenticationToken))
			model.addObject("goback", "window.history.back();");
		model.setViewName("reg");
		return model;
	}
	
	//for registration actions
	@RequestMapping(value = "/register", method = RequestMethod.POST)
	public @ResponseBody HashMap<String, String> createUser(@RequestBody UserModel user){
		//do some pre settings for the registration
		user.setUri("");
		user.setIsGroup(0);
		user.setIsAnyGroup(0);
		user.setSync(1);
		user.setHow("Authoring Tools");
		user.setIsInstructor(0);
		HashMap<String, String> rs = userRegisterService.addUserToDB(user);
		return rs;
	}
}
