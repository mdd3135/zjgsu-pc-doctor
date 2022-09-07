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
public class NoticeController {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/query_group")
    public Map<String, Object> query_group(){
        String sql = "select * from notice_table";
        String group;
        try{
            group = jdbcTemplate.queryForList(sql).get(0).get("id").toString();
        } catch(Exception e){
            return Map.of("code", 3);
        }
        return Map.of("code", 0, "group", group);
    }

    @PostMapping("/update_group")
    public Map<String, Object> update_group(@RequestParam Map<String, String> mp, @RequestHeader("Authorization") String session_id){
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
        if(level.compareTo("1") < 0 ){
            return Map.of("code", 5);
        }
        sql = "update notice_table set id='" + mp.get("id").toString() + "'";
        try{
            jdbcTemplate.update(sql);
        }catch (Exception e){
            e.printStackTrace();
            return Map.of("code", 3);
        }
        return Map.of("code", 0);
    }
}