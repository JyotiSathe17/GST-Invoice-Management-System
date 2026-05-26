package com.fullstack.gstbillingwithinvoicemngmt.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Mapper {
    /**
     * @apiNote <b>S</b> Input object type & <b>D</b> Output object type
     */

    private static final ObjectMapper OBJECT_MAPPER;

    static {
        OBJECT_MAPPER = new ObjectMapper();
    }

    private Mapper() {
        throw new IllegalStateException("The class is utility class can't create object");
    }

    public static <D, S> D map(final S fromClass, Class<D> toClass) {
        return OBJECT_MAPPER.convertValue(fromClass, toClass);
    }

    public static <D, S> List<D> map(final Collection<S> fromClass, Class<D> toClass) {
        return fromClass.parallelStream().map(f -> OBJECT_MAPPER.convertValue(f, toClass)).toList();
    }

    public static <D, S> Page<D> map(final Page<S> page, Class<D> toClass) {
        if (null != page && !page.isEmpty()) {
            List<D> oplist = map(page.getContent(), toClass);

            return new PageImpl<>(oplist, page.getPageable(), page.getTotalElements());
        }
        return new PageImpl<>(new ArrayList<>());
    }
}
