package com.loginService.service;

import java.sql.SQLException;
import java.util.Map;

import com.loginService.model.LoginUser;

import javax.servlet.http.HttpServletResponse;

public interface IUserRepository {
    LoginUser save(LoginUser loginUser) throws SQLException;
    LoginUser findByUserName(String userName) throws SQLException;
    String saveToDraftTokenTable(String userName, String randomToken);
    String resetSystemPassword(Map<String, String> details, HttpServletResponse response);
}
