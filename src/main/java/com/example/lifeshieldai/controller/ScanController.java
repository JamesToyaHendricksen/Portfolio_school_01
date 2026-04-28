package com.example.lifeshieldai.controller;

import com.example.lifeshieldai.dto.response.ScanResult;
import com.example.lifeshieldai.service.VirusTotalService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/scan")
@CrossOrigin(origins = "*")
public class ScanController {

    private final VirusTotalService virusTotalService;

    public ScanController(VirusTotalService virusTotalService) {
        this.virusTotalService = virusTotalService;
    }

    @PostMapping("/url")
    public ResponseEntity<ScanResult> scanUrl(@RequestBody Map<String, String> body) {
        String url = body.get("url");
        if (url == null || url.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        ScanResult result = virusTotalService.scanUrl(url);
        return ResponseEntity.ok(result);
    }
}
