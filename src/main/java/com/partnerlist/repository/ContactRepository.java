package com.partnerlist.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.partnerlist.model.Contact;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {
    
    List<Contact> findByCompany_Id(Long companyId);
    
    @Query("SELECT c FROM Contact c WHERE " +
           "LOWER(TRIM(COALESCE(c.personName, ''))) LIKE LOWER(CONCAT('%', :personName, '%'))")
    List<Contact> findByPersonNameContainingIgnoreCase(@Param("personName") String personName);
    
    @Query("SELECT c FROM Contact c WHERE " +
           "c.company.id = :companyId AND " +
           "LOWER(TRIM(COALESCE(c.personName, ''))) = LOWER(TRIM(:personName))")
    List<Contact> findByCompanyAndPersonNameExact(@Param("companyId") Long companyId,
                                                  @Param("personName") String personName);
    
    @Query("SELECT c FROM Contact c WHERE " +
           "LOWER(TRIM(COALESCE(c.email, ''))) LIKE LOWER(CONCAT('%', :email, '%'))")
    List<Contact> findByEmailContainingIgnoreCase(@Param("email") String email);
    
    @Query("SELECT c FROM Contact c WHERE " +
           "c.company.id = :companyId AND " +
           "LOWER(TRIM(COALESCE(c.email, ''))) = LOWER(TRIM(:email))")
    List<Contact> findByCompanyAndEmailExact(@Param("companyId") Long companyId,
                                             @Param("email") String email);
    
    @Query("SELECT c FROM Contact c WHERE " +
           "LOWER(TRIM(COALESCE(c.telephone, ''))) LIKE LOWER(CONCAT('%', :telephone, '%'))")
    List<Contact> findByTelephoneContainingIgnoreCase(@Param("telephone") String telephone);
    
    @Query("SELECT c FROM Contact c WHERE " +
           "LOWER(TRIM(COALESCE(c.address, ''))) LIKE LOWER(CONCAT('%', :address, '%'))")
    List<Contact> findByAddressContainingIgnoreCase(@Param("address") String address);
    
    @Query("SELECT c FROM Contact c WHERE " +
           "LOWER(TRIM(COALESCE(c.title, ''))) LIKE LOWER(CONCAT('%', :title, '%'))")
    List<Contact> findByTitleContainingIgnoreCase(@Param("title") String title);
    
    @Query("SELECT c FROM Contact c WHERE " +
           "LOWER(TRIM(COALESCE(c.personName, ''))) LIKE LOWER(CONCAT('%', :term, '%')) OR " +
           "LOWER(TRIM(COALESCE(c.email, ''))) LIKE LOWER(CONCAT('%', :term, '%')) OR " +
           "LOWER(TRIM(COALESCE(c.telephone, ''))) LIKE LOWER(CONCAT('%', :term, '%')) OR " +
           "LOWER(TRIM(COALESCE(c.address, ''))) LIKE LOWER(CONCAT('%', :term, '%')) OR " +
           "LOWER(TRIM(COALESCE(c.title, ''))) LIKE LOWER(CONCAT('%', :term, '%'))")
    List<Contact> searchAllFields(@Param("term") String term);
}
