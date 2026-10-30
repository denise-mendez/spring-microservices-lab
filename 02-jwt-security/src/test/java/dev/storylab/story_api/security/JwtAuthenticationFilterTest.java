package dev.storylab.story_api.security;

import jakarta.servlet.ServletException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class JwtAuthenticationFilterTest {
    private JwtService jwtService;
    private UserDetailsService userDetailsService;
    private JwtAuthenticationFilter filter;

    @BeforeEach
    void setUp() {
        jwtService = mock(JwtService.class);

        userDetailsService =
                mock(UserDetailsService.class);

        filter = new JwtAuthenticationFilter(
                jwtService,
                userDetailsService
        );
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void authenticateUserWithValidToken()
            throws ServletException, IOException {

        String token = "valid-token";
        String username = "Karine88";

        UserDetails userDetails =
                User.withUsername(username)
                        .password("encoded-password")
                        .roles("AUTHOR")
                        .build();

        when(jwtService.extractUsername(token))
                .thenReturn(username);

        when(
                userDetailsService.loadUserByUsername(
                        username
                )
        ).thenReturn(userDetails);

        when(
                jwtService.isTokenValid(
                        token,
                        userDetails
                )
        ).thenReturn(true);

        MockHttpServletRequest request =
                new MockHttpServletRequest();

        request.addHeader(
                "Authorization",
                "Bearer " + token
        );

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        MockFilterChain filterChain =
                new MockFilterChain();

        filter.doFilter(
                request,
                response,
                filterChain
        );

        var authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        assertNotNull(authentication);
        assertTrue(authentication.isAuthenticated());

        assertEquals(
                username,
                authentication.getName()
        );

        assertTrue(
                authentication.getAuthorities()
                        .stream()
                        .anyMatch(authority ->
                                authority.getAuthority()
                                        .equals("ROLE_AUTHOR")
                        )
        );

        verify(jwtService)
                .extractUsername(token);

        verify(userDetailsService)
                .loadUserByUsername(username);

        verify(jwtService)
                .isTokenValid(token, userDetails);
    }

    @Test
    void continueWithoutAuthenticationWhenHeaderIsMissing()
            throws ServletException, IOException {

        MockHttpServletRequest request =
                new MockHttpServletRequest();

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        MockFilterChain filterChain =
                new MockFilterChain();

        filter.doFilter(
                request,
                response,
                filterChain
        );

        assertNull(
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
        );

        verifyNoInteractions(jwtService);
        verifyNoInteractions(userDetailsService);
    }

    @Test
    void continueWithoutAuthenticationWhenHeaderIsNotBearer()
            throws ServletException, IOException {

        MockHttpServletRequest request =
                new MockHttpServletRequest();

        request.addHeader(
                "Authorization",
                "Basic credentials"
        );

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        MockFilterChain filterChain =
                new MockFilterChain();

        filter.doFilter(
                request,
                response,
                filterChain
        );

        assertNull(
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
        );

        verifyNoInteractions(jwtService);
        verifyNoInteractions(userDetailsService);
    }

    @Test
    void notAuthenticateWhenTokenIsInvalid()
            throws ServletException, IOException {

        String token = "invalid-token";
        String username = "Karine88";

        UserDetails userDetails =
                User.withUsername(username)
                        .password("encoded-password")
                        .roles("AUTHOR")
                        .build();

        when(jwtService.extractUsername(token))
                .thenReturn(username);

        when(
                userDetailsService.loadUserByUsername(
                        username
                )
        ).thenReturn(userDetails);

        when(
                jwtService.isTokenValid(
                        token,
                        userDetails
                )
        ).thenReturn(false);

        MockHttpServletRequest request =
                new MockHttpServletRequest();

        request.addHeader(
                "Authorization",
                "Bearer " + token
        );

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        MockFilterChain filterChain =
                new MockFilterChain();

        filter.doFilter(
                request,
                response,
                filterChain
        );

        assertNull(
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
        );

        verify(jwtService)
                .isTokenValid(token, userDetails);
    }

    @Test
    void clearAuthenticationWhenTokenParsingFails()
            throws ServletException, IOException {

        String token = "malformed-token";

        when(jwtService.extractUsername(token))
                .thenThrow(
                        new IllegalArgumentException(
                                "Invalid token"
                        )
                );

        MockHttpServletRequest request =
                new MockHttpServletRequest();

        request.addHeader(
                "Authorization",
                "Bearer " + token
        );

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        MockFilterChain filterChain =
                new MockFilterChain();

        filter.doFilter(
                request,
                response,
                filterChain
        );

        assertNull(
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
        );

        verifyNoInteractions(userDetailsService);
    }
}