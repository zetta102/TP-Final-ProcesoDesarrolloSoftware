package com.pds.tp.domain.entity;

import com.pds.tp.domain.valueobject.EmailVerificationStatus;
import com.pds.tp.domain.valueobject.UserRole;
import com.pds.tp.infrastructure.persistence.converter.EmailVerificationStatusConverter;
import com.pds.tp.infrastructure.persistence.converter.UserRoleConverter;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
public class Player {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String username;
    private String email;
    private String password;
    @Setter
    @Convert(converter = EmailVerificationStatusConverter.class)
    private EmailVerificationStatus emailVerificationStatus;
    @Setter
    @Convert(converter = UserRoleConverter.class)
    private UserRole role;
    private String preferredRole;
    private String region;
    private String platform;
    private String availability;
    @Setter
    private String visibleRank;
    @Setter
    private int averagePingMs;
    @Setter
    private int rank;
    @Setter
    private int gamesPlayed;
    @Setter
    private int wins;
    @Setter
    private int losses;
    @Setter
    private double kda;
    @Setter
    private String enabledNotificationChannels;
    @Setter
    private int strikes;
    @Setter
    private boolean banned;
    @Setter
    private String verificationToken;

    public Player(String username, String email, String password, String preferredRole, String region, String platform, String availability) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.emailVerificationStatus = EmailVerificationStatus.PENDING;
        this.role = UserRole.USER;
        this.preferredRole = preferredRole;
        this.region = region;
        this.platform = platform;
        this.availability = availability;
        this.visibleRank = "BRONCE";
        this.averagePingMs = 60;
        this.rank = 0;
        this.gamesPlayed = 0;
        this.wins = 0;
        this.losses = 0;
        this.kda = 0.0;
        this.enabledNotificationChannels = "PUSH,EMAIL,DISCORD,ICAL";
        this.strikes = 0;
        this.banned = false;
        this.verificationToken = null;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(id).toHashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Player player = (Player) o;

        return new EqualsBuilder().append(id, player.id).isEquals();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
                .append("id", id)
                .append("username", username)
                .append("email", email)
                .append("password", password)
                .append("emailVerificationStatus", emailVerificationStatus)
                .append("role", role)
                .append("preferredRole", preferredRole)
                .append("region", region)
                .append("platform", platform)
                .append("availability", availability)
                .append("visibleRank", visibleRank)
                .append("averagePingMs", averagePingMs)
                .append("rank", rank)
                .append("gamesPlayed", gamesPlayed)
                .append("wins", wins)
                .append("losses", losses)
                .append("kda", kda)
                .toString();
    }
}


