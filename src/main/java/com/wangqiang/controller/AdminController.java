package com.wangqiang.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @version : V1.0
 * @ClassName: AuthcController
 * @Description: TODO
 * @Auther: wangqiang
 * @Date: 2020/2/26 22:33
 */

@RestController
public class AdminController {

    //管理者页面（需要admin权限）
    @GetMapping("admin")
    public Object admin() {
        return "管理者界面";
    }
    // 添加更新操作（需要add,update权限）
    @GetMapping("/admin/renewable")
    public Object renewable() {
        return "添加或更新操作";
    }
    // 删除操作（需要delete权限）
    @GetMapping("/admin/removable")
    public Object removable() {
        return "删除操作";
    }
}
