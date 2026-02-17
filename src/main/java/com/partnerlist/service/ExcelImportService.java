package com.partnerlist.service;

import com.partnerlist.model.Company;
import com.partnerlist.model.Contact;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
public class ExcelImportService {
    
    @Autowired
    private CompanyService companyService;
    
    @Autowired
    private ContactService contactService;
    
    @Transactional
    public Map<String, Object> importFromExcel(MultipartFile file) throws IOException {
        Map<String, Object> result = new HashMap<>();
        int importedCompanies = 0;
        int importedContacts = 0;
        List<String> errors = new ArrayList<>();
        int processedRows = 0;
        int skippedRows = 0;
        
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            
            if (sheet.getPhysicalNumberOfRows() < 2) {
                result.put("success", false);
                result.put("message", "Die Excel-Datei ist leer oder enthält nicht genügend Zeilen.");
                return result;
            }
            
            // Başlık satırını oku
            Row headerRow = sheet.getRow(0);
            if (headerRow == null) {
                result.put("success", false);
                result.put("message", "Die Excel-Datei enthält keine Kopfzeile.");
                return result;
            }
            
            Map<String, Integer> columnMap = mapColumns(headerRow);
            
            // Zorunlu sütunları kontrol et
            if (!hasRequiredColumns(columnMap)) {
                // Debug: Hangi sütunların bulunduğunu göster
                StringBuilder foundColumns = new StringBuilder();
                columnMap.forEach((key, value) -> foundColumns.append(key).append("(").append(value).append("), "));
                result.put("success", false);
                result.put("message", "Erforderliche Spalten wurden nicht gefunden: Partner/Agent (Firma), Web, e-mail, Adress. Gefundene Spalten: " + foundColumns.toString());
                return result;
            }
            
            Company currentCompany = null;
            String currentCompanyName = null;
            
            // Verileri oku
            int totalRows = sheet.getLastRowNum();
            
            for (int i = 1; i <= totalRows; i++) {
                Row row = sheet.getRow(i);
                if (row == null) {
                    skippedRows++;
                    continue;
                }
                
                try {
                    // Firma adını al
                    String companyName = getCellValue(row, columnMap.get("company_name"));
                    if (companyName != null && !companyName.trim().isEmpty()) {
                        currentCompanyName = companyName.trim();
                    }
                    
                    if (currentCompanyName == null || currentCompanyName.isEmpty()) {
                        skippedRows++;
                        continue; // Firma adı olmayan satırları atla
                    }
                    
                    processedRows++;
                    
                    // Firma bilgilerini al
                    String webUrl = getCellValue(row, columnMap.get("web_url"));
                    String email = getCellValue(row, columnMap.get("email"));
                    String address = getCellValue(row, columnMap.get("address"));
                    
                    // Zorunlu alanları kontrol et
                    if ((webUrl == null || webUrl.trim().isEmpty()) ||
                        (email == null || email.trim().isEmpty()) ||
                        (address == null || address.trim().isEmpty())) {
                        
                        // Mevcut firmadan bilgileri al
                        if (currentCompany != null) {
                            webUrl = webUrl != null && !webUrl.trim().isEmpty() ? webUrl : currentCompany.getWebUrl();
                            email = email != null && !email.trim().isEmpty() ? email : currentCompany.getEmail();
                            address = address != null && !address.trim().isEmpty() ? address : currentCompany.getAddress();
                        } else {
                            errors.add("Zeile " + (i + 1) + ": Erforderliche Felder fehlen");
                            continue;
                        }
                    }
                    
                    // Firma var mı kontrol et, yoksa oluştur
                    if (currentCompany == null || !currentCompany.getName().equals(currentCompanyName)) {
                        // Lambda içinde kullanılabilmesi için final kopya
                        final String companyNameFinal = currentCompanyName;
                        Optional<Company> existing = companyService.findAll().stream()
                                .filter(c -> c.getName().equals(companyNameFinal))
                                .findFirst();
                        
                        if (existing.isPresent()) {
                            currentCompany = existing.get();
                        } else {
                            currentCompany = new Company();
                            currentCompany.setName(currentCompanyName);
                            currentCompany.setWebUrl(webUrl.trim());
                            currentCompany.setEmail(email.trim());
                            currentCompany.setAddress(address.trim());
                            
                            // Opsiyonel alanlar
                            String vatId = getCellValue(row, columnMap.get("vat_id"));
                            if (vatId != null && !vatId.trim().isEmpty()) {
                                currentCompany.setVatId(vatId.trim());
                            }
                            
                            String courtInfo = getCellValue(row, columnMap.get("court_info"));
                            if (courtInfo != null && !courtInfo.trim().isEmpty()) {
                                currentCompany.setCourtInfo(courtInfo.trim());
                            }
                            
                            String comment = getCellValue(row, columnMap.get("comment"));
                            if (comment != null && !comment.trim().isEmpty()) {
                                currentCompany.setComment(comment.trim());
                            }
                            
                            currentCompany = companyService.save(currentCompany);
                            importedCompanies++;
                        }
                    }
                    
                    // Personel bilgilerini al
                    String personName = getCellValue(row, columnMap.get("person_name"));
                    if (personName != null && !personName.trim().isEmpty()) {
                        Contact contact = new Contact();
                        contact.setCompany(currentCompany);
                        contact.setPersonName(personName.trim());
                        
                        String number = getCellValue(row, columnMap.get("number"));
                        if (number != null && !number.trim().isEmpty()) {
                            try {
                                contact.setNumber(Integer.parseInt(number.trim()));
                            } catch (NumberFormatException e) {
                                // Ignore
                            }
                        }
                        
                        String gender = getCellValue(row, columnMap.get("gender"));
                        if (gender != null && !gender.trim().isEmpty()) {
                            contact.setGender(gender.trim());
                        }
                        
                        String title = getCellValue(row, columnMap.get("title"));
                        if (title != null && !title.trim().isEmpty()) {
                            contact.setTitle(title.trim());
                        }
                        
                        String telephone = getCellValue(row, columnMap.get("telephone"));
                        if (telephone != null && !telephone.trim().isEmpty()) {
                            contact.setTelephone(telephone.trim());
                        }
                        
                        String fax = getCellValue(row, columnMap.get("fax"));
                        if (fax != null && !fax.trim().isEmpty()) {
                            contact.setFax(fax.trim());
                        }
                        
                        String contactEmail = getCellValue(row, columnMap.get("email"));
                        if (contactEmail != null && !contactEmail.trim().isEmpty() && 
                            !contactEmail.trim().equals(currentCompany.getEmail())) {
                            contact.setEmail(contactEmail.trim());
                        }
                        
                        String contactAddress = getCellValue(row, columnMap.get("address"));
                        if (contactAddress != null && !contactAddress.trim().isEmpty() && 
                            !contactAddress.trim().equals(currentCompany.getAddress())) {
                            contact.setAddress(contactAddress.trim());
                        }
                        
                        contactService.save(contact);
                        importedContacts++;
                    }
                    
                } catch (Exception e) {
                    errors.add("Zeile " + (i + 1) + ": " + e.getMessage());
                }
            }
        }
        
        result.put("success", true);
        result.put("importedCompanies", importedCompanies);
        result.put("importedContacts", importedContacts);
        result.put("errors", errors);
        
        // Eğer hiçbir firma import edilmediyse, daha detaylı mesaj ver
        if (importedCompanies == 0 && importedContacts == 0) {
            result.put("message", String.format("Import abgeschlossen: Keine Daten gefunden. Verarbeitete Zeilen: %d, Übersprungene Zeilen: %d. Bitte überprüfen Sie, ob die Excel-Datei Daten enthält und die Spaltenüberschriften korrekt sind.", 
                    processedRows, skippedRows));
        } else {
            result.put("message", String.format("Import abgeschlossen: %d Unternehmen, %d Kontakte hinzugefügt.", 
                    importedCompanies, importedContacts));
        }
        
        return result;
    }
    
    private Map<String, Integer> mapColumns(Row headerRow) {
        Map<String, Integer> columnMap = new HashMap<>();
        
        for (Cell cell : headerRow) {
            String header = getCellValue(headerRow, cell.getColumnIndex());
            if (header == null) continue;
            
            String headerLower = header.toLowerCase().trim();
            
            if (headerLower.contains("partner") || headerLower.contains("agent") || headerLower.contains("firma")) {
                columnMap.put("company_name", cell.getColumnIndex());
            } else if (headerLower.contains("gender") || headerLower.contains("cinsiyet")) {
                columnMap.put("gender", cell.getColumnIndex());
            } else if (headerLower.contains("person") || headerLower.contains("kişi")) {
                columnMap.put("person_name", cell.getColumnIndex());
            } else if (headerLower.contains("title") || headerLower.contains("ünvan") || headerLower.contains("pozisyon")) {
                columnMap.put("title", cell.getColumnIndex());
            } else if (headerLower.contains("telephone") || headerLower.contains("telefon") || headerLower.contains("tel")) {
                columnMap.put("telephone", cell.getColumnIndex());
            } else if (headerLower.contains("fax")) {
                columnMap.put("fax", cell.getColumnIndex());
            } else if (headerLower.contains("e-mail") || headerLower.contains("email") || headerLower.contains("eposta")) {
                columnMap.put("email", cell.getColumnIndex());
            } else if (headerLower.contains("web") || headerLower.contains("website")) {
                columnMap.put("web_url", cell.getColumnIndex());
            } else if (headerLower.contains("umsatzsteuer") || headerLower.contains("vat") || headerLower.contains("kdv")) {
                columnMap.put("vat_id", cell.getColumnIndex());
            } else if (headerLower.contains("amtsgericht") || headerLower.contains("court") || headerLower.contains("mahkeme")) {
                columnMap.put("court_info", cell.getColumnIndex());
            } else if (headerLower.contains("adress") || headerLower.contains("address") || headerLower.contains("adres")) {
                columnMap.put("address", cell.getColumnIndex());
            } else if (headerLower.contains("kommentar") || headerLower.contains("comment") || headerLower.contains("yorum")) {
                columnMap.put("comment", cell.getColumnIndex());
            } else if (headerLower.contains("nr") || headerLower.contains("numara") || headerLower.contains("no")) {
                columnMap.put("number", cell.getColumnIndex());
            }
        }
        
        // Eğer beklenen zorunlu sütunlar hâlâ yoksa, bilinen şablona göre indeksleri zorla ayarla
        // 0: Nr., 1: Partner/Agent, 7: e-mail, 8: Web, 11: Adress
        if (!hasRequiredColumns(columnMap)) {
            columnMap.putIfAbsent("company_name", 1);
            columnMap.putIfAbsent("email", 7);
            columnMap.putIfAbsent("web_url", 8);
            columnMap.putIfAbsent("address", 11);
        }
        
        return columnMap;
    }
    
    private boolean hasRequiredColumns(Map<String, Integer> columnMap) {
        return columnMap.containsKey("company_name") &&
               columnMap.containsKey("web_url") &&
               columnMap.containsKey("email") &&
               columnMap.containsKey("address");
    }
    
    private String getCellValue(Row row, Integer columnIndex) {
        if (columnIndex == null || row == null) {
            return null;
        }
        
        Cell cell = row.getCell(columnIndex);
        if (cell == null) {
            return null;
        }
        
        try {
            // DataFormatter kullanarak hücre değerini al (hyperlink'ler dahil, görünen metni verir)
            DataFormatter formatter = new DataFormatter();
            String formattedValue = formatter.formatCellValue(cell);
            
            if (formattedValue != null && !formattedValue.trim().isEmpty()) {
                return formattedValue.trim();
            }
            
            // Eğer DataFormatter boş döndüyse, cell type'a göre değer al
            switch (cell.getCellType()) {
                case STRING:
                    String stringValue = cell.getStringCellValue();
                    return stringValue != null && !stringValue.trim().isEmpty() ? stringValue.trim() : null;
                case NUMERIC:
                    if (DateUtil.isCellDateFormatted(cell)) {
                        return cell.getDateCellValue().toString();
                    } else {
                        double numValue = cell.getNumericCellValue();
                        if (numValue == (long) numValue) {
                            return String.valueOf((long) numValue);
                        } else {
                            return String.valueOf(numValue);
                        }
                    }
                case BOOLEAN:
                    return String.valueOf(cell.getBooleanCellValue());
                case FORMULA:
                    // Formülün değerini al
                    try {
                        switch (cell.getCachedFormulaResultType()) {
                            case STRING:
                                return cell.getStringCellValue();
                            case NUMERIC:
                                double numValue = cell.getNumericCellValue();
                                if (numValue == (long) numValue) {
                                    return String.valueOf((long) numValue);
                                } else {
                                    return String.valueOf(numValue);
                                }
                            case BOOLEAN:
                                return String.valueOf(cell.getBooleanCellValue());
                            default:
                                return formatter.formatCellValue(cell).trim();
                        }
                    } catch (Exception e) {
                        return formatter.formatCellValue(cell).trim();
                    }
                case BLANK:
                    return null;
                default:
                    return null;
            }
        } catch (Exception e) {
            // Hata durumunda tekrar DataFormatter dene
            try {
                DataFormatter formatter = new DataFormatter();
                String value = formatter.formatCellValue(cell);
                return value != null && !value.trim().isEmpty() ? value.trim() : null;
            } catch (Exception ex) {
                return null;
            }
        }
    }
}
