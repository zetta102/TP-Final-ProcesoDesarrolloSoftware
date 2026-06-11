package com.pds.tp.domain.valueobject;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
    void shouldExposeScrimApplicationStatePredicates() {
        assertTrue(ScrimApplicationStatus.PENDING.isPending());
        assertTrue(ScrimApplicationStatus.ACCEPTED.isAccepted());
        assertTrue(ScrimApplicationStatus.REJECTED.isRejected());
        assertEquals("ACCEPTED", ScrimApplicationStatus.of("accepted").value());
    }

    @Test
    void shouldNormalizeNotificationTypesAndAliases() {
        assertSame(NotificationChannel.DISCORD, NotificationChannel.of("discord"));
        assertTrue(NotificationChannel.PUSH.isPush());

        assertSame(NotificationStatus.FAILED, NotificationStatus.of("error"));
        assertTrue(NotificationStatus.SENT.isSent());
    }

    @Test
    void shouldRejectUnsupportedValues() {
        assertThrows(IllegalArgumentException.class, () -> EmailVerificationStatus.of("x"));
        assertThrows(IllegalArgumentException.class, () -> UserRole.of("guest"));
        assertThrows(IllegalArgumentException.class, () -> WaitlistStatus.of("stale"));
        assertThrows(IllegalArgumentException.class, () -> ScrimApplicationStatus.of("on_hold"));
        assertThrows(IllegalArgumentException.class, () -> NotificationChannel.of("sms"));
        assertThrows(IllegalArgumentException.class, () -> NotificationStatus.of("queued"));
    }
}


