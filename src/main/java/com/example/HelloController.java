package com.example;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

@CrossOrigin(origins = "*")
@RestController
public class HelloController {
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
    @CrossOrigin(origins = "*")
	@RequestMapping("/getTable")
    public List<Map<String, Object>> getTable(){
        String sql = "select * from appointment_table";
        List<Map<String, Object>> list =  jdbcTemplate.queryForList(sql);
        for (Map<String, Object> map : list) {
            Set<Entry<String, Object>> entries = map.entrySet( );
            if(entries != null) {
                Iterator<Entry<String, Object>> iterator = entries.iterator( );
                while(iterator.hasNext( )) {
                    Entry<String, Object> entry =(Entry<String, Object>) iterator.next( );
                    Object key = entry.getKey( );
                    Object value = entry.getValue();
                    System.out.println(key+":"+value);
                }
            }
        }
        return list;
	}

    @CrossOrigin(origins = "*")
    @RequestMapping("/{demo}")
    public List<Map<String, Object>> demo(@PathVariable(name = "demo") String demo, @RequestParam(name = "name") String name){
        String sql = "SELECT * FROM appointment_table WHERE name = \"" + name + "\"";
        List<Map<String, Object>> list =  jdbcTemplate.queryForList(sql);
        for (Map<String, Object> map : list) {
            Set<Entry<String, Object>> entries = map.entrySet( );
            if(entries != null) {
                Iterator<Entry<String, Object>> iterator = entries.iterator( );
                while(iterator.hasNext( )) {
                    Entry<String, Object> entry =(Entry<String, Object>) iterator.next( );
                    Object key = entry.getKey( );
                    Object value = entry.getValue();
                    System.out.println(key+":"+value);
                }
            }
        }
        return list;
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/query")
    public List<Map<String, Object>> query(@RequestParam Map<String, String> mp){
        int first = 0;
        int page = -1;
        String sql = "SELECT * FROM appointment_table ";
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
        sql = sql + "order by id desc";
        if(page != -1){
            int lmt = page * 10 - 10;
            sql = sql +  " limit " + lmt + ",10";
        }
        List<Map<String, Object>> list =  jdbcTemplate.queryForList(sql);
        return list;
    }

    @CrossOrigin(origins = "*")
    @PostMapping("/submit")
    public String submit(@RequestParam Map<String, String> mp){
        int first = 0; 
        int ok = 1;
        Date date_time = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyy-MM-dd HH:mm:ss");
        String sql_time = sdf.format(date_time);
        mp.put("create_time", sql_time);
        String sql = "insert into appointment_table (";
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
            Map<String, String> map2 = Map.of("id", map.get("LAST_INSERT_ID()").toString());
            return query(map2).toString();
        }
    }

    @CrossOrigin(origins = "*")
    @PostMapping("/delete")
    public String delete(@RequestParam Map<String, String> mp){
        int ok = 1;
        String sql = "delete from appointment_table where id = ";
        for(String key : mp.keySet()){
            if(key.equals("id")) {
                sql = sql + mp.get(key);
                break;
            }
        }
        try{
            jdbcTemplate.update(sql);
        }catch(Exception e){
            e.printStackTrace();
            ok = 0;
        }
        if(ok == 0) return "FAIL";
        else return "OK";
    }

    @CrossOrigin(origins = "*")
    @PostMapping("/update")
    public String update(@RequestParam Map<String, String> mp){
        int ok = 1;
        int id = 0;
        int first = 0;
        String sql = "update appointment_table set ";
        for(String key : mp.keySet()){
            if(key.equals("id")){
                id = Integer.parseInt(mp.get(key));
            }
            else if(first == 0){
                sql = sql + key + "='" + mp.get(key) + "'";
                first = 1;
            }
            else{
                sql = sql + "," + key + "='" + mp.get(key) + "'";
            }
        }
        sql = sql + " where id=" + id;
        try{
            jdbcTemplate.update(sql);
        }catch(Exception e){
            e.printStackTrace();
            ok = 0;
        }
        if(ok == 1) return "OK";
        else return "FAIL";
    }
    @CrossOrigin(origins = "*")
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

    @CrossOrigin(origins = "*")
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
