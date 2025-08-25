package com.findmypet.controller.dataportal;

import com.findmypet.dataportal.api.model.Kind;
import com.findmypet.service.dataportal.KindService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/dataportal")
@RequiredArgsConstructor
public class KindController {
    private final KindService kindService;

    @GetMapping("/kind")
    public ResponseEntity<List<Kind>> getKinds(@RequestParam("upKindCd") String upKindCd) {
        return ResponseEntity.ok(kindService.getKinds(upKindCd));
    }
}
