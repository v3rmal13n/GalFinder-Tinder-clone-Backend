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
    public ResponseEntity<Profile> otherProfile(Authentication authentication) {
        String email = authentication.getName();
        Profile profile = profileRepository.findByEmail(email);
        if (profile != null && profile.getGender() == "male") {
            return ResponseEntity.ok(profileRepository.findByAgeAndGender(profile.getAge(), "female"));
        }
        if (profile != null && profile.getGender() == "female") {
            return ResponseEntity.ok(profileRepository.findByAgeAndGender(profile.getAge(), "male"));
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}