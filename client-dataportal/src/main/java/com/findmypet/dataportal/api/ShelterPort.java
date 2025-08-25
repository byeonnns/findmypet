package com.findmypet.dataportal.api;

import com.findmypet.dataportal.api.model.Shelter;

import java.util.List;

public interface ShelterPort {
    List<Shelter> getShelters(String uprCd, String orgCd);
}
