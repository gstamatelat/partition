package gr.james.partition;

import java.util.*;
import java.util.function.Function;
import java.util.stream.IntStream;

/**
 * Implementation of the {@link Partition} interface based on the Union-Find-Delete scheme in
 * <a href="https://doi.org/10.1007/11523468_7">Union-Find with Constant Time Deletions (Alstrup, Gørtz, Rauhe, Thorup,
 * Zwick)</a>.
 * <p>
 * The implementation uses a classic union-find with union-by-size and path splitting optimizations. During, the
 * <i>find</i> operation, path splitting makes every node on the path point to its grandparent. This optimization can
 * be done in a single <i>find</i> pass.
 * <p>
 * A cyclic doubly linked list is also present for every subset to allow for fast iteration over the elements of that
 * subset. The union operation merges the two linked lists in constant time. A similar doubly linked list is implemented
 * among all the root elements of the trees to allow for fast subset enumeration. As new root nodes are created or
 * existing ones deleted, this linked list is updated in constant time.
 * <p>
 * Element deletions are handled via the concept of vacant nodes. More specifically, when an element is removed from a
 * subset tree, its status is simply set to vacant and it is also removed from the subtree linked list. When deleting
 * root nodes, the root node is first swapped with any element on that subtree to avoid a persistent vacant root. As
 * path compressions occur in that tree, vacant nodes will eventually become leafs and reclaimed by the garbage
 * collector as there are no more parental references to them.
 *
 * @param <T> the element type
 */
public class UnionFindPartition<T> extends AbstractPartition<T> {
    private final Map<T, Item> items;
    private Item anyRoot;
    private int count;

    /**
     * Constructs a new empty {@link UnionFindPartition} with no elements and no subsets.
     */
    public UnionFindPartition() {
        this.items = new HashMap<>();
        this.anyRoot = null;
        this.count = 0;
    }

    private void validate() {
        assert items.values().stream().map(Item::rootNoCompress).distinct().count() == count;
        assert items.entrySet().stream().allMatch(e -> e.getKey().equals(e.getValue().item));
        assert items.values().stream().distinct().count() == items.keySet().size();
        assert items.values().stream().allMatch(el -> {
            boolean a = el.nextItem == el;
            boolean b = el.previousItem == el;
            boolean c = el.size == 1;
            boolean d = el.parent == el;
            if (a) {
                return b && c && d;
            } else {
                return true;
            }
        });
        assert items.values().stream().allMatch(el -> {
            int rootSize = el.rootNoCompress().size;
            return rootSize == cycleLength(el, x -> x.nextItem);
        });
        assert IntStream.of(0).allMatch(unused -> {
            if (anyRoot == null) {
                return count == 0;
            }
            return count == cycleLength(anyRoot, x -> x.nextComponent);
        });
        assert (anyRoot == null) || (anyRoot.parent == anyRoot);
    }

    private int cycleLength(Item start, Function<Item, Item> next) {
        int cycleSize = 1;
        for (Item current = next.apply(start); current != start; current = next.apply(current)) {
            cycleSize++;
        }
        return cycleSize;
    }

    private Item get(T t) {
        if (t == null) {
            throw new NullPointerException();
        }
        final Item item = items.get(t);
        if (item == null) {
            throw new IllegalArgumentException();
        }
        return item;
    }

    /**
     * Returns the root element of {@code t} in this {@link UnionFindPartition}.
     *
     * @param t the element
     * @return the root element of {@code t} in this {@link UnionFindPartition}
     * @throws NullPointerException     if {@code t} is {@code null}
     * @throws IllegalArgumentException if {@code t} is not in this partition
     */
    public T root(T t) {
        return get(t).root().item;
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    public int size() {
        return items.size();
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    public int subsetCount() {
        return count;
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    public Set<T> elements() {
        return Collections.unmodifiableSet(items.keySet());
    }

    /**
     * {@inheritDoc}
     *
     * @param t {@inheritDoc}
     * @return {@inheritDoc}
     * @throws NullPointerException {@inheritDoc}
     */
    @Override
    public boolean contains(T t) {
        if (t == null) {
            throw new NullPointerException();
        }
        return items.containsKey(t);
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    public Set<Set<T>> subsets() {
        return new AbstractSet<Set<T>>() {
            @Override
            public Iterator<Set<T>> iterator() {
                if (anyRoot == null) {
                    return Collections.emptyIterator();
                } else {
                    return new Iterator<Set<T>>() {
                        Item current = anyRoot;
                        boolean hasNext = true;

                        @Override
                        public boolean hasNext() {
                            return hasNext;
                        }

                        @Override
                        public Set<T> next() {
                            if (!hasNext) {
                                throw new NoSuchElementException();
                            }
                            final Item r = current;
                            current = current.nextComponent;
                            if (current == anyRoot) {
                                hasNext = false;
                            }
                            return subset(r.item);
                        }
                    };
                }
            }

            @Override
            public boolean contains(Object o) {
                if (o == null) {
                    throw new NullPointerException();
                }
                if (!(o instanceof Set)) {
                    return false;
                }
                Set otherSet = (Set) o;
                if (otherSet.isEmpty()) {
                    return false;
                }
                Item root = null;
                for (Object obj : otherSet) {
                    if (obj == null) {
                        throw new NullPointerException();
                    }
                    final Item c = items.get(obj);
                    if (c == null) {
                        return false;
                    }
                    if (root == null) {
                        root = c.root();
                        if (root.size != otherSet.size()) {
                            return false;
                        }
                    }
                    if (c.root() != root) {
                        return false;
                    }
                }
                return true;
            }

            @Override
            public int size() {
                return count;
            }
        };
    }

    /**
     * {@inheritDoc}
     *
     * @param t {@inheritDoc}
     * @return {@inheritDoc}
     * @throws NullPointerException     {@inheritDoc}
     * @throws IllegalArgumentException {@inheritDoc}
     */
    @Override
    public Set<T> subset(T t) {
        get(t);
        return new AbstractSet<T>() {
            @Override
            public Iterator<T> iterator() {
                return new Iterator<T>() {
                    Item start = get(t);
                    Item current = start;
                    boolean hasNext = true;

                    @Override
                    public boolean hasNext() {
                        return hasNext;
                    }

                    @Override
                    public T next() {
                        if (!hasNext) {
                            throw new NoSuchElementException();
                        }
                        final Item r = current;
                        current = current.nextItem;
                        if (current == start) {
                            hasNext = false;
                        }
                        return r.item;
                    }
                };
            }

            @Override
            public boolean contains(Object o) {
                if (o == null) {
                    throw new NullPointerException();
                }
                final Item c = items.get(o);
                if (c == null) {
                    return false;
                }
                return c.root() == get(t).root();
            }

            @Override
            public int size() {
                return get(t).root().size;
            }
        };
    }

    /**
     * {@inheritDoc}
     *
     * @param x {@inheritDoc}
     * @param y {@inheritDoc}
     * @return {@inheritDoc}
     * @throws NullPointerException     {@inheritDoc}
     * @throws IllegalArgumentException {@inheritDoc}
     */
    @Override
    public boolean connected(T x, T y) {
        return get(x).root() == get(y).root();
    }

    /**
     * {@inheritDoc}
     *
     * @param t {@inheritDoc}
     * @return {@inheritDoc}
     * @throws NullPointerException {@inheritDoc}
     */
    @Override
    public boolean add(T t) {
        final Item newItem = new Item(t);
        final Item previous = items.putIfAbsent(t, newItem);
        if (previous != null) {
            return false;
        }

        newItem.addToComponentList();

        count++;

        validate();

        return true;
    }

    /**
     * {@inheritDoc}
     *
     * @param t {@inheritDoc}
     * @return {@inheritDoc}
     * @throws NullPointerException {@inheritDoc}
     */
    @Override
    public boolean remove(T t) {
        if (t == null) {
            throw new NullPointerException();
        }
        Item item = items.get(t);
        if (item == null) {
            return false;
        }

        // If t is a subset by itself
        if (item.nextItem == item) {
            item.removeFromComponentList();
            final Item removed = items.remove(t);
            assert removed == item;
            count--;
            validate();
            return true;
        }

        // If item is root, swap it with any other element of the subset
        if (item.parent == item) {
            T tmp = item.item;
            item.item = item.nextItem.item;
            item.nextItem.item = tmp;
            items.put(item.item, item);
            items.put(item.nextItem.item, item.nextItem);
            item = item.nextItem;
        }

        assert item == items.get(t);
        assert item != null;
        Item root = item.root();
        assert item != root;
        assert item.parent != item;

        item.previousItem.nextItem = item.nextItem;
        item.nextItem.previousItem = item.previousItem;

        final Item removed = items.remove(t);
        assert removed == item;

        root.size--;

        validate();

        return true;
    }

    /**
     * {@inheritDoc}
     *
     * @param subset {@inheritDoc}
     * @throws NullPointerException     {@inheritDoc}
     * @throws IllegalArgumentException {@inheritDoc}
     */
    @Override
    public void addSubset(Set<T> subset) {
        if (subset.isEmpty()) {
            throw new IllegalArgumentException();
        }
        final Iterator<T> it = subset.iterator();

        assert it.hasNext();
        final T root = it.next();
        if (root == null) {
            throw new NullPointerException();
        }
        final Item rootItem = new Item(root);
        final Item previous = items.putIfAbsent(root, rootItem);
        if (previous != null) {
            throw new IllegalArgumentException();
        }
        rootItem.size = subset.size();

        Item current = rootItem;

        while (it.hasNext()) {
            final T next = it.next();
            if (next == null) {
                throw new NullPointerException();
            }
            final Item nextItem = new Item(next, rootItem);
            final Item previousOther = items.putIfAbsent(next, nextItem);
            if (previousOther != null) {
                throw new IllegalArgumentException();
            }
            current.nextItem = nextItem;
            nextItem.previousItem = current;
            current = nextItem;
        }

        rootItem.addToComponentList();

        current.nextItem = rootItem;
        rootItem.previousItem = current;

        count++;

        assert subset.stream().map(t -> get(t).parent).distinct().count() == 1;

        validate();
    }

    /**
     * {@inheritDoc}
     *
     * @param x {@inheritDoc}
     * @param y {@inheritDoc}
     * @return {@inheritDoc}
     * @throws NullPointerException     {@inheritDoc}
     * @throws IllegalArgumentException {@inheritDoc}
     */
    @Override
    public boolean union(T x, T y) {
        final Item item1 = get(x);
        final Item item2 = get(y);

        final Item root1 = item1.root();
        final Item root2 = item2.root();

        if (root1 == root2) {
            return false;
        }

        if (root1.size >= root2.size) {
            root2.parent = root1;
            root2.removeFromComponentList();
            root1.size += root2.size;
        } else {
            root1.parent = root2;
            root1.removeFromComponentList();
            root2.size += root1.size;
        }

        final Item tmp = item1.nextItem;
        item1.nextItem = item2.nextItem;
        item2.nextItem.previousItem = item1;
        item2.nextItem = tmp;
        tmp.previousItem = item2;

        count--;

        assert item1.rootNoCompress() == item2.rootNoCompress();

        validate();

        return true;
    }

    /**
     * {@inheritDoc}
     *
     * @param t {@inheritDoc}
     * @return {@inheritDoc}
     * @throws NullPointerException     {@inheritDoc}
     * @throws IllegalArgumentException {@inheritDoc}
     */
    @Override
    public boolean split(T t) {
        Item item = get(t);

        // If t is a subset by itself return false
        if (item.nextItem == item) {
            return false;
        }

        // If item is root, swap it with any other element of the subset
        if (item.parent == item) {
            T tmp = item.item;
            item.item = item.nextItem.item;
            item.nextItem.item = tmp;
            items.put(item.item, item);
            items.put(item.nextItem.item, item.nextItem);
            item = item.nextItem;
        }

        assert item == items.get(t);
        assert item != null;
        Item root = item.root();
        assert item != root;
        assert item.parent != item;

        item.previousItem.nextItem = item.nextItem;
        item.nextItem.previousItem = item.previousItem;

        final Item newItem = new Item(item.item);
        items.put(t, newItem);
        newItem.addToComponentList();

        root.size--;

        count++;

        validate();

        return true;
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return super.hashCode();
    }

    /**
     * {@inheritDoc}
     *
     * @param obj {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    public String toString() {
        return super.toString();
    }

    private class Item {
        T item;
        Item parent;
        Item nextItem;
        Item previousItem;
        Item nextComponent;
        Item previousComponent;
        int size;

        Item(T item) {
            this.item = item;
            this.parent = this;
            this.nextItem = this;
            this.previousItem = this;
            this.size = 1;
        }

        Item(T item, Item parent) {
            this.item = item;
            this.parent = parent;
            this.nextItem = this;
            this.previousItem = this;
            this.size = 1;
        }

        private Item root() {
            Item t = Item.this;
            while (t.parent != t.parent.parent) {
                Item next = t.parent;
                t.parent = next.parent;
                t = next;
            }
            return t.parent;
        }

        private Item rootNoCompress() {
            Item t = Item.this;
            while (t != t.parent) {
                t = t.parent;
            }
            return t;
        }

        private void addToComponentList() {
            if (anyRoot == null) {
                this.nextComponent = this;
                this.previousComponent = this;
                anyRoot = this;
            } else {
                final Item oldNext = anyRoot.nextComponent;
                this.nextComponent = oldNext;
                this.previousComponent = anyRoot;
                anyRoot.nextComponent = this;
                oldNext.previousComponent = this;
            }
        }

        private void removeFromComponentList() {
            if (this.nextComponent == this) {
                anyRoot = null;
            } else {
                anyRoot = this.previousComponent;
                anyRoot.nextComponent = this.nextComponent;
                this.nextComponent.previousComponent = anyRoot;
            }
        }
    }
}
