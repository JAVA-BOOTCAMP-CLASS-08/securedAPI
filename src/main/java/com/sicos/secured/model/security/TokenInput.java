package com.sicos.secured.model.security;

import lombok.Data;

@Data
public class TokenInput {
    private String user;
    private String password;
}
