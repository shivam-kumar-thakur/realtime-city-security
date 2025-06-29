package com.detection.Detection.controller;

import com.detection.Detection.service.DetectionService;
import com.detection.Detection.service.InfluxService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
public class UploadController {

    @Autowired
    private DetectionService detectionService;

    @Autowired
    private InfluxService influxService;

    @PostMapping("/upload")
    public ResponseEntity<String> handleUpload(
            @RequestParam("vehicleId") String vehicleId,
            @RequestParam("timestamp") String timestamp,
            @RequestParam("image") MultipartFile imageFile
    ) {
        try {
            byte[] imageBytes = imageFile.getBytes();

            // Store raw image metadata
            influxService.writeRawMetadata(vehicleId, timestamp);

            // Call detection model
            String outcome = detectionService.detect(imageBytes).toString();

            // Store detection result
            influxService.writeDetectionJSON(vehicleId, timestamp, outcome);

            return ResponseEntity.ok("Received and processed.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error processing image");
        }
    }
}
