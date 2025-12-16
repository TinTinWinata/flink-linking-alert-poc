package org.example.sink;

import org.apache.flink.configuration.Configuration;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.example.model.VehicleDuplicateAlert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

public class VehicleDuplicateHttpSink extends RichSinkFunction<VehicleDuplicateAlert> {
    private transient CloseableHttpClient httpClient;
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger log = LoggerFactory.getLogger(VehicleDuplicateAlert.class);

    @Override
    public void open(Configuration parameters) throws Exception {
        httpClient = HttpClients.createDefault();
    }

    @Override
    public void invoke(VehicleDuplicateAlert value, Context context) throws Exception {
        String jsonPayload = objectMapper.writeValueAsString(value);

        HttpPost httpPost = new HttpPost("http://localhost:5000/test");
        httpPost.setHeader("Content-Type", "application/json");
        httpPost.setEntity(new StringEntity(jsonPayload, StandardCharsets.UTF_8));

        try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
            int statusCode = response.getCode();
            if (statusCode >= 200 && statusCode < 300) {
                log.info("Successfully sent duplicate alert!");
            } else {
                log.warn("Sent duplicate alert with response code not 200: {}", response.toString());
            }
        } catch (Exception ex) {
            log.warn("Error sending duplicate alert! {}", ex.getMessage());
        }
    }
}
