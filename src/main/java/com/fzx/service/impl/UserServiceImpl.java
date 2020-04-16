package com.fzx.service.impl;

import com.fzx.entity.User;
import com.fzx.mapper.UserMapper;
import com.fzx.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author zxfuc
 * @since 2020-01-22
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

}
