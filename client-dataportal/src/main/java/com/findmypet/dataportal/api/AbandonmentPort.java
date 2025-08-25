package com.findmypet.dataportal.api;

import com.findmypet.dataportal.api.model.Animal;
import com.findmypet.dataportal.api.model.PageResult;

import java.util.Map;

public interface AbandonmentPort {
    PageResult<Animal> getAbandonments(Map<String, String> params);
}
