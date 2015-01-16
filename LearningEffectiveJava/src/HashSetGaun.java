import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Set;

public class HashSetGaun<E> implements Set<E>, Cloneable, Serializable {

	private static final long serialVersionUID = 3726931159771361696L;

	/*
	 * Value Add (key) -> hash() return hash -> (hashBucket[hash].get(key) ==
	 * null) ? hashBucket[hash].add(key) : return false;
	 * 
	 * HashBucket : array[initialCapacity] list : LinkedList<E>
	 * 
	 * Method : hash();
	 */
	private static final int INITIAL_CAPACITY = 20;

	/*
	 * The table, resized as necessary. Length MUST Always be a power of two.
	 */
	private LinkedListGaun<E>[] bucket;

	private transient int size = 0;
	private transient int modCount = 0;

	/*
	 * Constructs a new, empty set; the backing ArrayList - LinkedList instance
	 * has default initial capacity (16) and load factor (0.75). load factor :
	 * measure a capacity of hash table before increment automatically
	 */
	@SuppressWarnings("unchecked")
	public HashSetGaun() {
		bucket = (LinkedListGaun<E>[]) new LinkedListGaun<?>[INITIAL_CAPACITY];
	}

	@SuppressWarnings("unchecked")
	public HashSetGaun(Collection<? extends E> c) {
		bucket = (LinkedListGaun<E>[]) new LinkedListGaun<?>[INITIAL_CAPACITY];
		addAll(c);
	}

	@SuppressWarnings("unchecked")
	public HashSetGaun(int initialCapacity) {
		bucket = (LinkedListGaun<E>[]) new LinkedListGaun<?>[initialCapacity];
	}

	@SuppressWarnings("unchecked")
	public HashSetGaun(int initialCapacity, float loadFactor) {
		bucket = (LinkedListGaun<E>[]) new LinkedListGaun<?>[initialCapacity];
	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public boolean isEmpty() {
		return (size != 0);
	}

	@Override
	public boolean contains(Object o) {
		for (LinkedListGaun<E> item : bucket) {
			if (item != null && item.contains(o)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Iterator<E> iterator() {
		return new Iter();
	}

	private class Iter implements Iterator<E> {
		private int bucketIndex, listIndex, cursor;
		private int expectedModCount; 
		LinkedListGaun<E> nextlist;
 		
		public Iter(){
			expectedModCount = modCount;
			bucketIndex = listIndex = 0;
			cursor = 0;
		}

		@Override
		public boolean hasNext() {
			// 각 버킷 LinkedList의 마지막 노드인 경우에 다음 버킷으로 이동.
			if (size < 0) {
				return false;
			}
			return (cursor < size);
		}

		@Override
		public E next() {
			CheckModCount(expectedModCount);
			
			if (size < 0) {
				return null;
			}
			
			E result = null;
			cursor++;
			
			for (int i = bucketIndex; i < bucket.length; i++) {
				nextlist = bucket[i];
				if (nextlist != null) {
					result = nextlist.get(listIndex);
					if (listIndex < nextlist.size() -1) {
						listIndex++;
					} else {
						listIndex = 0;
						bucketIndex = i + 1;
					}
					return result;
				}
			}
			
			return null;
		}

		@Override
		public void remove() {
			CheckModCount(expectedModCount);
		}

		private void CheckModCount(int expectedModCount) {
			if (expectedModCount != modCount) {
				throw new ConcurrentModificationException();
			}
		}
	}

	@Override
	public Object[] toArray() {
		Object[] array = new Object[size];
		int i = 0;
		Iterator<E> iter = iterator();
		
		while (iter.hasNext()) {
			array[i++] = iter.next();
		}
		
		return array;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T[] toArray(T[] a) {
		int alength = a.length;
		int i = 0;

		if (alength < size) {
			Arrays.copyOf(a, size);
		}
		
		Iterator<E> iter = iterator();
		
		while (iter.hasNext()) {
			a[i++] = (T) iter.next();
		}
		
		if (a.length > size) {
			a[size] = null;
		}
		
		return a;
	}

	@Override
	public boolean add(E e) {
		int hashCode = hash(e);
		LinkedListGaun<E> list = bucket[hashCode];

		if (list == null) {
			list = new LinkedListGaun<E>();
		} else {
			if (list.contains(e))
				return false;
		}
		
		list.add(e);

		bucket[hashCode] = list;
		size++;
		modCount++;

		return true;
	}

	/* initialCapacity ? return 0~9 : return 0 ~ capacity; */
	private int hash(E e) { 
		if (e == null) {
			return 0;
		}
		int seed = e.hashCode();
		return seed % INITIAL_CAPACITY;
	}

	@Override
	@SuppressWarnings({"unchecked"})
	public boolean remove(Object o) {
		if (o == null) {
			return false;
		}
		
		int hashCode = hash((E) o);
		LinkedListGaun<E> item = bucket[hashCode];

		if (! item.removeFirstOccurrence(o)) {
			return false;
		}
		
		if (item.size() == 0) {
			bucket[hashCode] = null;
		}
		
		size--;
		modCount++;
		
		return true;
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		Object[] array = c.toArray();
 		for (int i=0; i<c.size(); i++) {
			if (! contains(array[i])) {
				return false;
			}
		}
		
		return true;
	}

	@Override
	public boolean addAll(Collection<? extends E> c) {
		if (c.size() == 0) {
			return false;
		}
		
		Iterator<? extends E> iter = c.iterator();
		while (iter.hasNext()) {
			add(iter.next());
		}
		
		return true;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return removeComperator(c, false);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return removeComperator(c, true);
	}
	
	private boolean removeComperator(Collection<?> c, boolean condition){
		int originSize = size;
		ListIterator<E> iter = null;
		
		for (LinkedListGaun<E> item : bucket) {
			if (item != null) {
				iter = item.listIterator(item.size()-1);
				for (E e = item.getLast(); e != null;) {
					if (c.contains(e) == condition) {
						remove(e);
					}
					
					e = iter.hasPrevious() ? iter.previous() : null; 
				}
			}
		}
		
		if (size != originSize) {
			return true;
		}
		
		return false;
	}

	@Override
	public void clear() {
		int bucketLength = bucket.length;
		LinkedListGaun<E> item = null;
		
		for(int x = 0; x < bucketLength; x++) {
			item = bucket[x];
			if (item != null) {
				item.clear();
			}
		}
		size = 0;
	}

	@Override
	public String toString() {
		int bucketSize = bucket.length;
		LinkedListGaun<E> item = null;
		ListIterator<E> iter = null;
		StringBuilder result = new StringBuilder(this.size * 10);
		result.append("[");
		for (int i = 0; i < bucketSize; i++) {
			item = bucket[i];
			if (item != null) {
				iter = item.listIterator();
				while (iter.hasNext()) {
					result.append(iter.next());
					result.append(", ");
				}
			}
		}
		if (size != 0) {
			result.delete(result.length()-2, result.length());
		}
		result.append("]");
		return result.toString();
	}
}