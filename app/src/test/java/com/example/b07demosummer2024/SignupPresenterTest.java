package com.example.b07demosummer2024;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

public class SignupPresenterTest {

    @Mock
    private SignupContract.View mockView;

    @Mock
    private SignupContract.Model mockModel;

    private SignupPresenter presenter;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        presenter = new SignupPresenter(mockView, mockModel);
    }

    @Test
    public void testSignup_NullEmail_ShowsEmailError() {
        presenter.signup(null, "user123", "password123");
        verify(mockView).showEmailError("Email is required");
    }

    @Test
    public void testSignup_EmptyEmail_ShowsEmailError() {
        presenter.signup("", "user123", "password123");
        verify(mockView).showEmailError("Email is required");
    }

    @Test
    public void testSignup_NullUsername_ShowsUsernameError() {
        presenter.signup("test@example.com", null, "password123");
        verify(mockView).showUsernameError("Username is required");
    }

    @Test
    public void testSignup_EmptyUsername_ShowsUsernameError() {
        presenter.signup("test@example.com", "   ", "password123");
        verify(mockView).showUsernameError("Username is required");
    }

    @Test
    public void testSignup_NullPassword_ShowsPasswordError() {
        presenter.signup("test@example.com", "user123", null);
        verify(mockView).showPasswordError("Password is required");
    }

    @Test
    public void testSignup_EmptyPassword_ShowsPasswordError() {
        presenter.signup("test@example.com", "user123", "");
        verify(mockView).showPasswordError("Password is required");
    }

    @Test
    public void testSignup_ShortPassword_ShowsPasswordError() {
        presenter.signup("test@example.com", "user123", "12345");
        verify(mockView).showPasswordError("Password must be at least 6 characters");
    }

    @Test
    public void testSignup_Success_HidesLoadingAndShowsSuccess() {
        presenter.signup("test@example.com", "user123", "password123");
        verify(mockView).showLoading();

        ArgumentCaptor<LoginContract.LoginCallback> captor = ArgumentCaptor.forClass(LoginContract.LoginCallback.class);
        verify(mockModel).signup(eq("test@example.com"), eq("user123"), eq("password123"), captor.capture());

        captor.getValue().onSuccess();

        verify(mockView).hideLoading();
        verify(mockView).showSignupSuccess();
    }

    @Test
    public void testSignup_Failure_HidesLoadingAndShowsError() {
        presenter.signup("test@example.com", "user123", "password123");
        verify(mockView).showLoading();

        ArgumentCaptor<LoginContract.LoginCallback> captor = ArgumentCaptor.forClass(LoginContract.LoginCallback.class);
        verify(mockModel).signup(eq("test@example.com"), eq("user123"), eq("password123"), captor.capture());

        captor.getValue().onFailure("Username already exists");

        verify(mockView).hideLoading();
        verify(mockView).showSignupError("Username already exists");
    }
}