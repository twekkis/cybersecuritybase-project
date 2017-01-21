package sec.project.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
    
    /***A6-Sensitive Data Exposure***/
    /*FIX: This application does not use HTTPS so it means that given
    credentials are transffered in plain text. HTTPS required to 
    encrypt the traffic. Certificate generation and installation is not
    described here.*/
    
    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        /***A8-Cross-Site Request Forgery (CSRF)***/
        /*FIX: Do not disable csrf tokens*/
        //http.csrf().disable();
            
        http
            .authorizeRequests()
                /***A4-Insecure Direct Object References***/
                /*FIX: use "/admin/**"*/
                .antMatchers("/admin/").authenticated()
                .and()
            .formLogin()
                .permitAll()
                .and()
            .logout()                                    
                .permitAll();

    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
