package com.sicos.secured.service;

import com.sicos.secured.model.Rol;

import java.util.List;

public interface RolService {

	Rol getRolByName(String name);

	Rol save(Rol rol);
	
	void setStatus(String rolName, boolean status);

	void deleteRol(String rolName);
	
	List<Rol> getAllRoles();
	
}
