package com.second.hand.trading.server.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@RestController
public class GeocodeController {

    @Value("${amap.server.key:}")
    private String amapServerKey;

    private final RestTemplate restTemplate = new RestTemplate();

    @GetMapping(value = {"/api/geocode", "/geocode"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> geocode(@RequestParam("address") String address) {
        Map<String, Object> resp = new HashMap<>();
        if (address == null || address.trim().isEmpty()) {
            resp.put("status_code", 0);
            resp.put("msg", "address required");
            return resp;
        }

        try {
            // Prefer server-side AMap geocode API
            if (amapServerKey != null && !amapServerKey.isEmpty()) {
                String enc = URLEncoder.encode(address, StandardCharsets.UTF_8.name());
                String url = String.format("https://restapi.amap.com/v3/geocode/geo?key=%s&address=%s", amapServerKey, enc);
                String result = restTemplate.getForObject(url, String.class);
                if (result != null) {
                    JSONObject json = JSON.parseObject(result);
                    if ("1".equals(json.getString("status"))) {
                        if (json.containsKey("geocodes")) {
                            if (json.getJSONArray("geocodes").size() > 0) {
                                JSONObject g = json.getJSONArray("geocodes").getJSONObject(0);
                                String location = g.getString("location"); // "lng,lat"
                                String formatted = g.getString("formatted_address");
                                if (location != null && location.contains(",")) {
                                    String[] parts = location.split(",");
                                    double lng = Double.parseDouble(parts[0]);
                                    double lat = Double.parseDouble(parts[1]);
                                    Map<String, Object> data = new HashMap<>();
                                    data.put("lng", lng);
                                    data.put("lat", lat);
                                    data.put("formattedAddress", formatted != null ? formatted : address);
                                    resp.put("status_code", 1);
                                    resp.put("data", data);
                                    return resp;
                                }
                            }
                        }
                    }
                }
                // fallback to place/text search
                String enc2 = URLEncoder.encode(address, StandardCharsets.UTF_8.name());
                String url2 = String.format("https://restapi.amap.com/v3/place/text?key=%s&keywords=%s&city=&extensions=all", amapServerKey, enc2);
                String result2 = restTemplate.getForObject(url2, String.class);
                if (result2 != null) {
                    JSONObject json2 = JSON.parseObject(result2);
                    if ("1".equals(json2.getString("status")) && json2.containsKey("pois")) {
                        if (json2.getJSONArray("pois").size() > 0) {
                            JSONObject poi = json2.getJSONArray("pois").getJSONObject(0);
                            String location = poi.getString("location");
                            String pname = poi.getString("name");
                            String paddr = poi.getString("address");
                            if (location != null && location.contains(",")) {
                                String[] parts = location.split(",");
                                double lng = Double.parseDouble(parts[0]);
                                double lat = Double.parseDouble(parts[1]);
                                Map<String, Object> data = new HashMap<>();
                                data.put("lng", lng);
                                data.put("lat", lat);
                                data.put("formattedAddress", (pname != null ? pname + " " : "") + (paddr != null ? paddr : address));
                                resp.put("status_code", 1);
                                resp.put("data", data);
                                return resp;
                            }
                        }
                    }
                }
            }

            // If server key not configured or AMap failed, return failure
            resp.put("status_code", 0);
            resp.put("msg", "geocode failed or server key not configured");
            return resp;
        } catch (Exception e) {
            resp.put("status_code", 0);
            resp.put("msg", "exception: " + e.getMessage());
            return resp;
        }
    }
}

