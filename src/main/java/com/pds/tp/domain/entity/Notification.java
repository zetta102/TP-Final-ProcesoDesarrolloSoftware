package com.pds.tp.domain.entity;

import com.pds.tp.domain.valueobject.NotificationChannel;
import com.pds.tp.domain.valueobject.NotificationStatus;
import com.pds.tp.infrastructure.persistence.converter.NotificationChannelConverter;
import com.pds.tp.infrastructure.persistence.converter.NotificationStatusConverter;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String type;
    private String payload;

    @Convert(converter = NotificationChannelConverter.class)
    private NotificationChannel channel;

    @Setter
    @Convert(converter = NotificationStatusConverter.class)
    private NotificationStatus status = NotificationStatus.PENDING;
}


