package com.ravinder.project.blog.payload;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterDto {
    @NotEmpty(message = "Name should not be null or empty")
    private String name;
    @NotEmpty(message = "username should not be null or empty")
    private String username;
    @NotEmpty(message = "Email should not be null or empty")
    @Email
    private String email;
    private String password;
}
