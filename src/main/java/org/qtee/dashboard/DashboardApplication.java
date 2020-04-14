package org.qtee.dashboard;

import org.qtee.dashboard.controller.web.RegistrationForm;
import org.qtee.dashboard.data.UserRepository;
import org.qtee.dashboard.entity.User;
import org.qtee.dashboard.service.UserRepositoryUserDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class DashboardApplication {
	public static void main(String[] args) {
		SpringApplication.run(DashboardApplication.class, args);
	}
}
