package com.example;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class FileController {
    @Value("${file.upload.path}")
    private String path;

    @PostMapping("/upload")
    @ResponseBody
    public Map<String, Object> upload(@RequestParam("file") MultipartFile file, @RequestParam("type") String type){
        String oldName = file.getOriginalFilename();
        String fileName = String.valueOf(System.currentTimeMillis()) + oldName.substring(oldName.lastIndexOf("."));
        String filePath = path + type + "/" + fileName;
        File dest = new File(filePath);
        if(dest.getParentFile().exists() == false){
            dest.getParentFile().mkdirs();
        }
        try {
            file.transferTo(dest);
        } catch (Exception e) {
            e.printStackTrace();
            return Map.of("code", 3);
        }
        return Map.of("code", 0, "file_name", fileName);
    }

    public static void delete_file(String filePath){
        // todo
        File file = new File(filePath);
        file.delete();
    }

    @GetMapping("/download")
    public String download(HttpServletResponse response, @RequestParam("file") String file){
        int ok = 1;
        File dest = new File(path + file);
        response.reset();
        response.setContentType("application/octet-stream");
        response.setCharacterEncoding("utf-8");
        response.setContentLength((int)dest.length());
        response.setHeader("Content-Disposition", "attachment;filename="+file);
        try(BufferedInputStream bis = new BufferedInputStream(new FileInputStream(dest));) {
            byte[] buff = new byte[1024];
            OutputStream os  = response.getOutputStream();
            int i = 0;
            while ((i = bis.read(buff)) != -1) {
                os.write(buff, 0, i);
                os.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
            ok = 0;
        }
        if(ok == 1) return "OK";
        else return "FAIL";
    }
}
