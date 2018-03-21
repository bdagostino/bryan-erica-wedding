package net.ddns.buckeyeflash.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {


    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication().withUser("Bryan").password("{noop}secret").roles("ADMIN_EDIT")
                .and().withUser("Erica").password("{noop}secret").roles("ADMIN_READ");

    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        super.configure(web);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests().anyRequest().permitAll()
                .and().formLogin()
                .and().httpBasic()
                .and().logout().invalidateHttpSession(true).deleteCookies("JSESSIONID")
                .and().sessionManagement().maximumSessions(1).expiredUrl("/");

//        http.requestMatcher(EndpointRequest.toAnyEndpoint()).authorizeRequests()
//                .anyRequest().permitAll();
    }
}
