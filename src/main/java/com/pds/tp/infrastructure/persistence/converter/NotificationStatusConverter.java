package com.pds.tp.infrastructure.persistence.converter;

import com.pds.tp.domain.valueobject.NotificationStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class NotificationStatusConverter implements AttributeConverter<NotificationStatus, String> {
    @Override
    public String convertToDatabaseColumn(NotificationStatus attribute) {
        return attribute == null ? null : attribute.value();
    }

    @Override
    public NotificationStatus convertToEntityAttribute(String dbData) {
        return dbData == null ? null : NotificationStatus.of(dbData);
    }
}

