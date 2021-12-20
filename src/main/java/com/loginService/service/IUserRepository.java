package com.loginService.service;

import java.sql.SQLException;
import com.loginService.model.LoginUser;

public interface IUserRepository {
    LoginUser save(LoginUser loginUser) throws SQLException;
    LoginUser findByUserName(String userName) throws SQLException;
    String saveToDraftTokenTable(String userName, String randomToken);
    String resetSystemPassword();
}
