package gr.james.partition;

import java.util.Set;

/**
 * A partition of a set {@code U} is a set of nonempty subsets of {@code U} such that every element {@code u} in
 * {@code U} is in exactly one of these subsets. The definition is from the book <i>Naive Set Theory (Halmos)</i>.
 *
 * @param <T> the element type
 */
public interface Partition<T> {
    /**
     * Returns the number of elements in this partition.
     * <p>
     * This method is equivalent to
     * <pre><code>
     * return elements().size();
     * </code></pre>
     *
     * @return the number of elements in this partition
     */
    int size();

    /**
     * Returns the number of subsets in this partition.
     * <p>
     * This method is equivalent to
     * <pre><code>
     * return subsets().size();
     * </code></pre>
     *
     * @return the number of subsets in this partition
     */
    int subsetCount();

    /**
     * Returns a readonly {@link Set} view of all the elements in this partition.
     * <p>
     * The returned {@link Set} will reflect changes to the partition. The elements are in no particular order inside
     * the returned {@link Set}.
     *
     * @return a readonly {@link Set} view of all the elements in this partition
     */
    Set<T> elements();

    /**
     * Returns a value indicating whether this partition contains the specified element.
     *
     * @param t the element
     * @return {@code true} is this partition contains {@code t}, otherwise {@code false}
     * @throws NullPointerException if {@code t} is {@code null}
     */
    boolean contains(T t);

    /**
     * Returns a readonly {@link Set} view of the subsets in this partition.
     * <p>
     * The returned {@link Set} contains all the disjoint sets in a set-of-sets scheme. The returned {@link Set} will
     * reflect changes to the partition. The disjoint sets are in no particular order inside the container.
     *
     * @return a readonly {@link Set} view of the subsets in this partition
     */
    Set<Set<T>> subsets();

    /**
     * Returns a readonly view of the subset that encloses {@code t}.
     * <p>
     * If the element {@code t} is removed from the partition in a future time, any method in the returned view will
     * throw {@link IllegalArgumentException}, until this element is reinserted in the partition.
     * <p>
     * The elements are in no particular order inside the returned {@link Set}.
     *
     * @param t the element
     * @return a readonly view of the subset that encloses {@code t}
     * @throws NullPointerException     if {@code t} is {@code null}
     * @throws IllegalArgumentException if {@code t} is not in this partition
     */
    Set<T> subset(T t);

    /**
     * Returns a value indicating whether two elements are in the same subset.
     * <p>
     * More specifically, returns {@code true} if {@code x} and {@code y} are in the same subset, otherwise
     * {@code false}.
     * <p>
     * The {@code connected} method is commutative and the call {@code connected(x, y)} will have exactly the same
     * behavior as {@code connected(y, x)}.
     * <p>
     * This method is equivalent to
     * <pre><code>
     * return Objects.equals(subset(x), subset(y));
     * </code></pre>
     *
     * @param x one element
     * @param y the other element
     * @return {@code true} if {@code x} and {@code y} and in the same subset, otherwise {@code false}
     * @throws NullPointerException     if {@code x} or {@code y} is {@code null}
     * @throws IllegalArgumentException if not both {@code x} and {@code y} are elements of this partition
     */
    boolean connected(T x, T y);

    /**
     * Inserts a new element in its own subset if it's not already present in the partition.
     *
     * @param t the new element
     * @return {@code true} if this partition did not already contain the specified element, otherwise {@code false}
     * @throws NullPointerException          if {@code t} is {@code null}
     * @throws UnsupportedOperationException if the operation is not supported by this partition
     */
    boolean add(T t);

    /**
     * Inserts a new element in an existing subset if it's not already present in the partition.
     * <p>
     * If the element is already present in this partition, no further operation will be performed and {@code false}
     * will be returned. As a result the call to {@code union(z, z)} will return {@code false} if {@code z} is already
     * in the partition and {@code true} if it's not. In the latter case, {@code z} will end up in its own subset.
     * <p>
     * This method is equivalent to
     * <pre><code>
     * if (add(x)) {
     *     union(x, y);
     *     return true;
     * } else {
     *     return false;
     * }
     * </code></pre>
     * but may be optimized in the underlying implementation to run faster.
     *
     * @param x the new element
     * @param y the existing element to connect to
     * @return {@code true} if this partition did not already contain the specified element, otherwise {@code false}
     * @throws NullPointerException          if {@code x} or {@code y} is {@code null}
     * @throws IllegalArgumentException      if {@code y} is not an element of this partition
     * @throws UnsupportedOperationException if the operation is not supported by this partition
     */
    boolean merge(T x, T y);

    /**
     * Removes the specified element from this partition if it is present.
     * <p>
     * As a result, this method also removes the element from its disjoint subset. The return value indicates whether a
     * change in the partition occurred as a result of this method. {@code true} indicates that {@code t} was present in
     * the partition and it was removed, while {@code false} indicates that the element was not present and no action
     * took place.
     *
     * @param t the element to be removed from this partition, if present
     * @return {@code true} if this partition contained the specific element, otherwise {@code false}
     * @throws NullPointerException          if {@code t} is {@code null}
     * @throws UnsupportedOperationException if the operation is not supported by this partition
     */
    boolean remove(T t);

    /**
     * Removes all the elements from this partition.
     * <p>
     * The partition will be empty after this method returns. An empty partition contains no elements and no subsets.
     *
     * @throws UnsupportedOperationException if the operation is not supported by this partition
     */
    void clear();

    /**
     * Inserts a {@link Set} of elements as a connected subset.
     *
     * @param subset the subset
     * @throws NullPointerException          if {@code subset} or any element in {@code subset} is {@code null}
     * @throws IllegalArgumentException      if any element in {@code subset} is already an element of this partition
     * @throws IllegalArgumentException      if {@code subset} is empty
     * @throws UnsupportedOperationException if the operation is not supported by this partition
     */
    void addSubset(Set<T> subset);

    /**
     * Removes the subset enclosing the specified element from this partition.
     * <p>
     * In other words, this method will remove all elements in the same subset as {@code t} (including {@code t}) from
     * the partition. The return value indicates whether a change in the partition occurred as a result of this method.
     * {@code true} indicates that {@code t} was present in the partition and at least one element was removed, while
     * {@code false} indicates that the element was not present and no action took place.
     *
     * @param t the element
     * @return {@code true} if this partition contained the specific element and its subset was removed, otherwise
     * {@code false}
     * @throws NullPointerException          if {@code t} is {@code null}
     * @throws UnsupportedOperationException if the operation is not supported by this partition
     */
    boolean removeSubset(T t);

    /**
     * Connect {@code x} and {@code y} so that their disjoint subsets are merged.
     * <p>
     * If {@code x} and {@code y} are on the same subset or if {@code x.equals(y)}, this method does nothing and returns
     * {@code false}. Otherwise, the disjoint sets of {@code x} and {@code y} will be merged into a single one and
     * {@code true} will be returned. As a result, the union of an element with itself is a no-op and will return
     * {@code false}.
     * <p>
     * The {@code union} method is commutative and the call {@code union(x, y)} will have exactly the same behavior as
     * {@code union(y, x)}.
     *
     * @param x one element
     * @param y the other element
     * @return {@code true} if {@code x} and {@code y} were not already in the same subset, otherwise {@code false}
     * @throws NullPointerException          if {@code x} or {@code y} is {@code null}
     * @throws IllegalArgumentException      if not both {@code x} and {@code y} are elements of this partition
     * @throws UnsupportedOperationException if the operation is not supported by this partition
     */
    boolean union(T x, T y);

    /**
     * Splits an element into its own subset.
     * <p>
     * If {@code t} is already a singleton subset, this method does nothing and returns {@code false}.
     * <p>
     * This method is semantically equivalent to
     * <pre><code>
     * if (elements().contains(t)) {
     *     remove(t);
     *     add(t);
     * }
     * </code></pre>
     *
     * @param t the element
     * @return {@code true} if {@code t} was splitted into its own subset, or {@code false} if {@code t} was already a
     * singleton subset
     * @throws NullPointerException          if {@code t} is {@code null}
     * @throws IllegalArgumentException      if {@code t} is not in this partition
     * @throws UnsupportedOperationException if the operation is not supported by this partition
     */
    boolean split(T t);

    /**
     * Moves an element to a different subset.
     * <p>
     * More specifically, this method will remove {@code x} from its subset and place it in the subset containing
     * {@code y}. If {@code x} and {@code y} are already in the same subset, this method does nothing and returns
     * {@code false}.
     * <p>
     * This method is, thus, semantically equivalent to
     * <pre><code>
     * if (!connected(x, y)) {
     *     split(x);
     *     union(x, y);
     * }
     * </code></pre>
     *
     * @param x the source element to move
     * @param y the element that portrays the target subset
     * @return {@code true} if {@code x} and {@code y} were not already in the same subset, otherwise {@code false}
     * @throws NullPointerException          if {@code x} or {@code y} is {@code null}
     * @throws IllegalArgumentException      if not both {@code x} and {@code y} are elements of this partition
     * @throws UnsupportedOperationException if the operation is not supported by this partition
     */
    boolean move(T x, T y);

    /**
     * Returns a string representation of this partition.
     *
     * @return a string representation of this partition
     */
    @Override
    String toString();

    /**
     * Indicates whether some object is equal to this partition.
     * <p>
     * This method will return {@code true} if {@code obj} is of type {@link Partition} and is equal to this partition.
     * Two partitions are equal if they contain the same number of elements, the same number of subsets and each subset
     * exists in both partitions.
     *
     * @param obj the reference object with which to compare
     * @return {@code true} if {@code obj} is equal to this partition, otherwise {@code false}
     */
    @Override
    boolean equals(Object obj);

    /**
     * Returns a hash code value for this partition.
     *
     * @return a hash code value for this partition
     */
    @Override
    int hashCode();
}
