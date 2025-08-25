package com.findmypet.service.dataportal;

import com.findmypet.dataportal.api.LocalCodePort;
import com.findmypet.dataportal.api.model.Sido;
import com.findmypet.dataportal.api.model.Sigungu;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LocalCodeService {
    private final LocalCodePort port;

    public LocalCodeService(LocalCodePort port) {
        this.port = port;
    }

    public List<Sido> listSido() {
        return port.getSido();
    }

    public List<Sigungu> listSigungu(String uprCd) {
        return port.getSigungu(uprCd);
    }
}
