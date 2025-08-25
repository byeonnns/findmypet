package com.findmypet.dataportal.api;

import com.findmypet.dataportal.api.model.Sido;
import com.findmypet.dataportal.api.model.Sigungu;
import java.util.List;

public interface LocalCodePort {
    List<Sido> getSido();
    List<Sigungu> getSigungu(String uprCd);
}