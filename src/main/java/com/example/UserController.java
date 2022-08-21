package com.example;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

@RestController
public class UserController {

    @Autowired
	private JdbcTemplate jdbcTemplate;

    @PostMapping("/login")
    public Map<String, Object> login (@RequestParam Map<String, String> mp){
        String user_id = mp.get("user_id");
        String pwd_md5 = mp.get("pwd_md5");
        String now_stamp = String.valueOf(System.currentTimeMillis());
        String sql = "select * from user_table where user_id = '" + user_id + "'";
        // 查询用户id，若存在则验证pwd_md5是否一致
        List<Map<String, Object>> ls;
        try{
            ls =  jdbcTemplate.queryForList(sql);
        }catch(Exception e){
            e.printStackTrace();
            return Map.of("code", 003);
        }
        if(ls.size() == 0){
            return Map.of("code", 001);
        }
        else if(ls.get(0).get("pwd_md5").equals(pwd_md5) == false){
            return Map.of("code", 002);
        }
        else {
            String old_expiration_time = ls.get(0).get("expiration_time").toString();
            if(now_stamp.compareTo(old_expiration_time) < 0){
                // 没过期，返回之前的session_id
                Map<String, Object> res = new HashMap<String, Object>();
                res.put("code", 000);
                res.put("session_id", ls.get(0).get("session_id").toString());
                return res;
            }
            // 更新数据表中的session_id和expiration_time
            String expiration_time = String.valueOf(Long.parseLong(now_stamp) + 7 * 24 * 3600 * 1000);
            sql = "update user_table set session_id = '" + now_stamp + "', expiration_time ='" + expiration_time + "' where user_id = '" + user_id + "'";
            try{
                jdbcTemplate.update(sql);
            }catch(Exception e){
                e.printStackTrace();
                return Map.of("code", 003);
            }
            return Map.of("code", 000, "session_id", now_stamp);
        }
    }

    //这个方法用来为其他查询接口服务，便于代码复用
    private Map<String, Object> query_user_without_session_id(Map<String, String>mp){
        int page = -1;
        int first = 0;
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
                sql += "and " + key + "= '" + mp.get(key) + "' ";
            }
        }
        List<Map<String, Object>> ls = jdbcTemplate.queryForList(sql);
        int size = ls.size();
        if(page != -1){
            try{
                ls = ls.subList(page*10 - 10, min(page*10, size));
            }catch(Exception e){
                e.printStackTrace();
                return Map.of("code", 006);
            }
        }
        // 除去不必要的返回内容
        for(int i = 0; i < ls.size(); i++){
            Map<String, Object> tmp = ls.get(i);
            tmp.remove("pwd_md5");
            tmp.remove("session_id");
            tmp.remove("expiration_time");
        }
        Map<String, Object> res = Map.of("code", 000, "user_list", ls, "size", size);
        return res;
    }

    @GetMapping("/query_user")
    public Map<String, Object> query_user(@RequestParam Map<String, String> mp, @RequestHeader("Authorization") String session_id){
        String sql = "select * from user_table where session_id='" + session_id + "'";
        List<Map<String, Object>> ls;
        try{ 
            ls =jdbcTemplate.queryForList(sql);
        }catch(Exception e){
            e.printStackTrace();
            return Map.of("code", 003);
        }
        if(ls.size() == 0){
            return Map.of("code", 8);
        }
        String level = ls.get(0).get("level").toString();
        if(String.valueOf(System.currentTimeMillis()).compareTo(ls.get(0).get("expiration_time").toString()) > 0){
            return Map.of("code", 004);
        }
        if(level.compareTo("2") < 0){
            return Map.of("code", 005);
        }
        return query_user_without_session_id(mp);
    }

    @GetMapping("/query_doctor")
    public Map<String, Object> query_doctor(@RequestParam Map<String, String>mp){
        mp.put("level", "1");
        return query_user_without_session_id(mp);
    }

    @GetMapping("/query_self")
    public Map<String, Object> query_self(@RequestParam Map<String, String> mp, @RequestHeader("Authorization") String session_id){
        String sql = "select * from user_table where session_id='" + session_id + "'";
        List<Map<String, Object>> ls;
        try{
            ls = jdbcTemplate.queryForList(sql);
        }catch(Exception e){
            e.printStackTrace();
            return Map.of("code", 003);
        }
        if(ls.size() == 0){
            return Map.of("code", 8);
        }
        String expiration_time = ls.get(0).get("expiration_time").toString();
        if(String.valueOf(System.currentTimeMillis()).compareTo(expiration_time) > 0){
            return Map.of("code", 004);
        }
        else {
            Map<String, Object> tmp = ls.get(0);
            tmp.remove("pwd_md5");
            tmp.remove("session_id");
            tmp.remove("expiration_time");
            return Map.of("code", 000, "user_info", tmp);
        }
    }

    private int min(int a, int b) {
        if(a < b) return a;
        else return b;
    }

    @PostMapping("/register")
    public Map<String, Object> register(@RequestParam Map<String, String> mp){
        int first = 0;
        String sql = "select * from user_table where user_id='" + mp.get("user_id") + "'";
        if(jdbcTemplate.queryForList(sql).size() != 0){
            return Map.of("code", 007);
        }
        String now_stamp = String.valueOf(System.currentTimeMillis());
        mp.put("session_id", now_stamp);
        mp.put("expiration_time", now_stamp);
        sql = "insert into user_table (";
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
            System.out.println(sql);
            e.printStackTrace();
            return Map.of("code", 003);
        }
        return Map.of("code", 000);
    }

    @PostMapping("/update_user")
    public Map<String, Object> update_user(@RequestParam Map<String, String> mp, @RequestHeader("Authorization") String session_id){
        int first = 0;
        String sql = "select * from user_table where session_id='" + session_id + "'";
        List<Map<String, Object>> ls;
        try{
            ls = jdbcTemplate.queryForList(sql);
        }catch(Exception e){
            e.printStackTrace();
            return Map.of("code", 003);
        }
        if(ls.size() == 0){
            return Map.of("code", 8);
        }
        String user_id = ls.get(0).get("user_id").toString();
        String level = ls.get(0).get("level").toString();
        String dest_user_id = mp.get("user_id");
        if(String.valueOf(System.currentTimeMillis()).compareTo(ls.get(0).get("expiration_time").toString()) > 0){
            return Map.of("code", 004);
        }
        if(user_id.compareTo(dest_user_id) != 0 && level.compareTo("2") < 0){
            return Map.of("code", 005);
        }
        // 拒绝非管理员修改自己的权限
        if(mp.containsKey("level") && level.compareTo("2") < 0){
            return Map.of("code", 005);
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
        try{
            jdbcTemplate.update(sql);
        }catch(Exception e){
            e.printStackTrace();
            return Map.of("code", 003);
        }
        return Map.of("code", 000);
    }

    @PostMapping("/delete_user")
    public Map<String, Object> delete_user(@RequestParam Map<String, String> mp, @RequestHeader("Authorization") String session_id){
        String sql = "select * from user_table where session_id='" + session_id + "'";
        List<Map<String, Object>> ls;
        try{
            ls = jdbcTemplate.queryForList(sql);
        }catch(Exception e){
            e.printStackTrace();
            return Map.of("code", 003);
        }
        if(ls.size() == 0){
            return Map.of("code", 8);
        }
        String user_id = ls.get(0).get("user_id").toString();
        String level = ls.get(0).get("level").toString();
        String dest_user_id = mp.get("user_id");
        if(String.valueOf(System.currentTimeMillis()).compareTo(ls.get(0).get("expiration_time").toString()) > 0){
            return Map.of("code", 004);
        }
        if(user_id.compareTo(dest_user_id) != 0 && level.compareTo("2") < 0){
            return Map.of("code", 005);
        }
        sql = "select * from user_table where user_id ='" + dest_user_id + "'";
        try{
            ls =jdbcTemplate.queryForList(sql);
        }catch (Exception e){
            e.printStackTrace();
            return Map.of("code", 3);
        }
        if(ls.size() == 0){
            return Map.of("code", 9);
        }
        sql = "delete from user_table where user_id='" + dest_user_id + "'";
        try{
            jdbcTemplate.update(sql);
        }catch (Exception e){
            e.printStackTrace();
            return Map.of("code", 003);
        }
        return Map.of("code", 000);
    }
}
