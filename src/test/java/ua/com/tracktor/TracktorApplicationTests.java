package ua.com.tracktor;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.test.context.ContextConfiguration;
import ua.com.tracktor.configuration.WebConfig;

@SpringBootTest
@ContextConfiguration(classes = WebConfig.class,
		initializers = TracktorApplicationTests.class)
public class TracktorApplicationTests implements ApplicationContextInitializer<ConfigurableApplicationContext>{
	@Autowired
	ApplicationContext context;

	@Test
	void contextLoads() {

	}

	@Override
	public void initialize(ConfigurableApplicationContext applicationContext) {
		MockEnvironment environment = new MockEnvironment();
		environment.setProperty("server.ssl-redirection-port", 	"8080");
		environment.setProperty("server.port", 					"8443");
		environment.setProperty("server.ssl-redirection",		"false");

		applicationContext.setEnvironment(environment);
	}
}
