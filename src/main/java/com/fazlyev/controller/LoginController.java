package com.fazlyev.controller;

import com.fazlyev.model.LoginForm;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("loginForm", new LoginForm());
        return "login";
    }

//    @PostMapping("/login")
//    public String loginProcess(
//            @ModelAttribute("loginForm") LoginForm loginForm,
//            HttpSession session,
//            Model model) {
//        try {
//            UserRecord userRecord = FirebaseAuth.getInstance().getUserByEmail(loginForm.getEmail());
//            session.setAttribute("uid", userRecord.getUid());
//            session.setAttribute("email", userRecord.getEmail());
//            session.setMaxInactiveInterval(30 * 60);
//            return "redirect:/personalAccount";
//        } catch (FirebaseAuthException e) {
//            model.addAttribute("error", "Неверный email или пароль");
//            return "login";
//        }
//    }

}