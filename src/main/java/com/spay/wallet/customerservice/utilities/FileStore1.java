package com.spay.wallet.customerservice.utilities;

import com.spay.wallet.customerservice.common.exceptions.ApiException;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.resizers.configurations.ScalingMode;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import static java.nio.file.Files.copy;
import static java.nio.file.Paths.get;

@Component
public class FileStore1 {

    private static final String BASE_DIRECTORY = System.getProperty("user.home");
    private final String path = String.format("%s/%s/", BASE_DIRECTORY, "profile");

    public void storeFile(String fileName, InputStream theFile) {
        try (InputStream file = theFile) {
            Path fileStorage = get(path, fileName).normalize();
            Files.createDirectories(fileStorage.getParent());
            copy(file, fileStorage, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new ApiException("File is not uploaded", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public FileDownload downloadFile(String filename, boolean isDownload) {
        try {
            if(filename == null)
                filename = "";
            Path filePath = get(path).toAbsolutePath().normalize().resolve(filename);
            Resource resource = new UrlResource(filePath.toUri());
            HttpHeaders headers = new HttpHeaders();
            headers.add("file-name", filename);
            if (isDownload)
                headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment;File-Name=" + resource.getFilename());
            return new FileDownload(getFileContentType(filename), headers, resource);
        } catch (Exception e) {
            throw new ApiException("File not found ",HttpStatus.NOT_FOUND);
        }
    }

    public void deleteFiles(String fileName) {
        try {
            new File(path + fileName).delete();
        }catch (Exception ignore){

        }

    }

    public InputStream resizeImage(MultipartFile file) {
        var image = convertMultipartFileToBufferedImage(file);
        return compressedImage(image);
    }



    private InputStream compressedImage(BufferedImage imageFile) {
        try (var stream = new ByteArrayOutputStream()) {
            Thumbnails.of(imageFile)
                    .size(520, 520)
                    .scalingMode(ScalingMode.PROGRESSIVE_BILINEAR)
                    .outputFormat("JPEG")
                    .outputQuality(1)
                    .toOutputStream(stream);
            return new ByteArrayInputStream(stream.toByteArray());
        } catch (IOException e) {
            throw new ApiException("Unexpected error occurred while optimizing image.",HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    private BufferedImage convertMultipartFileToBufferedImage(MultipartFile file) {
        try (InputStream stream = file.getInputStream()) {
            return ImageIO.read(stream);
        } catch (Exception exception) {
            throw new ApiException("Is not valid Image", HttpStatus.NOT_ACCEPTABLE);
        }
    }

    private String getFileContentType(String fileName) {
        return URLConnection.getFileNameMap().getContentTypeFor(fileName);
    }


    public record FileDownload(String contentType, HttpHeaders headers, Resource resource) {
    }

}
