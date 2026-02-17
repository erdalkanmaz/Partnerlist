package com.partnerlist.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.partnerlist.model.Company;
import com.partnerlist.model.Contact;
import com.partnerlist.service.CompanyService;
import com.partnerlist.service.ContactService;

@Controller
@RequestMapping("/companies")
public class CompanyController {
    
    @Autowired
    private CompanyService companyService;

    @Autowired
    private ContactService contactService;
    
    @GetMapping
    public String list(@RequestParam(required = false) String search,
                       Model model) {
        List<Company> companies;

        if (search != null && !search.trim().isEmpty()) {
            companies = companyService.searchByName(search.trim());
        } else {
            companies = companyService.findAll();
        }

        // Kontakte laden (Lazy Loading auslösen)
        companies.forEach(comp -> comp.getContacts().size());

        model.addAttribute("companies", companies);
        model.addAttribute("searchTerm", search);
        return "companies";
    }
    
    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("company", new Company());
        return "company_form";
    }
    
    @PostMapping("/add")
    public String add(@ModelAttribute Company company,
                      @RequestParam(required = false) Integer contactNumber,
                      @RequestParam(required = false) String contactGender,
                      @RequestParam(required = false) String contactPersonName,
                      @RequestParam(required = false) String contactTitle,
                      @RequestParam(required = false) String contactTelephone,
                      @RequestParam(required = false) String contactFax,
                      @RequestParam(required = false) String contactEmail,
                      @RequestParam(required = false) String contactAddress,
                      @RequestParam(value = "duplicateWarningShown", required = false) Boolean duplicateWarningShown,
                      Model model,
                      RedirectAttributes redirectAttributes) {
        // E-posta alanını trim et (başta/sonda boşlukları temizle)
        if (company.getEmail() != null) {
            company.setEmail(company.getEmail().trim());
        }
        
        // Dublettenprüfung: zuerst Warnfenster (Formular wird erneut angezeigt),
        // wenn der Benutzer erneut speichert, wird die eigentliche Speicherung ausgeführt.
        boolean warningAlreadyShown = Boolean.TRUE.equals(duplicateWarningShown);
        if (!warningAlreadyShown) {
            List<Company> duplicates = companyService.findPotentialDuplicates(company);
            if (!duplicates.isEmpty()) {
                String name = company.getName() != null ? company.getName().trim() : null;
                String email = company.getEmail() != null ? company.getEmail().trim() : null;
                String address = company.getAddress() != null ? company.getAddress().trim() : null;
                
                boolean hasExact = false;
                boolean hasPartial = false;
                
                for (Company dup : duplicates) {
                    String dName = dup.getName() != null ? dup.getName().trim() : null;
                    String dEmail = dup.getEmail() != null ? dup.getEmail().trim() : null;
                    String dAddress = dup.getAddress() != null ? dup.getAddress().trim() : null;
                    
                    boolean sameName = (name == null && dName == null) || (name != null && name.equalsIgnoreCase(dName));
                    boolean sameEmail = (email == null && dEmail == null) || (email != null && email.equalsIgnoreCase(dEmail));
                    boolean sameAddress = (address == null && dAddress == null) || (address != null && address.equalsIgnoreCase(dAddress));
                    
                    if (sameName && sameEmail && sameAddress) {
                        hasExact = true;
                    } else {
                        hasPartial = true;
                    }
                }
                
                model.addAttribute("company", company);
                model.addAttribute("duplicateCompanies", duplicates);
                model.addAttribute("hasExactDuplicate", hasExact);
                model.addAttribute("hasPartialDuplicate", hasPartial);
                model.addAttribute("duplicateWarningShown", true);
                return "company_form";
            }
        }
        
        // Zuerst das Unternehmen speichern
        companyService.save(company);

        // Wenn Kontaktdaten ausgefüllt sind, ersten Kontakt anlegen
        boolean hasContactData =
                (contactPersonName != null && !contactPersonName.isBlank()) ||
                (contactTelephone != null && !contactTelephone.isBlank()) ||
                (contactEmail != null && !contactEmail.isBlank()) ||
                (contactAddress != null && !contactAddress.isBlank());

        if (hasContactData) {
            Contact contact = new Contact();
            contact.setCompany(company);
            contact.setNumber(contactNumber);
            contact.setGender(contactGender);
            contact.setPersonName(contactPersonName);
            contact.setTitle(contactTitle);
            contact.setTelephone(contactTelephone);
            contact.setFax(contactFax);
            
            // E-Mail-Feld trimmen (Leerzeichen am Anfang/Ende entfernen)
            if (contactEmail != null) {
                contact.setEmail(contactEmail.trim());
            }

            // Wenn die Adresse leer ist, die Unternehmensadresse verwenden
            if (contactAddress != null && !contactAddress.isBlank()) {
                contact.setAddress(contactAddress);
            } else {
                contact.setAddress(company.getAddress());
            }

            contactService.save(contact);
        }

        redirectAttributes.addFlashAttribute("message", "Unternehmen und zugehörige Kontaktdaten wurden erfolgreich gespeichert.");
        redirectAttributes.addFlashAttribute("messageType", "success");
        return "redirect:/companies/" + company.getId();
    }
    
    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Optional<Company> company = companyService.findById(id);
        if (company.isEmpty()) {
            return "redirect:/companies";
        }
        Company comp = company.get();
        // Contacts'ları yükle
        comp.getContacts().size(); // Lazy loading trigger
        model.addAttribute("company", comp);
        return "company_detail";
    }
    
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        Optional<Company> company = companyService.findById(id);
        if (company.isEmpty()) {
            return "redirect:/companies";
        }
        model.addAttribute("company", company.get());
        return "company_form";
    }
    
    @PostMapping("/{id}/edit")
    public String update(@PathVariable Long id, @ModelAttribute Company company, 
                       RedirectAttributes redirectAttributes) {
        Optional<Company> existing = companyService.findById(id);
        if (existing.isEmpty()) {
            return "redirect:/companies";
        }
        
        Company updated = existing.get();
        updated.setName(company.getName());
        updated.setWebUrl(company.getWebUrl());
        
        // E-posta alanını trim et (başta/sonda boşlukları temizle)
        if (company.getEmail() != null) {
            updated.setEmail(company.getEmail().trim());
        } else {
            updated.setEmail(null);
        }
        
        updated.setAddress(company.getAddress());
        updated.setVatId(company.getVatId());
        updated.setCourtInfo(company.getCourtInfo());
        updated.setComment(company.getComment());
        updated.setTelephone(company.getTelephone());
        updated.setFax(company.getFax());
        
        companyService.save(updated);
        redirectAttributes.addFlashAttribute("message", "Unternehmen wurde erfolgreich aktualisiert.");
        redirectAttributes.addFlashAttribute("messageType", "success");
        return "redirect:/companies/" + id;
    }
    
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        // Önce firmaya bağlı tüm personelleri sil
        contactService.findByCompanyId(id).forEach(contact -> contactService.deleteById(contact.getId()));

        // Ardından firmayı sil
        companyService.deleteById(id);

        redirectAttributes.addFlashAttribute("message", "Unternehmen und zugehörige Kontakte wurden erfolgreich gelöscht.");
        redirectAttributes.addFlashAttribute("messageType", "success");
        return "redirect:/companies";
    }
}
