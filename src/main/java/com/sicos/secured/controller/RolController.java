package com.sicos.secured.controller;

import com.sicos.secured.model.Message;
import com.sicos.secured.model.Rol;
import com.sicos.secured.service.RolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/rol", produces = "application/json")
public class RolController {

    @Autowired
    private RolService rolService;

    @GetMapping(path = "/{name}")
    @ResponseStatus(HttpStatus.OK)
    public Rol getByName(@PathVariable String name) {
        return rolService.getRolByName(name);
    }

    @PostMapping(path = "/")
    @ResponseStatus(HttpStatus.CREATED)
    public Rol create(@RequestBody Rol rol) {
        return rolService.save(rol);
    }

    @PatchMapping(path = "/enable/{name}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Message> enable(@PathVariable String name) {
        this.rolService.setStatus(name, true);
        return ResponseEntity.ok(new Message("Enabled"));
    }

    @PatchMapping(path = "/disable/{name}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Message> disable(@PathVariable String name) {
        this.rolService.setStatus(name, false);
        return ResponseEntity.ok(new Message("Disabled"));
    }

    @DeleteMapping(path = "/{name}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Message> delete(@PathVariable String name) {
        this.rolService.deleteRol(name);
        return ResponseEntity.ok(new Message("Deleted"));
    }

    @GetMapping(path = "/")
    @ResponseStatus(HttpStatus.OK)
    public List<Rol> getAll() {
        return rolService.getAllRoles();
    }

}
