package com.loginService.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import com.loginService.model.LoginUser;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;

@Service
public class UserRepositoryImpl implements IUserRepository {

    private static final String INSERT = "INSERT INTO TBL_LOGIN_USER(USERNAME,PASSWORD) VALUES (?,?)";
    private static final String INSERT_INTO_USER_DRAFT_TOKEN_TABLE = "INSERT INTO USER_DRAFT_TOKEN(USER_ID, TOKEN) VALUES(?,?)";
    private static final String SELECT_BY_USERNAME = "SELECT * FROM USER_CRED WHERE USER_ID = ?";

    @Autowired
    public JdbcTemplate jdbcTemplate;

    @Override
    public LoginUser save(LoginUser loginUser) throws SQLException {
        Object[] object = new Object[]{loginUser.getUserName(), loginUser.getPassword()};
        int save = jdbcTemplate.update(INSERT, object);
        if (save == 1)
            System.out.println("Record added successfully");
        return loginUser;
    }

    @Override
    public LoginUser findByUserName(String userName) throws SQLException {
        Object[] objects = new Object[]{userName};
        return this.jdbcTemplate.queryForObject(SELECT_BY_USERNAME,objects,new UserRowMapper());
    }

    @Override
    public String saveToDraftTokenTable(String userName, String randomToken) {
        Object[] objects = new Object[]{userName,randomToken};
        int save =  jdbcTemplate.update(INSERT_INTO_USER_DRAFT_TOKEN_TABLE,objects);
        return "No of rows added"+save;
    }

    @Override
    public String resetSystemPassword() {
        return null;
    }

    public static class UserRowMapper implements RowMapper<LoginUser> {
        @Override
        public LoginUser mapRow(ResultSet resultSet, int i) throws SQLException {
            Integer id = resultSet.getInt("ID");
            String userId = resultSet.getString("USER_ID");
            String password = resultSet.getString("PASSWORD");
            String  status = resultSet.getString("STATUS");
            String  role = resultSet.getString("ROLE");

            LoginUser loginUser = new LoginUser();
            loginUser.setId(id);
            loginUser.setUserName(userId);
            loginUser.setPassword(password);
            loginUser.setStatus(status);
            loginUser.setRole(role);

            return loginUser;
        }
    }

}
