package io.mycompany.user.api;

public class UserNotFoundException extends RuntimeException{
    private final String userId;

    public UserNotFoundException(String id) {
        this.userId = id;
    }

    public String getUserId() {
        return userId;
    }
}
