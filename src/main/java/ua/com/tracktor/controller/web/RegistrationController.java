package ua.com.tracktor.controller.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ua.com.tracktor.data.UserRepository;
import ua.com.tracktor.entity.Account;
import ua.com.tracktor.entity.User;
import ua.com.tracktor.service.AccountService;

@Controller
@RequestMapping("/register")
public class RegistrationController {
  
  private final UserRepository userRepo;
  private final PasswordEncoder passwordEncoder;

  @Autowired
  private AccountService accountService;

  public RegistrationController(
      UserRepository userRepo, PasswordEncoder passwordEncoder) {
    this.userRepo = userRepo;
    this.passwordEncoder = passwordEncoder;
  }
  
  @GetMapping
  public String registerForm() {
    return "registration";
  }
  
  @PostMapping
  public String processRegistration(RegistrationForm form) {
    Account account = null;
    String accountName = form.getAccount().trim();

    if (!accountName.isEmpty()) {
      account = accountService.getAccountByName(accountName);
      if (account == null) {
        account = new Account();
        account.setName(accountName);
        account.setPrice((long) (form.getPrice() * 100)); // price in coins

        account = accountService.save(account);
      }
    }

    User newUser = form.toUser(passwordEncoder, account);

    userRepo.save(newUser);
    return "redirect:/login";
  }

}
