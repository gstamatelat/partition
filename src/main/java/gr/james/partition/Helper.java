package gr.james.partition;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

class Helper {
    @SafeVarargs
    @SuppressWarnings("varargs")
    static <E> Set<E> newHashSet(E... elements) {
        final Set<E> set = new HashSet<>(elements.length);
        Collections.addAll(set, elements);
        return set;
    }

    @SafeVarargs
    @SuppressWarnings("varargs")
    static <E> Set<E> newLinkedHashSet(E... elements) {
        final Set<E> set = new LinkedHashSet<>(elements.length);
        Collections.addAll(set, elements);
        return set;
    }
}
