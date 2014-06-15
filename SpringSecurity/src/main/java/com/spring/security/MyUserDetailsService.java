package com.spring.security;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class MyUserDetailsService implements UserDetailsService {
	 
    private DataSource  dataSource;
 
    public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException, DataAccessException {
        String sql = "select * from user where name like :username";
        MapSqlParameterSource source = new MapSqlParameterSource();
        source.addValue("username", username);
 
        @SuppressWarnings("deprecation")
		SimpleJdbcTemplate sjt = new SimpleJdbcTemplate(getDataSource());
        User user = sjt.queryForObject(sql, new UserMapper(), source);
        return user;
    }
 
    @SuppressWarnings("deprecation")
	private Collection<? extends GrantedAuthority> getAuthorities(boolean isAdmin) {
        List<GrantedAuthority> authList = new ArrayList<GrantedAuthority>(2);
        authList.add(new GrantedAuthorityImpl("ROLE_USER"));
        if (isAdmin) {
            authList.add(new GrantedAuthorityImpl("ROLE_ADMIN"));
        }
        return authList;
    }
 
    private class UserMapper implements ParameterizedRowMapper<User> {
 
        @Override
        public User mapRow(ResultSet rs, int arg1) throws SQLException {
            return new User(rs.getString("name"), rs.getString("password"), true, true, true, true, getAuthorities(rs.getBoolean("role")));
        }

		
 
    }
}