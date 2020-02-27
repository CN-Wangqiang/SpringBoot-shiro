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


/**
 * @version : V1.0
 * @ClassName: HomeController
 * @Description: TODO
 * @Auther: wangqiang
 * @Date: 2020/2/26 22:30
 */
@Controller
public class LoginController {
    @Autowired
    private LoginService loginService;

    //登陆页面
    @GetMapping(value = "/login")
    public String login() {
        return "login";
    }

    //登陆请求
    @PostMapping("/login")
    public String doLogin(@RequestParam String username,
                          @RequestParam String password,
                          Model model) {
        UsernamePasswordToken token = new UsernamePasswordToken(username, password);
        Subject subject = SecurityUtils.getSubject();
        try {
            subject.login(token);
        } catch (IncorrectCredentialsException ice) {
            model.addAttribute("msg","密码错误");
            return "login";
        } catch (UnknownAccountException uae) {
            model.addAttribute("msg","用户名错误");
            return "login";
        }

        User user = loginService.getUserByName(username);
        subject.getSession().setAttribute("user", user);
        return "redirct:/index";
    }

    //未授权请求index将会跳转到登陆页面
    @GetMapping("/index")
    public String index() {
        return "index";
    }

}
