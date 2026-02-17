package com.partnerlist.controller;

import com.partnerlist.service.ExcelImportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

@Controller
@RequestMapping("/import")
public class ExcelImportController {
    
    @Autowired
    private ExcelImportService excelImportService;
    
    @GetMapping
    public String showImportForm() {
        return "import_excel";
    }
    
    @PostMapping
    public String importExcel(@RequestParam("file") MultipartFile file,
                             RedirectAttributes redirectAttributes) {
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Bitte wählen Sie eine Datei aus.");
            redirectAttributes.addFlashAttribute("messageType", "error");
            return "redirect:/import";
        }
        
        if (!file.getOriginalFilename().endsWith(".xlsx") && 
            !file.getOriginalFilename().endsWith(".xls")) {
            redirectAttributes.addFlashAttribute("message", "Nur Excel-Dateien (.xlsx, .xls) können hochgeladen werden.");
            redirectAttributes.addFlashAttribute("messageType", "error");
            return "redirect:/import";
        }
        
        try {
            Map<String, Object> result = excelImportService.importFromExcel(file);
            
            if ((Boolean) result.get("success")) {
                redirectAttributes.addFlashAttribute("message", result.get("message"));
                redirectAttributes.addFlashAttribute("messageType", "success");
                
                @SuppressWarnings("unchecked")
                java.util.List<String> errors = (java.util.List<String>) result.get("errors");
                if (errors != null && !errors.isEmpty()) {
                    redirectAttributes.addFlashAttribute("errors", errors);
                }
            } else {
                redirectAttributes.addFlashAttribute("message", result.get("message"));
                redirectAttributes.addFlashAttribute("messageType", "error");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Fehler: " + e.getMessage());
            redirectAttributes.addFlashAttribute("messageType", "error");
        }
        
        return "redirect:/import";
    }
}
