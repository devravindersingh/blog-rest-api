package com.ravinder.project.blog.service;

import com.ravinder.project.blog.payload.LoginDto;
import com.ravinder.project.blog.payload.RegisterDto;

public interface AuthService {

    String login(LoginDto loginDto);
    String register(RegisterDto registerDto);
}
