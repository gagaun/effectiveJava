import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Set;
import java.util.SortedMap;

public class TreeMapGaun<K, V> implements NavigableMap<K, V>, Cloneable,
		Serializable {

	private static final long serialVersionUID = 2979441756534653715L;

	private static final boolean BLACK = true;
	private static final boolean RED = false;

	private final Comparator<? super K> comparator;
	private transient Entry<K, V> root = null; // tree root node

	private final Collection<V> EMPTY_VALUES = new ArrayList<V>(0);
	private final Set<K> EMPTY_KEYSET = new HashSet<K>(0);
	private final Set<java.util.Map.Entry<K, V>> EMPTY_ENTRYSET = new HashSet<java.util.Map.Entry<K, V>>(0);

	private transient int size = 0;
	private transient int modCount = 0;

	public TreeMapGaun() {
		comparator = null;
	}

	public TreeMapGaun(Comparator<? super K> comparator) {
		this.comparator = comparator;
	}

	public TreeMapGaun(Map<? extends K, ? extends V> m) {
		comparator = null;
	}

	public TreeMapGaun(SortedMap<K, ? extends V> m) {
		comparator = m.comparator();
	}

	@Override
	public Comparator<? super K> comparator() {
		return comparator;
	}

	/**
	 * Returns the first (lowest) key currently in this map. 가장 왼쪽 아래 노드의 키
	 */
	@Override
	public K firstKey() {
		return firstEntry().getKey();
	}

	@Override
	public K lastKey() {
		return lastEntry().getKey();
	}

	@Override
	public Set<K> keySet() {
		if (root == null) {
			return EMPTY_KEYSET;
		}

		Entry<K, V> r = root;
		for (; r.left != null;) { // get first Node
			r = r.left;
		}

		Set<K> set = new HashSet<K>(size);
		set.add(r.key);

		for (; r.parent != null;) { // left
			r = r.parent;
			set.add(r.key);
			if (r.right != null && r != root)
				set.add(r.right.key);
		}

		for (; r.right != null;) { // right
			r = r.right;
			if (r.left != null)
				set.add(r.left.key);
			set.add(r.key);
		}

		return set;
	}

	@Override
	public Collection<V> values() {
		if (root == null) {
			return EMPTY_VALUES;
		}

		Entry<K, V> r = root;
		for (; r.left != null;) {
			r = r.left;
		}

		Collection<V> values = new ArrayList<V>(size);
		values.add(r.value);

		for (; r.parent != null;) {
			r = r.parent;
			values.add(r.value);
			if (r.right != null && r != root)
				values.add(r.right.value);
		}

		for (; r.right != null;) {
			r = r.right;
			if (r.left != null)
				values.add(r.left.value);
			values.add(r.value);
		}

		return values;
	}

	@Override
	public Set<java.util.Map.Entry<K, V>> entrySet() {
		if (root == null) {
			return EMPTY_ENTRYSET;
		}

		Entry<K, V> r = root;
		for (; r.left != null;) {
			r = r.left;
		}

		Set<java.util.Map.Entry<K, V>> set = new HashSet<Map.Entry<K, V>>(size);
		set.add(r);

		for (; r.parent != null;) {
			r = r.parent;
			set.add(r);
			if (r.right != null && r != root)
				set.add(r.right);
		}

		for (; r.right != null;) {
			r = r.right;
			if (r.left != null)
				set.add(r.left);
			set.add(r);
		}

		return set;
	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public boolean isEmpty() {
		return (size == 0);
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean containsKey(Object key) {
		int comp;
		Entry<K, V> r = root;

		if (comparator == null) {
			Comparable<? super K> k = (Comparable<? super K>) key;
			do {
				comp = k.compareTo(r.key);
				if (comp < 0) {
					r = r.left;
				} else if (comp > 0) {
					r = r.right;
				} else {
					return true;
				}
			} while (r != null);
		} else {
			do {
				comp = comparator.compare((K) key, r.key);
				if (comp < 0) {
					r = r.left;
				} else if (comp > 0) {
					r = r.right;
				} else {
					return true;
				}
			} while (r != null);
		}

		return false;
	}

	@Override
	public boolean containsValue(Object value) {
		Entry<K, V> r = root;
		for (; r.left != null;) { // get first Node
			r = r.left;
		}

		for (; r.parent != null;) {
			if (r.value.equals(value)) {
				return true;
			}
			r = r.parent;
			if (r.right != null && r.right != root
					&& r.right.value.equals(value)) {
				return true;
			}
		}

		if (r.value.equals(value)) {
			return true;
		}

		for (; r != null;) {
			if ((r.value.equals(value))
					|| (r.left != null && r.value.equals(value))) {
				return true;
			}
			r = r.right;
		}

		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public V get(Object key) {
		if (key.equals(root.key)) {
			return root.value;
		}

		int comp;
		Entry<K, V> r = root;

		if (comparator == null) {
			Comparable<? super K> k = (Comparable<? super K>) key;
			do {
				comp = k.compareTo(r.key);
				if (comp < 0) {
					r = r.left;
				} else if (comp > 0) {
					r = r.right;
				} else {
					return r.value;
				}
			} while (r != null);
		} else {
			do {
				comp = comparator.compare((K) key, r.key);
				if (comp < 0) {
					r = r.left;
				} else if (comp > 0) {
					r = r.right;
				} else {
					return r.value;
				}
			} while (r != null);
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public V put(K key, V value) {
		Entry<K, V> r = root;

		if (r == null) {
			root = new Entry<>(key, value, null);
			size++;
			modCount++;
			return null;
		}

		int comp;
		Entry<K, V> parent;

        Comparator<? super K> cpr = comparator;
		if (cpr == null) {
			if (key == null)
                throw new NullPointerException();
			
			Comparable<? super K> k = ((Comparable<? super K>) key);
			do {
				parent = r;
				comp = k.compareTo(r.key);
				if (comp < 0) {
					r = r.left;
				} else if (comp > 0) {
					r = r.right;
				} else {
					return r.setValue(value); // key가 같을 땐 newValue로 바꿈
				}
			} while (r != null);
		} else {
			do {
				parent = r;
				comp = comparator.compare(key, r.key);
				if (comp < 0) {
					r = r.left;
				} else if (comp > 0) {
					r = r.right;
				} else {
					return r.setValue(value);
				}
			} while (r != null);
		}

		Entry<K, V> entry = new Entry<K, V>(key, value, parent);
		if (comp < 0) {
			parent.left = entry;
		} else {
			parent.right = entry;
		}

		fixAfterInsertion(entry);
		size++;
		modCount++;
		
		return null;
	}

	private void fixAfterInsertion(Entry<K, V> x) { // setColor, rotate
		x.color = RED;

			if (grand(x) == null) {
				x.color = BLACK;
			} else if (parent(x) == leftOf(grand(x))) {
				Entry<K, V> uncle = rightOf(grand(x));
				if (uncle != null && uncle.color == RED) {
					uncle.color = BLACK;
					x.parent.color = BLACK;
					grand(x).color = RED;
					while (x != null && x != root && x.parent.color == RED) {
						x = grand(x);
						fixAfterInsertion(x);
					}
				} else {
					if (x == rightOf(x.parent)) {
						rotateLeft(x.parent);
						x = x.left;
					} else {
						x.parent.color = BLACK;
						grand(x).color = RED;
						rotateRight(grand(x));
					}
				}
			} else {
				Entry<K, V> uncle = leftOf(grand(x));
				if (uncle != null && uncle.color == RED) {
					uncle.color = BLACK;
					x.parent.color = BLACK;
					grand(x).color = RED;
					while (x != null && x != root && x.parent.color == RED) {
						x = grand(x);
						fixAfterInsertion(x);
					}
				} else {
					if (x == leftOf(x.parent)) {
						rotateRight(x.parent);
						x = x.right;
					} else {
						x.parent.color = BLACK;
						grand(x).color = RED;
						rotateLeft(grand(x));
					}
				}
			}	
		

		root.color = BLACK;
	}
	

	@SuppressWarnings("unchecked")
	private Entry<K, V> getEntry(Object key) {
		if (key.equals(root.key)) {
			return root;
		}

		int comp;
		Entry<K, V> r = root;

		if (comparator == null) {
			Comparable<? super K> k = (Comparable<? super K>) key;
			do {
				comp = k.compareTo(r.key);
				if (comp < 0) {
					r = r.left;
				} else if (comp > 0) {
					r = r.right;
				} else {
					return r;
				}
			} while (r != null);
		} else {
			do {
				comp = comparator.compare((K) key, r.key);
				if (comp < 0) {
					r = r.left;
				} else if (comp > 0) {
					r = r.right;
				} else {
					return r;
				}
			} while (r != null);
		}

		return null;
	}

	@Override
	public V remove(Object key) {
		Entry<K, V> r = getEntry(key);

		if (r == null) {
			return null;
		}

		return deleteEntry(r);
	}

	private V deleteEntry(Entry<K, V> x) {
		V v = x.value;

		if (x.left == null && x.right == null) {
			if (x.parent == null) {
				root = null;
			} else {
				if (x == leftOf(x.parent)) {
					x.parent.left = null;
				} else {
					x.parent.right = null;
				}
			}
		} else {
			Entry<K, V> retireEntry = (x.left == null) ? x.right : x.left;

			if (x == root) {
				root = retireEntry;
			} else if (x == x.parent.left) {
				x.parent.left = retireEntry;
			} else {
				x.parent.right = retireEntry;
			}

			retireEntry.parent = x.parent;
			fixAfterRemove(retireEntry);
		}

		x = null;
		size--;
		modCount++;

		return v;
	}

	private void fixAfterRemove(Entry<K, V> x) {
		while (x != root && x.color == BLACK) {
			if (x == leftOf(x.parent)) {
				Entry<K, V> sib = rightOf(x.parent);

				if (sib.color == RED) {
					sib.color = BLACK;
					x.parent.color = BLACK;
					rotateLeft(x.parent);
					sib = rightOf(x.parent);
				}

				if (leftOf(sib).color == BLACK && rightOf(sib).color == BLACK) {
					sib.color = RED;
					x = x.parent;
				} else {
					if (rightOf(sib).color == BLACK) {
						leftOf(sib).color = BLACK;
						sib.color = RED;
						rotateRight(sib);
						sib = rightOf(x.parent);
					}
					sib.color = x.parent.color;
					x.parent.color = BLACK;
					rightOf(sib).color = BLACK;
					rotateLeft(x.parent);
					x = root;
				}
			} else {
				Entry<K, V> sib = leftOf(x.parent);

				if (sib.color == RED) {
					sib.color = BLACK;
					x.parent.color = BLACK;
					rotateRight(x.parent);
					sib = leftOf(x.parent);
				}

				if (rightOf(sib).color == BLACK && leftOf(sib).color == BLACK) {
					sib.color = RED;
					x = x.parent;
				} else {
					if (leftOf(sib).color == BLACK) {
						rightOf(sib).color = BLACK;
						sib.color = RED;
						rotateLeft(sib);
						sib = leftOf(x.parent);
					}
					sib.color = x.parent.color;
					x.parent.color = BLACK;
					leftOf(sib).color = BLACK;
					rotateRight(x.parent);
					x = root;
				}

			}
		}
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> map) {
	}

	@Override
	public void clear() {
		size = 0;
		root = null;
	}

	@Override
	public java.util.Map.Entry<K, V> lowerEntry(K key) {
		int comp = compare(key, root.key);
		Entry<K, V> r = getEntry(key);

		if (comp < 0) { // left of root
			return r == rightOf(r.parent) ? r.parent : rightOf(leftOf(r));
		} else if (comp > 0) {
			if (r == rightOf(r.parent)) {
				return (leftOf(r) != null) ? leftOf(r) : r.parent;
			} else {
				return grand(r);
			}
		} else {
			return rightOf(leftOf(r));
		}
	}

	@Override
	public K lowerKey(K key) {
		return lowerEntry(key).getKey();
	}

	@Override
	public java.util.Map.Entry<K, V> floorEntry(K key) {
		return null;
	}

	@Override
	public K floorKey(K key) {
		return null;
	}

	@Override
	public java.util.Map.Entry<K, V> ceilingEntry(K key) {
		return null;
	}

	@Override
	public K ceilingKey(K key) {
		return null;
	}

	/**
	 * Returns a key-value mapping associated with the least key strictly
	 * greater than the given key, or null if there is no such key.
	 **/
	@Override
	public java.util.Map.Entry<K, V> higherEntry(K key) {
		int comp = compare(key, root.key);
		Entry<K, V> r = getEntry(key);

		if (comp < 0) { // left of root
			return (r == rightOf(r.parent) && grand(r) != null) ? grand(r)
					: rightOf(r);
		} else if (comp > 0) {
			return (r == leftOf(r.parent)) ? r.parent : leftOf(rightOf(r));
		} else {
			return leftOf(rightOf(r));
		}
	}

	@Override
	public K higherKey(K key) {
		return higherEntry(key).getKey();
	}

	@Override
	public java.util.Map.Entry<K, V> firstEntry() {
		if (root == null) {
			return null;
		}

		Entry<K, V> r = root;
		for (; r.left != null;) {
			r = r.left;
		}
		return r;
	}

	@Override
	public java.util.Map.Entry<K, V> lastEntry() {
		if (root == null) {
			return null;
		}

		Entry<K, V> r = root;
		while (r.right != null) {
			r = r.right;
		}

		return r;
	}

	@Override
	public java.util.Map.Entry<K, V> pollFirstEntry() {
		if (root == null) {
			return null;
		}

		Entry<K, V> r = root;
		for (; r.left != null;) {
			r = r.left;
		}

		deleteEntry(r);

		return r;
	}

	@Override
	public java.util.Map.Entry<K, V> pollLastEntry() {
		if (root == null) {
			return null;
		}

		Entry<K, V> r = root;
		for (; r.right != null;) {
			r = r.right;
		}

		deleteEntry(r);

		return r;
	}

	@Override
	public NavigableMap<K, V> descendingMap() {
		return null;
	}

	@Override
	public NavigableSet<K> navigableKeySet() {
		return null;
	}

	@Override
	public NavigableSet<K> descendingKeySet() {
		return null;
	}

	@Override
	public NavigableMap<K, V> subMap(K fromKey, boolean fromInclusive, K toKey,
			boolean toInclusive) {
		return null;
	}

	@Override
	public NavigableMap<K, V> headMap(K toKey, boolean inclusive) {
		return null;
	}

	@Override
	public NavigableMap<K, V> tailMap(K fromKey, boolean inclusive) {
		return null;
	}

	@Override
	public SortedMap<K, V> subMap(K fromKey, K toKey) {
		return null;
	}

	@Override
	public SortedMap<K, V> headMap(K toKey) {
		return null;
	}

	@Override
	public SortedMap<K, V> tailMap(K fromKey) {
		return null;
	}

	@SuppressWarnings("unchecked")
	final int compare(Object key1, Object key2) {
		int comp = 0;
		if (comparator == null) {
			Comparable<? super K> k = (Comparable<? super K>) key1;
			comp = k.compareTo((K) key2);
		} else {
			comp = comparator.compare((K) key1, (K) key2);
		}
		return comp;
	}

	static final class Entry<K,V> implements Map.Entry<K,V> {
        K key;
        V value;
        Entry<K,V> left = null;
        Entry<K,V> right = null;
        Entry<K,V> parent;
        boolean color = BLACK;

        Entry(K key, V value, Entry<K,V> parent) {
            this.key = key;
            this.value = value;
            this.parent = parent;
        }

        public K getKey() {
            return key;
        }

        public V getValue() {
            return value;
        }

        public V setValue(V value) {
            V oldValue = this.value;
            this.value = value;
            return oldValue;
        }

        public String toString() {
            return key + "=" + value + (color == true ? "black" : "red" );
        }
    }	
	static final boolean valEquals(Object o1, Object o2) {
        return (o1==null ? o2==null : o1.equals(o2));
    }
	
	private static <K,V> Entry<K,V> parent(Entry<K,V> p) {
        return (p == null ? null: p.parent);
    }

	private static <K, V> Entry<K, V> grand(Entry<K, V> x) {
		return (x.parent != null && x.parent.parent != null)  ? x.parent.parent : null;
	}

	private static <K, V> Entry<K, V> leftOf(Entry<K, V> x) {
		return x == null ? null : x.left;
	}

	private static <K, V> Entry<K, V> rightOf(Entry<K, V> x) {
		return x == null ? null : x.right;
	}
	
	private void rotateLeft(Entry<K, V> p) {
		if (p != null) {
			Entry<K, V> r = p.right;
			p.right = r.left;
			if (r.left != null)
				r.left.parent = p;
			r.parent = p.parent;
			if (p.parent == null)
				root = r;
			else if (p.parent.left == p)
				p.parent.left = r;
			else
				p.parent.right = r;
			r.left = p;
			p.parent = r;
		}
	}

	private void rotateRight(Entry<K, V> p) {
		if (p != null) {
			Entry<K, V> l = p.left;
			p.left = l.right;
			if (l.right != null)
				l.right.parent = p;
			l.parent = p.parent;
			if (p.parent == null)
				root = l;
			else if (p.parent.right == p)
				p.parent.right = l;
			else
				p.parent.left = l;
			l.right = p;
			p.parent = l;
		}
	}

	@Override
	public String toString() {
		if (root == null) {
			return "[ ]";
		}

		Entry<K, V> r = root;
		for (; r.left != null;) { // get first Node
			r = r.left;
		}

		StringBuilder sb = new StringBuilder(size * 10);
		sb.append("[");
		sb.append(r + ", ");

		for (; r.parent != null;) { // left
			r = r.parent;
			sb.append(r + ", ");
			if (r.right != null && r != root)
				sb.append(r.right + ", ");

		}

		for (; r.right != null;) { // right
			r = r.right;
			if (r.left != null)
				sb.append(r.left + ", ");
			sb.append(r + ", ");
		}

		sb.delete(sb.length() - 2, sb.length());
		sb.append("]");

		System.err.println("root : " + root);
		
		return sb.toString();
	}
}
