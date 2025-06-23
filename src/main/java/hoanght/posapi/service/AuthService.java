package hoanght.posapi.service;

import hoanght.posapi.dto.*;

public interface AuthService {
    /**
     * Đăng nhập người dùng.
     *
     * @param loginRequest DTO chứa thông tin đăng nhập của người dùng.
     * @return AuthResponse chứa thông tin xác thực nếu đăng nhập thành công.
     */
    AuthResponse login(LoginRequest loginRequest);

    /**
     * Đăng ký người dùng mới.
     *
     * @param registerRequest DTO chứa thông tin đăng ký người dùng.
     * @return AuthResponse chứa thông tin xác thực nếu đăng ký thành công.
     */
    AuthResponse register(RegisterRequest registerRequest);

    /**
     * Làm mới token của người dùng bằng refresh token.
     *
     * @param refreshTokenRequest DTO chứa refresh token cần làm mới.
     * @return AuthResponse chứa token mới nếu làm mới thành công.
     */
    AuthResponse refreshToken(RefreshTokenRequest refreshTokenRequest);

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
     * @param refreshTokenRequest Giá trị của refresh token cần xóa.
     */
    void logout(RefreshTokenRequest refreshTokenRequest);
}
