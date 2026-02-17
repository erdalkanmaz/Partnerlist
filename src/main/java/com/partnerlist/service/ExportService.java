package com.partnerlist.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.partnerlist.model.Company;
import com.partnerlist.model.Contact;

@Service
public class ExportService {
    
    @Autowired
    private CompanyService companyService;
    
    public byte[] exportToExcel(List<Long> companyIds, String format) throws IOException {
        List<Company> companies = getCompanies(companyIds);
        Map<String, List<String>> formatConfig = parseFormat(format);
        List<String> companyFields = formatConfig.getOrDefault("company", List.of("alle"));
        List<String> contactFields = formatConfig.getOrDefault("contact", List.of());
        
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Partnerliste");
            
            int rowNum = 0;
            
            // Header satırı oluştur
            Row headerRow = sheet.createRow(rowNum++);
            int colIndex = 0;
            
            // Şirket alanları için header
            if (shouldIncludeField(companyFields, "name")) {
                headerRow.createCell(colIndex++).setCellValue("Firma");
            }
            if (shouldIncludeField(companyFields, "telephone")) {
                headerRow.createCell(colIndex++).setCellValue("Telefon");
            }
            if (shouldIncludeField(companyFields, "fax")) {
                headerRow.createCell(colIndex++).setCellValue("Fax");
            }
            if (shouldIncludeField(companyFields, "email")) {
                headerRow.createCell(colIndex++).setCellValue("E-Mail");
            }
            if (shouldIncludeField(companyFields, "web")) {
                headerRow.createCell(colIndex++).setCellValue("Web");
            }
            if (shouldIncludeField(companyFields, "vatId")) {
                headerRow.createCell(colIndex++).setCellValue("Umsatzsteuer-ID");
            }
            if (shouldIncludeField(companyFields, "courtInfo")) {
                headerRow.createCell(colIndex++).setCellValue("Amtsgericht");
            }
            if (shouldIncludeField(companyFields, "address")) {
                headerRow.createCell(colIndex++).setCellValue("Adresse");
            }
            
            // Personel alanları için header (eğer personel gösterilecekse)
            boolean showContacts = contactFields != null && !contactFields.isEmpty();
            if (showContacts) {
                if (shouldIncludeField(contactFields, "name")) {
                    headerRow.createCell(colIndex++).setCellValue("Person");
                }
                if (shouldIncludeField(contactFields, "telephone")) {
                    headerRow.createCell(colIndex++).setCellValue("Person Telefon");
                }
                if (shouldIncludeField(contactFields, "email")) {
                    headerRow.createCell(colIndex++).setCellValue("Person E-Mail");
                }
                if (shouldIncludeField(contactFields, "address")) {
                    headerRow.createCell(colIndex++).setCellValue("Person Adresse");
                }
            }
            
            // Veri satırları
            for (Company company : companies) {
                if (company.getContacts() != null && !company.getContacts().isEmpty() && showContacts) {
                    // Her personel için ayrı satır
                    for (Contact contact : company.getContacts()) {
                        Row dataRow = sheet.createRow(rowNum++);
                        colIndex = 0;
                        
                        // Şirket bilgileri
                        if (shouldIncludeField(companyFields, "name")) {
                            dataRow.createCell(colIndex++).setCellValue(company.getName());
                        }
                        if (shouldIncludeField(companyFields, "telephone")) {
                            dataRow.createCell(colIndex++).setCellValue(company.getTelephone() != null ? company.getTelephone() : "");
                        }
                        if (shouldIncludeField(companyFields, "fax")) {
                            dataRow.createCell(colIndex++).setCellValue(company.getFax() != null ? company.getFax() : "");
                        }
                        if (shouldIncludeField(companyFields, "email")) {
                            dataRow.createCell(colIndex++).setCellValue(company.getEmail());
                        }
                        if (shouldIncludeField(companyFields, "web")) {
                            dataRow.createCell(colIndex++).setCellValue(company.getWebUrl());
                        }
                        if (shouldIncludeField(companyFields, "vatId")) {
                            dataRow.createCell(colIndex++).setCellValue(company.getVatId() != null ? company.getVatId() : "");
                        }
                        if (shouldIncludeField(companyFields, "courtInfo")) {
                            dataRow.createCell(colIndex++).setCellValue(company.getCourtInfo() != null ? company.getCourtInfo() : "");
                        }
                        if (shouldIncludeField(companyFields, "address")) {
                            dataRow.createCell(colIndex++).setCellValue(company.getAddress());
                        }
                        
                        // Personel bilgileri
                        if (shouldIncludeField(contactFields, "name")) {
                            dataRow.createCell(colIndex++).setCellValue(contact.getPersonName() != null ? contact.getPersonName() : "");
                        }
                        if (shouldIncludeField(contactFields, "telephone")) {
                            dataRow.createCell(colIndex++).setCellValue(contact.getTelephone() != null ? contact.getTelephone() : "");
                        }
                        if (shouldIncludeField(contactFields, "email")) {
                            dataRow.createCell(colIndex++).setCellValue(contact.getEmail() != null ? contact.getEmail() : "");
                        }
                        if (shouldIncludeField(contactFields, "address")) {
                            dataRow.createCell(colIndex++).setCellValue(contact.getAddress() != null ? contact.getAddress() : company.getAddress());
                        }
                    }
                } else {
                    // Sadece şirket bilgileri
                    Row dataRow = sheet.createRow(rowNum++);
                    colIndex = 0;
                    
                    if (shouldIncludeField(companyFields, "name")) {
                        dataRow.createCell(colIndex++).setCellValue(company.getName());
                    }
                    if (shouldIncludeField(companyFields, "telephone")) {
                        dataRow.createCell(colIndex++).setCellValue(company.getTelephone() != null ? company.getTelephone() : "");
                    }
                    if (shouldIncludeField(companyFields, "fax")) {
                        dataRow.createCell(colIndex++).setCellValue(company.getFax() != null ? company.getFax() : "");
                    }
                    if (shouldIncludeField(companyFields, "email")) {
                        dataRow.createCell(colIndex++).setCellValue(company.getEmail());
                    }
                    if (shouldIncludeField(companyFields, "web")) {
                        dataRow.createCell(colIndex++).setCellValue(company.getWebUrl());
                    }
                    if (shouldIncludeField(companyFields, "vatId")) {
                        dataRow.createCell(colIndex++).setCellValue(company.getVatId() != null ? company.getVatId() : "");
                    }
                    if (shouldIncludeField(companyFields, "courtInfo")) {
                        dataRow.createCell(colIndex++).setCellValue(company.getCourtInfo() != null ? company.getCourtInfo() : "");
                    }
                    if (shouldIncludeField(companyFields, "address")) {
                        dataRow.createCell(colIndex++).setCellValue(company.getAddress());
                    }
                }
            }
            
            // Sütun genişliklerini ayarla
            for (int i = 0; i < colIndex; i++) {
                sheet.autoSizeColumn(i);
            }
            
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }
    
    public byte[] exportToCSV(List<Long> companyIds, String format) throws IOException {
        List<Company> companies = getCompanies(companyIds);
        Map<String, List<String>> formatConfig = parseFormat(format);
        List<String> companyFields = formatConfig.getOrDefault("company", List.of("alle"));
        List<String> contactFields = formatConfig.getOrDefault("contact", List.of());
        
        StringBuilder csv = new StringBuilder();
        
        // BOM for Excel UTF-8 support
        csv.append("\uFEFF");
        
        // Header satırı oluştur
        List<String> headers = new ArrayList<>();
        
        // Şirket alanları için header
        if (shouldIncludeField(companyFields, "name")) {
            headers.add("Firma");
        }
        if (shouldIncludeField(companyFields, "telephone")) {
            headers.add("Telefon");
        }
        if (shouldIncludeField(companyFields, "fax")) {
            headers.add("Fax");
        }
        if (shouldIncludeField(companyFields, "email")) {
            headers.add("E-Mail");
        }
        if (shouldIncludeField(companyFields, "web")) {
            headers.add("Web");
        }
        if (shouldIncludeField(companyFields, "vatId")) {
            headers.add("Umsatzsteuer-ID");
        }
        if (shouldIncludeField(companyFields, "courtInfo")) {
            headers.add("Amtsgericht");
        }
        if (shouldIncludeField(companyFields, "address")) {
            headers.add("Adresse");
        }
        
        // Personel alanları için header
        boolean showContacts = contactFields != null && !contactFields.isEmpty();
        if (showContacts) {
            if (shouldIncludeField(contactFields, "name")) {
                headers.add("Person");
            }
            if (shouldIncludeField(contactFields, "telephone")) {
                headers.add("Person Telefon");
            }
            if (shouldIncludeField(contactFields, "email")) {
                headers.add("Person E-Mail");
            }
            if (shouldIncludeField(contactFields, "address")) {
                headers.add("Person Adresse");
            }
        }
        
        // Header satırını CSV'ye ekle
        csv.append(String.join(";", headers)).append("\n");
        
        // Veri satırları
        for (Company company : companies) {
            if (company.getContacts() != null && !company.getContacts().isEmpty() && showContacts) {
                // Her personel için ayrı satır
                for (Contact contact : company.getContacts()) {
                    List<String> rowData = new ArrayList<>();
                    
                    // Şirket bilgileri
                    if (shouldIncludeField(companyFields, "name")) {
                        rowData.add(escapeCSV(company.getName()));
                    }
                    if (shouldIncludeField(companyFields, "telephone")) {
                        rowData.add(escapeCSV(company.getTelephone() != null ? company.getTelephone() : ""));
                    }
                    if (shouldIncludeField(companyFields, "fax")) {
                        rowData.add(escapeCSV(company.getFax() != null ? company.getFax() : ""));
                    }
                    if (shouldIncludeField(companyFields, "email")) {
                        rowData.add(escapeCSV(company.getEmail()));
                    }
                    if (shouldIncludeField(companyFields, "web")) {
                        rowData.add(escapeCSV(company.getWebUrl()));
                    }
                    if (shouldIncludeField(companyFields, "vatId")) {
                        rowData.add(escapeCSV(company.getVatId() != null ? company.getVatId() : ""));
                    }
                    if (shouldIncludeField(companyFields, "courtInfo")) {
                        rowData.add(escapeCSV(company.getCourtInfo() != null ? company.getCourtInfo() : ""));
                    }
                    if (shouldIncludeField(companyFields, "address")) {
                        rowData.add(escapeCSV(company.getAddress()));
                    }
                    
                    // Personel bilgileri
                    if (shouldIncludeField(contactFields, "name")) {
                        rowData.add(escapeCSV(contact.getPersonName() != null ? contact.getPersonName() : ""));
                    }
                    if (shouldIncludeField(contactFields, "telephone")) {
                        rowData.add(escapeCSV(contact.getTelephone() != null ? contact.getTelephone() : ""));
                    }
                    if (shouldIncludeField(contactFields, "email")) {
                        rowData.add(escapeCSV(contact.getEmail() != null ? contact.getEmail() : ""));
                    }
                    if (shouldIncludeField(contactFields, "address")) {
                        rowData.add(escapeCSV(contact.getAddress() != null ? contact.getAddress() : company.getAddress()));
                    }
                    
                    csv.append(String.join(";", rowData)).append("\n");
                }
            } else {
                // Sadece şirket bilgileri
                List<String> rowData = new ArrayList<>();
                
                if (shouldIncludeField(companyFields, "name")) {
                    rowData.add(escapeCSV(company.getName()));
                }
                if (shouldIncludeField(companyFields, "telephone")) {
                    rowData.add(escapeCSV(company.getTelephone() != null ? company.getTelephone() : ""));
                }
                if (shouldIncludeField(companyFields, "fax")) {
                    rowData.add(escapeCSV(company.getFax() != null ? company.getFax() : ""));
                }
                if (shouldIncludeField(companyFields, "email")) {
                    rowData.add(escapeCSV(company.getEmail()));
                }
                if (shouldIncludeField(companyFields, "web")) {
                    rowData.add(escapeCSV(company.getWebUrl()));
                }
                if (shouldIncludeField(companyFields, "vatId")) {
                    rowData.add(escapeCSV(company.getVatId() != null ? company.getVatId() : ""));
                }
                if (shouldIncludeField(companyFields, "courtInfo")) {
                    rowData.add(escapeCSV(company.getCourtInfo() != null ? company.getCourtInfo() : ""));
                }
                if (shouldIncludeField(companyFields, "address")) {
                    rowData.add(escapeCSV(company.getAddress()));
                }
                
                csv.append(String.join(";", rowData)).append("\n");
            }
        }
        
        return csv.toString().getBytes("UTF-8");
    }
    
    private List<Company> getCompanies(List<Long> companyIds) {
        if (companyIds == null || companyIds.isEmpty()) {
            return companyService.findAll();
        }
        
        return companyIds.stream()
                .map(companyService::findById)
                .filter(java.util.Optional::isPresent)
                .map(java.util.Optional::get)
                .peek(comp -> comp.getContacts().size()) // Lazy load
                .collect(java.util.stream.Collectors.toList());
    }
    
    private void createNameOnlyRow(Sheet sheet, int rowNum, Company company) {
        Row row = sheet.createRow(rowNum);
        Cell cell = row.createCell(0);
        cell.setCellValue(company.getName());
    }
    
    private void createSummaryRow(Sheet sheet, int rowNum, Company company) {
        Row row = sheet.createRow(rowNum);
        row.createCell(0).setCellValue(company.getName());
        row.createCell(1).setCellValue(company.getEmail());
        row.createCell(2).setCellValue(company.getWebUrl());
        
        if (company.getContacts() != null && !company.getContacts().isEmpty()) {
            StringBuilder contacts = new StringBuilder();
            for (Contact contact : company.getContacts()) {
                if (contact.getPersonName() != null) {
                    if (contacts.length() > 0) contacts.append(", ");
                    contacts.append(contact.getPersonName());
                }
            }
            row.createCell(3).setCellValue(contacts.toString());
        }
    }
    
    private void createCompanyOnlyRows(Sheet sheet, int rowNum, Company company) {
        Row headerRow = sheet.createRow(rowNum);
        headerRow.createCell(0).setCellValue("Firma");
        headerRow.createCell(1).setCellValue("E-Mail");
        headerRow.createCell(2).setCellValue("Web");
        headerRow.createCell(3).setCellValue("Adresse");
        headerRow.createCell(4).setCellValue("Umsatzsteuer-ID");
        headerRow.createCell(5).setCellValue("Amtsgericht");
        headerRow.createCell(6).setCellValue("Kommentar");
        
        Row dataRow = sheet.createRow(rowNum + 1);
        dataRow.createCell(0).setCellValue(company.getName());
        dataRow.createCell(1).setCellValue(company.getEmail());
        dataRow.createCell(2).setCellValue(company.getWebUrl());
        dataRow.createCell(3).setCellValue(company.getAddress());
        if (company.getVatId() != null) dataRow.createCell(4).setCellValue(company.getVatId());
        if (company.getCourtInfo() != null) dataRow.createCell(5).setCellValue(company.getCourtInfo());
        if (company.getComment() != null) dataRow.createCell(6).setCellValue(company.getComment());
    }
    
    private int createFullRows(Sheet sheet, int startRow, Company company) {
        int rowNum = startRow;
        
        // Header
        Row headerRow = sheet.createRow(rowNum++);
        headerRow.createCell(0).setCellValue("Firma");
        headerRow.createCell(1).setCellValue("E-Mail");
        headerRow.createCell(2).setCellValue("Web");
        headerRow.createCell(3).setCellValue("Adresse");
        headerRow.createCell(4).setCellValue("Umsatzsteuer-ID");
        headerRow.createCell(5).setCellValue("Amtsgericht");
        headerRow.createCell(6).setCellValue("Kommentar");
        headerRow.createCell(7).setCellValue("Person");
        headerRow.createCell(8).setCellValue("Gender");
        headerRow.createCell(9).setCellValue("Titel");
        headerRow.createCell(10).setCellValue("Telefon");
        headerRow.createCell(11).setCellValue("Fax");
        headerRow.createCell(12).setCellValue("E-Mail");
        headerRow.createCell(13).setCellValue("Adresse");
        
        // Company data row
        Row companyRow = sheet.createRow(rowNum++);
        companyRow.createCell(0).setCellValue(company.getName());
        companyRow.createCell(1).setCellValue(company.getEmail());
        companyRow.createCell(2).setCellValue(company.getWebUrl());
        companyRow.createCell(3).setCellValue(company.getAddress());
        if (company.getVatId() != null) companyRow.createCell(4).setCellValue(company.getVatId());
        if (company.getCourtInfo() != null) companyRow.createCell(5).setCellValue(company.getCourtInfo());
        if (company.getComment() != null) companyRow.createCell(6).setCellValue(company.getComment());
        
        // Contacts
        if (company.getContacts() != null && !company.getContacts().isEmpty()) {
            for (Contact contact : company.getContacts()) {
                Row contactRow = sheet.createRow(rowNum++);
                contactRow.createCell(7).setCellValue(contact.getPersonName() != null ? contact.getPersonName() : "");
                contactRow.createCell(8).setCellValue(contact.getGender() != null ? contact.getGender() : "");
                contactRow.createCell(9).setCellValue(contact.getTitle() != null ? contact.getTitle() : "");
                contactRow.createCell(10).setCellValue(contact.getTelephone() != null ? contact.getTelephone() : "");
                contactRow.createCell(11).setCellValue(contact.getFax() != null ? contact.getFax() : "");
                contactRow.createCell(12).setCellValue(contact.getEmail() != null ? contact.getEmail() : "");
                contactRow.createCell(13).setCellValue(contact.getAddress() != null ? contact.getAddress() : company.getAddress());
            }
        }
        
        // Empty row between companies
        rowNum++;
        
        return rowNum;
    }
    
    private String escapeCSV(String value) {
        if (value == null) return "";
        if (value.contains(";") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
    
    private Map<String, List<String>> parseFormat(String format) {
        Map<String, List<String>> config = new HashMap<>();
        
        // Eski format string'leri için geriye dönük uyumluluk
        if (format == null || format.isEmpty() || format.equals("full")) {
            config.put("company", List.of("alle", "name", "telephone", "fax", "email", "web", "vatId", "courtInfo", "address"));
            config.put("contact", List.of("alle", "name", "telephone", "email", "address"));
            return config;
        }
        
        if (format.equals("names")) {
            config.put("company", List.of("name"));
            config.put("contact", List.of());
            return config;
        }
        
        if (format.equals("summary")) {
            config.put("company", List.of("name", "email", "web"));
            config.put("contact", List.of("name"));
            return config;
        }
        
        if (format.equals("company-only")) {
            config.put("company", List.of("alle", "name", "telephone", "fax", "email", "web", "vatId", "courtInfo", "address"));
            config.put("contact", List.of());
            return config;
        }
        
        // Yeni JSON formatını parse et
        try {
            ObjectMapper mapper = new ObjectMapper();
            @SuppressWarnings("unchecked")
            Map<String, Object> jsonMap = mapper.readValue(format, Map.class);
            
            @SuppressWarnings("unchecked")
            List<String> companyFields = (List<String>) jsonMap.getOrDefault("company", List.of("alle"));
            @SuppressWarnings("unchecked")
            List<String> contactFields = (List<String>) jsonMap.getOrDefault("contact", List.of("alle"));
            
            config.put("company", companyFields);
            config.put("contact", contactFields);
        } catch (Exception e) {
            // JSON parse hatası durumunda varsayılan değerleri kullan
            config.put("company", List.of("alle", "name", "telephone", "fax", "email", "web", "vatId", "courtInfo", "address"));
            config.put("contact", List.of("alle", "name", "telephone", "email", "address"));
        }
        
        return config;
    }
    
    private boolean shouldIncludeField(List<String> fields, String fieldName) {
        if (fields == null || fields.isEmpty()) return false;
        if (fields.contains("alle")) return true;
        return fields.contains(fieldName);
    }
}
