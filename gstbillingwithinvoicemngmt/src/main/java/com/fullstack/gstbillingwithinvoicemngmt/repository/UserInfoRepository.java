package com.fullstack.gstbillingwithinvoicemngmt.repository;

import com.fullstack.gstbillingwithinvoicemngmt.model.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface UserInfoRepository extends JpaRepository<UserInfo,Long> {
    @Query("SELECT u FROM UserInfo u WHERE u.userName=?1 OR u.email=?1")
    Optional<UserInfo> findByUserNameOrEmail(String userName);

    @Query("SELECT u FROM UserInfo u WHERE (?1 IS NULL OR u.userName=?1) AND (?2 IS NULL OR u.email=?2)")
    Optional<UserInfo> findByUserNameAndEmail(String userName, String email);
}
