package com.example;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

@RestController
public class MessageController {
    @Autowired
	private JdbcTemplate jdbcTemplate;
    
    @PostMapping("/add_message")
    public String add_message(@RequestParam Map<String, String> mp){
        int first = 0; 
        int ok = 1;
        Date date_time = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyy-MM-dd HH:mm:ss");
        String sql_time = sdf.format(date_time);
        mp.put("time", sql_time);
        String sql = "insert into message_table (";
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
        try{
            jdbcTemplate.update(sql);
        }catch(Exception e){
            e.printStackTrace();
            ok = 0;
        }
        if(ok == 0) return "FAIL";
        else {
            sql = "SELECT LAST_INSERT_ID()";
            Map<String, Object> map = jdbcTemplate.queryForMap(sql);
            System.out.println(map);
            return map.get("LAST_INSERT_ID()").toString();
        }
    }

    @GetMapping("/query_message")
    public List<Map<String, Object>> query_message(@RequestParam Map<String, String> mp){
        int first = 0;
        int page = -1;
        String sql = "SELECT * FROM message_table ";
        for(String key : mp.keySet()){
            if(key.equals("page")){
                page = Integer.parseInt(mp.get(key));
            }
            else if(first == 0){
                sql = sql + "where " + key + " = " + "\"" + mp.get(key) + "\"";
                first = 1;
            }
            else{
                sql = sql + " and " + key + " = " + "\"" + mp.get(key) + "\"";
            }
        }
        // sql = sql + "order by id desc";
        if(page != -1){
            int lmt = page * 10 - 10;
            sql = sql +  " limit " + lmt + ",10";
        }
        List<Map<String, Object>> list =jdbcTemplate.queryForList(sql);
        return list;
    }
}
