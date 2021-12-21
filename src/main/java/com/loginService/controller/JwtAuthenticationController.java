package com.loginService.controller;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import com.loginService.service.UserRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import com.loginService.config.JwtTokenUtil;
import com.loginService.model.JwtRequest;
import com.loginService.model.JwtResponse;
import com.loginService.model.LoginUser;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@CrossOrigin
public class JwtAuthenticationController {

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Autowired
	private UserDetailsService jwtUserDetailsService;

	@Autowired
	private UserRepositoryImpl userRepository;

	@PostMapping("/authenticate")
	public ResponseEntity<?> createAuthenticationToken(@RequestBody JwtRequest authenticationRequest,
													   HttpServletResponse response)
			throws Exception {
		LoginUser loginUser = userRepository.findByUserName(authenticationRequest.getUsername());

		if (loginUser.getStatus().equals("DRAFT")){
			String randomToken = UUID.randomUUID().toString();
			return ResponseEntity.ok(userRepository.saveToDraftTokenTable(authenticationRequest.getUsername(),
					randomToken));
		}
		authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword());

		final UserDetails userDetails = jwtUserDetailsService
				.loadUserByUsername(authenticationRequest.getUsername());

		final String token = jwtTokenUtil.generateToken(userDetails);

		return ResponseEntity.ok(new JwtResponse(token));
	}

	@PostMapping("/register")
	public LoginUser saveUser(@RequestBody  LoginUser loginUser) throws Exception{
		System.out.println(loginUser.toString());
		return userRepository.save(loginUser);
	}

	@PostMapping("/reset-system-password")
	public String resetSystemPassword(@RequestBody Map<String, String> details, HttpServletResponse response){
//		response.setHeader("token",);
		userRepository.resetSystemPassword(details,response);
		return null;
	}

	private void authenticate(String username, String password) throws Exception {
		Objects.requireNonNull(username);
		Objects.requireNonNull(password);

		try {
			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
		} catch (DisabledException e) {
			throw new Exception("USER_DISABLED", e);
		} catch (BadCredentialsException e) {
			throw new Exception("INVALID_CREDENTIALS", e);
		}
	}
}
