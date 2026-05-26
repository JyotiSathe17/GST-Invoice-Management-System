package com.fullstack.gstbillingwithinvoicemngmt.dao;

import com.fullstack.gstbillingwithinvoicemngmt.model.GSTBill;

import java.util.Date;
import java.util.List;

public interface GSTBilldao {
    GSTBill generateNewBill(GSTBill gstBill);

    GSTBill getBillById(String invoiceId);

    List<GSTBill> getAllBills();

    void deleteBillById(String invoiceId);

    List<GSTBill> filterBillsByDate(Date fromDate, Date toDate);

}
