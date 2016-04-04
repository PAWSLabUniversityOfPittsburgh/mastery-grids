package com.pawslab.toolsuc.user;

import com.pawslab.toolsuc.user.UserModel;

public interface UserDAO{
	
	public boolean insert(UserModel user);
	
	public boolean isExist(String username);
	
	public UserModel findByUsername(String username);
}
