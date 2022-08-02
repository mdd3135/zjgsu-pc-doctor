package com.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

@CrossOrigin(origins = "*")
@RestController
public class LoginController {

    @Autowired
	private JdbcTemplate jdbcTemplate;

    @CrossOrigin(origins = "*")
    @PostMapping("/login")
    public String login (@CookieValue(value = "session_id", defaultValue = "null") String cookie_session, @RequestParam Map<String, String> mp, HttpServletResponse response){
        //  cookie有效的情况可以直接通过
        String sql = "select * from user_table where session_id = '" + cookie_session + "'";
        try{
            List<Map<String, Object>> ls =  jdbcTemplate.queryForList(sql);
            if(ls.get(0).equals(cookie_session)){
                return ls.get(0).get("user_name").toString();
            }
        }catch(Exception e){

        }
        String user_name = mp.get("user_name");
        String pwd_md5 = mp.get("pwd_md5");
        String session_id = String.valueOf(System.currentTimeMillis());
        sql = "select * from user_table where user_name = '" + user_name + "'";
        // try中查询用户名，若存在则验证pwd_md5是否一致，若不存在则跳转到catch块，新建用户记录
        try{ 
            List<Map<String, Object>> ls =  jdbcTemplate.queryForList(sql);
            if(ls.get(0).get("pwd_md5").equals(pwd_md5)){
                // 验证通过，分配cookie并设置cookie7天有效，同时更新数据表中的session_id
                Cookie cookie = new Cookie("session_id", session_id);
                cookie.setMaxAge(7*24*60*60);
                response.addCookie(cookie);
                sql = "update user_table set session_id = '" + session_id + "' where user_name = '" + user_name + "'";
                jdbcTemplate.update(sql);
                return user_name;
            } 
            else{
                return "FAIL";
            }
        }catch(Exception e){
            sql = "insert into user_table (level,user_name,pwd_md5,session_id) values(1,";
            sql += "'" + user_name + "','" + pwd_md5 + "','" + session_id + "')";
            jdbcTemplate.update(sql);
            Cookie cookie = new Cookie("session_id", session_id);
            cookie.setMaxAge(7*24*60*60);
            response.addCookie(cookie);
        }
        return user_name;
    }
}
