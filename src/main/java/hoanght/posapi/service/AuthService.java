package hoanght.posapi.service;

import hoanght.posapi.dto.request.ForgotPasswordRequest;
import hoanght.posapi.dto.request.LoginRequest;
import hoanght.posapi.dto.request.RegisterRequest;
import hoanght.posapi.dto.request.ResetPasswordRequest;
import hoanght.posapi.dto.response.AuthResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {
    /**
     * Đăng nhập người dùng.
     *
     * @param loginRequest DTO chứa thông tin đăng nhập của người dùng.
     * @return AuthResponse chứa thông tin xác thực nếu đăng nhập thành công.
     */
    AuthResponse login(LoginRequest loginRequest, HttpServletResponse response);

    /**
     * Đăng ký người dùng mới.
     *
     * @param registerRequest DTO chứa thông tin đăng ký người dùng.
     */
    void register(RegisterRequest registerRequest);

    /**
     * Làm mới token của người dùng bằng refresh token.
     *
     * @param request HttpServletRequest chứa thông tin yêu cầu.
     * @param response HttpServletResponse để gửi phản hồi.
     * @return AuthResponse chứa token mới nếu làm mới thành công.
     */
    AuthResponse refreshToken(HttpServletRequest request, HttpServletResponse response);

    /**
     * Gửi email đặt lại mật khẩu cho người dùng.
     *
     * @param forgotPasswordRequest DTO chứa thông tin email của người dùng.
     */
    void forgotPassword(ForgotPasswordRequest forgotPasswordRequest);

    /**
     * Xử lý việc đặt lại mật khẩu bằng cách sử dụng token đặt lại.
     *
     * @param resetPasswordRequest DTO chứa token và mật khẩu mới.
     */
    void resetPassword(ResetPasswordRequest resetPasswordRequest);

    /**
     * Đăng xuất người dùng bằng cách xóa refresh token.
     *
     * @param request HttpServletRequest chứa thông tin yêu cầu.
     * @param response HttpServletResponse để gửi phản hồi.
     */
    void logout(HttpServletRequest request, HttpServletResponse response);
}
