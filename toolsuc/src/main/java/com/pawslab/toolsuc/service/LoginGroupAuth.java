package com.pawslab.toolsuc.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pawslab.toolsuc.user.UserP2DAO;

@Component
public class LoginGroupAuth {
	
	@Autowired
	private UserP2DAO p2dao;
	
	public String getPass(String username){
		return p2dao.getPassword(username);
	}
}
