package com.example.demo.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.mapper.UserMapper;
import com.example.demo.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;

@Service
public class UserService extends ServiceImpl<UserMapper, User> {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * 普通用户登录
     */
    public User login(String username, String rawPassword) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        User user = userMapper.selectOne(wrapper);

        if (user != null && passwordEncoder.matches(rawPassword, user.getPassword())) {
            // [新增] 更新最后登录时间
            user.setLastLoginTime(new Date());
            this.updateById(user);

            return user;
        }
        return null;
    }

    public String register(User user) {
        // 查用户名
        LambdaQueryWrapper<User> nameWrapper = new LambdaQueryWrapper<>();
        nameWrapper.eq(User::getUsername, user.getUsername());
        if (userMapper.selectCount(nameWrapper) > 0) return "注册失败：用户名已存在";

        // 查手机号
        if (StringUtils.hasLength(user.getPhone())) {
            LambdaQueryWrapper<User> phoneWrapper = new LambdaQueryWrapper<>();
            phoneWrapper.eq(User::getPhone, user.getPhone());
            if (userMapper.selectCount(phoneWrapper) > 0) return "注册失败：手机号已被绑定";
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole("user");
        if (!StringUtils.hasLength(user.getNickname())) user.setNickname(user.getUsername());
        if (!StringUtils.hasLength(user.getAvatarUrl())) user.setAvatarUrl("/images/default-user-avatar.png");
        if (user.getGender() == null) user.setGender(0);

        boolean saveSuccess = this.save(user);
        return saveSuccess ? null : "注册失败：系统保存异常";
    }

    public boolean updateUser(User user) {
        if (StringUtils.hasLength(user.getPassword())) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        } else {
            user.setPassword(null);
        }
        return this.updateById(user);
    }

    public boolean verifyAndResetPassword(String username, String phone, String newPassword) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        wrapper.eq(User::getPhone, phone);
        User user = userMapper.selectOne(wrapper);

        if (user == null) return false;

        user.setPassword(passwordEncoder.encode(newPassword));
        return this.updateById(user);
    }
}