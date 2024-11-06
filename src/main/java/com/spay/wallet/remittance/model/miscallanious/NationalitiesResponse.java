package com.spay.wallet.remittance.model.miscallanious;


import com.spay.wallet.remittance.model.ApiResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class NationalitiesResponse{
    public ApiResponse api_response;
    public List<DestCountry> dest_countries;
}
