package com.fullstack.gstbillingwithinvoicemngmt.repository;

import com.fullstack.gstbillingwithinvoicemngmt.model.GSTBill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
@Repository
public interface GSTBillRepo extends JpaRepository<GSTBill,String> {
    List<GSTBill> findByBillDateBetween(Date fromDate,Date toDate);
}
