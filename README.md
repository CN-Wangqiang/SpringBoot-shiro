## SpringBoot-Shiro

### Shiro简介

Apache Shiro是一个强大且易用的Java安全框架,执行身份验证、授权、密码和会话管理。使用Shiro的易于理解的API,您可以快速、轻松地获得任何应用程序,从最小的移动应用程序到最大的网络和企业应用程序。

#### 核心组件

- Subject：主体，一般指用户。
- SecurityManager：安全管理器，管理所有Subject，可以配合内部安全组件。(类似于SpringMVC中的DispatcherServlet)
- Realms：用于进行权限信息的验证，一般需要自己实现。

其他介绍自行百度，本文主要记录springboot与shiro的集成

### 集成SpringBoot

#### 环境要求

- IDEA
- Maven

#### 基本环境搭建

1. 新建Spring项目，添加Lombok支持

2. 相关的pom依赖

   ```xml
   <dependencies>
           <dependency>
               <groupId>org.springframework.boot</groupId>
               <artifactId>spring-boot-starter-web</artifactId>
           </dependency>
   
           <dependency>
               <groupId>org.projectlombok</groupId>
               <artifactId>lombok</artifactId>
               <optional>true</optional>
           </dependency>
           <dependency>
               <groupId>org.springframework.boot</groupId>
               <artifactId>spring-boot-starter-test</artifactId>
               <scope>test</scope>
               <exclusions>
                   <exclusion>
                       <groupId>org.junit.vintage</groupId>
                       <artifactId>junit-vintage-engine</artifactId>
                   </exclusion>
               </exclusions>
           </dependency>
           <dependency>
               <groupId>org.apache.shiro</groupId>
               <artifactId>shiro-spring</artifactId>
               <version>1.4.0</version>
           </dependency>
           <dependency>
               <groupId>org.junit.jupiter</groupId>
               <artifactId>junit-jupiter</artifactId>
               <version>RELEASE</version>
               <scope>test</scope>
           </dependency>
       </dependencies>
   ```

3. 建立基本的结构框架

   - com.wangqiang.config
   - com.wangqiang.controller
   - com.wangqiang.pojo
   - com.wangqiang.service
   - com.wangqiang.shiro

#### 创建实体类

1. 创建Permission权限实体类

   ```java
   package com.wangqiang.pojo;
   import lombok.AllArgsConstructor;
   import lombok.Data;
   import lombok.NoArgsConstructor;
   
   @Data
   @AllArgsConstructor
   @NoArgsConstructor
   public class Permissions {
       private int id;
       //权限名称
       private String permissionsName;
   }
   ```

2. 创建Role角色实体类

   ```java
   package com.wangqiang.pojo;
   import lombok.AllArgsConstructor;
   import lombok.Data;
   import lombok.NoArgsConstructor;
   import java.util.Set;
   
   @Data
   @AllArgsConstructor
   @NoArgsConstructor
   public class Role {
       private int id;
       private String Name;
       //角色对应权限
       private Set<Permissions> permissions;
   }
   
   ```

3. 创建User用户实体类

   ```java
   package com.wangqiang.pojo;
   import lombok.AllArgsConstructor;
   import lombok.Data;
   import lombok.NoArgsConstructor;
   import java.util.Set;
   
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
   ```

#### Service层

本项目没有导入数据库，在service模拟数据库操作

1. 创建LoginService接口

   ```java
   package com.wangqiang.service;
   import com.wangqiang.pojo.User;
   
   public interface LoginService {
       User getUserByName(String name);
   }
   ```

2. 创建LoginImpl实现类

   ```java
   package com.wangqiang.service;
   import com.wangqiang.pojo.Permissions;
   import com.wangqiang.pojo.Role;
   import com.wangqiang.pojo.User;
   import org.springframework.stereotype.Service;
   import java.util.HashMap;
   import java.util.HashSet;
   import java.util.Set;
   
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
           //四种权限 增删改查
           Permissions query = new Permissions(1,"query");
           Permissions add = new Permissions(2,"add");
           Permissions update = new Permissions(2,"update");
           Permissions delete = new Permissions(2,"delete");
           Set<Permissions> permissionSet = new HashSet<>();
   				permissionSet.add(query);
           permissionSet.add(add);
           permissionSet.add(update);
           permissionSet.add(delete);
           //角色admin 赋予四种权限
           Role role = new Role(1,"admin",permissionSet);
           HashSet<Role> roleSet = new HashSet<>();
           roleSet.add(role);
           //给用户wangqiang赋予admin角色
           User user1 = new User(1, "wangqiang", "123456", roleSet);
           HashMap<String, User> map = new HashMap<>();
           map.put(user1.getUserName(),user1);
   
           HashSet<Permissions> permissionSet1 = new HashSet<>();
           permissionSet1.add(query);
           //角色user 赋予一种权限
           Role role1 = new Role(2,"user",permissionSet1);
           HashSet<Role> roleSet1 = new HashSet<>();
           roleSet1.add(role1);
         	//给用户zhangsan赋予user角色
           User user2 = new User(2, "zhangsan", "123456", roleSet1);
           map.put(user2.getUserName(),user2);
           return map.get(name);
       }
   }
   ```

   考虑如果在数据库实现可以建三张表，用户表，角色表，权限表，用户一对多角色，角色一对多权限

#### Shiro配置

1. 编写认证授权规则配置 MyRealm

   ```java
   package com.wangqiang.shiro;
   import com.wangqiang.pojo.Permissions;
   import com.wangqiang.pojo.Role;
   import com.wangqiang.pojo.User;
   import com.wangqiang.service.LoginService;
   import org.apache.shiro.authc.AuthenticationException;
   import org.apache.shiro.authc.AuthenticationInfo;
   import org.apache.shiro.authc.AuthenticationToken;
   import org.apache.shiro.authc.SimpleAuthenticationInfo;
   import org.apache.shiro.authz.AuthorizationInfo;
   import org.apache.shiro.authz.SimpleAuthorizationInfo;
   import org.apache.shiro.realm.AuthorizingRealm;
   import org.apache.shiro.subject.PrincipalCollection;
   import org.springframework.beans.factory.annotation.Autowired;
   
   public class MyRealm extends AuthorizingRealm {
   
       @Autowired
       private LoginService loginService;
       
       //授权
       @Override
       protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
           //获取用户登陆名
           String name = (String) principalCollection.getPrimaryPrincipal();
           //根据用户名去模拟查询数据
           User user = loginService.getUserByName(name);
           //添加角色权限
           SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
           for (Role role : user.getRoles()) {
               //添加角色
               simpleAuthorizationInfo.addRole(role.getName());
               //添加权限
               for (Permissions permission : role.getPermissions()) {
                  simpleAuthorizationInfo.addStringPermission(permission.getPermissionsName());
               }
           }
           return simpleAuthorizationInfo;
       }
   
       //认证
       @Override
       protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
           //发起post请求的时候先认证，然后再请求
           if (authenticationToken.getPrincipal() == null) {
               return null;
           }
           //获取用户信息
           String name = authenticationToken.getPrincipal().toString();
           User user = loginService.getUserByName(name);
           if (user == null) {
               return null;
           }else {
               SimpleAuthenticationInfo simpleAuthenticationInfo = new SimpleAuthenticationInfo(name, user.getPassword(), getName());
               return simpleAuthenticationInfo;
           }
       }
   }
   ```

2. 编写ShiroConfig文件

   ```java
   package com.wangqiang.config;
   import com.wangqiang.shiro.MyRealm;
   import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
   import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
   import org.springframework.beans.factory.annotation.Qualifier;
   import org.springframework.context.annotation.Bean;
   import org.springframework.context.annotation.Configuration;
   import java.util.LinkedHashMap;
   import java.util.Map;
   
   @Configuration
   public class ShiroConfig {
      //ShiroFilterFactoryBean
       @Bean
       public ShiroFilterFactoryBean getShiroFilterFactoryBean(@Qualifier(value = "SecurityManager") DefaultWebSecurityManager defaultWebSecurityManager){
           ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
           //设置SecurityManager
           shiroFilterFactoryBean.setSecurityManager(defaultWebSecurityManager);
           //登陆页
           shiroFilterFactoryBean.setLoginUrl("/login");
           //登陆成功页
           shiroFilterFactoryBean.setSuccessUrl("/index");
           //授权未通过页
           shiroFilterFactoryBean.setUnauthorizedUrl("/unauthc");
           //添加shiro内置过滤器
           /**
            * anno:无需认证即可访问
            * authc:必须认证了才能访问
            * user: 必须拥有记住我功能才能访问
            * perms:拥有对某个资源的权限才能访问
            * role:拥有某个角色权限才能访问
            *
            */
           Map<String, String> filterChainDefinitionMap = new LinkedHashMap<>();
           //对所有进入index页面的用户过滤
           filterChainDefinitionMap.put("/index", "authc");
           //对进入admin管理页面的用户过滤，角色为admin放行
           filterChainDefinitionMap.put("/admin", "roles[admin]");
           //对想要执行修改删除操作的用户权限进行过滤
           filterChainDefinitionMap.put("/admin/renewable", "perms[add,update]");
           filterChainDefinitionMap.put("/admin/removable", "perms[delete]");
           shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
           return shiroFilterFactoryBean;
       }
       
       //DefaultWebSecurityManager
       @Bean(name = "SecurityManager")
       public DefaultWebSecurityManager defaultWebSecurityManager(@Qualifier(value = "Realm") MyRealm myRealm){
           DefaultWebSecurityManager defaultWebSecurityManager = new DefaultWebSecurityManager();
           //关联Realm
           defaultWebSecurityManager.setRealm(myRealm);
           return defaultWebSecurityManager;
       }
   
       //创建realm对象，自定义
       @Bean(name = "Realm")
       public MyRealm myRealm(){
           return new MyRealm();
       }
   }
   ```

#### Controller层

1. 编写登陆LoginController

   ```java
   package com.wangqiang.controller;
   import com.wangqiang.pojo.User;
   import com.wangqiang.service.LoginService;
   import org.apache.shiro.SecurityUtils;
   import org.apache.shiro.authc.IncorrectCredentialsException;
   import org.apache.shiro.authc.UnknownAccountException;
   import org.apache.shiro.authc.UsernamePasswordToken;
   import org.apache.shiro.subject.Subject;
   import org.springframework.beans.factory.annotation.Autowired;
   import org.springframework.web.bind.annotation.GetMapping;
   import org.springframework.web.bind.annotation.RequestParam;
   import org.springframework.web.bind.annotation.RestController;
   
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
   ```

2. 编写权限信息AuthcController

   ```java
   package com.wangqiang.controller;
   import com.wangqiang.pojo.User;
   import org.apache.shiro.SecurityUtils;
   import org.apache.shiro.subject.Subject;
   import org.springframework.web.bind.annotation.GetMapping;
   import org.springframework.web.bind.annotation.RestController;
   
   @RestController
   public class AuthcController {
       //未授权页面
       @GetMapping("unauthc")
       public Object unauthc() {
           return "对不起，您的权限不够";
       }
   
       //查看用户权限信息
       @GetMapping("/user")
       public Object user() {
           Subject subject = SecurityUtils.getSubject();
           User user = (User) subject.getSession().getAttribute("user");
           return user.toString();
       }
   }
   ```

3. 编写系统操作AdminController

   ```java
   package com.wangqiang.controller;
   import org.springframework.web.bind.annotation.GetMapping;
   import org.springframework.web.bind.annotation.RestController;
   
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
   ```

#### 测试

- http://localhost:8080/index   未登录状态下访问任何路径都将跳到登陆页面
- http://localhost:8080/doLogin?username=wangqiang&password=123456 用户wangqiang拥有admin角色登陆成功
  - http://localhost:8080/user 用户权限信息 User(id=1, userName=wangqiang, password=123456, roles=[Role(id=1, Name=admin, permissions=[Permissions(id=2, permissionsName=add), Permissions(id=1, permissionsName=query), Permissions(id=2, permissionsName=update), Permissions(id=2, permissionsName=delete) ])])
  - http://localhost:8080/admin 拥有admin的角色
  - http://localhost:8080/admin/renewable 拥有增加或更新的操作
- http://localhost:8080/doLogin?username=zhangsan&password=123456 用户zhangsan拥有user角色登陆成功
  - http://localhost:8080/user 用户权限信息 User(id=2, userName=zhangsan, password=123456, roles=[Role(id=2, Name=user, permissions=[Permissions(id=1, permissionsName=query)])])
  - http://localhost:8080/admin 对不起，您的权限不够
  - http://localhost:8080/admin/removable 对不起，您的权限不够

#### 参考资料

[博客园](https://www.cnblogs.com/learnhow/p/9747134.html)

[简书](https://www.jianshu.com/p/7f724bec3dc3)



