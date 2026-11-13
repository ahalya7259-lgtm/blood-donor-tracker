package com.blooddonor.controller;

import com.blooddonor.model.BloodGroup;
import com.blooddonor.service.CsvExportService;
import com.blooddonor.service.StockService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

@Controller
@RequestMapping("/stock")
public class StockController {

    private final StockService stockService;
    private final CsvExportService csvExportService;

    public StockController(StockService stockService, CsvExportService csvExportService) {
        this.stockService = stockService;
        this.csvExportService = csvExportService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("units", stockService.findInStock());
        model.addAttribute("stockByGroup", stockService.stockByGroup());
        model.addAttribute("bloodGroups", BloodGroup.values());
        model.addAttribute("lowStock", stockService.lowStockGroups());
        model.addAttribute("expiring", stockService.expiringSoon());
        return "stock/list";
    }

    @PostMapping
    public String add(@RequestParam String bloodGroup,
                      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate collectionDate,
                      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate expiryDate,
                      RedirectAttributes ra) {
        try {
            stockService.addUnit(BloodGroup.fromLabel(bloodGroup), collectionDate, expiryDate);
            ra.addFlashAttribute("message", "Unit added (SAMPLE DATA).");
        } catch (Exception ex) {
            ra.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/stock";
    }

    @PostMapping("/{id}/remove")
    public String remove(@PathVariable Long id, RedirectAttributes ra) {
        stockService.removeUnit(id);
        ra.addFlashAttribute("message", "Unit marked as issued/removed.");
        return "redirect:/stock";
    }

    @GetMapping("/export")
    public ResponseEntity<String> export() {
        String csv = csvExportService.stockToCsv(stockService.findAll());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=stock-sample.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csv);
    }
}
