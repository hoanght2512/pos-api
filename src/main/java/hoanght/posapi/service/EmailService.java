package hoanght.posapi.service;

public interface EmailService {
    void sendEmailResetPassword(String email, String username, String token);
}
