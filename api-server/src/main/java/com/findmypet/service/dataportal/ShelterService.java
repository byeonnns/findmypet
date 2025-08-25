package com.findmypet.service.dataportal;

import com.findmypet.dataportal.api.ShelterPort;
import com.findmypet.dataportal.api.model.Shelter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ShelterService {
    private final ShelterPort shelterPort;

    public List<Shelter> getShelters(String uprCd, String orgCd) {
        return shelterPort.getShelters(uprCd, orgCd);
    }
}
