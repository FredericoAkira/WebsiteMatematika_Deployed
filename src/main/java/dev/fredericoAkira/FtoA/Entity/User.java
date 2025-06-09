package dev.fredericoAkira.FtoA.Entity;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.mongodb.lang.NonNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "users")
@Data // getter setters
@AllArgsConstructor // constructor all fields
@NoArgsConstructor // constructor no fields

public class User implements UserDetails {
    @Id
    private ObjectId userId;
    @NonNull
    private String username;
    @NonNull
    private String email;
    @NonNull
    private String password;
    @NonNull
    private Role role;
    private String grade;
    private String profilePhoto;
    private String level;
    private List<Integer> point;

    private String lastMaterial;
    private String lastQuiz;
    private String lastTopic;
    private List<String> students;

    private Boolean doDaily = false;
    private Integer dailyStreak = 0; // default streak
    private LocalDate lastDoDailyDate;
    
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}