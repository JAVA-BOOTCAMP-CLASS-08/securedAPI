package com.sicos.secured.security;

import java.util.HashSet;
import java.util.Set;

public class JWTRepository {
	
	private static JWTRepository instance = null;
	
	private Set<String> tokens;
	
	private JWTRepository() {
		tokens = new HashSet<String>();
	}

	public static JWTRepository getInstance() {
		if (instance == null) {
			instance = new JWTRepository();
		}
		
		return instance;
	}

	public Set<String> getTokens() {
		return tokens;
	}

	public void setTokens(Set<String> tokens) {
		this.tokens = tokens;
	}
}
