package com.sicos.secured.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.sicos.secured.exceptions.security.UnauthorizedException;
import com.sicos.secured.model.Rol;
import com.sicos.secured.model.User;
import com.sicos.secured.model.security.TokenInput;
import com.sicos.secured.model.security.TokenOutput;
import com.sicos.secured.security.Constants;
import com.sicos.secured.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.UUID;

@RestController
@RequestMapping(value = "/api/v1", produces = "application/json")
public class LoginController {

    @Autowired
    private UserService userService;

    @PostMapping(path = "/login")
    @Operation(description = "Obtener un token JWT",
            method = "POST",
            operationId = "login",
            tags = "Token"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token JWT obtenido"),
            @ApiResponse(responseCode = "401", description = "Usuario no autorizado")
    })
    @ResponseStatus(HttpStatus.OK)
    public TokenOutput login(@RequestBody TokenInput input) {

        if (input == null ||
                input.getUser() == null || input.getUser().trim().isEmpty() ||
                input.getPassword() == null || input.getPassword().trim().isEmpty()) {

            throw new UnauthorizedException("Incomplete information");
        }

        User user = this.userService.getUserByName(input.getUser());

        if (!user.getPassword().equals(input.getPassword())) {
            throw new UnauthorizedException("Invalid password");
        }

        if (!user.isHabilitado()) {
            throw new UnauthorizedException("Disabled user");
        }

        String[] aRoles = user.getRoles()
                                .stream()
                                .map(Rol::getNombre)
                                .toList()
                                .toArray(new String[0]);

        String token = JWT.create().withIssuer("AWS")
                .withSubject("API-Authentication")
                .withAudience("API")
                .withJWTId(UUID.randomUUID().toString())
                .withArrayClaim(Constants.PRIVATE_CLAIM_PREFIX + "USER_ROLES", aRoles)
                .withExpiresAt(new Date(System.currentTimeMillis() + 50000)) //En 50 segundos se vence
                .sign(Algorithm.HMAC512(Constants.SECRET));

        return TokenOutput.builder().token(token).build();

    }
}
