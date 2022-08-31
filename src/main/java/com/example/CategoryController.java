package com.example;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

@RestController
public class CategoryController {
    @Autowired
	private JdbcTemplate jdbcTemplate;

    @PostMapping("/add_category")
    public Map<String, Object> add_category(@RequestParam Map<String, String> mp, @RequestHeader("Authorization") String session_id){
        String sql = "select * from user_table where session_id='" + session_id + "'";
        List<Map<String, Object>> ls;
        try{
            ls = jdbcTemplate.queryForList(sql);
        }catch(Exception e){
            e.printStackTrace();
            return Map.of("code", 3);
        }
        if(ls.size() == 0){
            return Map.of("code", 8);
        }
        String level = ls.get(0).get("level").toString();
        String expiration_time = ls.get(0).get("expiration_time").toString();
        if(String.valueOf(System.currentTimeMillis()).compareTo(expiration_time) > 0){
            return Map.of("code", 4);
        }
        if(level.compareTo("2") < 0){
            return Map.of("code", 5);
        }
        sql = "select * from category_table where category='" + mp.get("category") + "'";
        if(jdbcTemplate.queryForList(sql).size() != 0){
            return Map.of("code", 10);
        }
        sql = "insert into category_table (category) values('" + mp.get("category") + "')";
        try{
            jdbcTemplate.update(sql);
        }catch(Exception e){
            e.printStackTrace();
            return Map.of("code", 3);
        }
        return Map.of("code", 0);
    }

    @GetMapping("/query_category")
    public Map<String, Object> query_category(@RequestParam Map<String, String> mp){
        String sql = "select * from category_table";
        List<Map<String, Object>> ls;
        try{
            ls = jdbcTemplate.queryForList(sql);
        }catch(Exception e){
            e.printStackTrace();
            return Map.of("code", 3);
        }
        return Map.of("code", 0, "list", ls);
    }

    @PostMapping("/delete_category")
    public Map<String, Object> delete_category(@RequestParam Map<String, String> mp, @RequestHeader("Authorization") String session_id){
        String sql = "select * from user_table where session_id='" + session_id + "'";
        List<Map<String, Object>> ls;
        try{
            ls = jdbcTemplate.queryForList(sql);
        }catch(Exception e){
            e.printStackTrace();
            return Map.of("code", 3);
        }
        if(ls.size() == 0){
            System.out.println(sql);
            return Map.of("code", 8);
        }
        String level = ls.get(0).get("level").toString();
        String expiration_time = ls.get(0).get("expiration_time").toString();
        if(String.valueOf(System.currentTimeMillis()).compareTo(expiration_time) > 0){
            return Map.of("code", 4);
        }
        if(level.compareTo("2") < 0){
            return Map.of("code", 5);
        }
        sql = "select * from category_table where category='" + mp.get("category") + "'";
        if(jdbcTemplate.queryForList(sql).size() == 0){
            return Map.of("code", 11);
        }
        sql = "delete from category_table where category='" + mp.get("category") + "'";
        try{
            jdbcTemplate.update(sql);
        }catch(Exception e){
            e.printStackTrace();
            return Map.of("code", 3);
        }
        return Map.of("code", 0);
    }
}
