package com.spay.wallet.remittance.model.miscallanious;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.spay.wallet.remittance.model.ApiResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class AreaResponse {
    private ApiResponse api_response;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<Area> areas;
}