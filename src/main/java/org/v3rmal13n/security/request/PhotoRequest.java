package org.v3rmal13n.security.controller;

import jakarta.persistence.Column;
import jakarta.persistence.Lob;
import lombok.Data;

@Data
public class PhotoRequest {
    @Lob
    @Column(name = "photo", columnDefinition = "BLOB")
    private byte[] photo;

}
