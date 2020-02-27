package com.wangqiang.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @version : V1.0
 * @ClassName: Permissions
 * @Description: TODO
 * @Auther: wangqiang
 * @Date: 2020/2/26 20:04
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Permissions {
    private int id;
    private String permissionsName;
}
