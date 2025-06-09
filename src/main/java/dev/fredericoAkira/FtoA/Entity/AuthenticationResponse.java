package dev.fredericoAkira.FtoA.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // getter setters
@AllArgsConstructor // constructor all fields
@NoArgsConstructor // constructor no fields
public class AuthenticationResponse {
    private String token;
}
