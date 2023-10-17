package org.v3rmal13n.security.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.v3rmal13n.security.user.Profile;

import java.util.List;

public interface ProfileRepository extends JpaRepository<Profile, Long> {
    public Profile findByEmail(String email);
    public Profile findByAgeAndGender(int age, String gender);
    public List<Profile> findByGenderAndAgeBetween(String gender, int minAge, int maxAge); 
    public Profile findByAge(int age);
}
