package com.fullstack.gstbillingwithinvoicemngmt.dao;

import com.fullstack.gstbillingwithinvoicemngmt.model.UserInfo;

public interface UserInfodao {
    void checkUser(String userName, String email);

    UserInfo saveUser(UserInfo userInfo);

    UserInfo getUser(String user);
}
