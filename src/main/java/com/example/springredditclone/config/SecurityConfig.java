//1//
package com.example.springredditclone.config;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//base class, it provides all config security for class we can overRide and customize
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;


@EnableWebSecurity//Main annotation enable web security for our backend
@AllArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    //Load user data from different sources (DB))
    /*2*/private final UserDetailsService userDetailsService;
    /*3*///and back to AuthService to impl login
    @Bean(BeanIds.AUTHENTICATION_MANAGER)
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
    @Override
    //take http security object as an input
   /*1*/ public void configure(HttpSecurity httpSecurity) throws Exception {
        /*disable CSRF protection or for our backend bc CSRF attacks can mainly occur when there are sessions or
        cookies to authenticate the session information as REST API and as we can using JSON to authorization
        */
        httpSecurity.csrf().disable()
                .authorizeRequests()//allow all incoming api who endpoint api start /api/auth
                .antMatchers("/api/auth/**")//make sure any other request which does not match patten should be authenticate//
                .permitAll()
                .anyRequest()
                .authenticated();
    }
            /*2*
            the first thing we need to do is to update our security config, we have to create authentication manager
        */
    @Autowired
        public void configureGlobal(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
            //takes input of type user details service and create imp class UserDetailServiceImp
            authenticationManagerBuilder.userDetailsService(userDetailsService/*interface*/)
                    .passwordEncoder(passwordEncoder());
    }

    @Bean
    //Interface
    PasswordEncoder passwordEncoder(){
        return  new BCryptPasswordEncoder();
        //back to auth service
    }
}
