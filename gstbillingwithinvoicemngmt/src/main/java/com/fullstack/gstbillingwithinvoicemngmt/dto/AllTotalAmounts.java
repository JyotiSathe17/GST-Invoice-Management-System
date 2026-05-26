package com.fullstack.gstbillingwithinvoicemngmt.dto;

import com.fullstack.gstbillingwithinvoicemngmt.model.GSTBill;

import java.util.List;

public record AllTotalAmounts(int allAmount, int allCgstAmount, int allSgstAmount, int allTotalAmount) {

    public static AllTotalAmounts getSummary(List<GSTBill> list) {
        return list.parallelStream().reduce(
                new AllTotalAmounts(0, 0, 0, 0),
                AllTotalAmounts::addAmount,
                AllTotalAmounts::combineAmounts
        );
    }

    public AllTotalAmounts addAmount(GSTBill bill) {
        return new AllTotalAmounts(
                this.allAmount + bill.getAmount(),
                this.allCgstAmount + bill.getCgstAmount(),
                this.allSgstAmount + bill.getSgstAmount(),
                this.allAmount + bill.getAmount()
        );
    }

    public AllTotalAmounts combineAmounts(AllTotalAmounts amounts) {
        return new AllTotalAmounts(
                this.allAmount + amounts.allAmount,
                this.allCgstAmount + amounts.allCgstAmount,
                this.allSgstAmount + amounts.allSgstAmount,
                this.allAmount + amounts.allAmount
        );
    }
}
