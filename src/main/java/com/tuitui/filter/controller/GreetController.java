package com.tuitui.filter.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author liujianxue
 * @since 2018/8/13
 */
@Controller
@RequestMapping("/test")
public class GreetController {

    @RequestMapping(value = "/greet",method = RequestMethod.POST)
    @ResponseBody
    public String sayHello(){
        return "hello";
    }


}
