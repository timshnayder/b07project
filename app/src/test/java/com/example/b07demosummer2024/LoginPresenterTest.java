package com.example.b07demosummer2024;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

public class LoginPresenterTest {

    @Mock
    private LoginContract.View mockView;

    @Mock
    private LoginContract.Model mockModel;

    private LoginPresenter presenter;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        presenter = new LoginPresenter(mockView, mockModel);
    }

    @Test
    public void testLogin_NullEmail_ShowsEmailError() {
        presenter.login(null, "password123");
        verify(mockView).showEmailError("Email is required");
    }

    @Test
    public void testLogin_EmptyEmail_ShowsEmailError() {
        presenter.login("", "password123");
        verify(mockView).showEmailError("Email is required");
    }

    @Test
    public void testLogin_NullPassword_ShowsPasswordError() {
        presenter.login("test@example.com", null);
        verify(mockView).showPasswordError("Password is required");
    }

    @Test
    public void testLogin_EmptyPassword_ShowsPasswordError() {
        presenter.login("test@example.com", "   ");
        verify(mockView).showPasswordError("Password is required");
    }

    @Test
    public void testLogin_Success_HidesLoadingAndShowsSuccess() {
        presenter.login("test@example.com", "password123");
        verify(mockView).showLoading();

        ArgumentCaptor<LoginContract.LoginCallback> captor = ArgumentCaptor.forClass(LoginContract.LoginCallback.class);
        verify(mockModel).login(eq("test@example.com"), eq("password123"), captor.capture());

        captor.getValue().onSuccess();

        verify(mockView).hideLoading();
        verify(mockView).showLoginSuccess();
    }

    @Test
    public void testLogin_Failure_HidesLoadingAndShowsError() {
        presenter.login("test@example.com", "password123");
        verify(mockView).showLoading();

        ArgumentCaptor<LoginContract.LoginCallback> captor = ArgumentCaptor.forClass(LoginContract.LoginCallback.class);
        verify(mockModel).login(eq("test@example.com"), eq("password123"), captor.capture());

        captor.getValue().onFailure("Network Error");

        verify(mockView).hideLoading();
        verify(mockView).showLoginError("Network Error");
    }
}