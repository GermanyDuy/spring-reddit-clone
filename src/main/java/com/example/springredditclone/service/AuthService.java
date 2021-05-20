//1//
package com.example.springredditclone.service;

/* This class contain the main business logic to register user that mean
    creating the user object and saving it to the database and sending
    out activation email

    We are mapping the RegisterRequest object to the User object and when setting the password,
    we are calling the encodePassword() method. This method is using the BCryptPasswordEncoder to encode our password.
    After that, we save the user into the database. Note that we are setting the enabled flag as false,
    as we want to disable the user after registration, and we only enable the user after verifying the user’s email address.
*/

import com.example.springredditclone.dto.AuthenticationResponse;
import com.example.springredditclone.dto.LoginRequest;
import com.example.springredditclone.dto.RegisterRequest;
import com.example.springredditclone.exceptions.SpringRedditException;
import com.example.springredditclone.model.NotificationEmail;
import com.example.springredditclone.model.User;
import com.example.springredditclone.model.VerificationToken;
import com.example.springredditclone.repository.UserRepository;
import com.example.springredditclone.repository.VerificationTokenRepository;
import com.example.springredditclone.security.JwtProvider;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;
import java.util.UUID;

@Service
/*4*/
@AllArgsConstructor
@Transactional

public class AuthService {
    /*Using autowired not rcm bc we are using field injection here
    and spring rcm we use constructor whenever possible
    remove autowire and declare field as final
//    */
//        @Autowired
//        //after that we hash password using bcrypt, go back security config and create bean password endCoder
//        /*2*/private PasswordEncoder passwordEncoder;//change registerRequest.getPassword() => passwordEncoder.encode(registerRequest.getPassword())
//
//    /*3*/// let save the our user to database, first, autowire the user repository
//        @Autowired
//        private UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    /*6*/
    private final MailService mailService;
    /*8 the authService contains the logic to create username and password auth token
        and use authentication manager to perform login, to do that, we autowire auth manager
        and come to SecurityConfig
    */
    private final AuthenticationManager authenticationManager;
    /*9*/
    private final JwtProvider jwtProvider;

    /*4*/ //create constructor and var for field
    @Transactional//to interacting with the relational database
    //Create method called signup inside this class which take RegisterRequest as input
    /*1*/ public void signup(RegisterRequest registerRequest) throws SpringRedditException {
        //the first thing we create an object for the user class
        User user = new User();
        //and we will map the data we have from the register request object to the user object
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
             /*pass in value as instant.now => this is java class took at current time
              and lastly we will disable and set it false
            */
        user.setEnabled(false);
        //save
        userRepository.save(user);
        /*5*/ //create verified user via email using UUID to generate
        String token = generateVerificationToken(user);
        //after that, create mailTemp.html
        /*6*/ //right after generate verification token method

        mailService.sendMail(new NotificationEmail("Please Active Your Account", user.getEmail(), "Thank you for signing up to Spring Reddit," +
                " please click on the below url to activate your account : "
                + "http://localhost:8080/api/auth/accountVerification" + "/" + token));
    }//we take a token whenever user clicks on url, and look it up DB, fetch the user who created token and enable user

    //remaining part here is call this signup from AuthController
    private String generateVerificationToken(User user) {
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUser(user);// after that we save it to repository
        verificationTokenRepository.save(verificationToken);
        return token;
    }
    @Transactional
    public User getCurrentUser() {
        org.springframework.security.core.userdetails.User principal = (org.springframework.security.core.userdetails.User) SecurityContextHolder.
                getContext().getAuthentication().getPrincipal();
        return userRepository.findByUsername(principal.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User name not found - " + principal.getUsername()));
    }
    /*7*/
    public void verifyAccount(String token) throws SpringRedditException {
        Optional<VerificationToken> verificationToken = verificationTokenRepository.findByToken(token);
        //in case the entity dose not exist, call orElseThrow and throw custom exception with message
        verificationToken.orElseThrow(() -> new SpringRedditException(("Invalid Token")));
        //Create method to associate this token and enable that user
        fetchUserAndEnable(verificationToken.get());//takes verificationToken as input
    }

    @Transactional
    void fetchUserAndEnable(VerificationToken verificationToken) throws SpringRedditException {
        //get username by typing verification token
        String username = verificationToken.getUser().getUsername();
        //and call orElseThrow as supplier to throws out custom springRedEx
        User user = userRepository.findByUsername(username).orElseThrow(() -> new SpringRedditException("User not found with name - " + username));
        //and store this result inside var user and enable this user by typing user.set
        user.setEnabled(true);
        userRepository.save(user);
        /*lastly sava user to database and mark the method as transaction
            and go back to auth controller and return response entity back to the client
            by typing written new response entity
            */
    }

    /*8*/
    public AuthenticationResponse login(LoginRequest loginRequest) throws SpringRedditException {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(),
                loginRequest.getPassword()));
        //and create class with named JWT provider in package security
        /*9*/
        SecurityContextHolder.getContext().setAuthentication(authentication);
        /*and if you want check the user login or not, we can just look up the security context
        and inject JWT provider inside class and back inside method, we call generate token
        */
        //now we can send this token back to user,use DTO called AuthenticationResponse
        String token = jwtProvider.generateToken(authentication);
        return new AuthenticationResponse(token,loginRequest.getUsername());//and back to AuthController
    }
}
