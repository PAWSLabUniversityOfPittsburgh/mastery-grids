package com.pawslab.toolsuc.user;

import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class UserAGGDAO extends JdbcDaoSupport implements UserDAO {
	
	public boolean insert(UserModel user) {
		String sql = "INSERT INTO `aggregate`.`ent_creator` (`creator_id`, `creator_name`, `affiliation`, `affiliation_code`)"
				+ "VALUES (?, ?, ?, ?)";
		int no = getJdbcTemplate().update(sql, new Object[]{
			user.getLogin(),
			user.getfName() + ", " + user.getlName(),
			user.getOrganization(),
			user.getAffiliation_code().toUpperCase()
		});
		if(no > 0){
			return true;
		}else{
			return false;
		}
	}

	public boolean isExist(String username) {
		//sql sentence for the count operation
		String sql = "SELECT COUNT(creator_id) FROM ent_creator WHERE creator_id = ?";
		//get the number
		int no = getJdbcTemplate().queryForObject(sql, new Object[]{username}, Integer.class);
		//return a boolean value
		if(no > 0){
			return true;
		}else {
			return false;
		}
	}

	public UserModel findByUsername(String username) {
		// TODO Auto-generated method stub
		return null;
	}

}
