package com.mndk.bouncerate.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@SuppressWarnings("unused")
public class SinglePageController {

    @GetMapping({ "/categories", "/setTopBoxes" })
    public String forward404() {
        return "forward:/";
    }

}
