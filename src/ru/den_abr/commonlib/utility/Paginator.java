package ru.den_abr.commonlib.utility;

import com.google.common.collect.ImmutableList;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Paginator<T>
{
    private final List<List<T>> pages;
    private final int pageSize;
    
    public Paginator(final Collection<T> objects, final int pageSize) {
        this.pageSize = pageSize;
        final ImmutableList.Builder<List<T>> builder = (ImmutableList.Builder<List<T>>)new ImmutableList.Builder();
        ImmutableList.Builder<T> pageBuilder = (ImmutableList.Builder<T>)new ImmutableList.Builder();
        int size = 0;
        for (final T object : objects) {
            if (size == pageSize) {
                builder.add(pageBuilder.build());
                pageBuilder = (ImmutableList.Builder<T>)new ImmutableList.Builder();
                size = 0;
            }
            pageBuilder.add(object);
            ++size;
        }
        builder.add(pageBuilder.build());
        this.pages = builder.build();
    }
    
    public int getPageSize() {
        return this.pageSize;
    }
    
    public int getStartIndex(final int page) {
        return (page - 1) * this.pageSize + 1;
    }
    
    public List<T> getPage(int p) {
        if (--p >= this.getPages() || p < 0) {
            return Collections.emptyList();
        }
        return this.pages.get(p);
    }
    
    public List<List<T>> getPagesList() {
        return this.pages;
    }
    
    public int getPages() {
        return this.pages.size();
    }
}
