package com.partnerlist.controller;

import com.partnerlist.model.Company;
import com.partnerlist.model.CompanyType;
import com.partnerlist.service.CompanyService;
import com.partnerlist.service.ContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Comparator;
import java.util.List;

@Controller
public class HomeController {
    
    @Autowired
    private CompanyService companyService;
    
    @Autowired
    private ContactService contactService;
    
    @GetMapping({"/", "/index"})
    public String index(@RequestParam(value = "type", required = false) String type,
                        Model model) {
        CompanyType filterType = parseCompanyType(type);
        
        long totalCompanies;
        long totalContacts;
        List<Company> recentCompanies;
        
        if (filterType == null) {
            // Gesamtsicht
            totalCompanies = companyService.count();
            totalContacts = contactService.count();
            recentCompanies = companyService.findRecent(5);
        } else {
            // Nach Typ gefilterte Sicht
            List<Company> all = companyService.findAll();
            // Kontakte laden (Lazy Loading)
            all.forEach(c -> c.getContacts().size());
            
            List<Company> filtered = all.stream()
                    .filter(c -> c.getCompanyType() == filterType)
                    .toList();
            
            totalCompanies = filtered.size();
            totalContacts = filtered.stream()
                    .mapToLong(c -> c.getContacts() != null ? c.getContacts().size() : 0)
                    .sum();
            
            recentCompanies = filtered.stream()
                    .sorted(Comparator.comparing(Company::getCreatedAt,
                            Comparator.nullsLast(Comparator.naturalOrder())).reversed())
                    .limit(5)
                    .toList();
        }
        
        model.addAttribute("totalCompanies", totalCompanies);
        model.addAttribute("totalContacts", totalContacts);
        model.addAttribute("recentCompanies", recentCompanies);
        model.addAttribute("selectedType", filterType != null ? filterType.name() : "");
        
        return "index";
    }
    
    private CompanyType parseCompanyType(String type) {
        if (type == null || type.isBlank()) {
            return null;
        }
        try {
            return CompanyType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }
}
