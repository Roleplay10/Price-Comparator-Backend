package com.roleplay10.price_comparator.service.alert;

import com.roleplay10.price_comparator.dto.request.alert.CreateAlertRequest;
import com.roleplay10.price_comparator.dto.response.alert.AlertResponse;

import java.util.List;

public interface PriceAlertService {
    AlertResponse createAlert(String username, CreateAlertRequest req);
    List<AlertResponse> listAlerts(String username);
    void disableAlert(String username, Long alertId);
}