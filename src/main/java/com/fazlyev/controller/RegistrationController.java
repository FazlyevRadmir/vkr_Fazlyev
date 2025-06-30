package com.fazlyev.controller;

import com.fazlyev.dto.RegistrationDto;
import com.fazlyev.util.FirebaseErrorTranslator;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.cloud.FirestoreClient;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.HashMap;
import java.util.Map;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Controller
public class RegistrationController {
    private static final Logger log = LoggerFactory.getLogger(RegistrationController.class);

    @GetMapping("/registration")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new RegistrationDto());
        return "registration";
    }

    @PostMapping("/registration")
    public String registerUser(@Valid @ModelAttribute("user") RegistrationDto dto,
                               BindingResult bindingResult,
                               Model model) {
        try {
            if (bindingResult.hasErrors()) {
                return "registration";
            }

            if (!dto.getPassword().equals(dto.getConfirmPassword())) {
                model.addAttribute("error", "Пароли не совпадают");
                return "registration";
            }

            UserRecord userRecord = FirebaseAuth.getInstance().createUser(
                    new UserRecord.CreateRequest()
                            .setEmail(dto.getEmail())
                            .setPassword(dto.getPassword())
            );

            Firestore db = FirestoreClient.getFirestore();
            Map<String, Object> userData = new HashMap<>();
            userData.put("uid", userRecord.getUid());
            userData.put("email", dto.getEmail());
            db.collection("users").document(userRecord.getUid()).set(userData);

            return "redirect:/login?success";

        } catch (FirebaseAuthException e) {
            model.addAttribute("error", FirebaseErrorTranslator.translateRegistrationError(e));
            model.addAttribute("user", dto);
            return "registration";
        }
    }
}
