package com.fullstack.gstbillingwithinvoicemngmt.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "USER_INFO", indexes = {
        @Index(name = "usr_fn", columnList = "fullName"),
        @Index(name = "usr_un", columnList = "userName"),
        @Index(name = "usr_em", columnList = "email"),
})

public class UserInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fullName;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(unique = true, nullable = false)
    private String userName;

    @JsonIgnore
    @Column(nullable = false)
    private String password;
}
