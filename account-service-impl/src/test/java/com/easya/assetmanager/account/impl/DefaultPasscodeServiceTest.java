package com.easya.assetmanager.account.impl;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;

import java.nio.file.Path;


@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DefaultPasscodeServiceTest {
    
    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        // Set the working directory to the temp directory for testing
        System.setProperty("user.home", tempDir.toString());
    }

    @Test
    @Order(1)
    @DisplayName("Test setting passcode for the first time")
    void testSetPasscodeFirstTime() {
        DefaultPasscodeService service = new DefaultPasscodeService();
        assertTrue(service.setPasscode("123456"));
        assertTrue(service.isValidPasscode("123456"));
    }

    @Test
    @Order(2)
    @DisplayName("Test cannot set passcode twice")
    void testCannotSetPasscodeTwice() {
        DefaultPasscodeService service = new DefaultPasscodeService();
        assertTrue(service.setPasscode("123456"));
        assertFalse(service.setPasscode("654321"));
        assertTrue(service.isValidPasscode("123456"));
        assertFalse(service.isValidPasscode("654321"));
    }

    @Test
    @Order(3)
    @DisplayName("Test invalid passcode length")
    void testInvalidPasscodeLength() {
        DefaultPasscodeService service = new DefaultPasscodeService();
        assertFalse(service.setPasscode("12345"));
        assertFalse(service.isValidPasscode("12345"));
    }

    @Test
    @Order(4)
    @DisplayName("Test null passcode")
    void testNullPasscode() {
        DefaultPasscodeService service = new DefaultPasscodeService();
        assertFalse(service.setPasscode(null));
        assertFalse(service.isValidPasscode(null));
    }

    @Test
    @Order(5)
    @DisplayName("Test non-numeric passcode")
    void testNonNumericPasscode() {
        DefaultPasscodeService service = new DefaultPasscodeService();
        assertFalse(service.setPasscode("abc123"));
        assertFalse(service.isValidPasscode("abc123"));
        assertFalse(service.setPasscode("123abc"));
        assertFalse(service.isValidPasscode("123abc"));
        assertFalse(service.setPasscode("123!456"));
        assertFalse(service.isValidPasscode("123!456"));
    }

    @Test
    @DisplayName("Test passcode persistence")
    void testPasscodePersistence() {
        // Set initial passcode
        DefaultPasscodeService service = new DefaultPasscodeService();
        assertTrue(service.setPasscode("123456"));
        
        // Create new instance to test loading from disk
        DefaultPasscodeService newPasscodeService = new DefaultPasscodeService();
        
        // Verify passcode was loaded correctly
        assertTrue(newPasscodeService.isValidPasscode("123456"));
        assertFalse(newPasscodeService.isValidPasscode("654321"));

        // Test isPasscodeSet
        assertTrue(newPasscodeService.isPasscodeSet());
    }
} 

