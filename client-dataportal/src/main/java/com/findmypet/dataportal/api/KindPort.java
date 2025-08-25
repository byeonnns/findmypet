package com.findmypet.dataportal.api;

import com.findmypet.dataportal.api.model.Kind;

import java.util.List;

public interface KindPort {
    List<Kind> getKinds(String upKindCd);
}
