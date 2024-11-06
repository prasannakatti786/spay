package com.spay.wallet.remittance.services;

import com.spay.wallet.remittance.model.ApiConfig;
import com.spay.wallet.remittance.model.miscallanious.*;
import com.spay.wallet.remittance.model.transaction.TokenResponse;
import com.spay.wallet.remittance.services.*;
import com.spay.wallet.common.RemittanceUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

@Component
public class MiscellaneousServiceImpl implements MiscellaneousService {

    @Autowired
    ApiConfig apiConfig;

    @Autowired
    RemittanceUtil remittanceUtil;

    @Override
    public NationalitiesResponse getNationalities() {
        String signInUri = apiConfig.getBaseUrl() + "/api/v1/auth/sign_in/";
        TokenResponse tokenResponse = remittanceUtil.getBearerToken(signInUri, apiConfig.getEmail(), apiConfig.getPassword());
        String nationalitiesUri = apiConfig.getBaseUrl() + "/api/v1/nationalities";
        return (NationalitiesResponse) remittanceUtil.getApiResponse(HttpMethod.GET, nationalitiesUri, tokenResponse.getToken(), NationalitiesResponse.class);
    }

    @Override
    public IDTypeResponse findIdTypesByProductCode(String productCode) {
        String signInUri = apiConfig.getBaseUrl() + "/api/v1/auth/sign_in/";
        TokenResponse tokenResponse = remittanceUtil.getBearerToken(signInUri, apiConfig.getEmail(), apiConfig.getPassword());
        String idTypesByProductCodeUri = apiConfig.getBaseUrl() + "/api/v1/id_types/" + productCode;
        return (IDTypeResponse) remittanceUtil.getApiResponse(HttpMethod.GET, idTypesByProductCodeUri, tokenResponse.getToken(), IDTypeResponse.class);
    }

    @Override
    public PurposeOfTransfersResponse findPurposeOfTransfersByProductCode(String productCode) {
        String signInUri = apiConfig.getBaseUrl() + "/api/v1/auth/sign_in/";
        TokenResponse tokenResponse = remittanceUtil.getBearerToken(signInUri, apiConfig.getEmail(), apiConfig.getPassword());
        String purposeOfTransfersByProductCodeUri = apiConfig.getBaseUrl() + "/api/v1/purpose_of_transfers/" + productCode;
        return (PurposeOfTransfersResponse) remittanceUtil.getApiResponse(HttpMethod.GET, purposeOfTransfersByProductCodeUri, tokenResponse.getToken(), PurposeOfTransfersResponse.class);

    }

    @Override
    public SourceOfIncomesResponse findSOIByProductCode(String productCode) {
        String signInUri = apiConfig.getBaseUrl() + "/api/v1/auth/sign_in/";
        TokenResponse tokenResponse = remittanceUtil.getBearerToken(signInUri, apiConfig.getEmail(), apiConfig.getPassword());
        String sourceOfIncomesByProductCodeUri = apiConfig.getBaseUrl() + "/api/v1/source_of_incomes/" + productCode;
        return (SourceOfIncomesResponse) remittanceUtil.getApiResponse(HttpMethod.GET, sourceOfIncomesByProductCodeUri, tokenResponse.getToken(), SourceOfIncomesResponse.class);
    }

    @Override
    public RelationsResponse getRelations() {
        String signInUri = apiConfig.getBaseUrl() + "/api/v1/auth/sign_in/";
        TokenResponse tokenResponse = remittanceUtil.getBearerToken(signInUri, apiConfig.getEmail(), apiConfig.getPassword());
        String relationsUri = apiConfig.getBaseUrl() + "/api/v1/relations";
        return (RelationsResponse) remittanceUtil.getApiResponse(HttpMethod.GET, relationsUri, tokenResponse.getToken(), RelationsResponse.class);

    }

    @Override
    public AreaResponse getAreas() {
        String signInUri = apiConfig.getBaseUrl() + "/api/v1/auth/sign_in/";
        TokenResponse tokenResponse = remittanceUtil.getBearerToken(signInUri, apiConfig.getEmail(), apiConfig.getPassword());
        String areasUri = apiConfig.getBaseUrl() + "/api/v1/areas";
        return (AreaResponse) remittanceUtil.getApiResponse(HttpMethod.GET, areasUri, tokenResponse.getToken(), AreaResponse.class);
    }
}
