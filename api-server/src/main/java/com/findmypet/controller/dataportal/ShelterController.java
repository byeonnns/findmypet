package com.findmypet.controller.dataportal;

import com.findmypet.dataportal.api.model.Shelter;
import com.findmypet.service.dataportal.ShelterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/dataportal")
@RequiredArgsConstructor
public class ShelterController {
    private final ShelterService shelterService;

    @GetMapping("/shelter")
    public ResponseEntity<List<Shelter>> getShelters(
            @RequestParam("uprCd") String uprCd,
            @RequestParam("orgCd") String orgCd
    ) {
        return ResponseEntity.ok(shelterService.getShelters(uprCd, orgCd));
    }
}
