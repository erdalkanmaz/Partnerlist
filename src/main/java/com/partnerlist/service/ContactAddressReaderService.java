package com.partnerlist.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

/**
 * Contact adresini JPA/Hibernate kullanmadan, doğrudan JDBC ile okur.
 * Neden: SQLite + Hibernate ile Contact yüklenirken, satırda email=NULL olduğunda
 * address alanı entity'de yanlışlıkla null geliyor (sürücü/diyalekt davranışı).
 * Bu servis entity'yi atlayıp sadece address sütununu okur; e-posta durumundan etkilenmez.
 */
@Service
public class ContactAddressReaderService {

    private final JdbcTemplate jdbcTemplate;

    public ContactAddressReaderService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public String getAddressByContactId(Long id) {
        if (id == null) {
            return "";
        }
        try {
            // SELECT * ile sütun adı farklı olsa bile bul (SQLite şemasına uyum)
            var list = jdbcTemplate.queryForList("SELECT * FROM contacts WHERE id = ?", id);
            if (list.isEmpty()) {
                return "";
            }
            var row = list.get(0);
            for (String key : row.keySet()) {
                if (key != null && "address".equalsIgnoreCase(key.trim())) {
                    Object val = row.get(key);
                    if (val != null) {
                        String s = val.toString().trim();
                        if (!s.isEmpty()) {
                            return s;
                        }
                    }
                    break;
                }
            }
            return "";
        } catch (Exception e) {
            return "";
        }
    }

    /** Sorun giderme: Bu id için satırdaki sütun adları ve adres değerini döndürür. */
    public String getAddressDebugInfo(Long id) {
        if (id == null) {
            return "id=null";
        }
        try {
            var list = jdbcTemplate.queryForList("SELECT * FROM contacts WHERE id = ?", id);
            if (list.isEmpty()) {
                return "Keine Zeile für id=" + id;
            }
            var row = list.get(0);
            String keys = String.join(", ", row.keySet());
            String addressVal = "";
            for (String key : row.keySet()) {
                if (key != null && "address".equalsIgnoreCase(key.trim())) {
                    Object v = row.get(key);
                    addressVal = v != null ? v.toString() : "(null)";
                    break;
                }
            }
            return "Spalten: " + keys + "\naddress-Wert: " + addressVal;
        } catch (Exception e) {
            return "Fehler: " + e.getMessage();
        }
    }
}
