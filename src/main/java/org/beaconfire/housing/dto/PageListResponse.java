package org.beaconfire.housing.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PageListResponse<T> {
    private List<T> list;
    private int current;
    private int pageSize;
    private long total;
}