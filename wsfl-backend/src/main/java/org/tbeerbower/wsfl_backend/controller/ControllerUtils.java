package org.tbeerbower.wsfl_backend.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

public class ControllerUtils {





    public static <T> Page<T> getPaginatedEntities(List<T> allEntities, Pageable pageable) {

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), allEntities.size());

        List<T> paginatedEntities = allEntities.subList(start, end);
        return new PageImpl<>(paginatedEntities, pageable, allEntities.size());
    }
}
