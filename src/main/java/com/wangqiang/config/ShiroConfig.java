package com.wangqiang.config;

import com.wangqiang.shiro.MyRealm;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @version : V1.0
 * @ClassName: ShiroConfig
 * @Description: TODO
 * @Auther: wangqiang
 * @Date: 2020/2/26 20:56
 */
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
