package org.qtee.dashboard.controller.web;

import lombok.Data;
import org.qtee.dashboard.entity.User;
import org.springframework.security.crypto.password.PasswordEncoder;

@Data
public class RegistrationForm {
  private String username;
  private String password;
  private String fullname;
  private String phone;
  
  public User toUser(PasswordEncoder passwordEncoder) {
    Boolean enabled = true;
    Integer role = 0; // 0 - user; 1 - administrator

    return new User(username, passwordEncoder.encode(password), fullname, phone, role, enabled);
  }
}
