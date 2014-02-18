package io.analytics.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.sql.DataSource;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.*;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import io.analytics.domain.Filter;
import io.analytics.domain.User;
import io.analytics.repository.interfaces.IUserRepository;
import io.analytics.security.Role;

/**
 * A repository for querying and updating Users in the database.
 * 
 * @author Dave Wong
 *
 */
@Repository
public class UserRepository implements IUserRepository {

	private final JdbcTemplate jdbc;
	
	@Autowired
	public UserRepository(DataSource dataSource) {
		this.jdbc = new JdbcTemplate(dataSource);
	}
	
	private static final class UserMapper implements RowMapper<User> {

		@Override
		public User mapRow(ResultSet rs, int row) throws SQLException {
			User user = new User(rs.getInt(UserTable.USER_ID));
			user.setFirstName(rs.getString(UserTable.FIRST_NAME));
			user.setLastName(rs.getString(UserTable.LAST_NAME));
			user.setEmail(rs.getString(UserTable.EMAIL_ADDRESS));
			user.setUsername(rs.getString(UserTable.USERNAME));
			user.setPasswordHash(rs.getString(UserTable.PASSWORD_HASH));
			user.setPasswordSalt(rs.getString(UserTable.PASSWORD_SALT));
			user.setProfileImageUrl(rs.getString(UserTable.PROFILE_IMAGE_URL));
			
			//TODO: Careful with time zones.
			Calendar joinDate = Calendar.getInstance();
			joinDate.setTime(rs.getDate(UserTable.JOIN_DATE));
			user.setJoinDate(joinDate);
			
			return user;
		}
		
	}
	

	public static final String USERS_TABLE = "Users";
	private static final class UserTable {
		public static final String USER_ID = "idUsers";
		public static final String FIRST_NAME = "firstName";
		public static final String LAST_NAME = "lastName";
		public static final String EMAIL_ADDRESS = "emailAddress";
		public static final String USERNAME = "username";
		public static final String PASSWORD_HASH = "passwordHash";
		public static final String PASSWORD_SALT = "passwordSalt";
		public static final String PROFILE_IMAGE_URL = "profileImageUrl";
		public static final String JOIN_DATE = "joinDate";
	}
	//TODO: Use a connection properties file.
	private static DriverManagerDataSource DATASOURCE = 
				new DriverManagerDataSource("jdbc:mysql://davidkainoa.com:3306/davidkai_analytics", 
						"davidkai_data", "PNjO_#a40@wZPmh-Q");

	//TODO: Maybe we can autowire this up too, since it's a database standard for the DATE type
	private static final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
	
	/**
	 * Adds a new user to the database. Ignores the User.getId() and lets database determine ID.
	 * 
	 * @param u
	 * @return
	 */
	public User addNewUser(User u) {

		//JdbcTemplate jdbc = new JdbcTemplate(DATASOURCE);
		String preStatement;
		Object[] args;
		int[] argTypes;
		
		String joinDate;
		if (u.getJoinDate() == null)
			joinDate = null;
		else
			joinDate = formatter.format(u.getJoinDate().getTime());
		preStatement = String.format("INSERT INTO `%s` (`%s`, `%s`, `%s`, `%s`, `%s`, `%s`, `%s`, `%s`) "
											+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?);", 
											USERS_TABLE, UserTable.FIRST_NAME, UserTable.LAST_NAME, UserTable.EMAIL_ADDRESS, UserTable.USERNAME,
				UserTable.PASSWORD_HASH, UserTable.PASSWORD_SALT, UserTable.PROFILE_IMAGE_URL, UserTable.JOIN_DATE);
		
		args = new Object[]{ u.getFirstName(), u.getLastName(), u.getEmail(), u.getUsername(), u.getPassword(), 
				u.getPasswordSalt(), u.getProfileImageUrl(), joinDate };
		
		argTypes = new int[]{ Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR , Types.VARCHAR, Types.VARCHAR, Types.DATE };
		
		
		int userId = insertUser(preStatement, args, argTypes);
		if (userId <= 0)
			return null;
		
		User result = new User(userId);
		result.setAuthorities(u.getAuthorities());
		result.setEmail(u.getEmail());
		result.setFirstName(u.getFirstName());
		result.setJoinDate(u.getJoinDate());
		result.setLastName(u.getLastName());
		result.setPasswordHash(u.getPassword());
		result.setPasswordSalt(u.getPasswordSalt());
		result.setProfileImageUrl(u.getProfileImageUrl());
		result.setUsername(u.getUsername());
		
		//Return true if the statement successfully affected one row.
		return result;
	}


	@Transactional("transactionManager")
	private int insertUser(String preStatement, Object[] args, int[] argTypes) {
		int affectedRows;
		int userId;
		try {
			affectedRows = this.jdbc.update(preStatement, args, argTypes);
			userId = this.jdbc.queryForInt("SELECT LAST_INSERT_ID();");
		} catch (DataAccessException e) {
			//TODO: Standardize error handling for the database.
			//e.printStackTrace();
			return -1;
		}
		
		if (affectedRows != 1)
			return -1;
		
		return userId;
	}
	
	
	public User loadUserByUsername(String username) throws UsernameNotFoundException {
		
		//JdbcTemplate jdbc = new JdbcTemplate(DATASOURCE);
		String preStatement;
		Object[] args;
		int[] argTypes;

		preStatement = String.format("SELECT * FROM `%s` WHERE `%s`=?", USERS_TABLE, UserTable.USERNAME);
		args = new Object[] { username };
		argTypes = new int[] { Types.VARCHAR };
		
		try {
			
			List<User> users = this.jdbc.query(preStatement, args, argTypes, new UserMapper());
			
			if (users.isEmpty())
				return null;
			
			User user = users.get(0);
			
			//TODO: What is this and how should we properly incorporate it?
			Set roles = new HashSet<GrantedAuthority>();
			roles.add(new Role("ROLE_USER"));
			user.setAuthorities(roles);
			
			return user; 
			
		} catch (DataAccessException e) {
			//TODO: Standardize error handling for the database.
			//e.printStackTrace();
			return null;
		}
		
	}


	@Override
	public User getUserById(int userId) {
		// TODO get User data and map into a User object.
		return null;
	}
} 
