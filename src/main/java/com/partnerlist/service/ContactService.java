package com.partnerlist.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.partnerlist.model.Contact;
import com.partnerlist.repository.ContactRepository;

@Service
@Transactional
public class ContactService {
    
    @Autowired
    private ContactRepository contactRepository;
    
    public List<Contact> findByCompanyId(Long companyId) {
        return contactRepository.findByCompany_Id(companyId);
    }
    
    public Optional<Contact> findById(Long id) {
        return contactRepository.findById(id);
    }
    
    /** Adres alanını entity'den bağımsız, doğrudan veritabanından native query ile okur. */
    public String getAddressByContactId(Long id) {
        return contactRepository.findAddressByIdNative(id).orElse("");
    }
    
    public Contact save(Contact contact) {
        return contactRepository.save(contact);
    }
    
    public void deleteById(Long id) {
        contactRepository.deleteById(id);
    }
    
    public List<Contact> search(String term, String field) {
        if (term == null || term.trim().isEmpty()) {
            return List.of();
        }
        
        term = term.trim();
        
        switch (field) {
            case "person_name":
                return contactRepository.findByPersonNameContainingIgnoreCase(term);
            case "email":
                return contactRepository.findByEmailContainingIgnoreCase(term);
            case "telephone":
                return contactRepository.findByTelephoneContainingIgnoreCase(term);
            case "address":
                return contactRepository.findByAddressContainingIgnoreCase(term);
            case "title":
                return contactRepository.findByTitleContainingIgnoreCase(term);
            case "all":
            default:
                return contactRepository.searchAllFields(term);
        }
    }
    
    public long count() {
        return contactRepository.count();
    }
    
    /**
     * Aynı firmada, aynı isim VEYA aynı e-posta ile kayıtlı personelleri döndürür.
     * İsim karşılaştırmasında küçük/büyük harf ve fazla boşluklar yok sayılır.
     */
    public List<Contact> findPotentialDuplicates(Contact contact) {
        if (contact == null || contact.getCompany() == null || contact.getCompany().getId() == null) {
            return List.of();
        }
        
        Long companyId = contact.getCompany().getId();
        String personName = normalize(contact.getPersonName());
        String email = normalize(contact.getEmail());
        
        if ((personName == null || personName.isEmpty()) &&
            (email == null || email.isEmpty())) {
            return List.of();
        }
        
        // Aynı firmadaki tüm personelleri getirip Java tarafında daha toleranslı karşılaştır
        List<Contact> allInCompany = contactRepository.findByCompany_Id(companyId);
        List<Contact> result = new ArrayList<>();
        
        for (Contact existing : allInCompany) {
            // Yeni kayıt henüz ID almamış olacak, ama yine de güvenlik için:
            if (contact.getId() != null && contact.getId().equals(existing.getId())) {
                continue;
            }
            
            String existingName = normalize(existing.getPersonName());
            String existingEmail = normalize(existing.getEmail());
            
            boolean sameName = personName != null && !personName.isEmpty()
                    && existingName != null && personName.equalsIgnoreCase(existingName);
            
            boolean sameEmail = email != null && !email.isEmpty()
                    && existingEmail != null && email.equalsIgnoreCase(existingEmail);
            
            if (sameName || sameEmail) {
                result.add(existing);
            }
        }
        
        return result.stream().distinct().toList();
    }
    
    private String normalize(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            return "";
        }
        // Birden fazla boşluğu tek boşluğa indir
        return trimmed.replaceAll("\\s+", " ");
    }
}
