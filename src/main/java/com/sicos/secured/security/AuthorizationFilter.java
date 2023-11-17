package com.sicos.secured.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.*;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.sicos.secured.exceptions.RolNotFoundException;
import com.sicos.secured.exceptions.UserNotFoundException;
import com.sicos.secured.exceptions.security.ClientBasicFaultException;
import com.sicos.secured.exceptions.security.ClientException;
import com.sicos.secured.exceptions.security.UnauthorizedException;
import com.sicos.secured.model.Rol;
import com.sicos.secured.model.User;
import com.sicos.secured.service.RolService;
import com.sicos.secured.service.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

@Aspect
@Component
public class AuthorizationFilter {
	
	private final Logger log = LogManager.getLogger(AuthorizationFilter.class);
	
	@Autowired
	private UserService userService;

	@Autowired
	private RolService rolService;

	@Before(value = "secureInterceptor() && @annotation(secured)", argNames = "secured" )
	public void adviceSecureInterceptor(JoinPoint jp, Secured secured) throws Throwable {
		
		if (log.isInfoEnabled()) {
			log.info("SECURED METHOD [" + jp.getSignature().getName() + "] - " + arrayToStr(secured.validateMethod()) + " - " + arrayToStr(secured.roles()));
		}
		
		Map<String, Object> parameters = this.getParameterValue(((MethodSignature) jp.getSignature()).getMethod(), jp.getArgs());
		
		List<String> methods = Arrays.asList(secured.validateMethod());
		
		boolean validated = false;
		
		if (methods.contains(Constants.TOKEN)) {
			if (parameters.get(Constants.TOKEN) != null && !parameters.get(Constants.TOKEN).toString().isEmpty()) {
				validateToken(parameters.get(Constants.TOKEN).toString(), secured.roles(), secured.unique());
				validated = true;
			}
		}
		
		if (methods.contains(Constants.APIKEY) && !validated) {
			if (parameters.get(Constants.APIKEY) != null && !parameters.get(Constants.APIKEY).toString().isEmpty()) {
				validateKey(parameters.get(Constants.APIKEY).toString());
				validated = true;
			}
		}

		if (methods.contains(Constants.BASIC) && !validated) {
			if (parameters.get(Constants.BASIC) != null && !parameters.get(Constants.BASIC).toString().isEmpty()) {
				validateBasic(parameters.get(Constants.BASIC).toString());
				validated = true;
			}
		}

		if (!validated) {
			if (log.isErrorEnabled()) {
				log.error("[VALIDATE] - Method validation not specific!");
			}
			
			throw new UnauthorizedException("UNAUTHORIZED - Method validation not specific!");
		}

		if (log.isInfoEnabled()) {
			log.info("SECURED METHOD [" + jp.getSignature().getName() + "] - " + arrayToStr(secured.validateMethod()) + " - " + arrayToStr(secured.roles()) + " --> AUTHORIZED!!!");
		}
	
	}

	private void validateKey(String apiKey) {
		if (apiKey == null || apiKey.trim().isEmpty()) {
			if (log.isErrorEnabled()) {
				log.error("[VALIDATE-API-KEY] - API-KEY NOT FOUND");
			}
			
			throw new UnauthorizedException("UNAUTHORIZED - Security API-KEY not found");
		}
		
		if (!Constants.VALID_API_KEY.equals(apiKey)) {
			if (log.isErrorEnabled()) {
				log.error("[VALIDATE-API-KEY] - ApiKey [" + apiKey + "] - UNAUTHORIZED");
			}
			
			throw new UnauthorizedException("UNAUTHORIZED - Authorization has been refused for the provided credentials with the request");
		}
	}

	private void validateBasic(String token) {
		if (token == null || token.trim().isEmpty()) {
			if (log.isErrorEnabled()) {
				log.error("[VALIDATE-TOKEN] - Token NOT FOUND");
			}
			
			throw new ClientBasicFaultException();
		} else {
			String subToken = token;
			
			if ((token.startsWith("Basic ")) || (token.startsWith("basic "))) {
				subToken = token.substring(6);
			}
			
			try {
				String decodeValue = new String(Base64.getDecoder().decode(subToken.getBytes()));
				
				String[] userKeyPair = decodeValue.split(":");
				
				if (userKeyPair.length == 2) {
					try {
						User user = userService.getUserByName(userKeyPair[0]);
						
						if (!user.getPassword().equals(userKeyPair[1])) {
							throw new ClientBasicFaultException();
						}
					} catch (UserNotFoundException e) {
						throw new ClientBasicFaultException();
					}
				} else {
					throw new ClientBasicFaultException();
				}
			} catch (IllegalArgumentException e) {
				throw new ClientBasicFaultException();
			}
		}
		
	}
	
	private void validateToken(String token, String[] roles, boolean accesoUnico) {
		if (token == null || token.trim().isEmpty()) {
			if (log.isErrorEnabled()) {
				log.error("[VALIDATE-TOKEN] - Token NOT FOUND");
			}
			
			throw new UnauthorizedException("UNAUTHORIZED - Security Authorization token not found");
		
		} else if (roles == null || roles.length == 0) {
		
			if (log.isErrorEnabled()) {
				log.error("[VALIDATE-TOKEN] - Roles Array Empty");
			}
			
			throw new UnauthorizedException("UNAUTHORIZED - Security Authorization token not indicate active rol");
		
		} else {
		
			String subToken = token;
			
			if ((token.startsWith("Bearer ")) || (token.startsWith("bearer "))) {
				subToken = token.substring(7);
			}

			if (accesoUnico) {
				if (JWTRepository.getInstance().getTokens().contains(subToken)) {
					throw new UnauthorizedException("UNAUTHORIZED - Token is used");
				}
			}
			
			try {
				
				Algorithm algorithm = Algorithm.HMAC512(Constants.SECRET);

				JWTVerifier verifier = JWT.require(algorithm)
									      .withIssuer("AWS")
									      .withAudience("API")
									      .withSubject("API-Authentication")
									      .build();

				DecodedJWT jwt = verifier.verify(subToken);

				List<String> jwtRoles = jwt.getClaim(Constants.PRIVATE_CLAIM_PREFIX + "USER_ROLES").asList(String.class);
				
				boolean valid = false;
				
				for (int i = 0; i < roles.length && !valid; i++) {
					if (jwtRoles.contains(roles[i])) {
						
						try {
							Rol rol = rolService.getRolByName(roles[i]);
							
							valid = rol.isHabilitado();
							
							if (log.isInfoEnabled()) {
								log.info("[VALIDATE TOKEN] - Rol " + roles[i] + " is Present in Token. [Status Rol = " + (rol.isHabilitado() ? "ENABLED" : "DISABLED") + "]");
							}
							
						} catch (RolNotFoundException e) {
							log.error("Rol not found!");
						}
						
					}
					
				}

				if (!valid) {
					if (log.isInfoEnabled()) {
						log.error("[VALIDATE TOKEN] - Roles not present.");
					}
					
					throw new UnauthorizedException("UNAUTHORIZED - Authorization has been refused for the provided credentials with the request");
					
				}
				
				JWTRepository.getInstance().getTokens().add(subToken);
				
			} catch (AlgorithmMismatchException e) {
				if (log.isErrorEnabled()) {
					log.error("[VALIDATE] - " + "Bad Request - JWT - Algorithm Mismatch Exception - " + e.getMessage());
				}
				
				throw new ClientException("Bad Request - JWT - Algorithm Mismatch Exception - " + e.getMessage());
			} catch (SignatureVerificationException e) {
				if (log.isErrorEnabled()) {
					log.error("[VALIDATE] - " + "Bad Request - JWT - Signature Verification Exception - " + e.getMessage());
				}
				
				throw new ClientException("Bad Request - JWT - Signature Verification Exception - " + e.getMessage());
			} catch (TokenExpiredException e) {
				if (log.isErrorEnabled()) {
					log.error("[VALIDATE] - " + "Bad Request - JWT - Token Expired Exception - " + e.getMessage());
				}
				
				throw new ClientException("Bad Request - JWT - Token Expired Exception - " + e.getMessage());
			} catch (InvalidClaimException e) {
				if (log.isErrorEnabled()) {
					log.error("[VALIDATE] - " + "Bad Request - JWT - Invalid Claim Exception - " + e.getMessage());
				}
				
				throw new ClientException("Bad Request - JWT - Invalid Claim Exception - " + e.getMessage());
			} catch (JWTVerificationException e) {
				if (log.isErrorEnabled()) {
					log.error("[VALIDATE] - " + "Bad Request - JWT - Unexpected JWTVerification Exception - " + e.getMessage());
				}
				
				throw new ClientException("Bad Request - JWT - Unexpected JWTVerification Exception - " + e.getMessage());
			} catch (IllegalArgumentException e) {
				if (log.isErrorEnabled()) {
					log.error("[VALIDATE] - " + "Bad Request - JWT - Illegal Argument Exception - " + e.getMessage());
				}
				
				throw new ClientException("Bad Request - JWT - Illegal Argument Exception - " + e.getMessage());
			}
				
		}
	}
	
	private Map<String, Object> getParameterValue(Method method, Object[] args) {
        Map<String, Object> parametersValue = new HashMap<>();
        Parameter[] parameters = method.getParameters();
        
        for (int i = 0; i < parameters.length; i++) {
        	Parameter p = parameters[i];
        	
        	if (p.isAnnotationPresent(TokenParameter.class)) {
            	parametersValue.put(Constants.TOKEN, args[i]);
        	} else if (p.isAnnotationPresent(ApiKeyParameter.class)) {
            	parametersValue.put(Constants.APIKEY, args[i]);
        	} else if (p.isAnnotationPresent(BasicParameter.class)) {
            	parametersValue.put(Constants.BASIC, args[i]);
        	} 
        }
        
        return parametersValue;
    }
	
	private String arrayToStr(String[] array) {
		StringBuilder str = new StringBuilder();
		 
		str.append("[");
	 
		boolean first = true;
		 
		for (String s : array) {
			if (first) {
				first = false;
			} else {
				str.append(", ");
			}
			
			str.append(s);
		}
		 
		str.append("]");
		 
		return str.toString();
	}
	 
	 @Pointcut("within(com..controller..*)")
	 public void secureInterceptor() {}	

	 
}
