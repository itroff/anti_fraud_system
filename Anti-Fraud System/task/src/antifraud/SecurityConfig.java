package antifraud;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    /* @Override
     protected void configure(AuthenticationManagerBuilder builder) throws Exception {
         //auth
     }
 */
    @Autowired
    RestAuthenticationEntryPoint restAuthenticationEntryPoint;

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.httpBasic()
                .authenticationEntryPoint(restAuthenticationEntryPoint) // Handles auth error
                .and()
                .csrf().disable().headers().frameOptions().disable() // for Postman, the H2 console
                .and()
                .authorizeRequests() // manage access
                .mvcMatchers(HttpMethod.DELETE, "/api/auth/user/*").hasRole("ADMINISTRATOR")
                .mvcMatchers("/api/auth/access").hasRole("ADMINISTRATOR")
                .mvcMatchers("/api/auth/role").hasRole("ADMINISTRATOR")
                .mvcMatchers("/api/auth/list").hasAnyRole("ADMINISTRATOR", "SUPPORT")
                .mvcMatchers("/api/antifraud/suspicious-ip").hasRole("SUPPORT")
                .mvcMatchers("/api/antifraud/suspicious-ip/*").hasRole("SUPPORT")
                .mvcMatchers("/api/antifraud/stolencard").hasRole("SUPPORT")
                .mvcMatchers("/api/antifraud/stolencard/*").hasRole("SUPPORT")
                .mvcMatchers("/api/antifraud/history").hasRole("SUPPORT")
                .mvcMatchers("/api/antifraud/history/*").hasRole("SUPPORT")
                .mvcMatchers(HttpMethod.PUT, "/api/antifraud/transaction").hasRole("SUPPORT")
                .mvcMatchers("/api/antifraud/transaction").hasRole("MERCHANT")
                .antMatchers(HttpMethod.POST, "/api/auth/user").permitAll()
                .antMatchers("/actuator/shutdown").permitAll() // needs to run test
                // other matchers
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS); // no session
    }

    @Bean
    public PasswordEncoder getEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    RestAuthenticationEntryPoint getRestAuthenticationEntryPoint() {
        return new RestAuthenticationEntryPoint();
    }
}
