package com.example;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.spec.EdECPoint;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

@RestController
public class HelloController {
	@Autowired
	private JdbcTemplate jdbcTemplate;
	

    // get接口为地址"/query"，该接口的具体用法看apifox，这里不再细说，下面几个方法也一样，不再注释了。
    // mp是接收的参数map
    @GetMapping("/query")
    public Map<String, Object> query(@RequestParam Map<String, String> mp){
        int page = -1;
        //拼接mysql语句，最后的sql语句类似于 SELECT * FROM appointment_table where xxx="xx" and xxx="xx" and xxx="xx" order by id desc 
        //接上句注释 order by id desc表示按id降序排序，新添加的记录再最前面，这里是同时查询appintment_table和user_table两张表。
        String sql = "SELECT * FROM appointment_table a, user_table b where a.user_id = b.user_id ";
        //遍历整个接收到的参数map
        for(String key : mp.keySet()){
            if(key.equals("page")){
                page = Integer.parseInt(mp.get(key));
            }
            else{
                sql = sql + " and " + key + " = " + "\"" + mp.get(key) + "\"";
            }
        }
        sql = sql + " order by a.id desc";
        // 执行sql语句，并把查询结果保存在list中，返回给前端。
        List<Map<String, Object>> list;
        try{
            list = jdbcTemplate.queryForList(sql);
        }catch(Exception e){
            e.printStackTrace();
            return Map.of("code", 003);
        }
        int size = list.size();
        if(page != -1){
            try{
                list = list.subList(page * 10 - 10, min(page * 10, size));
            }catch(Exception e){
                e.printStackTrace();
                return Map.of("code", 006);
            }
        }
        for(int i = 0; i < list.size(); i++){
            Map<String, Object> tmp = list.get(i);
            tmp.remove("pwd_md5");
            tmp.remove("session_id");
            tmp.remove("expiration_time");
        }
        Map<String, Object> res = Map.of("code", 000, "appointment_list", list, "size", size);
        return res;
    }

    private int min(int a, int b) {
        if(a > b) return b;
        else return a;
    }

    @PostMapping("/submit")
    public Map<String, Object> submit(@RequestParam Map<String, String> mp, @RequestHeader("Authorization") String session_id){
        int first = 0; 
        // 这部分是用来生成创建时间，再把它变成指定的格式，便于放入数据库中
        Date date_time = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyy-MM-dd HH:mm:ss");
        String sql_time = sdf.format(date_time);
        // 验证session_id 
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
        // 通过上一个if语句表示已经通过验证了
        mp.put("user_id", ls.get(0).get("user_id").toString());
        mp.put("create_time", sql_time);
        // 拼装sql语句，最后结果类似于 insert into appointment_table (xxx1,xxx2,xxx3) values ('xx1','xx2','xx3')
        sql = "insert into appointment_table (";
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
        // 这个sql语句用来查询最新插入的记录
        sql = "SELECT LAST_INSERT_ID()";
        Map<String, Object> map = jdbcTemplate.queryForMap(sql);
        // 构造map，便于调用之前写好的query方法查询最新插入的记录，并把结果保存在ls中，交给Roboot类处理机器人发送消息，并返回ls给前端
        Map<String, String> map2 = Map.of("id", map.get("LAST_INSERT_ID()").toString());
        ls = (List<Map<String, Object>>) query(map2).get("appointment_list");
        Roboot.send((Map<String, Object>)ls.get(0));
        return Map.of("code", 000, "appointment", ls.get(0));
    }

    @PostMapping("/delete")
    public Map<String, Object> delete(@RequestParam Map<String, String> mp, @RequestHeader("Authorization") String session_id){
        int ok = 1;
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
        String user_id = ls.get(0).get("user_id").toString();
        String level = ls.get(0).get("level").toString();
        String expiration_time = ls.get(0).get("expiration_time").toString();
        sql = "select * from appointment_table where id =" + mp.get("id");
        try{
            ls = jdbcTemplate.queryForList(sql);
        }catch(Exception e){
            e.printStackTrace();
            return Map.of("code", 3);
        }
        if(ls.size() == 0){
            return Map.of("code", 9);
        }
        String appointment_user_id = ls.get(0).get("user_id").toString();
        if(String.valueOf(System.currentTimeMillis()).compareTo(expiration_time) > 0){
            return Map.of("code", 4);
        }
        if(user_id.compareTo(appointment_user_id) != 0 && level.compareTo("2") < 0 ){
            return Map.of("code", 5);
        }
        // 拼装sql语句，结果类似于 delete from appointment_table where id = x
        sql = "delete from appointment_table where id = ";
        // 遍历mp，找到id值
        sql += mp.get("id");
        // 执行sql语句，这里加入异常处理，防止删除了一个不存在的id记录报错
        try{
            jdbcTemplate.update(sql);
        }catch(Exception e){
            e.printStackTrace();
            return Map.of("code", 3);
        }
        return Map.of("code", 0);
    }

    @PostMapping("/update")
    public Map<String, Object> update(@RequestParam Map<String, String> mp, @RequestHeader("Authorization") String session_id){
        int ok = 1;
        int id = 0;
        int first = 0;
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
        String user_id = ls.get(0).get("user_id").toString();
        String level = ls.get(0).get("level").toString();
        String expiration_time = ls.get(0).get("level").toString();
        sql = "select * from appointment_table where id =" + mp.get("id");
        ls = jdbcTemplate.queryForList(sql);
        String request_user_id = ls.get(0).get("user_id").toString();
        if(String.valueOf(System.currentTimeMillis()).compareTo(expiration_time) > 0){
            return Map.of("code", 4);
        }
        if(user_id.compareTo(request_user_id) != 0 && level.compareTo("2") < 0 ){
            return Map.of("code", 5);
        }
        // sql语句类似于update appointment_table set xxx='xx',xxx='xx',xxx='xx' where id = x
        sql = "update appointment_table set ";
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
        // 异常处理，防止更改了一条id不存在的记录
        try{
            jdbcTemplate.update(sql);
        }catch(Exception e){
            e.printStackTrace();
            return Map.of("code", 3);
        }
        return Map.of("code", 0);
    }

    // 添加消息的方法如submit
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

    // 查询消息的方法如 query
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
