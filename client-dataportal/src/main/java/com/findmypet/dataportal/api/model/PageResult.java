package com.findmypet.dataportal.api.model;

import java.util.List;

public record PageResult<T>(
        List<T> items,
        int pageNo,
        int numOfRows,
        int totalCount
) {
    public boolean isEmpty() {
        return items == null || items.isEmpty();
    }

    public int totalPages() {
        if (numOfRows <= 0) return 0;
        return (int) Math.ceil(totalCount / (double) numOfRows);
    }

    public boolean hasNext() {
        return pageNo < totalPages();
    }
}
