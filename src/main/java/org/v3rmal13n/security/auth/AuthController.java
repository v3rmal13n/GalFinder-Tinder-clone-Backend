package org.v3rmal13n.security.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.v3rmal13n.security.repository.ProfileRepository;
import org.v3rmal13n.security.user.Profile;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthenticationService service;
    private final ProfileRepository profileRepository;

    @CrossOrigin(origins = "http://localhost:3000")
    private Profile convertToProfile(RegisterRequest request) {
        Profile profile = new Profile();
        profile.setFirstname(request.getFirstname());
        profile.setLastname(request.getLastname());
        profile.setEmail(request.getEmail());
        return profile;
    }
    @PostMapping("/register")
    @CrossOrigin(origins = "http://localhost:3000")
    public ResponseEntity<AuthenticationResponse> register(
            @RequestBody RegisterRequest request
    ) {
        Profile profile = convertToProfile(request);
        profileRepository.save(profile);
        return ResponseEntity.ok(service.register(request));
    }

    @PostMapping("/authenticate")
    @CrossOrigin(origins = "http://localhost:3000")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody AuthenticateRequest request
    ) {
        return ResponseEntity.ok(service.authenticate(request));
    }
}
