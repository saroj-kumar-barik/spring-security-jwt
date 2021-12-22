package com.loginService.service;

import com.loginService.model.UserDraftToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import com.loginService.model.LoginUser;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserRepositoryImpl implements IUserRepository {

    private static final String INSERT = "INSERT INTO TBL_LOGIN_USER(USERNAME,PASSWORD) VALUES (?,?)";
    private static final String INSERT_INTO_USER_DRAFT_TOKEN_TABLE = "INSERT INTO USER_DRAFT_TOKEN(USER_ID, TOKEN) VALUES(?,?)";
    private static final String SELECT_BY_USERNAME = "SELECT * FROM USER_CRED WHERE USER_ID = ?";
    private static final String SELECT_TOKEN = "SELECT * FROM USER_DRAFT_TOKEN WHERE USER_ID = ?";


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
    public Map<String, String> saveToDraftTokenTable(String userName, String randomToken) {
        Object[] objects = new Object[]{userName,randomToken};
        int save =  jdbcTemplate.update(INSERT_INTO_USER_DRAFT_TOKEN_TABLE,objects);

        Map<String, String> draftTokenDetails = new HashMap<>();
        draftTokenDetails.put(userName, randomToken);
        return draftTokenDetails;
    }

    public UserDraftToken getTokenFromUser(String user){
        Object[] objects = new Object[]{user};
        UserDraftToken usr = this.jdbcTemplate.queryForObject(SELECT_TOKEN, objects, new UserRowMapper1());
        System.out.println(usr.toString());
        return usr;
    }

    @Override
    public String resetSystemPassword(Map<String, String> details,  HttpServletRequest request) {
        System.out.println("inside resetSystem pass 2..");
        String user = details.get(details.keySet().toArray()[0].toString());
        final String draftToken = request.getHeader("x-new-user");

        System.out.println(draftToken);
        System.out.println(user);
        String token = getTokenFromUser(user).getToken();
        System.out.println(token);
        System.out.println("getTokenFromUser(user).getUsername() - "+ getTokenFromUser(user).getUsername());
        System.out.println(" check - "+details.keySet().toArray()[0].equals(getTokenFromUser(user).getUsername()));
        System.out.println("draftToken.equals(token) - "+ draftToken.equals(token));
        if (details.keySet().toArray()[0].equals(getTokenFromUser(user).getUsername()) &&  draftToken.equals(token))
        {
            System.out.println("true");
        }
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

    public static class UserRowMapper1 implements RowMapper<UserDraftToken>{

        @Override
        public UserDraftToken mapRow(ResultSet resultSet, int i) throws SQLException {
            String user = resultSet.getString("USER_ID");
            String token = resultSet.getString("TOKEN");

            UserDraftToken userDraftToken = new UserDraftToken();
            userDraftToken.setUsername(user);
            userDraftToken.setToken(token);

            return userDraftToken;
        }
    }

}
