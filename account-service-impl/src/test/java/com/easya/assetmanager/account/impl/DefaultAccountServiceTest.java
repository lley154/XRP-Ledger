package com.easya.assetmanager.account.impl;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import com.easya.assetmanager.account.spi.PasscodeService;
import com.easya.assetmanager.common.auth.AuthorizationService;

class DefaultAccountServiceTest {

    @TempDir
    Path tempDir;

    private DefaultAccountService accountService;
    private PasscodeService passcodeService;
    private AuthorizationService authorizationService;

    @BeforeEach
    void setUp() {
        System.setProperty("user.home", tempDir.toString());
        passcodeService = new DefaultPasscodeService();
        authorizationService = new DefaultAuthorizationService();
        accountService = DefaultAccountService.create(passcodeService, authorizationService);
    }

    @Test
    @DisplayName("Test createDefault factory method")
    void testCreateDefault() {
        DefaultAccountService defaultService = DefaultAccountService.createDefault();
        assertNotNull(defaultService);
        assertNotNull(defaultService.getPasscodeService());
        assertNotNull(defaultService.getAuthorizationService());
    }

    @Test
    @DisplayName("Test create factory method with custom dependencies")
    void testCreateWithCustomDependencies() {
        DefaultAccountService customService = DefaultAccountService.create(passcodeService, authorizationService);
        assertNotNull(customService);
        assertEquals(passcodeService, customService.getPasscodeService());
        assertEquals(authorizationService, customService.getAuthorizationService());
    }

    @Test
    @DisplayName("Test getPasscodeService returns correct instance")
    void testGetPasscodeService() {
        PasscodeService service = accountService.getPasscodeService();
        assertNotNull(service);
        assertEquals(passcodeService, service);
    }

    @Test
    @DisplayName("Test getAuthorizationService returns correct instance")
    void testGetAuthorizationService() {
        AuthorizationService service = accountService.getAuthorizationService();
        assertNotNull(service);
        assertEquals(authorizationService, service);
    }
}
