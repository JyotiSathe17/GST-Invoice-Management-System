package com.fullstack.gstbillingwithinvoicemngmt.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "gst_billing")

public class GSTBill {

    @Id
    private String invoiceId;

    private String custName;

    @JsonFormat(pattern = "dd-MM-yyyy", timezone = "Asia/Kolkata")
    @Temporal(TemporalType.DATE)
    private Date billDate;

    private long custContact;

    private String custEmail;

    private String custAddress;

    @Column(name = "cust_gst_no")
    private String custGSTNO;

    private String billDescription;

    private String custBatch;

    private int amount;

    private int cgstAmount;

    private int sgstAmount;

    private int totalAmount;
}
