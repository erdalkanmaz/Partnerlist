package com.partnerlist.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.partnerlist.model.Company;
import com.partnerlist.service.CompanyService;

@Controller
@RequestMapping("/print")
public class PrintController {
    
    @Autowired
    private CompanyService companyService;
    
    @GetMapping
    public String print(@RequestParam(required = false) String companyId, Model model) {
        List<Company> companies = new ArrayList<>();

        if (companyId != null && !companyId.isBlank() && !"null".equalsIgnoreCase(companyId)) {
            try {
                Long id = Long.parseLong(companyId);
                companyService.findById(id).ifPresent(comp -> {
                    comp.getContacts().size(); // Lazy load contacts
                    companies.add(comp);
                });
                model.addAttribute("mode", "single");
            } catch (NumberFormatException ex) {
                // Geçersiz ID gelirse hiçbir şirket eklenmez, tüm liste yazdırılmaz
                model.addAttribute("mode", "all");
            }
        } else {
            model.addAttribute("mode", "all");
        }

        // Varsayılan format için boolean attribute'lar
        model.addAttribute("showCompanyName", true);
        model.addAttribute("showCompanyTelephone", true);
        model.addAttribute("showCompanyFax", true);
        model.addAttribute("showCompanyEmail", true);
        model.addAttribute("showCompanyWeb", true);
        model.addAttribute("showCompanyVatId", true);
        model.addAttribute("showCompanyCourtInfo", true);
        model.addAttribute("showCompanyAddress", true);
        model.addAttribute("showContactName", true);
        model.addAttribute("showContactTelephone", true);
        model.addAttribute("showContactEmail", true);
        model.addAttribute("showContactAddress", true);
        model.addAttribute("showContacts", true);

        model.addAttribute("companies", companies);
        return "print";
    }

    @PostMapping
    public String printSelected(@RequestParam(value = "companyIds", required = false) List<String> companyIds,
                               @RequestParam(value = "format", defaultValue = "full") String format,
                               Model model) {
        List<Company> companies = new ArrayList<>();

        if (companyIds != null) {
            for (String idStr : companyIds) {
                if (idStr == null || idStr.isBlank() || "null".equalsIgnoreCase(idStr)) {
                    continue;
                }
                try {
                    Long id = Long.parseLong(idStr);
                    companyService.findById(id).ifPresent(comp -> {
                        comp.getContacts().size(); // Lazy load contacts
                        companies.add(comp);
                    });
                } catch (NumberFormatException ignored) {
                    // Geçersiz ID'leri atla
                }
            }
        }

        // Format JSON'ını parse et
        Map<String, List<String>> formatConfig = parseFormat(format);
        List<String> companyFields = formatConfig.getOrDefault("company", List.of("alle"));
        List<String> contactFields = formatConfig.getOrDefault("contact", List.of("alle"));
        
        // Boolean attribute'lar oluştur
        model.addAttribute("showCompanyName", shouldIncludeField(companyFields, "name"));
        model.addAttribute("showCompanyTelephone", shouldIncludeField(companyFields, "telephone"));
        model.addAttribute("showCompanyFax", shouldIncludeField(companyFields, "fax"));
        model.addAttribute("showCompanyEmail", shouldIncludeField(companyFields, "email"));
        model.addAttribute("showCompanyWeb", shouldIncludeField(companyFields, "web"));
        model.addAttribute("showCompanyVatId", shouldIncludeField(companyFields, "vatId"));
        model.addAttribute("showCompanyCourtInfo", shouldIncludeField(companyFields, "courtInfo"));
        model.addAttribute("showCompanyAddress", shouldIncludeField(companyFields, "address"));
        
        model.addAttribute("showContactName", shouldIncludeField(contactFields, "name"));
        model.addAttribute("showContactTelephone", shouldIncludeField(contactFields, "telephone"));
        model.addAttribute("showContactEmail", shouldIncludeField(contactFields, "email"));
        model.addAttribute("showContactAddress", shouldIncludeField(contactFields, "address"));
        model.addAttribute("showContacts", contactFields != null && !contactFields.isEmpty());
        
        model.addAttribute("companies", companies);
        model.addAttribute("mode", "selected");
        model.addAttribute("format", format);
        model.addAttribute("formatConfig", formatConfig);
        return "print";
    }
    
    private boolean shouldIncludeField(List<String> fields, String fieldName) {
        if (fields == null || fields.isEmpty()) return false;
        if (fields.contains("alle")) return true;
        return fields.contains(fieldName);
    }
    
    private Map<String, List<String>> parseFormat(String format) {
        Map<String, List<String>> config = new HashMap<>();
        
        // Eski format string'leri için geriye dönük uyumluluk
        if (format == null || format.isEmpty() || format.equals("full")) {
            config.put("company", List.of("alle", "name", "telephone", "fax", "email", "web", "vatId", "courtInfo", "address"));
            config.put("contact", List.of("alle", "name", "telephone", "email", "address"));
            return config;
        }
        
        if (format.equals("names")) {
            config.put("company", List.of("name"));
            config.put("contact", List.of());
            return config;
        }
        
        if (format.equals("summary")) {
            config.put("company", List.of("name", "email", "web"));
            config.put("contact", List.of("name"));
            return config;
        }
        
        if (format.equals("company-only")) {
            config.put("company", List.of("alle", "name", "telephone", "fax", "email", "web", "vatId", "courtInfo", "address"));
            config.put("contact", List.of());
            return config;
        }
        
        // Yeni JSON formatını parse et
        try {
            ObjectMapper mapper = new ObjectMapper();
            @SuppressWarnings("unchecked")
            Map<String, Object> jsonMap = mapper.readValue(format, Map.class);
            
            @SuppressWarnings("unchecked")
            List<String> companyFields = (List<String>) jsonMap.getOrDefault("company", List.of("alle"));
            @SuppressWarnings("unchecked")
            List<String> contactFields = (List<String>) jsonMap.getOrDefault("contact", List.of("alle"));
            
            config.put("company", companyFields);
            config.put("contact", contactFields);
        } catch (Exception e) {
            // JSON parse hatası durumunda varsayılan değerleri kullan
            config.put("company", List.of("alle", "name", "telephone", "fax", "email", "web", "vatId", "courtInfo", "address"));
            config.put("contact", List.of("alle", "name", "telephone", "email", "address"));
        }
        
        return config;
    }
}
