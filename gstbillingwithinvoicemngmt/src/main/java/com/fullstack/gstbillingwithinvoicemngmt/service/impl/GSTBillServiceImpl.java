package com.fullstack.gstbillingwithinvoicemngmt.service.impl;

import com.fullstack.gstbillingwithinvoicemngmt.dao.GSTBilldao;
import com.fullstack.gstbillingwithinvoicemngmt.dto.AllTotalAmounts;
import com.fullstack.gstbillingwithinvoicemngmt.dto.GSTBillDTO;
import com.fullstack.gstbillingwithinvoicemngmt.dto.GSTReportResponse;
import com.fullstack.gstbillingwithinvoicemngmt.model.GSTBill;
import com.fullstack.gstbillingwithinvoicemngmt.service.GSTBillService;
import jakarta.mail.internet.MimeMessage;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

@Service
@Slf4j
public class GSTBillServiceImpl implements GSTBillService {

    private final GSTBilldao gstBilldao;
    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String fromMail;

    public GSTBillServiceImpl(@Lazy GSTBilldao gstBilldao, @Lazy JavaMailSender javaMailSender) {
        this.gstBilldao = gstBilldao;
        this.javaMailSender = javaMailSender;
    }

    @Override
    public GSTBill generateNewBill(GSTBillDTO dto) {
        GSTBill gstBill = new GSTBill();

        // ===== Invoice ID Generation (unchanged) =====
        if (!gstBilldao.getAllBills().isEmpty()) {
            String lastId = gstBilldao.getAllBills()
                    .stream()
                    .map(GSTBill::getInvoiceId)
                    .sorted(Comparator.comparing(String::valueOf).reversed())
                    .findFirst()
                    .orElse("DCT-1000");
            int newId = Integer.parseInt(lastId.replaceAll("\\D", ""));
            gstBill.setInvoiceId("DCT-" + (newId + 1));
        } else {
            gstBill.setInvoiceId("DCT-1000");
        }

        // ===== Basic Fields =====
        gstBill.setCustName(dto.custName());
        gstBill.setBillDate(new Date());
        gstBill.setCustContact(dto.custContact());
        gstBill.setCustEmail(dto.custEmail());
        gstBill.setCustAddress(dto.custAddress());
        gstBill.setCustGSTNO(dto.custGSTNO());
        gstBill.setBillDescription(dto.billDescription());
        gstBill.setCustBatch(dto.custBatch());

        // ===== GST calculation: treat dto.totalAmount() as the FINAL (total) amount =====
        double gstRate = 0.18;

        // Parse totalAmount (expected to be final amount as sent from frontend)
        double totalAmount = 0.0;
        try {
            totalAmount = Double.parseDouble(dto.totalAmount());
        } catch (NumberFormatException ex) {
            // handle parsing issue (fallback to zero or throw)
            totalAmount = 0.0;
        }

        // Reverse-calculate base and GST from total
        double baseAmount = totalAmount / (1 + gstRate);     // e.g. total/1.18
        double gstAmount = totalAmount - baseAmount;         // total - base

        double cgst = gstAmount / 2.0;
        double sgst = gstAmount / 2.0;

        // Round to nearest integer (cast to int if your entity fields are int)
        int baseRounded = (int) Math.round(baseAmount);
        int totalRounded = (int) Math.round(totalAmount);
        int cgstRounded = (int) Math.round(cgst);
        int sgstRounded = (int) Math.round(sgst);

        gstBill.setAmount(baseRounded);
        gstBill.setTotalAmount(totalRounded);
        gstBill.setCgstAmount(cgstRounded);
        gstBill.setSgstAmount(sgstRounded);

        return gstBilldao.generateNewBill(gstBill);
    }

    @Override
    public GSTBill getBillById(String invoiceId) {
        return gstBilldao.getBillById(invoiceId);
    }

    @Override
    public List<GSTBill> getAllBills() {
        return gstBilldao.getAllBills();
    }

    @Override
    public void deleteBillById(String invoiceId) {
        gstBilldao.deleteBillById(invoiceId);
    }

    @Override
    @SneakyThrows
    public String sendInvoiceToEmail(String invoiceId, MultipartFile file) {
        GSTBill bill = gstBilldao.getBillById(invoiceId);
        String fileName = bill.getCustName() + " Invoice " + bill.getInvoiceId() + ".pdf";

        File tempFile = File.createTempFile(fileName, ".pdf");
        try (FileOutputStream out = new FileOutputStream(tempFile)) {
            file.getInputStream().transferTo(out);
        }
        FileSystemResource resource = new FileSystemResource(tempFile);

        String emailMessage = """
                <div style='background-color: #ffa19c; margin: 15px; padding: 20px 20px 20px 30px'>
                   <p>Hi <b>%s</b>,</p>
                   <div style='padding-left:10px;'>
                       <p style='margin-bottom: -10px'>Thank you for your recent business with us. We have attached detail copy of invoice <b>'<i>%s</i>'</b> to this mail.</p>
                       <p style='margin-bottom: -10px'>The invoice total is <b>Rupees %d</b> paid to <b><i>%s</i></b>.</p>
                       <p >If you have any question or concerns regarding this invoice, please don't hesitate to get in touch with us at <b><i>contact@fullstackjavadeveloper.in</i></b>.</p>
                   </div>
                   <p style='margin-bottom: -10px'>Thanks,</p>
                   <p style='margin-bottom: -10px'><b><i>Kiran Jadhav</i></b></p>
                   <p>Managing Director</p>
                </div>
                """.formatted(bill.getCustName(), fileName, bill.getTotalAmount(), bill.getBillDate());

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message, true);
        mimeMessageHelper.setSubject("GST Invoice");
        mimeMessageHelper.setText(emailMessage, true);
        mimeMessageHelper.setFrom(fromMail, "Full Stack Java Developer Pune");
        mimeMessageHelper.setTo(bill.getCustEmail());
        mimeMessageHelper.addAttachment(fileName, resource);
        javaMailSender.send(message);

        tempFile.deleteOnExit();
        return "Invoice send successfully to " + bill.getCustName();
    }

    @Override
    public List<GSTBill> sortBills(String value) {
        return switch (value) {
            case "invoiceIDDesc" ->
                    gstBilldao.getAllBills().stream().sorted(Comparator.comparing(GSTBill::getInvoiceId).reversed()).toList();
            case "custNameAsc" ->
                    gstBilldao.getAllBills().stream().sorted(Comparator.comparing(GSTBill::getCustName)).toList();
            case "custNameDesc" ->
                    gstBilldao.getAllBills().stream().sorted(Comparator.comparing(GSTBill::getCustName).reversed()).toList();
            case "billDateAsc" ->
                    gstBilldao.getAllBills().stream().sorted(Comparator.comparing(GSTBill::getBillDate)).toList();
            case "billDateDesc" ->
                    gstBilldao.getAllBills().stream().sorted(Comparator.comparing(GSTBill::getBillDate).reversed()).toList();
            default -> gstBilldao.getAllBills().stream().sorted(Comparator.comparing(GSTBill::getInvoiceId)).toList();
        };
    }

    @Override
    public List<GSTBill> searchByAnyInput(String input) {
        List<GSTBill> gstBills = new ArrayList<>();

        if (input.matches("[a-zA-Z\\s]{4,30}")) {                                                   // Customer Name
            gstBilldao.getAllBills().stream().filter(gstBill -> gstBill.getCustName().equalsIgnoreCase(input)).forEach(gstBills::add);
        } else if (input.matches("^[A-Z]{2,4}-\\d{3,6}$")) {
            gstBilldao.getAllBills().stream().filter(gstBill -> gstBill.getInvoiceId().equals(input)).forEach(gstBills::add);
        } else if (input.matches("^\\d{10}+$")) {                                                 // Contact Number
            gstBilldao.getAllBills().stream().filter(gstBill -> gstBill.getCustContact() == Long.parseLong(input)).forEach(gstBills::add);
        } else if (input.matches("^[a-z0-9+.]+@[a-z]+[(.){1}]+[a-z]+$")) {                           // Email
            gstBilldao.getAllBills().stream().filter(gstBill -> gstBill.getCustEmail().equals(input)).forEach(gstBills::add);
        } else if (input.matches("(?i)^(Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Sept|Oct|Nov|Dec|January|February|March|April|May|June|July|August|September|October|November|December)\\s?\\d{2}$")) {
            // your batch logic here

            gstBilldao.getAllBills().stream().filter(gstBill -> gstBill.getCustBatch().equalsIgnoreCase(input)).forEach(gstBills::add);
        }

        else if (input.matches("(0[1-9]|1\\d|2\\d|3[0-1]|[1-9])-(0[1-9]|1[0-2]|[1-9])-(\\d{4})")) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");              // Invoice Date
            if (input.matches("(\\d{2})-(\\d{2})-(\\d{4})")) {         // dd-MM-yyyy
                gstBilldao.getAllBills().stream().filter(gstBill -> simpleDateFormat.format(gstBill.getBillDate()).equals(input)).forEach(gstBills::add);
            } else if (input.matches("(\\d)-(\\d)-(\\d{4})")) {  // d-M-yyyy
                String start = input.substring(0, 2);
                String last = input.substring(2, 8);
                String newInput = "0" + start + "0" + last;
                gstBilldao.getAllBills().stream().filter(gstBill -> simpleDateFormat.format(gstBill.getBillDate()).equals(newInput)).forEach(gstBills::add);
            } else if (input.matches("(\\d{2})-(\\d)-(\\d{4})")) {   // dd-M-yyyy
                String start = input.substring(0, 3);
                String last = input.substring(3, 9);
                String newInput = start + "0" + last;
                gstBilldao.getAllBills().stream().filter(gstBill ->
                        simpleDateFormat.format(gstBill.getBillDate()).equals(newInput)).forEach(gstBills::add);
            }

            else {                                                                 // d-MM-yyyy
                gstBilldao.getAllBills().stream().filter(gstBill -> simpleDateFormat.format(gstBill.getBillDate()).equals("0" + input)).forEach(gstBills::add);
            }
        }
        return gstBills;
    }

    @Override
    public AllTotalAmounts getAllTotalAmounts() {
        int allAmount = gstBilldao.getAllBills().stream().map(GSTBill::getAmount).reduce(0, Integer::sum);
        int allCgstAmount = gstBilldao.getAllBills().stream().map(GSTBill::getCgstAmount).reduce(0, Integer::sum);
        int allSgstAmount = gstBilldao.getAllBills().stream().map(GSTBill::getSgstAmount).reduce(0, Integer::sum);
        int allTotalAmount = gstBilldao.getAllBills().stream().map(GSTBill::getTotalAmount).reduce(0, Integer::sum);
        return new AllTotalAmounts(allAmount, allCgstAmount, allSgstAmount, allTotalAmount);
    }

    @Override
    public GSTBill updateBillByID(String invoiceId, GSTBillDTO dto) {
        GSTBill gstBill = gstBilldao.getBillById(invoiceId);
        gstBill.setCustName(dto.custName());
        gstBill.setCustContact(dto.custContact());
        gstBill.setCustEmail(dto.custEmail());
        gstBill.setCustAddress(dto.custAddress());
        gstBill.setCustGSTNO(dto.custGSTNO());
        gstBill.setBillDescription(dto.billDescription());
        gstBill.setCustBatch(dto.custBatch());


        int totalAmount = Integer.parseInt(dto.totalAmount());

// Calculate GST amount (18%)
        double gst = totalAmount * 18.0 / (100 + 18);
        int cgst = (int) Math.round(gst / 2);
        int sgst = (int) Math.round(gst / 2);

        gstBill.setCgstAmount(cgst);
        gstBill.setSgstAmount(sgst);

        if (Boolean.TRUE.equals(dto.includingGST())) {
            gstBill.setTotalAmount(totalAmount);
            gstBill.setAmount(totalAmount - cgst - sgst);
        } else {
            gstBill.setAmount(totalAmount);
            gstBill.setTotalAmount(totalAmount + cgst + sgst);
        }
        return gstBilldao.generateNewBill(gstBill);

    }

    @Override
    public List<GSTBill> filterBillsByDates(Date fromDate, Date toDate) {
        return gstBilldao.filterBillsByDate(fromDate, toDate);
    }

    @Override
    public GSTReportResponse generateReports(Integer months, Date fromDate, Date toDate) {
        List<GSTBill> bills;
        if (Objects.nonNull(months) && months != 0) {
            LocalDate currentDate = LocalDate.now();
            LocalDate startDate = currentDate.minusMonths(months);

            Date toDte = Date.from(currentDate.atStartOfDay(ZoneId.of("Asia/Kolkata")).toInstant());
            Date fromDte = Date.from(startDate.atStartOfDay(ZoneId.of("Asia/Kolkata")).toInstant());

            bills = gstBilldao.filterBillsByDate(fromDte, toDte);
        } else if (Objects.nonNull(fromDate) && Objects.nonNull(toDate))
            bills = gstBilldao.filterBillsByDate(fromDate, toDate);
        else
            throw new IllegalArgumentException(String.format("Invalid Months: %s or fromDate: %s & toDate: %s !!!", months, fromDate, toDate));

        AllTotalAmounts summary = AllTotalAmounts.getSummary(bills);

        return new GSTReportResponse(bills, summary);
    }

}
