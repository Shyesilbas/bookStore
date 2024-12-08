package com.serhat.bookstore.Controller;

import com.serhat.bookstore.dto.BuyBookRequest;
import com.serhat.bookstore.dto.BuyBookResponse;
import com.serhat.bookstore.dto.MostSellers;
import com.serhat.bookstore.model.Genre;
import com.serhat.bookstore.service.SoldBookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/buy")
public class SoldBookController {
    private final SoldBookService soldBookService;

    @PostMapping("/buyBook")
    private ResponseEntity<BuyBookResponse> buyBook (@RequestParam("title") String title, Principal principal){
        BuyBookRequest request = new BuyBookRequest(title);
        return ResponseEntity.ok(soldBookService.BuyBook(request, principal));
    }

    @GetMapping("/mostSellers")
    public ResponseEntity<List<MostSellers>> mostSellers(Principal principal){
        return ResponseEntity.ok(soldBookService.mostSellers(principal));
    }
    @GetMapping("/mostSellersByGenre")
    private ResponseEntity<List<MostSellers>> mostSellersByGenre(@RequestParam  Genre genre, Principal principal){
        return ResponseEntity.ok(soldBookService.mostSellersByGenre(genre , principal));
    }
    @GetMapping("/mostSellersOfAuthor")
    private ResponseEntity<List<MostSellers>> mostSellersOfAuthor(@RequestParam  String author, Principal principal){
        return ResponseEntity.ok(soldBookService.mostSellersOfAuthor(author , principal));
    }


}
