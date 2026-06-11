package com.pds.tp.infrastructure.persistence.converter;

import com.pds.tp.domain.valueobject.EmailVerificationStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class EmailVerificationStatusConverter implements AttributeConverter<EmailVerificationStatus, String> {
    @Override
    public String convertToDatabaseColumn(EmailVerificationStatus attribute) {
        return attribute == null ? null : attribute.value();
    }

    @Override
    public EmailVerificationStatus convertToEntityAttribute(String dbData) {
        return dbData == null ? null : EmailVerificationStatus.of(dbData);
    }
}

