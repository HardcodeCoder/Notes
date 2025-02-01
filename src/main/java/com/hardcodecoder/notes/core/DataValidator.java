package com.hardcodecoder.notes.core;

import org.springframework.lang.Nullable;

public interface DataValidator<E> {

    boolean validate(@Nullable E e);
}