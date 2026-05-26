package com.fullstack.gstbillingwithinvoicemngmt.dao.impl;

import com.fullstack.gstbillingwithinvoicemngmt.dao.UserInfodao;
import com.fullstack.gstbillingwithinvoicemngmt.exception.DuplicateUserException;
import com.fullstack.gstbillingwithinvoicemngmt.exception.NotFoundException;
import com.fullstack.gstbillingwithinvoicemngmt.model.UserInfo;
import com.fullstack.gstbillingwithinvoicemngmt.repository.UserInfoRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
public class UserInfodaoImp implements UserInfodao {
    private final UserInfoRepository userInfoRepository;

    public UserInfodaoImp(@Lazy UserInfoRepository userInfoRepository) {
        this.userInfoRepository = userInfoRepository;
    }

    @Override
    public void checkUser(String userName, String email) {
        var user = userInfoRepository.findByUserNameAndEmail(userName, email);

        if (user.isPresent())
            throw new DuplicateUserException("Username or Email already exists!!!! Please use different combinations");
    }

    @Override
    public UserInfo saveUser(UserInfo userInfo) {
        return userInfoRepository.save(userInfo);
    }

    @Override
    public UserInfo getUser(String user) {
        return userInfoRepository.findByUserNameOrEmail(user).orElseThrow(() ->
                new NotFoundException(String.format("User `%s` not found in DB !!!", user)));
    }
}
