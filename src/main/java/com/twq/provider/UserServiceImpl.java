package com.twq.provider;

import com.twq.publicInterface.UserService;
import com.twq.rpcFrame.entity.User;

/**
 * @Author: tangwq
 */
public class UserServiceImpl implements UserService {
    @Override
    public User getUser() {
        return User.builder().age(11).name("唐三章").build();
    }
}
