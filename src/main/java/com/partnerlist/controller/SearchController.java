package com.partnerlist.controller;

import com.partnerlist.model.Company;
import com.partnerlist.model.Contact;
import com.partnerlist.service.CompanyService;
import com.partnerlist.service.ContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/search")
public class SearchController {
    
    @Autowired
    private CompanyService companyService;
    
    @Autowired
    private ContactService contactService;
    
    @GetMapping
    public String search(@RequestParam(required = false) String q,
                         @RequestParam(required = false, defaultValue = "all") String field,
                         Model model) {

        List<Company> companies = new ArrayList<>();
        List<Contact> contacts = new ArrayList<>();

        if (q != null && !q.trim().isEmpty()) {
            String term = normalizeSearchTerm(q);

            switch (field) {
                case "company_name":
                    // Sadece firma ismi
                    companies.addAll(companyService.searchByName(term));
                    break;
                    
                case "person_name":
                    // Sadece personel ismi
                    contacts.addAll(contactService.search(term, "person_name"));
                    break;
                    
                case "email":
                    // Hem firma hem personel email
                    companies.addAll(companyService.searchByEmail(term));
                    contacts.addAll(contactService.search(term, "email"));
                    break;
                    
                case "telephone":
                    // Sadece personel telefon
                    contacts.addAll(contactService.search(term, "telephone"));
                    break;
                    
                case "address":
                    // Hem firma hem personel adres
                    companies.addAll(companyService.searchByAddress(term));
                    contacts.addAll(contactService.search(term, "address"));
                    break;
                    
                case "title":
                    // Sadece personel unvan
                    contacts.addAll(contactService.search(term, "title"));
                    break;
                    
                case "all":
                default:
                    // Tüm alanlarda arama
                    companies.addAll(companyService.searchByName(term));
                    contacts.addAll(contactService.search(term, "person_name"));
                    contacts.addAll(contactService.search(term, "email"));
                    contacts.addAll(contactService.search(term, "telephone"));
                    contacts.addAll(contactService.search(term, "address"));
                    contacts.addAll(contactService.search(term, "title"));
                    // Email ve address için firma kayıtlarını da ara
                    companies.addAll(companyService.searchByEmail(term));
                    companies.addAll(companyService.searchByAddress(term));
                    break;
            }

            // Doppelte Unternehmen entfernen (ID'ye göre)
            Set<Long> companyIds = new HashSet<>();
            List<Company> uniqueCompanies = new ArrayList<>();
            for (Company company : companies) {
                if (company.getId() != null && !companyIds.contains(company.getId())) {
                    companyIds.add(company.getId());
                    uniqueCompanies.add(company);
                }
            }
            companies = uniqueCompanies;
            
            // Doppelte Kontakte entfernen (ID'ye göre)
            Set<Long> contactIds = new HashSet<>();
            List<Contact> uniqueContacts = new ArrayList<>();
            for (Contact contact : contacts) {
                if (contact.getId() != null && !contactIds.contains(contact.getId())) {
                    contactIds.add(contact.getId());
                    uniqueContacts.add(contact);
                }
            }
            contacts = uniqueContacts;
        }

        model.addAttribute("companies", companies);
        model.addAttribute("contacts", contacts);
        model.addAttribute("searchTerm", q);
        model.addAttribute("searchField", field);

        return "search_results";
    }
    
    /**
     * Arama terimini normalize eder:
     * - Başta ve sonda boşlukları kaldırır
     * - Birden fazla boşluğu tek boşluğa çevirir
     * - Tab ve newline karakterlerini boşluğa çevirir
     */
    private String normalizeSearchTerm(String term) {
        if (term == null) {
            return "";
        }
        // Tab ve newline karakterlerini boşluğa çevir, birden fazla boşluğu tek boşluğa çevir, başta ve sonda boşlukları kaldır
        return term.replaceAll("[\\t\\n\\r]+", " ")
                   .replaceAll("\\s+", " ")
                   .trim();
    }
}
