//1//
package com.example.springredditclone.security;

import com.example.springredditclone.exceptions.SpringRedditException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.CertificateException;

import static io.jsonwebtoken.Jwts.parser;


@Service
public class JwtProvider {
    private KeyStore keyStore;

    @PostConstruct
    public void init() throws SpringRedditException {
        try {
            //providing keyStore instance of type check
            keyStore = KeyStore.getInstance("JKS");
            //getting inputStream from keyStore file
            //once we load inputStream from key store, we have to provide inputStream to load method of keystore follow the password of keystore
            InputStream resourceAsStream = getClass().getResourceAsStream("/springblog.jks");
            keyStore.load(resourceAsStream, "secret".toCharArray());
        } catch (KeyStoreException | CertificateException | NoSuchAlgorithmException | IOException e) {
            throw new SpringRedditException("Exception occurred while loading keystore");
        }
    }

    /*1*///create method public string generate token
    public String generateToken(Authentication authentication) throws SpringRedditException {
        //cast it to user obj and store it in variable called Principal
        User principal = (User) authentication.getPrincipal();
        //using jwt to construct, set username as subject
        return Jwts.builder()
                .setSubject(principal.getUsername())
                .signWith(getPrivateKey())
                .compact();
    }

    private PrivateKey getPrivateKey() throws SpringRedditException {
        try {
            return (PrivateKey) keyStore.getKey("springblog", "secret".toCharArray());
        } catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException e) {
            throw new SpringRedditException("Exception occured while retrieving public key from keystore");
        }
        //now let call generate token method of our JWT,before that we have to store the authentication object inside security context
        //back to authService
    }

    /*2*/
    public boolean validateToken(String jwt) throws SpringRedditException {
        parser().setSigningKey(getPublicKey());
        return true;//and inject jwt provider
    }

    private PublicKey getPublicKey() throws SpringRedditException {
        try {
            return keyStore.getCertificate("springblog").getPublicKey();
        } catch (KeyStoreException e) {
            throw new SpringRedditException("Exception occured while retrieving public key from keystore");
        }
    }
    //but here we are going to call certificate method of the keystore so we get the certificate
    /*3*/
    public String getUsernameFromJwt (String token) throws SpringRedditException {
        Claims claims = parser()
                .setSigningKey(getPublicKey())
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();//and back inside auth filter and get username from jwt method
    }
}


