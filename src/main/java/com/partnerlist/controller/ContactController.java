package com.partnerlist.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
@RequestMapping("/contacts")
public class ContactController {
    
    @Autowired
    private ContactService contactService;
    
    @Autowired
    private CompanyService companyService;
    
    @Autowired
    private com.partnerlist.service.ContactAddressReaderService contactAddressReader;
    
    @GetMapping("/add")
    public String showAddForm(@RequestParam(required = false) String companyId, Model model) {
        model.addAttribute("contact", new Contact());
        model.addAttribute("contactAddress", "");
        model.addAttribute("companies", companyService.findAll());

        if (companyId != null && !companyId.isBlank() && !"null".equalsIgnoreCase(companyId)) {
            try {
                Long companyIdLong = Long.parseLong(companyId);
                model.addAttribute("selectedCompanyId", companyIdLong);
            } catch (NumberFormatException ignored) {
                // Geçersiz ID gelirse sadece seçim yapma, hata göstermeye gerek yok
            }
        }
        return "contact_form";
    }
    
    @PostMapping("/add")
    public String add(@ModelAttribute Contact contact,
                     @RequestParam(required = false) String companyId,
                     @RequestParam(value = "duplicateWarningShown", required = false) Boolean duplicateWarningShown,
                     Model model,
                     RedirectAttributes redirectAttributes) {
        if (companyId == null || companyId.isBlank() || "null".equalsIgnoreCase(companyId)) {
            redirectAttributes.addFlashAttribute("message", "Bitte wählen Sie ein Unternehmen aus.");
            redirectAttributes.addFlashAttribute("messageType", "error");
            return "redirect:/contacts/add";
        }

        Long companyIdLong;
        try {
            companyIdLong = Long.parseLong(companyId);
        } catch (NumberFormatException ex) {
            redirectAttributes.addFlashAttribute("message", "Ungültige Unternehmens-ID.");
            redirectAttributes.addFlashAttribute("messageType", "error");
            return "redirect:/contacts/add";
        }

        Optional<Company> company = companyService.findById(companyIdLong);
        if (company.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Unternehmen wurde nicht gefunden.");
            redirectAttributes.addFlashAttribute("messageType", "error");
            return "redirect:/companies";
        }
        
        contact.setCompany(company.get());
        
        // E-Mail-Feld trimmen (Leerzeichen am Anfang/Ende entfernen)
        if (contact.getEmail() != null) {
            contact.setEmail(contact.getEmail().trim());
        }
        
        // Dublettenprüfung: zuerst Warnfenster (Formular wird erneut angezeigt),
        // wenn der Benutzer erneut speichert, wird die eigentliche Speicherung ausgeführt.
        boolean warningAlreadyShown = Boolean.TRUE.equals(duplicateWarningShown);
        if (!warningAlreadyShown) {
            var duplicates = contactService.findPotentialDuplicates(contact);
            if (!duplicates.isEmpty()) {
                String name = contact.getPersonName() != null ? contact.getPersonName().trim() : null;
                String email = contact.getEmail() != null ? contact.getEmail().trim() : null;
                
                boolean hasExact = false;
                boolean hasPartial = false;
                
                for (Contact dup : duplicates) {
                    String dName = dup.getPersonName() != null ? dup.getPersonName().trim() : null;
                    String dEmail = dup.getEmail() != null ? dup.getEmail().trim() : null;
                    
                    boolean sameName = (name == null && dName == null) || (name != null && name.equalsIgnoreCase(dName));
                    boolean sameEmail = (email == null && dEmail == null) || (email != null && email.equalsIgnoreCase(dEmail));
                    
                    if (sameName && sameEmail) {
                        hasExact = true;
                    } else {
                        hasPartial = true;
                    }
                }
                
                model.addAttribute("contact", contact);
                model.addAttribute("contactAddress", contact.getAddress() != null ? contact.getAddress() : "");
                model.addAttribute("companies", companyService.findAll());
                model.addAttribute("selectedCompanyId", companyIdLong);
                model.addAttribute("duplicateContacts", duplicates);
                model.addAttribute("hasExactDuplicate", hasExact);
                model.addAttribute("hasPartialDuplicate", hasPartial);
                model.addAttribute("duplicateWarningShown", true);
                return "contact_form";
            }
        }
        
        contactService.save(contact);
        redirectAttributes.addFlashAttribute("message", "Personal wurde erfolgreich hinzugefügt.");
        redirectAttributes.addFlashAttribute("messageType", "success");
        return "redirect:/companies/" + companyIdLong;
    }
    
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        // Adres her zaman JDBC ile okunur; Contact entity kullanılmaz. Sebep: SQLite/JPA ile
        // satırda email=null iken entity'de address alanı yanlışlıkla null geliyor (sürücü/diyalekt davranışı).
        String contactAddress = contactAddressReader.getAddressByContactId(id);
        Optional<Contact> contact = contactService.findById(id);
        if (contact.isEmpty()) {
            return "redirect:/companies";
        }
        Contact c = contact.get();
        model.addAttribute("contact", c);
        model.addAttribute("contactAddress", contactAddress != null ? contactAddress : "");
        model.addAttribute("companies", companyService.findAll());
        return "contact_form";
    }
    
    @PostMapping("/{id}/edit")
    public String update(@PathVariable Long id,
                        @ModelAttribute Contact contact,
                        @RequestParam(required = false) String companyId,
                        RedirectAttributes redirectAttributes) {
        if (companyId == null || companyId.isBlank() || "null".equalsIgnoreCase(companyId)) {
            redirectAttributes.addFlashAttribute("message", "Bitte wählen Sie ein Unternehmen aus.");
            redirectAttributes.addFlashAttribute("messageType", "error");
            return "redirect:/contacts/" + id + "/edit";
        }

        Long companyIdLong;
        try {
            companyIdLong = Long.parseLong(companyId);
        } catch (NumberFormatException ex) {
            redirectAttributes.addFlashAttribute("message", "Ungültige Unternehmens-ID.");
            redirectAttributes.addFlashAttribute("messageType", "error");
            return "redirect:/contacts/" + id + "/edit";
        }
        Optional<Contact> existing = contactService.findById(id);
        if (existing.isEmpty()) {
            return "redirect:/companies";
        }
        
        Optional<Company> company = companyService.findById(companyIdLong);
        if (company.isEmpty()) {
            return "redirect:/companies";
        }
        
        Contact updated = existing.get();
        updated.setCompany(company.get());
        updated.setGender(contact.getGender());
        updated.setPersonName(contact.getPersonName());
        updated.setTitle(contact.getTitle());
        updated.setTelephone(contact.getTelephone());
        updated.setFax(contact.getFax());
        updated.setComment(contact.getComment());
        
        // E-Mail-Feld trimmen (Leerzeichen am Anfang/Ende entfernen)
        if (contact.getEmail() != null) {
            updated.setEmail(contact.getEmail().trim());
        } else {
            updated.setEmail(null);
        }
        
        // Adres: Form boş gönderildiyse mevcut adresi koru (gösterim hatası nedeniyle kayıp önleme)
        String submittedAddress = contact.getAddress();
        if (submittedAddress != null && !submittedAddress.isBlank()) {
            updated.setAddress(submittedAddress.trim());
        } else {
            String currentAddress = contactAddressReader.getAddressByContactId(id);
            updated.setAddress(currentAddress != null && !currentAddress.isBlank() ? currentAddress : null);
        }
        updated.setNumber(contact.getNumber());
        
        contactService.save(updated);
        redirectAttributes.addFlashAttribute("message", "Personal wurde erfolgreich aktualisiert.");
        redirectAttributes.addFlashAttribute("messageType", "success");
        return "redirect:/companies/" + companyIdLong;
    }
    
    /** Adres metni; ?debug=1 ile satırdaki sütun adları ve adres değeri döner (Sorun giderme). */
    @GetMapping(value = "/{id}/address", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> getContactAddress(
            @PathVariable Long id,
            @RequestParam(value = "debug", required = false) String debug) {
        try {
            String address = contactAddressReader.getAddressByContactId(id);
            if ("1".equals(debug)) {
                String debugInfo = contactAddressReader.getAddressDebugInfo(id);
                return ResponseEntity.ok().body(debugInfo);
            }
            String body = (address != null && !address.isBlank()) ? address : "(leer)";
            return ResponseEntity.ok().body(body);
        } catch (Exception e) {
            return ResponseEntity.ok().body("Fehler: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Optional<Contact> contact = contactService.findById(id);
        if (contact.isEmpty()) {
            return "redirect:/companies";
        }
        model.addAttribute("contact", contact.get());
        return "contact_detail";
    }
    
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Optional<Contact> contact = contactService.findById(id);
        if (contact.isPresent()) {
            Long companyId = contact.get().getCompany().getId();
            contactService.deleteById(id);
            redirectAttributes.addFlashAttribute("message", "Personal wurde erfolgreich gelöscht.");
            redirectAttributes.addFlashAttribute("messageType", "success");
            return "redirect:/companies/" + companyId;
        }
        return "redirect:/companies";
    }
}
