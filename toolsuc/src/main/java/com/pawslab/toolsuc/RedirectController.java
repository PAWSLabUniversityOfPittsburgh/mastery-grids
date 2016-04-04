package com.pawslab.toolsuc;


import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.pawslab.toolsuc.service.LoginGroupAuth;

 
@Controller
public class RedirectController{
	
	@Autowired
	private LoginGroupAuth loginGA;
	
	//admins' redirect handler
	@RequestMapping(value="/redirect/admin", method = RequestMethod.GET)
	public String redirectAdmin(HttpServletRequest request, @RequestParam(value = "siteName", required = true) String siteName) {
		UserDetails userDetails = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String host = request.getRequestURL().toString().replace(request.getRequestURI(), "");
		if (siteName.equals("ca"))
			return "redirect:"+host+"/course-authoring/?username="+userDetails.getUsername();
		else if (siteName.equals("ga"))
			return "redirect:/redirect/admin/groupAuthoring";
		else
			return "redirect:/";
//		switch(siteName){
//			case "ca": return "redirect:"+host+"/course-authoring/?username="+userDetails.getUsername();
//			case "ga": return "redirect:/redirect/admin/groupAuthoring";
//			default: return "redirect:/";
//		}
	}
	
	//redirect handler specialized for groupAuthoring tool
	@RequestMapping(value="/redirect/admin/groupAuthoring", method = RequestMethod.GET)
	public ModelAndView gaRedirect(HttpServletRequest request){
		ModelAndView model = new ModelAndView();
		String host = request.getRequestURL().toString().replace(request.getRequestURI(), "");
		UserDetails userDetails = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		model.addObject("gaUsername", userDetails.getUsername());
		model.addObject("gaPassword", loginGA.getPass(userDetails.getUsername()));
		model.addObject("gaURL", host+"/GroupAuthoring/LoginService");
		model.addObject("script", "document.getElementById(\"gaForm\").submit();");
		model.setViewName("redirect");
		return model;
	}
	
	//students' redirect handler
	@RequestMapping(value="/redirect/student", method = RequestMethod.GET)
	public String redirectStudent(HttpServletRequest request, @RequestParam(value = "siteName", required = true) String siteName) {
		String host = request.getRequestURL().toString().replace(request.getRequestURI(), "");
		if (siteName.equals("mg"))
			return "redirect:"+host+"/mastergrid";
		else
			return "redirect:/";
//		switch(siteName){
//			case "mg": return "redirect:"+host+"/mastergrid";
//			default: return "redirect:/";
//		}
		}
}