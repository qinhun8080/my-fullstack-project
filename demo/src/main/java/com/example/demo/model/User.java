package com.example.demo.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.util.Date;

@TableName("users")
@Data
public class User {

    // [关键] 设置 ID 为数据库自增策略
    @TableId(type = IdType.AUTO)
    private Integer id;

    private String username;

    @TableField("role")
    private String role;

    private String nickname;

    @TableField("password_hash")
    private String password;

    private Integer gender;

    @TableField("phone_number")
    private String phone;

    private String birthday;

    @TableField("avatar_url")
    private String avatarUrl;

    // [新增] 最后登录时间
    @TableField("last_login_time")
    private Date lastLoginTime;
}