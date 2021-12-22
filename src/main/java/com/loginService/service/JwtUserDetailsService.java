package com.loginService.service;

import java.sql.SQLException;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.loginService.model.LoginUser;

@Service
public class JwtUserDetailsService implements UserDetailsService {


	@Autowired
	public UserRepositoryImpl userRepository;

	@Autowired
	public PasswordEncoder passwordEncoder;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		LoginUser loginUser = null;
		try {
			loginUser = userRepository.findByUserName(username);
		} catch (SQLException throwables) {
			throwables.printStackTrace();
		}

		if (loginUser != null) {
			return new User(loginUser.getUsername(),loginUser.getPassword(),
					new ArrayList<>());
		} else {
			throw new UsernameNotFoundException("LoginUser not found with username: " + username);
		}
	}



}