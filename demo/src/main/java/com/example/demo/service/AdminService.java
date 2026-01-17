package com.example.demo.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.mapper.AdminMapper;
import com.example.demo.model.Admin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

@Service
public class AdminService extends ServiceImpl<AdminMapper, Admin> {

    @Autowired
    private AdminMapper adminMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * 登录逻辑：验证通过后，自动更新最后登录时间
     */
    public Admin login(String username, String rawPassword) {
        LambdaQueryWrapper<Admin> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Admin::getUsername, username);
        Admin admin = adminMapper.selectOne(wrapper);

        // 用户不存在
        if (admin == null) {
            return null;
        }

        // 密码校验
        if (passwordEncoder.matches(rawPassword, admin.getPassword())) {
            // [新增] 校验成功，更新最后登录时间
            admin.setLastLoginTime(new Date());
            this.updateById(admin);

            return admin;
        } else {
            return null;
        }
    }

    public boolean register(Admin admin) {
        // 查重
        LambdaQueryWrapper<Admin> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Admin::getUsername, admin.getUsername());
        if (adminMapper.selectCount(wrapper) > 0) {
            return false;
        }

        // 密码加密
        admin.setPassword(passwordEncoder.encode(admin.getPassword()));

        // 默认值
        admin.setRole("admin");
        if (!StringUtils.hasLength(admin.getNickname())) admin.setNickname(admin.getUsername());
        if (!StringUtils.hasLength(admin.getAvatarUrl())) admin.setAvatarUrl("/images/default-avatar.png");

        return this.save(admin);
    }

    // 更新信息
    public boolean updateAdmin(Admin admin) {
        if (StringUtils.hasLength(admin.getPassword())) {
            admin.setPassword(passwordEncoder.encode(admin.getPassword()));
        } else {
            admin.setPassword(null); // 不更新密码
        }
        return this.updateById(admin);
    }

    // 重置密码
    public boolean verifyAndResetPassword(String username, String phone, String newPassword) {
        LambdaQueryWrapper<Admin> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Admin::getUsername, username);
        wrapper.eq(Admin::getPhone, phone);
        Admin admin = adminMapper.selectOne(wrapper);

        if (admin == null) return false;

        admin.setPassword(passwordEncoder.encode(newPassword));
        return this.updateById(admin);
    }

    // 获取列表
    public List<Admin> getUserListByRole(String role) {
        LambdaQueryWrapper<Admin> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Admin::getRole, role);
        wrapper.orderByDesc(Admin::getId); // ID 自增，倒序即为最新
        List<Admin> list = adminMapper.selectList(wrapper);
        list.forEach(u -> u.setPassword(null));
        return list;
    }

    // 添加用户
    public boolean addUser(Admin admin) {
        LambdaQueryWrapper<Admin> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Admin::getUsername, admin.getUsername());
        if (adminMapper.selectCount(wrapper) > 0) return false;

        if (StringUtils.hasLength(admin.getPassword())) {
            admin.setPassword(passwordEncoder.encode(admin.getPassword()));
        }
        admin.setRole("user");
        if (!StringUtils.hasLength(admin.getAvatarUrl())) admin.setAvatarUrl("/images/default-avatar.png");
        if (!StringUtils.hasLength(admin.getNickname())) admin.setNickname("用户" + admin.getUsername());

        return this.save(admin);
    }

    public boolean deleteUser(Integer id) {
        return this.removeById(id);
    }
}