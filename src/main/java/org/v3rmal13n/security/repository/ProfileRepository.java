package org.v3rmal13n.security.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.v3rmal13n.security.user.Profile;

public interface ProfileRepository extends JpaRepository<Profile, Long> {
    public Profile findByEmail(String email);
}
