package com.partnerlist.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.partnerlist.model.Company;
import com.partnerlist.model.CompanyGroup;
import com.partnerlist.repository.CompanyGroupRepository;

@Service
@Transactional
public class CompanyGroupService {

    @Autowired
    private CompanyGroupRepository companyGroupRepository;

    @Autowired
    private CompanyService companyService;

    public List<CompanyGroup> findAll() {
        return companyGroupRepository.findAllByOrderByNameAsc();
    }

    public Optional<CompanyGroup> findById(Long id) {
        return companyGroupRepository.findById(id);
    }

    public CompanyGroup save(CompanyGroup group) {
        return companyGroupRepository.save(group);
    }

    public void deleteById(Long id) {
        companyGroupRepository.deleteById(id);
    }

    /**
     * Create a new group with the given name and company IDs.
     */
    public CompanyGroup create(String name, List<Long> companyIds) {
        CompanyGroup group = new CompanyGroup();
        group.setName(name != null ? name.trim() : "");
        group.setCompanies(resolveCompanies(companyIds));
        return companyGroupRepository.save(group);
    }

    /**
     * Update an existing group's name and companies.
     */
    public CompanyGroup update(Long id, String name, List<Long> companyIds) {
        Optional<CompanyGroup> opt = companyGroupRepository.findById(id);
        if (opt.isEmpty()) {
            return null;
        }
        CompanyGroup group = opt.get();
        group.setName(name != null ? name.trim() : "");
        group.setCompanies(resolveCompanies(companyIds));
        return companyGroupRepository.save(group);
    }

    private List<Company> resolveCompanies(List<Long> companyIds) {
        if (companyIds == null || companyIds.isEmpty()) {
            return new ArrayList<>();
        }
        List<Company> result = new ArrayList<>();
        for (Long id : companyIds) {
            companyService.findById(id).ifPresent(result::add);
        }
        return result;
    }
}
