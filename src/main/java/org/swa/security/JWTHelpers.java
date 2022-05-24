package org.swa.security;

import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class JWTHelpers {

    private static JWTHelpers instance = null;
    private SecretKey jwtKey = null;

    private JWTHelpers() {
        KeyGenerator keyGenerator;
        try {
            keyGenerator = KeyGenerator.getInstance("HmacSha256");
            jwtKey = keyGenerator.generateKey();
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(AutenticazioneResource.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public SecretKey getJwtKey() {
        return jwtKey;
    }

    public static JWTHelpers getInstance() {
        if (instance == null) {
            instance = new JWTHelpers();
        }
        return instance;
    }
}
