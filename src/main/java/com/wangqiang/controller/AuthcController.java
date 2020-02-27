package com.wangqiang.controller;

import com.wangqiang.pojo.User;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @version : V1.0
 * @ClassName: AuthcController
 * @Description: TODO
 * @Auther: wangqiang
 * @Date: 2020/2/27 00:45
 */
@RestController
public class AuthcController {

    //未授权页面
    @GetMapping("unauthc")
    public String unauthc() {
        return "对不起，您的权限不够";
    }

    //查看用户权限信息
    @GetMapping("/user")
    public String user() {
        Subject subject = SecurityUtils.getSubject();
        User user = (User) subject.getSession().getAttribute("user");
        return user.toString();
    }
}
