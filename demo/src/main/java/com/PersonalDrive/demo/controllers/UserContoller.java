package com.PersonalDrive.demo.controllers;

import com.PersonalDrive.demo.models.User;
import com.PersonalDrive.demo.security.JwtTokenProvider;
import com.PersonalDrive.demo.services.UserService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/users")
public class UserContoller {


    @Autowired
    UserService userService;

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
    private String redirectUri;

    @Value("${spring.security.oauth2.client.registration.google.scope}")
    private String scope;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String clientSecret;

    @GetMapping("/google/url")
    public Map<String, String> getGoogleAuthUrl() {
        String authorizationUrl = "https://accounts.google.com/o/oauth2/v2/auth" +
                "?client_id=" + clientId +
                "&redirect_uri=" + redirectUri +
                "&response_type=code" +
                "&scope=openid email profile" +
                "&access_type=offline" +
                "&prompt=consent";

        Map<String, String> response = new HashMap<>();
        response.put("url", authorizationUrl);
        return response;
    }

    @GetMapping("/google/callback")
    public void handleGoogleCallback(@RequestParam("code") String code, HttpServletResponse response) throws IOException {
        String tokenUrl = "https://oauth2.googleapis.com/token";

        Map<String, String> params = new HashMap<>();
        params.put("code", code);
        params.put("client_id", clientId);
        params.put("client_secret", clientSecret);
        params.put("redirect_uri", redirectUri);
        params.put("grant_type", "authorization_code");

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Map> tokenResponse = restTemplate.postForEntity(tokenUrl, params, Map.class);

        if (tokenResponse.getStatusCode() == HttpStatus.OK) {
            String accessToken = (String) tokenResponse.getBody().get("access_token");

            String userInfoUrl = "https://www.googleapis.com/oauth2/v2/userinfo";

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);

            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<Map> userInfoResponse = restTemplate.exchange(userInfoUrl, HttpMethod.GET, entity, Map.class);

            if(userInfoResponse.getStatusCode() == HttpStatus.OK) {
                Map<String, Object> userInfo = userInfoResponse.getBody();
                String email = (String) userInfo.get("email");
                String googleId = (String) userInfo.get("id");

                User user = new User();
                user.setName(email);
                user.setPassword(googleId);

                if(userService.getByName(email) == null){
                    userService.registerUser(user);
                }

                String jwtToken = jwtTokenProvider.generateToken(user);
                String frontendUrl = "http://localhost:5173/register?code=" + jwtToken;

                response.sendRedirect(frontendUrl);
            }

        } else {
            response.sendError(HttpStatus.UNAUTHORIZED.value(), "Error during authentication");
        }
    }


    @GetMapping("/")
    public String test(){
        return "Hello World";
    }


    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody User user){
        try{
            if(!userService.loginUser(user)) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid Credentials");

            return ResponseEntity.ok(jwtTokenProvider.generateToken(user));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Login failed");
        }
    }

    @PostMapping(value = "/register", consumes = "application/json")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        try {

            if (!userService.registerUser(user)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User already exists");
            }

            return ResponseEntity.ok(jwtTokenProvider.generateToken(user));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Registration failed");
        }
    }

}
