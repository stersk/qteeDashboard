package ua.com.tracktor.controller.web;

import lombok.Data;
import org.springframework.security.crypto.password.PasswordEncoder;
import ua.com.tracktor.entity.Account;
import ua.com.tracktor.entity.User;

@Data
public class RegistrationForm {
  private String username;
  private String password;
  private String fullName;
  private String phone;
  private Account account;
  
  public User toUser(PasswordEncoder passwordEncoder) {
    Boolean enabled = true;
    Integer role = 0; // 0 - user; 1 - administrator

    return new User(username, passwordEncoder.encode(password), role, enabled, fullName, phone, account);
  }
}
