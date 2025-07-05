package com.homeease.controller;


import com.homeease.entity.Coupon;
import com.homeease.service.CouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/coupons")
public class AdminCouponController {

    @Autowired
    private CouponService couponService;

    @GetMapping
    public String viewCoupons(Model model) {
        model.addAttribute("coupons", couponService.getAllCoupons());
        return "admin/coupons/list";
    }

    @GetMapping("/add")
    public String addCouponForm(Model model) {
        model.addAttribute("coupon", new Coupon());
        return "admin/coupons/form";
    }

    @PostMapping("/save")
    public String saveCoupon(@ModelAttribute("coupon") Coupon coupon) {
        couponService.saveCoupon(coupon);
        return "redirect:/admin/coupons";
    }

    @GetMapping("/edit/{id}")
    public String editCoupon(@PathVariable int id, Model model) {
        model.addAttribute("coupon", couponService.getById(id));
        return "admin/coupons/form";
    }

    @GetMapping("/delete/{id}")
    public String deleteCoupon(@PathVariable int id) {
        couponService.deleteById(id);
        return "redirect:/admin/coupons";
    }
}
