package com.partnerlist.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.partnerlist.model.CompanyGroup;

@Repository
public interface CompanyGroupRepository extends JpaRepository<CompanyGroup, Long> {

    List<CompanyGroup> findAllByOrderByNameAsc();
}
