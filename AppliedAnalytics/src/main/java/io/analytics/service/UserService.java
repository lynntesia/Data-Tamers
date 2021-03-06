package io.analytics.service;


import io.analytics.domain.User;
import io.analytics.repository.interfaces.IUserRepository;
import io.analytics.service.interfaces.IUserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

public class UserService implements IUserService {

	@Autowired 
	private IUserRepository userRepository;
	
	@Override
	public User loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepository.loadUserByUsername(username);
		if (user == null)
			throw new UsernameNotFoundException("Username not found.");
		return user;
	}
	
	public User getUserById(int id) {
		return null;
	}
	
	/**
	 * Creates a new user.
	 * 
	 * @param username
	 * @param email
	 * @param password
	 * @return
	 */
	public User addNewUser(String username, String email, String password) {
		User u = new User(-1);
		u.setUsername(username);
		u.setEmail(email);
		u.setPassword(password);
		return this.addNewUser(u);
	}

	@Override
	public User addNewUser(User u) {
		if (u.getUsername() == null || u.getEmail() == null || u.getPassword() == null)
			return null;

		return userRepository.addNewUser(u);
	}

}
