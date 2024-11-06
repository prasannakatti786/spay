package com.spay.wallet.properies;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Security {
    private List<String> allowedRoutes;
    private Integer tokenDuration;
    private List<String> allowedOrigins;
    private String jwtSecret;

    public String[] getAllowedRoutesAsArray(){
        return allowedRoutes.toArray(String[]::new);
    }
}
