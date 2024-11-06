package com.spay.wallet.remittance.controllers;


import com.spay.wallet.remittance.model.miscallanious.AreaResponse;
import com.spay.wallet.remittance.model.miscallanious.IDTypeResponse;
import com.spay.wallet.remittance.model.miscallanious.NationalitiesResponse;
import com.spay.wallet.remittance.model.miscallanious.PurposeOfTransfersResponse;
import com.spay.wallet.remittance.model.miscallanious.RelationsResponse;
import com.spay.wallet.remittance.model.miscallanious.SourceOfIncomesResponse;
import com.spay.wallet.remittance.services.MiscellaneousService;
import com.spay.wallet.common.RemittanceUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

@RestController
@RequestMapping("/api/v1/miscellaneous")
@RequiredArgsConstructor
public class MiscellaneousController {

    private final MiscellaneousService miscellaneousService;
    private final RemittanceUtil remittanceUtil;

    @GetMapping("/nationalities")
    public ResponseEntity<NationalitiesResponse> getNationalities() {
        try {
            return ResponseEntity.ok(miscellaneousService.getNationalities());
        } catch (HttpClientErrorException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAs(NationalitiesResponse.class));
        }
    }

    @GetMapping("/id_types/{product_code}")
    public ResponseEntity<IDTypeResponse> getIdTypesByProductCode(@PathVariable("product_code") String productCode) {
        try {
            return ResponseEntity.ok(miscellaneousService.findIdTypesByProductCode(productCode));
        } catch (HttpClientErrorException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAs(IDTypeResponse.class));
        }
    }

    @GetMapping("/purpose_of_transfers/{product_code}")
    public ResponseEntity<PurposeOfTransfersResponse> getPurposeOfTransfersByProductCode(@PathVariable("product_code") String productCode) {
        try {
            return ResponseEntity.ok(miscellaneousService.findPurposeOfTransfersByProductCode(productCode));
        } catch (HttpClientErrorException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAs(PurposeOfTransfersResponse.class));
        }
    }

    @GetMapping("/source_of_incomes/{product_code}")
    public ResponseEntity<SourceOfIncomesResponse> getSOIByProductCode(@PathVariable("product_code") String productCode) {
        try {
            return ResponseEntity.ok(miscellaneousService.findSOIByProductCode(productCode));
        } catch (HttpClientErrorException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAs(SourceOfIncomesResponse.class));
        }
    }

    @GetMapping("/relations")
    public ResponseEntity<RelationsResponse> getRelations() {
        try {
            return ResponseEntity.ok(miscellaneousService.getRelations());
        } catch (HttpClientErrorException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAs(RelationsResponse.class));
        }
    }

    @GetMapping("/areas")
    public ResponseEntity<AreaResponse> getAreas() {
        try {
            return ResponseEntity.ok(miscellaneousService.getAreas());
        } catch (HttpClientErrorException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAs(AreaResponse.class));
        }
    }

}
