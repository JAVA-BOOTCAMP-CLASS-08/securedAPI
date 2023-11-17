package com.sicos.secured.controller;

import com.sicos.secured.model.Message;
import com.sicos.secured.model.User;
import com.sicos.secured.security.*;
import com.sicos.secured.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/user", produces = "application/json")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping(path = "/{name}")
    @ResponseStatus(HttpStatus.OK)
    public User getByName(@PathVariable String name) {
        return userService.getUserByName(name);
    }

    @PostMapping(path = "/")
    @Operation(description = "Agregar un usuario",
            method = "POST",
            operationId = "AddUser",
            tags = "Usuarios",
            security = {@SecurityRequirement(name = "bearerAuth"), @SecurityRequirement(name = "ApiKeyHeaderAuth")})
    @ResponseStatus(HttpStatus.OK)
    @Secured(validateMethod = {Constants.TOKEN, Constants.APIKEY}, roles = {"CREATE"}, unique = true)
    public User create(@RequestHeader(name=HttpHeaders.AUTHORIZATION, required = false) @TokenParameter String token,
                       @RequestHeader(name="X-API-KEY", required = false) @ApiKeyParameter String apiKey,
                       @RequestBody User user) {
        return userService.save(user);
    }

    @PutMapping(path = "/")
    @Operation(description = "Actualizar un usuario",
            method = "PUT",
            operationId = "UpdateUser",
            tags = "Usuarios",
            security = {@SecurityRequirement(name = "BasicAuth")})
    @ResponseStatus(HttpStatus.CREATED)
    @Secured(validateMethod = {Constants.BASIC}, roles = {"ADMIN"})
    public User update(@RequestHeader(name=HttpHeaders.AUTHORIZATION, required = false) @BasicParameter String token,
                       @RequestBody User user) {
        return userService.update(user);
    }

    @PatchMapping(path = "/enable/{name}")
    @Operation(description = "Habilitar un usuario",
            method = "PATCH",
            operationId = "EnableUser",
            tags = "Usuarios",
            security = {@SecurityRequirement(name = "ApiKeyHeaderAuth")})
    @ResponseStatus(HttpStatus.OK)
    @Secured(validateMethod = {Constants.APIKEY}, roles = {"CREATE", "READ"})
    public ResponseEntity<Message> enable(@RequestHeader("X-API-KEY") @ApiKeyParameter String apiKey,
                                          @PathVariable
                                          @Parameter(name = "name", in= ParameterIn.PATH, description = "Nombre del usuario", example = "User1") String name) {
        this.userService.setStatus(name, true);
        return ResponseEntity.ok(new Message("Enabled"));
    }

    @PatchMapping(path = "/disable/{name}")
    @Operation(description = "Deshabilitar un usuario",
            method = "PATCH",
            operationId = "DisableUser",
            tags = "Usuarios",
            security = {@SecurityRequirement(name = "ApiKeyHeaderAuth")})
    @ResponseStatus(HttpStatus.OK)
    @Secured(validateMethod = {Constants.APIKEY}, roles = {"CREATE", "READ"})
    public ResponseEntity<Message> disable(@RequestHeader("X-API-KEY") @ApiKeyParameter String apiKey,
                                           @PathVariable String name) {
        this.userService.setStatus(name, false);
        return ResponseEntity.ok(new Message("Disabled"));
    }

    @DeleteMapping(path = "/{name}")
    @Operation(description = "Eliminar un usuario",
            method = "DELETE",
            operationId = "DeleteUser",
            tags = "Usuarios",
            security = {@SecurityRequirement(name = "bearerAuth"), @SecurityRequirement(name = "ApiKeyHeaderAuth")})
    @ResponseStatus(HttpStatus.OK)
    @Secured(validateMethod = {Constants.TOKEN, Constants.APIKEY}, roles = {"CREATE"}, unique = true)
    public ResponseEntity<Message> delete(@RequestHeader(HttpHeaders.AUTHORIZATION) @TokenParameter String token,
                                          @RequestHeader("X-API-KEY") @ApiKeyParameter String apiKey,
                                          @PathVariable String name) {
        this.userService.deleteUser(name);
        return ResponseEntity.ok(new Message("Deleted"));
    }

    @GetMapping(path = "/")
    @ResponseStatus(HttpStatus.OK)
    public List<User> getAll() {
        return userService.getAllUsers();
    }

    @PatchMapping(path = "/{user}/assign/{rol}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Message> assignRol(@PathVariable String user, @PathVariable String rol) {
        this.userService.assignRol(user, rol);
        return ResponseEntity.ok(new Message("Assigned"));
    }

    @PatchMapping(path = "/{user}/unassign/{rol}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Message> unAssignRol(@PathVariable String user, @PathVariable String rol) {
        this.userService.unAssignRol(user, rol);
        return ResponseEntity.ok(new Message("Unassigned"));
    }
}
