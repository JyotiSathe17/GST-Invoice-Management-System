package com.fullstack.gstbillingwithinvoicemngmt.dto;

import com.fullstack.gstbillingwithinvoicemngmt.model.GSTBill;

import java.util.List;

public record GSTReportResponse(List<GSTBill> bills, AllTotalAmounts summary) {
}
