package com.spay.wallet.properies;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class DevAccount {
    private String name;
    private String phoneNumber;
    private String password;
    private String email;
}
