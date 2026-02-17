package com.partnerlist.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.partnerlist.model.Company;
import com.partnerlist.repository.CompanyRepository;

@Service
@Transactional
public class CompanyService {
    
    @Autowired
    private CompanyRepository companyRepository;
    
    public List<Company> findAll() {
        return companyRepository.findAllByOrderByNameAsc();
    }
    
    public Optional<Company> findById(Long id) {
        return companyRepository.findById(id);
    }
    
    public Company save(Company company) {
        return companyRepository.save(company);
    }
    
    public void deleteById(Long id) {
        companyRepository.deleteById(id);
    }
    
    public List<Company> searchByName(String term) {
        if (term == null || term.trim().isEmpty()) {
            return findAll();
        }
        
        term = term.trim();
        return companyRepository.findByNameContainingIgnoreCase(term);
    }
    
    public List<Company> searchByEmail(String term) {
        if (term == null || term.trim().isEmpty()) {
            return List.of();
        }
        
        term = term.trim();
        return companyRepository.findByEmailContainingIgnoreCase(term);
    }
    
    public List<Company> searchByAddress(String term) {
        if (term == null || term.trim().isEmpty()) {
            return List.of();
        }
        
        term = term.trim();
        return companyRepository.findByAddressContainingIgnoreCase(term);
    }
    
    public long count() {
        return companyRepository.count();
    }
    
    public List<Company> findRecent(int limit) {
        List<Company> all = companyRepository.findAll();
        return all.stream()
                .sorted((a, b) -> {
                    if (a.getCreatedAt() == null || b.getCreatedAt() == null) {
                        return 0;
                    }
                    return b.getCreatedAt().compareTo(a.getCreatedAt());
                })
                .limit(limit)
                .toList();
    }
    
    /**
     * Basit bir \"olası mükerrer\" kontrolü.
     * Aynı isim, e-posta veya adres'e sahip firmaları döndürür.
     */
    public List<Company> findPotentialDuplicates(Company company) {
        if (company == null) {
            return List.of();
        }
        
        String name = company.getName() != null ? company.getName().trim() : null;
        String email = company.getEmail() != null ? company.getEmail().trim() : null;
        String address = company.getAddress() != null ? company.getAddress().trim() : null;
        
        if ((name == null || name.isEmpty()) &&
            (email == null || email.isEmpty()) &&
            (address == null || address.isEmpty())) {
            return List.of();
        }
        
        List<Company> result = new ArrayList<>();
        
        if (name != null && !name.isEmpty()) {
            result.addAll(companyRepository.findByNameExactIgnoreCase(name));
        }
        if (email != null && !email.isEmpty()) {
            result.addAll(companyRepository.findByEmailExactIgnoreCase(email));
        }
        if (address != null && !address.isEmpty()) {
            result.addAll(companyRepository.findByAddressExactIgnoreCase(address));
        }
        
        // Tekrarlıları temizle
        return result.stream()
                .distinct()
                .toList();
    }
}
