package com.detection.Detection.service;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.domain.WritePrecision;
import org.springframework.stereotype.Service;

import java.time.Instant;

import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.write.Point;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class InfluxService {

    @Autowired
    private InfluxDBClient influxDBClient;

    public void writeDetectionJSON(String vehicleId, String timestamp, String jsonData) {
        Point point = Point
                .measurement("vehicle_detection")
                .addTag("vehicleId", vehicleId)
                .addField("timestamp", timestamp)
                .addField("detection", jsonData)
                .time(Instant.now(), WritePrecision.NS);

        WriteApiBlocking writeApi = influxDBClient.getWriteApiBlocking();
        writeApi.writePoint(point);
    }

    public void writeRawMetadata(String vehicleId, String timestamp) {
        Point point = Point
                .measurement("raw_metadata")
                .addTag("vehicleId", vehicleId)
                .addField("timestamp", timestamp)
                .time(Instant.now(), WritePrecision.NS);

        influxDBClient.getWriteApiBlocking().writePoint(point);
    }
}
