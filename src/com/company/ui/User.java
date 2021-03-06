package com.company.ui;

import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Objects;

/**
 * Class that is needed to store users
 */
public class User implements Serializable {

    private final String username;
    private final byte[] passwordMD5;

    /**
     * Username-only constructor
     * @param username username
     */
    public User(String username){
        this(username,"");
    }

    /**
     * Default constructor
     * @param username username
     * @param password password
     */
    public User(String username, String password) {
        this.username = username;
        byte[] finalPasswordMD5;
        try {
            finalPasswordMD5 = MessageDigest.getInstance("MD5").digest(password.getBytes());
        } catch (NoSuchAlgorithmException e) {
            finalPasswordMD5 = new byte[0];
            e.printStackTrace();
        }
        this.passwordMD5 = finalPasswordMD5;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return username.equals(user.username) && Arrays.equals(passwordMD5, user.passwordMD5);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(username);
        result = 31 * result + Arrays.hashCode(passwordMD5);
        return result;
    }

    /**
     * Username getter
     * @return usernamg
     */
    public String getUsername() {
        return username;
    }

    /**
     * Password getter
     * @return password in MD5 format
     */
    public byte[] getPasswordMD5() {
        return passwordMD5;
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", passwordMD5=" + Arrays.toString(passwordMD5) +
                '}';
    }
}
