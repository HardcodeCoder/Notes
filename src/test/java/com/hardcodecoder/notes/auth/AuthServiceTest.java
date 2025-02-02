package com.hardcodecoder.notes.auth;

import com.hardcodecoder.notes.account.AccountService;
import com.hardcodecoder.notes.auth.model.SignupRequest;
import com.hardcodecoder.notes.auth.validator.EmailValidator;
import com.hardcodecoder.notes.auth.validator.PasswordValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.mockito.ArgumentMatchers.anyString;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AuthServiceTest {

    private final AccountService accountService = Mockito.mock();
    private final AuthService service = new AuthService(
        accountService,
        new BCryptPasswordEncoder(),
        new EmailValidator(),
        new PasswordValidator()
    );

    @Test
    @DisplayName("When signup request is valid should return success response")
    void shouldReturnSuccessForValidRequest() {
        Mockito
            .when(accountService.create(anyString(), anyString(), anyString()))
            .thenReturn(true);

        var request = new SignupRequest("Test", "test@email.com", "Val1dP@ssword");
        var response = service.processSignUpRequest(request);

        Assertions.assertNotNull(response);
        Assertions.assertTrue(response.success());
        Assertions.assertEquals("Request processed", response.message());
    }

    @Test
    @DisplayName("When Email is null should return failed response")
    void shouldReturnFailedWhenEmailIsNull() {
        var request = new SignupRequest("Test", null, "Val1dP@ssword");
        var response = service.processSignUpRequest(request);

        Assertions.assertNotNull(response);
        Assertions.assertFalse(response.success());
        Assertions.assertEquals("Invalid Request", response.message());
    }

    @Test
    @DisplayName("When Password is null should return failed response")
    void shouldReturnFailedWhenPasswordIsNull() {
        var request = new SignupRequest("Test", "test@email.com", null);
        var response = service.processSignUpRequest(request);

        Assertions.assertNotNull(response);
        Assertions.assertFalse(response.success());
        Assertions.assertEquals("Invalid Request", response.message());
    }
}