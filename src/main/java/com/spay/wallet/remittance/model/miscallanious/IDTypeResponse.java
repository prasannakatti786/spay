package com.spay.wallet.remittance.model.miscallanious;

import com.spay.wallet.remittance.model.ApiResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class IDTypeResponse {
    private ApiResponse api_response;
    private List<IdType> id_types;
}
