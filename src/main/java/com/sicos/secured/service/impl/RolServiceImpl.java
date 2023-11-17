package com.sicos.secured.service.impl;

import com.sicos.secured.exceptions.RepositoryError;
import com.sicos.secured.exceptions.RolNotFoundException;
import com.sicos.secured.exceptions.RolAlreadyCreatedException;
import com.sicos.secured.model.Rol;
import com.sicos.secured.repository.RolRepository;
import com.sicos.secured.service.RolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RolServiceImpl implements RolService {

	@Autowired
	private RolRepository rolRepository;

	@Override
	public Rol save(Rol rol) {
		if (Optional.ofNullable(rolRepository.findByNombre(rol.getNombre()))
				.map(List::isEmpty)
				.orElse(false)) {

			return Optional.of(rol)
					.map(rolRepository::save)
					.orElseThrow(() -> new RepositoryError("Rol not save!"));
		} else {
			throw new RolAlreadyCreatedException("Rol [" + rol.getNombre() + "] ya existe!");
		}
	}

	@Override
	public void setStatus(String rolName, boolean status) {
		Optional.ofNullable(rolName)
				.map(rolRepository::findByNombre)
				.flatMap(l -> l.stream().findFirst())
				.map(r -> {
					r.setEnabled(status ? "Y" : "N");
					return  r;
				})
				.map(rolRepository::save)
				.orElseThrow(() -> new RolNotFoundException("Rol [" + rolName + "] not found!"));
	}

	@Override
	public void deleteRol(String rolName) {
		Optional.ofNullable(rolName)
				.map(rolRepository::findByNombre)
				.flatMap(l -> l.stream().findFirst())
				.map(r -> {
					rolRepository.delete(r);
					return r;
				})
				.orElseThrow(() -> new RolNotFoundException("Rol [" + rolName + "] not found!"));
	}

	@Override
	public List<Rol> getAllRoles() {
		return rolRepository.findAll().stream()
				.toList();
	}

	@Override
	public Rol getRolByName(String name) {
		return Optional.ofNullable(rolRepository.findByNombre(name))
				.filter(l -> !l.isEmpty())
				.map(l -> l.get(0))
				.orElseThrow(() -> new RolNotFoundException("Rol [" + name + "] not found!"));
	}
}
