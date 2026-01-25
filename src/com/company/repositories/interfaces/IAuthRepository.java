package com.company.repositories.interfaces;

public interface IAuthRepository {
    boolean register(String username, String passwordHash);
    Integer login(String username, String passwordHash);
    boolean usernameExists(String username);
}
