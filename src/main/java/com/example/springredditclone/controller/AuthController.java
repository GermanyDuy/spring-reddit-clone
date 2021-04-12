//1//
package com.example.springredditclone.controller;

import com.example.springredditclone.dto.AuthenticationResponse;
import com.example.springredditclone.dto.LoginRequest;
import com.example.springredditclone.exceptions.SpringRedditException;
import com.example.springredditclone.service.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {

    /*2*/private final AuthService authService;
//    /*1*/@PostMapping("/signup")
//    /*through this class we'll will be transferring user detail as a part of RequestBody
//    We call this kind of class as a DTO, create this class and put it inside a package called DTO
//    */
//    public ResponseEntity<String> signup (@RequestBody RegisterRequest registerRequest) throws SpringRedditException {
//        /*2
//        if registration is successful we send message to the client by just retuning a response entity
//        which take a string as a argument
//        */
//        authService.signup(registerRequest);
//        return new ResponseEntity<>("User Registration Successful",
//                HttpStatus.OK);
//    }
    /*3*/
    /*Handling account verification
        After we signup a user and get a email through mailTrap which get link localhost8080 to verify account.
        but we not endpoint to run this link, this is the reason this function created
     */

    @GetMapping("accountVerification/{token}")
    //Create method response entity of type string with name as verify account and passing parameter inside url
    public ResponseEntity<String> verifyAccount(@PathVariable String token) throws SpringRedditException {
        //call verify account and pass token into method
        authService.verifyAccount(token);
    //create verifyAccount
        /*4*/return new ResponseEntity<>("Account Activated Successfully",HttpStatus.OK);
    }
    /*In next chap we discuss abt JWT and using file SecurityConfig*/
    /*5*/
    /*This is the similar DTO we used registered called register request and create LoginRequest */
    @PostMapping("login")
    public AuthenticationResponse login(@RequestBody LoginRequest loginRequest) throws SpringRedditException {
       return authService.login(loginRequest);
        //create method login inside authService
        /*6*///Test api post /login username: password: and next chap we discuss how to validate json web token and create api to
        //read and read subreddits and create JWTAuthenticationFilter
    }

}
