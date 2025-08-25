package com.findmypet.controller.dataportal;

import com.findmypet.dataportal.api.model.Sido;
import com.findmypet.dataportal.api.model.Sigungu;
import com.findmypet.service.dataportal.LocalCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/dataportal")
public class LocalCodeController {

    private final LocalCodeService service;

    @GetMapping("/sido")
    public List<Sido> sido() {
        return service.listSido();
    }

    @GetMapping("/sigungu")
    public List<Sigungu> sigungu(@RequestParam String uprCd) {
        return service.listSigungu(uprCd);
    }
}
