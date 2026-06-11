package com.pds.tp.infrastructure.persistence.converter;

import com.pds.tp.domain.valueobject.WaitlistStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class WaitlistStatusConverter implements AttributeConverter<WaitlistStatus, String> {
    @Override
    public String convertToDatabaseColumn(WaitlistStatus attribute) {
        return attribute == null ? null : attribute.value();
    }

    @Override
    public WaitlistStatus convertToEntityAttribute(String dbData) {
        return dbData == null ? null : WaitlistStatus.of(dbData);
    }
}

