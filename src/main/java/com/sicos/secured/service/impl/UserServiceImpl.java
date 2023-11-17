package com.sicos.secured.service.impl;

import com.sicos.secured.exceptions.*;
import com.sicos.secured.model.Rol;
import com.sicos.secured.model.User;
import com.sicos.secured.repository.RolRepository;
import com.sicos.secured.repository.UserRepository;
import com.sicos.secured.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.StreamSupport;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RolRepository rolRepository;

	@Override
	public User getUserByName(String name) {
		return Optional.ofNullable(userRepository.findByNombre(name))
				.filter(l -> !l.isEmpty())
				.map(l -> l.get(0))
				.orElseThrow(() -> new UserNotFoundException("User [" + name + "] not found!"));
	}

	@Override
	public User save(User user) {
		if (Optional.ofNullable(userRepository.findByNombre(user.getNombre()))
				.map(List::isEmpty)
				.orElse(false)) {

			return Optional.of(user)
					.map(userRepository::save)
					.orElseThrow(() -> new RepositoryError("Rol not save!"));
		} else {
			throw new UserAlreadyCreatedException("User [" + user.getNombre() + "] ya existe!");
		}
	}

	@Override
	public User update(User user) {
		return Optional.ofNullable(user)
				.map(User::getNombre)
				.map(userRepository::findByNombre)
				.flatMap(l -> l.stream().findFirst())
				.map(r -> {
					r.setPassword(user.getPassword());
					r.setEnabled(user.getEnabled());
					return  r;
				})
				.map(userRepository::save)
				.orElseThrow(() -> new UserNotFoundException("User [" + Objects.requireNonNull(user).getNombre() + "] not found!"));
	}

	@Override
	public void setStatus(String userName, boolean status) {
		Optional.ofNullable(userName)
				.map(userRepository::findByNombre)
				.flatMap(l -> l.stream().findFirst())
				.map(r -> {
					r.setEnabled(status ? "Y" : "N");
					return  r;
				})
				.map(userRepository::save)
				.orElseThrow(() -> new UserNotFoundException("User [" + userName + "] not found!"));
	}

	@Override
	public void deleteUser(String userName) {
		Optional.ofNullable(userName)
				.map(userRepository::findByNombre)
				.flatMap(l -> l.stream().findFirst())
				.map(r -> {
					userRepository.delete(r);
					return r;
				})
				.orElseThrow(() -> new UserNotFoundException("User [" + userName + "] not found!"));
	}

	@Override
	public List<User> getAllUsers() {
		return userRepository.findAll().stream()
				.toList();
	}

	@Override
	public void assignRol(String userName, String rolName) {
		User user = Optional.ofNullable(userRepository.findByNombre(userName))
							.filter(l -> !l.isEmpty())
							.map(l -> l.get(0))
							.orElseThrow(() -> new UserNotFoundException("User [" + userName + "] not found!"));

		Rol rol = Optional.ofNullable(rolRepository.findByNombre(rolName))
				.filter(l -> !l.isEmpty())
				.map(l -> l.get(0))
				.orElseThrow(() -> new RolNotFoundException("Rol [" + rolName + "] not found!"));

		if (user.getRoles().stream().anyMatch(r -> r.getNombre().equalsIgnoreCase(rolName)))
			throw new RolAlreadyAssignedException("User [" + userName + "] ya posee asignado el rol [" + rolName + "]");

		user.addRol(rol);

		userRepository.save(user);
	}
	
	@Override
	public void unAssignRol(String userName, String rolName) {
		User user = Optional.ofNullable(userRepository.findByNombre(userName))
				.filter(l -> !l.isEmpty())
				.map(l -> l.get(0))
				.orElseThrow(() -> new UserNotFoundException("User [" + userName + "] not found!"));

		Rol rol = Optional.ofNullable(rolRepository.findByNombre(rolName))
				.filter(l -> !l.isEmpty())
				.map(l -> l.get(0))
				.orElseThrow(() -> new RolNotFoundException("Rol [" + rolName + "] not found!"));

		if (user.getRoles().stream().noneMatch(r -> r.getNombre().equalsIgnoreCase(rolName)))
			throw new RolNotAssignedException("User [" + userName + "] NO posee asignado el rol [" + rolName + "]");

		user.removeRol(rol);

		userRepository.save(user);
	}
}
