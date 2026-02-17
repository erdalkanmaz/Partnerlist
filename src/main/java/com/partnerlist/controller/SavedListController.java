package com.partnerlist.controller;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.partnerlist.model.Company;
import com.partnerlist.model.CompanyGroup;
import com.partnerlist.service.CompanyGroupService;
import com.partnerlist.service.CompanyService;

@Controller
@RequestMapping("/lists")
public class SavedListController {

    @Autowired
    private CompanyGroupService companyGroupService;

    @Autowired
    private CompanyService companyService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("groups", companyGroupService.findAll());
        return "lists";
    }

    @GetMapping("/new")
    public String newForm(@RequestParam(value = "companyIds", required = false) List<String> companyIds, Model model) {
        model.addAttribute("group", new CompanyGroup());
        model.addAttribute("allCompanies", companyService.findAll());
        model.addAttribute("preselectedIds", parseIds(companyIds));
        return "list_form";
    }

    @PostMapping
    public String create(@RequestParam("name") String name,
                        @RequestParam(value = "companyIds", required = false) List<String> companyIds,
                        RedirectAttributes redirectAttributes) {
        if (name == null || name.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Bitte geben Sie einen Listennamen ein.");
            redirectAttributes.addFlashAttribute("messageType", "error");
            return "redirect:/lists/new";
        }
        List<Long> ids = parseIds(companyIds);
        companyGroupService.create(name.trim(), ids);
        redirectAttributes.addFlashAttribute("message", "Liste wurde erfolgreich erstellt.");
        redirectAttributes.addFlashAttribute("messageType", "success");
        return "redirect:/lists";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Optional<CompanyGroup> opt = companyGroupService.findById(id);
        if (opt.isEmpty()) {
            return "redirect:/lists";
        }
        CompanyGroup group = opt.get();
        List<Company> companies = new ArrayList<>(group.getCompanies());
        companies.sort(Comparator.comparing(c -> c.getName() != null ? c.getName().toLowerCase() : ""));
        model.addAttribute("group", group);
        model.addAttribute("companies", companies);
        return "list_detail";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Optional<CompanyGroup> opt = companyGroupService.findById(id);
        if (opt.isEmpty()) {
            return "redirect:/lists";
        }
        model.addAttribute("group", opt.get());
        model.addAttribute("allCompanies", companyService.findAll());
        model.addAttribute("preselectedIds", opt.get().getCompanies().stream().map(Company::getId).collect(Collectors.toList()));
        return "list_form";
    }

    @PostMapping("/{id}/edit")
    public String update(@PathVariable Long id,
                        @RequestParam("name") String name,
                        @RequestParam(value = "companyIds", required = false) List<String> companyIds,
                        RedirectAttributes redirectAttributes) {
        Optional<CompanyGroup> opt = companyGroupService.findById(id);
        if (opt.isEmpty()) {
            return "redirect:/lists";
        }
        if (name == null || name.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Bitte geben Sie einen Listennamen ein.");
            redirectAttributes.addFlashAttribute("messageType", "error");
            return "redirect:/lists/" + id + "/edit";
        }
        companyGroupService.update(id, name.trim(), parseIds(companyIds));
        redirectAttributes.addFlashAttribute("message", "Liste wurde erfolgreich aktualisiert.");
        redirectAttributes.addFlashAttribute("messageType", "success");
        return "redirect:/lists/" + id;
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        companyGroupService.deleteById(id);
        redirectAttributes.addFlashAttribute("message", "Liste wurde gelöscht.");
        redirectAttributes.addFlashAttribute("messageType", "success");
        return "redirect:/lists";
    }

    private List<Long> parseIds(List<String> companyIds) {
        List<Long> ids = new ArrayList<>();
        if (companyIds == null) return ids;
        for (String s : companyIds) {
            if (s == null || s.isBlank() || "null".equalsIgnoreCase(s)) continue;
            try {
                ids.add(Long.parseLong(s.trim()));
            } catch (NumberFormatException ignored) {
            }
        }
        return ids;
    }
}
