package unidue.ub.elisaconnector.client;

import feign.auth.BasicAuthRequestInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

public class FeignConfiguration {

    @Value("${spring.security.user.name}")
    private String username;

    @Value("${spring.security.user.password}")
    private String password;

    private static Logger log = LoggerFactory.getLogger(FeignConfiguration.class);

    @Bean
    public BasicAuthRequestInterceptor basicAuthRequestInterceptor() {
        log.info("requesting backend service with user " + username);
        return new BasicAuthRequestInterceptor(username, password);
    }
}
