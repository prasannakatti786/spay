package com.spay.wallet.remittance.services;

import com.spay.wallet.remittance.model.miscallanious.*;
import org.springframework.http.ResponseEntity;

public interface MiscellaneousService {

    NationalitiesResponse getNationalities();

    IDTypeResponse findIdTypesByProductCode(String productCode);

    PurposeOfTransfersResponse findPurposeOfTransfersByProductCode(String productCode);

    SourceOfIncomesResponse findSOIByProductCode(String productCode);

    RelationsResponse getRelations();

    AreaResponse getAreas();
}
