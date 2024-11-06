package com.spay.wallet.common;

import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
public class CustomPage<T> {
    private final Long totalItems;
    private final Boolean isLast;
    private final Boolean isFirst;
    private final Integer pageNumber;
    private final Integer totalPages;
    private final Integer pageSize;
    private final Integer count;
    public final List<T> items;

    public CustomPage(Page<T> page){
        this.items =  page.getContent();
        this.totalItems =  page.getTotalElements();
        this.isLast = page.isLast();
        this.isFirst = page.isFirst();
        this.pageNumber =  page.getNumber();
        this.totalPages = page.getTotalPages();
        this.pageSize = page.getSize();
        this.count = this.items.size();
    }
}
