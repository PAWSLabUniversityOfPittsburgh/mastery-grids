package com.pawslab.toolsuc.user;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class UserUM2DAO extends JdbcDaoSupport implements UserDAO {
	
	public boolean insert(UserModel user) {
		String sql = "INSERT INTO `um2`.`ent_user` (`URI`, `Login`, `Name`, `Pass`, `IsGroup`, `IsAnyGroup`, `Sync`, `EMail`, `Organization`, `City`, `Country`, `How`)"
					 + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		int no = getJdbcTemplate().update(sql, new Object[]{
			user.getUri(),
			user.getLogin(),
			user.getfName() + ", " + user.getlName(),
			user.getPass(),
			user.getIsGroup(),
			user.getIsAnyGroup(),
			user.getSync(),
			user.getEmail(),
			user.getOrganization(),
			user.getCity(),
			user.getCountry(),
			user.getHow()
		});
		if(no > 0){
			return true;
		}else{
			return false;
		}
	}
	
	public boolean buildRel(UserModel user){
		UserModel currUser = this.findByUsername(user.getLogin());
		String sql = "INSERT INTO `um2`.`rel_user_user` (`GroupID`, `UserID`) VALUES (?, ?)";
		int no = getJdbcTemplate().update(sql, new Object[]{"68", currUser.getUserID()});
		if(no > 0){
			return true;
		}else{
			return false;
		}
	}

	public boolean isExist(String username) {
		//sql sentence for the count operation
		String sql = "SELECT COUNT(Login) FROM ent_user WHERE Login = ?";
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
		String sql = "SELECT * FROM `um2`.`ent_user` WHERE Login = ?";
		//get the row
		UserModel model = (UserModel) getJdbcTemplate()
			.queryForObject(sql, new Object[]{username}, new BeanPropertyRowMapper(UserModel.class));
		return model;
	}

}
