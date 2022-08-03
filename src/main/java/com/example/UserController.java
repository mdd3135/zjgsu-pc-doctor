package com.example;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

@RestController
public class UserController {

    @Autowired
	private JdbcTemplate jdbcTemplate;

    @PostMapping("/login")
    public String login (@RequestParam Map<String, String> mp){
        String user_id = mp.get("user_id");
        String pwd_md5 = mp.get("pwd_md5");
        String now_stamp = String.valueOf(System.currentTimeMillis());
        String sql = "select * from user_table where user_id = '" + user_id + "'";
        // 查询用户id，若存在则验证pwd_md5是否一致，若不存在则返回FAIL
        List<Map<String, Object>> ls =  jdbcTemplate.queryForList(sql);
        if(ls.size() > 0 && ls.get(0).get("pwd_md5").equals(pwd_md5)){
            // 验证通过，更新数据表中的session_id和expiration_time
            String expiration_time = String.valueOf(Long.parseLong(now_stamp) + 7 * 24 * 3600 * 1000);
            sql = "update user_table set session_id = '" + now_stamp + "', expiration_time ='" + expiration_time + "' where user_id = '" + user_id + "'";
            jdbcTemplate.update(sql);
            return now_stamp;
        }
        else{
            return "FAIL";
        }
    }

    @GetMapping("/query_user")
    public List<Map<String, Object>> query_user(@RequestParam Map<String, String> mp){
        int first = 0;
        int page = -1;
        String sql = "select * from user_table ";
        for(String key : mp.keySet()){
            if(key.equals("page")){
                page = Integer.parseInt(mp.get("page"));
            }
            else if(first == 0){
                first = 1;
                sql += "where " + key + "= '" + mp.get(key) + "' ";
            }
            else{
                sql += "and " + key + "= " + mp.get(key) + "' ";
            }
        }
        if(page != -1){
            int lmt = page * 10 - 10;
            sql = sql +  " limit " + lmt + ",10";
        }
        List<Map<String, Object>> ls = jdbcTemplate.queryForList(sql);
        return ls;
    }

    @PostMapping("/register")
    public String register(@RequestParam Map<String, String> mp){
        int first = 0;
        String sql = "insert into user_table (";
        for(String key : mp.keySet()){
            if(first == 0){
                sql = sql + key;
                first = 1;
            }
            else{
                sql = sql + "," + key;
            }
        }
        sql = sql + ") values (";
        first = 0;
        for(String key : mp.keySet()){
            if(first == 0){
                sql = sql + "'" + mp.get(key) + "'";
                first = 1;
            }
            else{
                sql = sql + "," + "'" + mp.get(key) + "'";
            }
        }
        sql = sql + ")";
        // 执行sql语句，插入记录成功
        try {
            jdbcTemplate.update(sql);
        }catch (Exception e){
            return "FAIL";
        }
        return "OK";
    }

    @PostMapping("/update_user")
    public String update_user(@RequestParam Map<String, String> mp, @RequestHeader("Authorization") String session_id){
        int first = 0;
        String sql = "select * from user_table where session_id='" + session_id + "'";
        List<Map<String, Object>> ls = jdbcTemplate.queryForList(sql);
        String user_id = ls.get(0).get("user_id").toString();
        String level = ls.get(0).get("level").toString();
        String dest_user_id = mp.get("user_id");
        if(user_id.compareTo(dest_user_id) != 0 && level.compareTo("2") < 0){
            return "FAIL";
        }
        // 拒绝非管理员修改自己的权限
        if(mp.containsKey("level") && level.compareTo("2") < 0){
            return "FAIL";
        }
        sql = "update user_table set ";
        for(String key : mp.keySet()){
            if(key.equals("user_id")){
                continue;
            }
            else if(first == 0){
                sql = sql + key + "='" + mp.get(key) + "'";
                first = 1;
            }
            else{
                sql = sql + "," + key + "='" + mp.get(key) + "'";
            }
        }
        sql = sql + " where user_id='" + dest_user_id + "'";
        jdbcTemplate.update(sql);
        return "OK";
    }

    @PostMapping("/delete_user")
    public String delete_user(@RequestParam Map<String, String> mp, @RequestHeader("Authorization") String session_id){
        String sql = "select * from user_table where session_id='" + session_id + "'";
        List<Map<String, Object>> ls = jdbcTemplate.queryForList(sql);
        String user_id = ls.get(0).get("user_id").toString();
        String level = ls.get(0).get("level").toString();
        String dest_user_id = mp.get("user_id");
        if(user_id.compareTo(dest_user_id) != 0 && level.compareTo("2") < 0){
            return "FAIL";
        }
        sql = "delete from user_table where user_id='" + dest_user_id + "'";
        jdbcTemplate.update(sql);
        return "OK";
    }
}
