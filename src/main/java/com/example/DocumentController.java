package com.example;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

@RestController
public class DocumentController {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PostMapping("/submit_doc")
    public Map<String, Object> submit_doc(@RequestParam Map<String, String> mp, @RequestHeader("Authorization") String session_id){
        Date date_time = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyy-MM-dd HH:mm:ss");
        String sql_time = sdf.format(date_time);
        String sql = "select * from user_table where session_id = '" + session_id + "'";
        List<Map<String, Object>> ls;
        try {
            ls = jdbcTemplate.queryForList(sql);
        }catch(Exception e){
            e.printStackTrace();
            return Map.of("code", 003);
        }
        if(ls.size() == 0){
            return Map.of("code", 8);
        }
        if(String.valueOf(System.currentTimeMillis()).compareTo(ls.get(0).get("expiration_time").toString()) > 0){
            // session_id过期
            return Map.of("code", 004);
        }
        if(ls.get(0).get("level").toString().equals("2") != true){
            return Map.of("code", 005);
        }
        mp.put("create_time", sql_time);
        sql = "insert into document_table (";
        int first = 0;
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
        try{
            jdbcTemplate.update(sql);
        }catch(Exception e){
            e.printStackTrace();
            return Map.of("code", 003);
        }
        return Map.of("code", 0);
    }

    @GetMapping("/query_doc")
    public Map<String, Object> query_doc(@RequestParam Map<String, String> mp){
        String sql = "select * from document_table ";
        if(mp.containsKey("id")){
            sql += "where id = " + mp.get("id");
        }
        else if(mp.containsKey("title")){
            sql += "where title like '%" + mp.get("title") + "%'";
        }
        sql = sql + " order by id desc";
        List<Map<String, Object>> list;
        try{
            list = jdbcTemplate.queryForList(sql);
        }catch(Exception e){
            e.printStackTrace();
            return Map.of("code", 3);
        }
        return Map.of("code", 0, "doc_list", list);
    }

    @PostMapping("/delete_doc")
    public Map<String, Object> delete_doc(@RequestParam Map<String, String> mp, @RequestHeader("Authorization") String session_id){
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
        if(level.compareTo("2") < 0 ){
            return Map.of("code", 5);
        }
        sql = "delete from document_table where id = ";
        sql += mp.get("id");
        try{
            jdbcTemplate.update(sql);
        }catch(Exception e){
            e.printStackTrace();
            return Map.of("code", 9);
        }
        return Map.of("code", 0);
    }
}
