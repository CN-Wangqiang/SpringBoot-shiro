package com.wangqiang.service;

import com.wangqiang.pojo.User;

/**
 * @version : V1.0
 * @ClassName: LoginService
 * @Description: TODO
 * @Auther: wangqiang
 * @Date: 2020/2/26 20:09
 */
public interface LoginService {
    User getUserByName(String name);
}
