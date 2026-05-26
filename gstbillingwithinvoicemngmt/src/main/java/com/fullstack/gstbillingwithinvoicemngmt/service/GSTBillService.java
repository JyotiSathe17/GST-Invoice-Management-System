package com.fullstack.gstbillingwithinvoicemngmt.service;

import com.fullstack.gstbillingwithinvoicemngmt.dto.AllTotalAmounts;
import com.fullstack.gstbillingwithinvoicemngmt.dto.GSTBillDTO;
import com.fullstack.gstbillingwithinvoicemngmt.dto.GSTReportResponse;
import com.fullstack.gstbillingwithinvoicemngmt.model.GSTBill;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.List;

public interface GSTBillService {
    GSTBill generateNewBill(GSTBillDTO dto);

    GSTBill getBillById(String invoiceId);

    List<GSTBill> getAllBills();

    void deleteBillById(String invoiceId);

    String sendInvoiceToEmail(String invoiceId, MultipartFile file) throws IOException;

    List<GSTBill> sortBills(String value);

    List<GSTBill> searchByAnyInput(String input);

    AllTotalAmounts getAllTotalAmounts();

    GSTBill updateBillByID(String invoiceId, GSTBillDTO gstBillDTO);

    List<GSTBill> filterBillsByDates(Date fromDate, Date toDate);

    GSTReportResponse generateReports(Integer months, Date fromDate, Date toDate);
}
