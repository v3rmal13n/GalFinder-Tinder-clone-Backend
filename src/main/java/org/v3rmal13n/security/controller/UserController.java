package org.v3rmal13n.security.controller;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.v3rmal13n.security.repository.ProfileRepository;
import org.v3rmal13n.security.req.AgeRequest;
import org.v3rmal13n.security.req.GenderRequest;
import org.v3rmal13n.security.req.TelegramRequest;
import org.v3rmal13n.security.user.Profile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/v1/user")
public class UserController {
    Logger logger = LoggerFactory.getLogger(UserController.class);
    private final ProfileRepository profileRepository;

    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping("/profile/principals")
    public ResponseEntity<?> getUserProfile(Authentication authentication) {
        Map<String, Object> res = new HashMap<>();
        res.put("username", authentication.getPrincipal());

        return ResponseEntity.ok(res);
    }

    @GetMapping("/profile")
    public ResponseEntity<Profile> getProfile(Authentication authentication) {
        return ResponseEntity.ok(profileRepository.findByEmail(authentication.getName()));
    }

    @PutMapping("/profile/gender")
    public ResponseEntity<Profile> putGender(@RequestBody GenderRequest genderRequest, Authentication authentication) {
        String email = authentication.getName();
        Profile profile = profileRepository.findByEmail(email);

        if (profile != null) {
            // Изменяем поле "gender" на новое значение из запроса
            profile.setGender(genderRequest.getGender());

            // Сохраняем обновленный профиль в репозитории
            profile = profileRepository.save(profile);

            return ResponseEntity.ok(profile);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/profile/photo")
    public ResponseEntity<Profile> putPhoto(@RequestPart("file") MultipartFile file, Authentication authentication) {
        String email = authentication.getName();
        Profile profile = profileRepository.findByEmail(email);
        System.out.println("start try/catch");
        if (profile != null) {
            try {
                System.out.println("file before getBytes"+file);
                profile.setPhoto(file.getBytes());
                System.out.println("file after getBy tes"+file);
                profile = profileRepository.save(profile);
                return ResponseEntity.ok(profile);
            } catch (IOException e) {
                e.getMessage();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }

        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/profile/photo")
    public ResponseEntity<byte[]> getPhoto(Authentication authentication) {
        String email = authentication.getName();
        Profile profile = profileRepository.findByEmail(email);
        if (profile != null && profile.getPhoto() != null) {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG);

            return new ResponseEntity<>(profile.getPhoto(), headers, HttpStatus.OK);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/profile/telegram")
    public ResponseEntity<Profile> putTelegram(@RequestBody TelegramRequest telegramRequest, Authentication authentication) {
        String email = authentication.getName();
        Profile profile = profileRepository.findByEmail(email);
        if (profile != null) {
            profile.setTelegram(telegramRequest.getTelegram());
            return ResponseEntity.ok(profileRepository.save(profile));
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @PutMapping("/profile/age")
    public ResponseEntity<Profile> putAge(@RequestBody AgeRequest ageRequest, Authentication authentication) {
        String email = authentication.getName();
        Profile profile = profileRepository.findByEmail(email);
        if (profile != null) {
            profile.setAge(ageRequest.getAge());
            return ResponseEntity.ok(profileRepository.save(profile));
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
 }
