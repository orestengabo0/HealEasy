package org.healeasy.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.healeasy.DTOs.PaginationRequest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PaginationUtils {

    public static Pageable getPageable(PaginationRequest request) {
        // Spring's page is 0-based, but our DTO uses 1-based indexing
        int zeroBasedPage = request.getPage() - 1;
        return PageRequest.of(zeroBasedPage, request.getSize(), request.getDirection(), request.getSortField());
    }
}
