package com.koushaljhait.controller.common;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    // Use unique paths that won't conflict
    @GetMapping("/app")
    public String appHome() {
        return "forward:/index.html";
    }
}