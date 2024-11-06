package com.spay.wallet.download;

import com.spay.wallet.customer.services.CustomerService;
import com.spay.wallet.customer.utilities.FileStore;
import com.spay.wallet.exections.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/download")
@RequiredArgsConstructor
public class ImageDownloadController {
    private final FileStore fileStore;
    private final CustomerService customerService;

    @GetMapping("/profile/{fileName}")
    public ResponseEntity<Resource> downloadImage(@PathVariable String fileName) {
        var image = fileStore.downloadFile(fileName,false);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(image.contentType()))
                .headers(image.headers())
                .body(image.resource());
    }

    @GetMapping("/profile-by-customer/{id}")
    public ResponseEntity<Resource> downloadImageByCustomerId(@PathVariable Long id) {
        var fileName = customerService.getCustomerProfile(id);
        if(fileName == null)
            throw new ApiException("No profile image", HttpStatus.NOT_FOUND);
        var image = fileStore.downloadFile(fileName,false);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(image.contentType()))
                .headers(image.headers())
                .body(image.resource());
    }

}
