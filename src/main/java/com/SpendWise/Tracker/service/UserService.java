package com.SpendWise.Tracker.service;


import com.SpendWise.Tracker.dto.RegisterRequest;
import com.SpendWise.Tracker.entity.User;

public interface UserService {

    User registerUser(RegisterRequest request);
}

