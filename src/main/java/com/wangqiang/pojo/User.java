package com.wangqiang.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * @version : V1.0
 * @ClassName: User
 * @Description: TODO
 * @Auther: wangqiang
 * @Date: 2020/2/26 20:07
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private int id;
    private String userName;
    private String password;
    //用户对应的角色
    private Set<Role> roles;
}
