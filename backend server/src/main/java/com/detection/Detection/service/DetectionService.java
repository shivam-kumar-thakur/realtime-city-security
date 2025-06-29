package com.detection.Detection.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

@Service
public class DetectionService {

    public List<Map<String, Object>> detect(byte[] imageBytes) throws IOException, InterruptedException {
        // Save image to temp file
        Path tempFile = Files.createTempFile("vehicle", ".jpg");
        Files.write(tempFile, imageBytes);

        // Call Python script
        ProcessBuilder pb = new ProcessBuilder("python3", "detect.py", tempFile.toString());
        pb.redirectErrorStream(true);
        Process process = pb.start();

        // Read script output (JSON)
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        StringBuilder jsonOutput = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            jsonOutput.append(line);
        }

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new RuntimeException("Python detection failed.");
        }

        // Convert JSON to Java List
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(jsonOutput.toString(), List.class);
    }
}

