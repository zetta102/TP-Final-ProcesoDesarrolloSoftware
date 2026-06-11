package com.pds.tp.domain.valueobject;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ValueObjectsTest {

    @Test
    void shouldNormalizeAndAliasEmailVerificationStatus() {
        assertSame(EmailVerificationStatus.VERIFIED, EmailVerificationStatus.of("verified"));
        assertSame(EmailVerificationStatus.PENDING, EmailVerificationStatus.of("PENDIENTE"));
        assertTrue(EmailVerificationStatus.PENDING.isUnverified());
    }

    @Test
    void shouldNormalizeAndAliasUserRole() {
        assertSame(UserRole.MODERATOR, UserRole.of("mod"));
        assertSame(UserRole.ADMIN, UserRole.of("ADMIN"));
        assertTrue(UserRole.MODERATOR.isModerator());
    }

    @Test
    void shouldNormalizeAndAliasWaitlistStatus() {
        assertSame(WaitlistStatus.PENDING, WaitlistStatus.of("pendiente"));
        assertSame(WaitlistStatus.PROMOTED, WaitlistStatus.of("promoted"));
        assertTrue(WaitlistStatus.PENDING.isPending());
        assertTrue(WaitlistStatus.PROMOTED.isPromoted());
    }

    @Test
    void shouldRejectUnsupportedValues() {
        assertThrows(IllegalArgumentException.class, () -> EmailVerificationStatus.of("x"));
        assertThrows(IllegalArgumentException.class, () -> UserRole.of("guest"));
        assertThrows(IllegalArgumentException.class, () -> WaitlistStatus.of("stale"));
    }
}


