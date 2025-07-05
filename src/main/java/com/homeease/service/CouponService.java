package com.homeease.service;


import com.homeease.entity.Coupon;
import com.homeease.repository.CouponRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class CouponService {

    @Autowired
    private CouponRepository couponRepo;
    @Autowired
    private CouponRepository couponRepository;
    
    

    public List<Coupon> getAllCoupons() {
        return couponRepo.findAll();
    }

    public void saveCoupon(Coupon coupon) {
        couponRepo.save(coupon);
    }

    public Coupon getById(int id) {
        return couponRepo.findById(id).orElse(null);
    }

    public void deleteById(int id) {
        couponRepo.deleteById(id);
    }
    public Coupon getValidCoupon(String code) {
        Optional<Coupon> couponOpt = couponRepository.findByCode(code);
        if (couponOpt.isPresent()) {
            Coupon coupon = couponOpt.get();
            if (coupon.isActive() && !coupon.getExpiryDate().isBefore(LocalDate.now())) {
                return coupon;
            }
        }
        return null;
    }

}
