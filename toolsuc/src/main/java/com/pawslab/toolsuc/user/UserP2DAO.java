package com.pawslab.toolsuc.user;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class UserP2DAO extends JdbcDaoSupport implements UserDAO {
	
	public boolean insert(UserModel user) {
		String sql = "INSERT INTO ent_user (URI, Login, Name, Pass, IsGroup, Sync, Email, Organization, City, Country, How, IsInstructor)"
					+ "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		int no = getJdbcTemplate().update(sql, new Object[]{
			user.getUri(),
			user.getLogin(),
			user.getfName() + ", " + user.getlName(),
			user.getPass(),
			user.getIsGroup(),
			user.getSync(),
			user.getEmail(),
			user.getOrganization(),
			user.getCity(),
			user.getCountry(),
			user.getHow(),
			user.getIsInstructor()
		});
		if(no > 0){
			return true;
		}else{
			return false;
		}
	}

	public boolean buildRel(UserModel user){
		UserModel currUser = this.findByUsername(user.getLogin());
		String sql = "INSERT INTO `portal_test2`.`rel_user_user` (`ParentUserID`, `ChildUserID`) VALUES (?, ?)";
		int no = getJdbcTemplate().update(sql, new Object[]{"3", currUser.getUserID()});
		if(no > 0){
			return true;
		}else{
			return false;
		}
	}
	
	public boolean buildRole(UserModel user){
		String sql = "INSERT INTO `portal_test2`.`seq_role` (`Login`, `Role`) VALUES (?, ?)";
		int no = getJdbcTemplate().update(sql, new Object[]{user.getLogin(), "admin"});
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
		//sql sentence for the count operation
		String sql = "SELECT * FROM `portal_test2`.`ent_user` WHERE Login = ?";
		//get the row
		UserModel model = (UserModel) getJdbcTemplate()
				.queryForObject(sql, new Object[]{username}, new BeanPropertyRowMapper(UserModel.class));
		return model;
	}
	
	public String getPassword(String username){
		//sql sentence for the count operation
		String sql = "SELECT `ent_user`.`Pass` FROM `portal_test2`.`ent_user` WHERE Login = ?;";
		//get the pass
		String passString = getJdbcTemplate().queryForObject(sql, new Object[]{username}, String.class);
		return passString;
	}
}
