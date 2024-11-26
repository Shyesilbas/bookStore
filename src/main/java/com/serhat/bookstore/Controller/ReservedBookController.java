package com.serhat.bookstore.Controller;

import com.serhat.bookstore.dto.*;
import com.serhat.bookstore.service.ReservedBookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RequestMapping("/api/reservation")
@RequiredArgsConstructor
@RestController
public class ReservedBookController {
    private final ReservedBookService reservedBookService;

    @PostMapping("/reserveBook")
    public ResponseEntity<ReserveBookResponse> reserveBook(@RequestBody ReserveBookRequest request , Principal principal){
        return ResponseEntity.ok(reservedBookService.reserveABook(request , principal));
    }
    @PostMapping("/returnReservedBook")
    public ResponseEntity<ReturnReservedBookResponse> returnReservedBook(@RequestBody ReturnReservedBookRequest request , Principal principal){
        return ResponseEntity.ok(reservedBookService.returnReservedBook(request,principal));
    }

    @GetMapping("/listActiveReservations")
    public ResponseEntity<List<ActiveReservationsResponse>> getActiveReservations(Principal principal){
        return ResponseEntity.ok(reservedBookService.activeReservationsList(principal));
    }

    @GetMapping("/listExpiredReservations")
    public ResponseEntity<List<ExpiredReservationsResponse>> getExpiredReservations(Principal principal){
        return ResponseEntity.ok(reservedBookService.expiredReservationsList(principal));
    }

    @GetMapping("/listLateReservations")
    public ResponseEntity<List<LateReservationResponse>> getLateReservations(Principal principal){
        return ResponseEntity.ok(reservedBookService.lateReservationsList(principal));
    }


}
