package hoanght.posapi.service;

import hoanght.posapi.dto.auth.AuthResponse;
import hoanght.posapi.dto.auth.LoginRequest;
import hoanght.posapi.dto.auth.RegisterRequest;
import hoanght.posapi.dto.auth.ResetPasswordRequest;

public interface AuthService {
    /**
     * Đăng nhập người dùng.
     *
     * @param request DTO chứa thông tin đăng nhập của người dùng.
     */
    AuthResponse login(LoginRequest request);

    /**
     * Đăng ký người dùng mới.
     *
     * @param request DTO chứa thông tin đăng ký người dùng.
     */
    AuthResponse register(RegisterRequest request);

    /**
     * Làm mới token của người dùng bằng refresh token.
     *
     * @param token Refresh token của người dùng.
     */
    AuthResponse refresh(String token);

    /**
     * Đăng xuất người dùng bằng cách xóa refresh token.
     *
     * @param token Refresh token của người dùng cần đăng xuất.
     */
    void logout(String token);

    /**
     * Gửi email đặt lại mật khẩu cho người dùng.
     *
     * @param email DTO chứa thông tin email của người dùng.
     */
    void forgotPassword(String email);

    /**
     * Xử lý việc đặt lại mật khẩu bằng cách sử dụng token đặt lại.
     *
     * @param request DTO chứa token và mật khẩu mới.
     */
    void resetPassword(ResetPasswordRequest request);
}
