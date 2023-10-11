package org.v3rmal13n.security.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "_profile")
public class Profile {
    @Id
    @GeneratedValue
    private Long id;
    private String firstname;
    private String lastname;
    private String email;

    @Lob
    @Column(name = "photo", columnDefinition = "BLOB")
    private byte[] photo;
    private String gender;
}
