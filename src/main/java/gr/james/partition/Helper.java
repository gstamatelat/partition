package gr.james.partition;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

class Helper {
    @SafeVarargs
    @SuppressWarnings("varargs")
    static <E> Set<E> hashSetOf(E... elements) {
        final Set<E> set = new HashSet<>();
        Collections.addAll(set, elements);
        return set;
    }

    @SafeVarargs
    @SuppressWarnings("varargs")
    static <E> Set<E> linkedSetOf(E... elements) {
        final Set<E> set = new LinkedHashSet<>();
        Collections.addAll(set, elements);
        return set;
    }
}
