package com.pawslab.toolsuc.service;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pawslab.toolsuc.user.UserAGGDAO;
import com.pawslab.toolsuc.user.UserModel;
import com.pawslab.toolsuc.user.UserP2DAO;
import com.pawslab.toolsuc.user.UserUM2DAO;

@Component
public class UserRegister {

	@Autowired
	private UserP2DAO p2dao;
	@Autowired
	private UserUM2DAO um2dao;
	@Autowired
	private UserAGGDAO aggdao;
	
	public HashMap<String, String> addUserToDB(UserModel user){
		//result map
		HashMap<String, String> rs = new HashMap<String, String>();
		//check if this user has a record in pt2
		if(this.checkUserP2(user.getLogin())){
			//yes then this username is used
			rs.put("code", "1");
			rs.put("info", "username taken");
		}else{
			//no then put this user into pt2
			boolean p2 = this.addUserP2(user);
			//put record into um2
			boolean um2 = this.addUserUM2(user);
			//put record into agg
			boolean agg = this.addUserAGG(user);
			//if the results are both good, then success
			if(p2 && um2 && agg){
				rs.put("code", "0");
				rs.put("info", "successfully registered");
			}else{
				rs.put("code", "2");
				rs.put("info", "the user is not added successfully");
			}
		}
		return rs;
	}
	
	public void aggUserComplete(String username){
		//check if agg database has this user
		if(!aggdao.isExist(username)){
			//no then fill it in
			UserModel curr = p2dao.findByUsername(username);
			String[] names = curr.getName().split(", ");
			curr.setAffiliation_code("NULL");
			curr.setfName(names[0]);
			if(names.length > 1)
				curr.setlName(names[1]);
			else
				curr.setlName(names[0]);
			aggdao.insert(curr);
		}
	}
	
	private boolean checkUserP2(String username){
		return p2dao.isExist(username);
	}

	private boolean addUserP2(UserModel user){
		boolean userTable = p2dao.insert(user);
		boolean userRelTable = p2dao.buildRel(user);
		boolean userRoleTable = p2dao.buildRole(user);
		return (userTable && userRelTable && userRoleTable);
	}

	private boolean addUserUM2(UserModel user){
		boolean userTable = um2dao.insert(user);
		boolean userRelTable = um2dao.buildRel(user);
		return (userTable && userRelTable);
	}
	
	private boolean addUserAGG(UserModel user){
		return aggdao.insert(user);
	}
}
