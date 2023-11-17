package com.sicos.secured.service;

import com.sicos.secured.model.User;

import java.util.List;

public interface UserService {

	User getUserByName(String name);
	
	User save(User user);

	User update(User user);

	void setStatus(String userName, boolean status);

	void deleteUser(String userName);
	
	List<User> getAllUsers();
	
	void assignRol(String userName, String rolName);

	void unAssignRol(String userName, String rolName);
}
