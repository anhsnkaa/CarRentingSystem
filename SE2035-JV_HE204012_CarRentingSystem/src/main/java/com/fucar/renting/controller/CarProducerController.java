package com.fucar.renting.controller;

import com.fucar.renting.dto.CarProducerRequest;
import com.fucar.renting.entity.CarProducer;
import com.fucar.renting.service.CarProducerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/producers")
@RequiredArgsConstructor
public class CarProducerController {

    private final CarProducerService producerService;

    @GetMapping
    public String list(@RequestParam(defaultValue = "1") int page,
                       @RequestParam(defaultValue = "10") int size,
                       Model model) {
        Page<CarProducer> producers = producerService.findAll(page, size);
        model.addAttribute("producers", producers);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", producers.getTotalPages());
        model.addAttribute("pageSize", size);
        model.addAttribute("activeMenu", "producers");
        return "admin/producers/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("producerRequest", new CarProducerRequest());
        model.addAttribute("activeMenu", "producers");
        return "admin/producers/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("producerRequest") CarProducerRequest request,
                         BindingResult binding,
                         RedirectAttributes ra) {
        if (binding.hasErrors()) {
            return "admin/producers/form";
        }
        producerService.create(request);
        ra.addFlashAttribute("toastMessage", "Producer created");
        ra.addFlashAttribute("toastType", "success");
        return "redirect:/admin/producers";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Integer id, Model model, RedirectAttributes ra) {
        CarProducer p = producerService.findById(id);
        if (p == null) {
            ra.addFlashAttribute("toastMessage", "Producer not found");
            ra.addFlashAttribute("toastType", "error");
            return "redirect:/admin/producers";
        }
        CarProducerRequest req = CarProducerRequest.builder()
                .producerName(p.getProducerName())
                .address(p.getAddress())
                .country(p.getCountry())
                .build();
        model.addAttribute("producerRequest", req);
        model.addAttribute("producerId", id);
        model.addAttribute("activeMenu", "producers");
        return "admin/producers/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Integer id,
                         @Valid @ModelAttribute("producerRequest") CarProducerRequest request,
                         BindingResult binding,
                         Model model,
                         RedirectAttributes ra) {
        if (binding.hasErrors()) {
            model.addAttribute("producerId", id);
            model.addAttribute("activeMenu", "producers");
            return "admin/producers/form";
        }
        producerService.update(id, request);
        ra.addFlashAttribute("toastMessage", "Producer updated");
        ra.addFlashAttribute("toastType", "success");
        return "redirect:/admin/producers";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Integer id, RedirectAttributes ra) {
        try {
            producerService.delete(id);
            ra.addFlashAttribute("toastMessage", "Producer deleted");
            ra.addFlashAttribute("toastType", "success");
        } catch (Exception e) {
            ra.addFlashAttribute("toastMessage", "Cannot delete: producer has cars");
            ra.addFlashAttribute("toastType", "error");
        }
        return "redirect:/admin/producers";
    }
}