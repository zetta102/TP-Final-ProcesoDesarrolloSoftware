package com.pds.tp.infrastructure.persistence.converter;

import com.pds.tp.domain.valueobject.NotificationChannel;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class NotificationChannelConverter implements AttributeConverter<NotificationChannel, String> {
    @Override
    public String convertToDatabaseColumn(NotificationChannel attribute) {
        return attribute == null ? null : attribute.value();
    }

    @Override
    public NotificationChannel convertToEntityAttribute(String dbData) {
        return dbData == null ? null : NotificationChannel.of(dbData);
    }
}

