package org.v3rmal13n.security.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.v3rmal13n.security.repository.ProfileRepository;
import org.v3rmal13n.security.user.Profile;

import java.util.List;
import java.util.Random;

@RestController
@RequiredArgsConstructor
@RequestMapping("/other")
public class OtherUserController {
    
    private final ProfileRepository profileRepository;
    
//    @GetMapping("/profile")
//    public ResponseEntity<Profile> otherProfile(Authentication authentication) {
//        String email = authentication.getName();
//        Profile profile = profileRepository.findByEmail(email);
//        if (profile != null) {
//            
//            return ResponseEntity.ok(profileRepository.findByAge(profile.getAge()));
//        }
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
//    }

    @GetMapping("/profile")
    public ResponseEntity<?> otherProfile(Authentication authentication) {
        String email = authentication.getName();
        Profile profile = profileRepository.findByEmail(email);
        String gender = profile.getGender();
        int age = profile.getAge();
        int minAge = age - 3;
        int maxAge = age + 3;

        if (profile != null && "male".equals(gender)) {
            List<Profile> profiles = profileRepository.findByGenderAndAgeBetween(
                    "female", minAge, maxAge
            );
            int count = profiles.size();
            return ResponseEntity.ok(count);
        }
        if (profile != null && "female".equals(gender)) {
            List<Profile> profiles = profileRepository.findByGenderAndAgeBetween(
                    "male", minAge, maxAge
            );
            int count = profiles.size();
            return ResponseEntity.ok(count);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

}