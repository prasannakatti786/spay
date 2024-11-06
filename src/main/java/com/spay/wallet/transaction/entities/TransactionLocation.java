package com.spay.wallet.transaction.entities;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Embeddable
@AllArgsConstructor
@Builder
@NoArgsConstructor
@ToString
public class TransactionLocation {
    @NotNull(message = "Country code is required")
    private String countryCode;
    @NotNull(message = "Device type is required")
    private String deviceType;
    @NotNull(message = "Device id is required")
    private String deviceId;
    @NotNull(message = "Device OS is required")
    private String deviceOS;
    @NotNull(message = "Longitude is required")
    private String lon;
    @NotNull(message = "Latitude is required")
    private String lat;
}