package com.example;

import java.util.Map;

public class Roboot {
    
    public static void send(Map<String, Object> mesg, String group){
        String content = "";
        for(String key : mesg.keySet()){
            if(key.equals("name")){
                content += "姓名：" + mesg.get(key) + "\n";
            }
            else if(key.equals("contact_details")){
                content += "联系方式：" + mesg.get(key) + "\n";
            }
            else if(key.equals("problem_description")){
                content += "问题描述：" + mesg.get(key) + "\n";
            }
            else if(key.equals("problem_category")){
                content += "问题分类：" + mesg.get(key) + "\n";
            }
        }
        String str[] = {"python", "python/roboot_send_mesg.py", group, content};

        try {
            Process p = Runtime.getRuntime().exec(str);
            p.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
