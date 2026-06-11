package com.pds.tp.infrastructure.persistence.converter;

import com.pds.tp.domain.valueobject.ScrimApplicationStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class ScrimApplicationStatusConverter implements AttributeConverter<ScrimApplicationStatus, String> {
    @Override
    public String convertToDatabaseColumn(ScrimApplicationStatus attribute) {
        return attribute == null ? null : attribute.value();
    }

    @Override
    public ScrimApplicationStatus convertToEntityAttribute(String dbData) {
        return dbData == null ? null : ScrimApplicationStatus.of(dbData);
    }
}

