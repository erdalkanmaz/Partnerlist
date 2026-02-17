package com.partnerlist.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.partnerlist.model.Company;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {
    
    @Query("SELECT c FROM Company c WHERE " +
           "LOWER(TRIM(c.name)) LIKE LOWER(CONCAT('%', :name, '%')) " +
           "ORDER BY LOWER(c.name) ASC")
    List<Company> findByNameContainingIgnoreCase(@Param("name") String name);
    
    @Query("SELECT c FROM Company c WHERE LOWER(TRIM(c.name)) = LOWER(TRIM(:name))")
    List<Company> findByNameExactIgnoreCase(@Param("name") String name);
    
    @Query("SELECT c FROM Company c WHERE LOWER(TRIM(COALESCE(c.email, ''))) = LOWER(TRIM(:email))")
    List<Company> findByEmailExactIgnoreCase(@Param("email") String email);
    
    @Query("SELECT c FROM Company c WHERE LOWER(TRIM(COALESCE(c.address, ''))) = LOWER(TRIM(:address))")
    List<Company> findByAddressExactIgnoreCase(@Param("address") String address);
    
    @Query("SELECT c FROM Company c WHERE " +
           "LOWER(TRIM(c.email)) LIKE LOWER(CONCAT('%', :email, '%'))")
    List<Company> findByEmailContainingIgnoreCase(@Param("email") String email);
    
    @Query("SELECT c FROM Company c WHERE " +
           "LOWER(TRIM(c.address)) LIKE LOWER(CONCAT('%', :address, '%'))")
    List<Company> findByAddressContainingIgnoreCase(@Param("address") String address);
    
    @Query("SELECT c FROM Company c WHERE " +
           "LOWER(TRIM(c.name)) LIKE LOWER(CONCAT('%', :term, '%')) OR " +
           "LOWER(TRIM(c.email)) LIKE LOWER(CONCAT('%', :term, '%')) OR " +
           "LOWER(TRIM(c.address)) LIKE LOWER(CONCAT('%', :term, '%')) OR " +
           "LOWER(TRIM(c.webUrl)) LIKE LOWER(CONCAT('%', :term, '%')) OR " +
           "LOWER(TRIM(COALESCE(c.vatId, ''))) LIKE LOWER(CONCAT('%', :term, '%')) OR " +
           "LOWER(TRIM(COALESCE(c.courtInfo, ''))) LIKE LOWER(CONCAT('%', :term, '%'))")
    List<Company> searchAllFields(@Param("term") String term);
    
    @Query("SELECT c FROM Company c ORDER BY LOWER(c.name) ASC")
    List<Company> findAllByOrderByNameAsc();
}
