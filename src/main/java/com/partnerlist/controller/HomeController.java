package com.partnerlist.controller;

import com.partnerlist.model.Company;
import com.partnerlist.service.CompanyService;
import com.partnerlist.service.ContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {
    
    @Autowired
    private CompanyService companyService;
    
    @Autowired
    private ContactService contactService;
    
    @GetMapping({"/", "/index"})
    public String index(Model model) {
        long totalCompanies = companyService.count();
        long totalContacts = contactService.count();
        List<Company> recentCompanies = companyService.findRecent(5);
        
        model.addAttribute("totalCompanies", totalCompanies);
        model.addAttribute("totalContacts", totalContacts);
        model.addAttribute("recentCompanies", recentCompanies);
        
        return "index";
    }
}
