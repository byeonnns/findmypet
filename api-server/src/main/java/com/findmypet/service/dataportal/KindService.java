package com.findmypet.service.dataportal;

import com.findmypet.dataportal.api.KindPort;
import com.findmypet.dataportal.api.model.Kind;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class KindService {
    private final KindPort kindPort;

    public List<Kind> getKinds(String upKindCd) {
        return kindPort.getKinds(upKindCd);
    }
}
