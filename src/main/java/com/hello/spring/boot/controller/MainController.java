package com.hello.spring.boot.controller;

import com.hello.spring.boot.entity.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    @GetMapping("index")
    public String index(Model model){
        User user = new User();
        user.setUserName("haha");

        model.addAttribute("user",user);

        return "index";
    }
}
