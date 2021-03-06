package io.analytics.repository.interfaces;

import io.analytics.domain.User;

import java.io.Serializable;

public interface IUserRepository {
	
	public User addNewUser(User u);
	
	public User loadUserByUsername(String username);
	
	public User getUserById(int userId);

}
