package com.wangqiang.controller;


import com.wangqiang.pojo.User;
import com.wangqiang.service.LoginService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


/**
 * @version : V1.0
 * @ClassName: HomeController
 * @Description: TODO
 * @Auther: wangqiang
 * @Date: 2020/2/26 22:30
 */
@RestController
public class LoginController {
    @Autowired
    private LoginService loginService;

    //登陆页面
    @GetMapping("login")
    public Object login() {
        return "登陆页面";
    }

    //登陆请求
    @GetMapping("doLogin")
    public Object doLogin(@RequestParam String username, @RequestParam String password) {
        UsernamePasswordToken token = new UsernamePasswordToken(username, password);
        Subject subject = SecurityUtils.getSubject();
        try {
            subject.login(token);
        } catch (IncorrectCredentialsException ice) {
            return "密码错误";
        } catch (UnknownAccountException uae) {
            return "用户名错误";
        }

        User user = loginService.getUserByName(username);
        subject.getSession().setAttribute("user", user);
        return "登陆成功";
    }

    //未授权请求index将会跳转到登陆页面
    @GetMapping("/index")
    public Object index() {
        return "授权认证成功页面";
    }
}


