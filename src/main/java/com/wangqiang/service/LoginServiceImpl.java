package com.wangqiang.service;

import com.wangqiang.pojo.Permissions;
import com.wangqiang.pojo.Role;
import com.wangqiang.pojo.User;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * @version : V1.0
 * @ClassName: LoginServiceImpl
 * @Description: TODO
 * @Auther: wangqiang
 * @Date: 2020/2/26 20:10
 */
@Service
public class LoginServiceImpl implements LoginService {
    @Override
    public User getUserByName(String name) {
        return getMapByName(name);
    }

    /**
     * 模拟数据库查询
     * @param name
     * @return
     */
    private User getMapByName(String name){
        Permissions query = new Permissions(1,"query");
        Permissions add = new Permissions(2,"add");
        Permissions update = new Permissions(2,"update");
        Permissions delete = new Permissions(2,"delete");
        Set<Permissions> permissionSet = new HashSet<>();
        permissionSet.add(query);
        permissionSet.add(add);
        permissionSet.add(update);
        permissionSet.add(delete);
        Role role = new Role(1,"admin",permissionSet);
        HashSet<Role> roleSet = new HashSet<>();
        roleSet.add(role);
        User user1 = new User(1, "wangqiang", "123456", roleSet);
        HashMap<String, User> map = new HashMap<>();
        map.put(user1.getUserName(),user1);

        HashSet<Permissions> permissionSet1 = new HashSet<>();
        permissionSet1.add(query);
        Role role1 = new Role(2,"user",permissionSet1);
        HashSet<Role> roleSet1 = new HashSet<>();
        roleSet1.add(role1);
        User user2 = new User(2, "zhangsan", "123456", roleSet1);
        map.put(user2.getUserName(),user2);

        return map.get(name);
    }
}
