package io.reactivestax.ems.service;

import io.reactivestax.ems.dto.BaseDTO;
import io.reactivestax.ems.enums.NotificationMethod;

public interface MessagingService {
    void save(BaseDTO baseDTO, NotificationMethod notificationMethod);
}
