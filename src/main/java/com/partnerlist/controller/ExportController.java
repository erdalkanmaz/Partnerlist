package com.partnerlist.controller;

import com.partnerlist.service.ExportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/export")
public class ExportController {
    
    @Autowired
    private ExportService exportService;
    
    @PostMapping
    public ResponseEntity<byte[]> export(@RequestParam(value = "companyIds", required = false) List<String> companyIds,
                                        @RequestParam(value = "action") String action,
                                        @RequestParam(value = "format", defaultValue = "full") String format) {
        try {
            List<Long> ids = new ArrayList<>();
            if (companyIds != null) {
                for (String idStr : companyIds) {
                    if (idStr != null && !idStr.isBlank() && !"null".equalsIgnoreCase(idStr)) {
                        try {
                            ids.add(Long.parseLong(idStr));
                        } catch (NumberFormatException ignored) {
                            // Geçersiz ID'leri atla
                        }
                    }
                }
            }
            
            byte[] data;
            String filename;
            String contentType;
            
            if ("excel".equals(action)) {
                data = exportService.exportToExcel(ids, format);
                filename = "partnerliste_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".xlsx";
                contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            } else if ("csv".equals(action)) {
                data = exportService.exportToCSV(ids, format);
                filename = "partnerliste_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".csv";
                contentType = "text/csv; charset=UTF-8";
            } else {
                return ResponseEntity.badRequest().build();
            }
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(contentType));
            headers.setContentDispositionFormData("attachment", filename);
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(data);
                    
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
