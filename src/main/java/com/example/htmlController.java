package com.example;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;

@CrossOrigin(origins = "*")
@Controller
public class htmlController {
    @GetMapping("/upload.html")
    @CrossOrigin(origins = "*")
    public String uploadPage(){
        return "upload";
    }
    
    @GetMapping("/")
    @CrossOrigin(origins = "*")
    public String homePage(){
        return "home";
    }
}
