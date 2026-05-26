package com.fullstack.gstbillingwithinvoicemngmt.dao.impl;

import com.fullstack.gstbillingwithinvoicemngmt.dao.GSTBilldao;
import com.fullstack.gstbillingwithinvoicemngmt.exception.NotFoundException;
import com.fullstack.gstbillingwithinvoicemngmt.model.GSTBill;
import com.fullstack.gstbillingwithinvoicemngmt.repository.GSTBillRepo;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class GSTBilldaoImp implements GSTBilldao {
    private final GSTBillRepo gstBillRepo;

    public GSTBilldaoImp(@Lazy GSTBillRepo gstBillRepo) {
        this.gstBillRepo = gstBillRepo;
    }

    @Override
    @CacheEvict(value = {"AllBills", "BilByID"}, allEntries = true)
    public GSTBill generateNewBill(GSTBill gstBill) {
        return gstBillRepo.save(gstBill);
    }

    @Override
    @Cacheable(value = "BilByID")
    public GSTBill getBillById(String invoiceId) {
        return gstBillRepo.findById(invoiceId).orElseThrow(() -> new NotFoundException("GST bill having id " + invoiceId + " is not found"));
    }

    @Override
    @Cacheable(value = "AllBills")
    public List<GSTBill> getAllBills() {
        return gstBillRepo.findAll();
    }

    @Override
    @CacheEvict(value = {"AllBills", "BilByID"}, allEntries = true)
    public void deleteBillById(String invoiceId) {
        GSTBill bill = gstBillRepo.findById(invoiceId).orElseThrow(() ->
                new NotFoundException("GST bill having id " + invoiceId + " is not found"));
        gstBillRepo.delete(bill);
    }

    @Override
    public List<GSTBill> filterBillsByDate(Date fromDate, Date toDate) {
        return gstBillRepo.findByBillDateBetween(fromDate, toDate);
    }
}
