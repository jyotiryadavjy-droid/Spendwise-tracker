package com.SpendWise.Tracker.mapper;

import com.SpendWise.Tracker.dto.RegisterRequest;
import com.SpendWise.Tracker.entity.User;

public class UserMapper {

    public User toEntity(RegisterRequest request) {
        User user = new User();

        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setContactNumber(request.getContactNumber());

        return user;
    }
}
