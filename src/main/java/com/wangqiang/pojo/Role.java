package com.wangqiang.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * @version : V1.0
 * @ClassName: Role
 * @Description: TODO
 * @Auther: wangqiang
 * @Date: 2020/2/26 20:06
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Role {
    private int id;
    private String Name;
    //角色对应权限
    private Set<Permissions> permissions;
}
