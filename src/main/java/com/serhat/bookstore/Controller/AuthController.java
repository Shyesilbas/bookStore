package com.serhat.bookstore.Controller;

import com.serhat.bookstore.Repository.BookRepository;
import com.serhat.bookstore.Repository.CustomerRepository;
import com.serhat.bookstore.dto.MostSellers;
import com.serhat.bookstore.service.KeycloakTokenService;
import com.serhat.bookstore.service.BookService;
import com.serhat.bookstore.service.SoldBookService;
import com.serhat.bookstore.exception.InvalidCredentialsException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final KeycloakTokenService keycloakTokenService;
    private final CustomerRepository customerRepository;
    private final SoldBookService soldBookService;
    private final BookService bookService;
    private final BookRepository bookRepository;

    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }
    @GetMapping("/home")
    public String home(Model model, Principal principal) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = "Anonymous";

        if (authentication != null && authentication.getPrincipal() instanceof OAuth2User oAuth2User) {
            username = oAuth2User.getAttribute("preferred_username");
            if (username == null) {
                username = oAuth2User.getAttribute("name");
            }
        }

        List<MostSellers> mostSellers = soldBookService.mostSellers(principal);
        model.addAttribute("mostSellers", mostSellers);

        model.addAttribute("username", username);
        return "home";
    }
}
