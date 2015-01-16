import java.util.Arrays;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.RandomAccess;

public class ArrayListGaun<E> implements List<E>, RandomAccess, Cloneable,
		java.io.Serializable {

	private static final long serialVersionUID = 1486191524892870488L;	 //serialize unique id
	private static final int INITIAL_CAPACITY = 10;
	private static final int MAX_CAPACITY = Integer.MAX_VALUE - 8;	
	private int size;
	
	private transient Object[] arraylist;	// transient >> writeObject 에서 serializing
	private transient int modCount = 0;	

	public ArrayListGaun(int initialCapacity) {
		if (initialCapacity < 0) {
			throw new IllegalArgumentException();
		}
		this.arraylist = new Object[initialCapacity];
	}

	public ArrayListGaun() {
		this.arraylist = new Object[INITIAL_CAPACITY];
	}

	public ArrayListGaun(Collection<? extends E> c) {
		if (c == null) {
			throw new NullPointerException();
		}
		this.arraylist = c.toArray();
	}

	public void trimToSize() {
		// Trims the capacity of this ArrayList instance to be the list's
		// current size.
		// An application can use this operation to minimize the storage of an
		// ArrayList instance.
		modCount++;
		if (size < arraylist.length) {
			arraylist = Arrays.copyOf(arraylist, size);
		}
	}

	private void ensureCapacity(int minCapacity) {
		// Increases the capacity of this ArrayList instance, if necessary,
		// to ensure that it can hold at least the number of elements specified
		// by the minimum capacity argument.
		modCount++;

		if (minCapacity - arraylist.length > 0) {
			grow(minCapacity);
		}
	}

	private void grow(int minCapacity) {
		int oldLength = arraylist.length;
		int newLength = oldLength + (oldLength >> 1); // 1.5배
		if (newLength - minCapacity < 0) {
			newLength = minCapacity;
		}
		if (newLength - MAX_CAPACITY > 0) {
			newLength = hugeCapacity(minCapacity);
		}

		arraylist = Arrays.copyOf(arraylist, newLength);
	}

	private static int hugeCapacity(int minCapacity) {
		if (minCapacity < 0) // overflow
			throw new OutOfMemoryError();
		return (minCapacity > MAX_CAPACITY) ? Integer.MAX_VALUE : MAX_CAPACITY;
	}

	@Override
	public int size() { // 원소의 개수
		return size;
	}

	@Override
	public boolean isEmpty() {
		return (this.size == 0);
	}

	@Override
	public boolean contains(Object o) {
		return (indexOf(o) > -1);
	}

	@Override
	public int indexOf(Object o) { // (o==null ? get(i)==null :
									// o.equals(get(i))), or -1
		if (o == null) {
			for (int i = 0; i < size + 1; i++)
				if (arraylist[i] == null)
					return i;
		} else {
			for (int i = 0; i < size; i++)
				if (o.equals(arraylist[i]))
					return i;
		}
		return -1;
	}

	@Override
	public int lastIndexOf(Object o) {
		if (o == null) {
			for (int i = size - 1; i > -1; i--)
				if (arraylist[i] == null)
					return i;
		} else {
			for (int i = size - 1; i > -1; i--) {
				if (o.equals(arraylist[i]))
					return i;
			}
		}

		return -1;
	}

	@Override
	public void clear() {
		modCount++;

		for (int i = 0; i < size; i++) {
			arraylist[i] = null;
		}
		size = 0;
	}

	@Override
	public Object[] toArray() {
		return arraylist;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T[] toArray(T[] a) {
		return (T[]) arraylist;
	}

	@SuppressWarnings("unchecked")
	@Override
	public E get(int index) {
		return (E) arraylist[index];
	}

	@SuppressWarnings("unchecked")
	@Override
	public E set(int index, E element) {
		ensureCapacity(index); // modCount++;
		Object o = arraylist[index];
		arraylist[index] = element;
		return (E) o;
	}

	@Override
	public boolean add(E e) {
		ensureCapacity(size + 1);
		arraylist[size++] = e;
		return true;
	}

	@Override
	public void add(int index, E element) {
		int numMoved = arraylist.length - index - 1;
		if (numMoved < 0) {
			ensureCapacity(index);
		}
		System.arraycopy(arraylist, index, arraylist, index + 1, numMoved);
		arraylist[index] = element;
		size++;
	}

	@SuppressWarnings("unchecked")
	@Override
	public E remove(int index) {
		E oldValue = (E) arraylist[index];

		int numMoved = size - index - 1;
		if (numMoved > 0) {
			System.arraycopy(arraylist, index + 1, arraylist, index, numMoved);
			modCount--;
		}
		arraylist[--size] = null;

		return oldValue;
	}

	@Override
	public boolean remove(Object o) {
		if (contains(o)) {
			int index = indexOf(o);
			int numMoved = size - index - 1;
			if (numMoved > 0) {
				System.arraycopy(arraylist, index + 1, arraylist, index,
						numMoved);
				arraylist[--size] = null;
			} else {
			}
			return true;
		}
		return false;
	}

	@Override
	public boolean addAll(Collection<? extends E> c) {
		Object[] array = c.toArray();
		int arrayLength = array.length;
		ensureCapacity(size + arrayLength);
		System.arraycopy(array, 0, arraylist, size, arrayLength);
		size += arrayLength;
		return true;
	}

	@Override
	public boolean addAll(int index, Collection<? extends E> c) {
		Object[] array = c.toArray();
		int numMoved = array.length;
		int destPost = index + array.length;

		ensureCapacity(size + numMoved);

		if (index > size || index < 0) {
			throw new IndexOutOfBoundsException();
		}

		System.arraycopy(arraylist, index, arraylist, destPost, size - index);
		System.arraycopy(array, 0, arraylist, index, numMoved);
		size += numMoved;

		return true;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return removeComparator(c, false);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return removeComparator(c, true);
	}

	private boolean removeComparator(Collection<?> c, boolean condition) {
		int expectedModCount = modCount;
		Object[] buffer = new ArrayListGaun<E>(size).toArray();
		int j = 0;

		for (int i = 0; i < size; i++) {
			if (c.contains(arraylist[i]) == condition) {
				buffer[j++] = arraylist[i];
				modCount--;
			}
		}

		if (modCount == expectedModCount) {
			return false;
		}

		size = j;
		arraylist = buffer;

		return true;
	}

	@Override
	public Iterator<E> iterator() {
		return new InnerIterator();
	}

	private class InnerIterator implements Iterator<E> {
		private int cursor;
		private int expectedModCount = modCount;

		@Override
		public boolean hasNext() {
			return size != cursor;
		}

		@SuppressWarnings("unchecked")
		@Override
		public E next() {
			if (cursor >= size - 1) {
				throw new NoSuchElementException();
			}
			return (E) arraylist[++cursor];
		}

		@Override
		public void remove() {
			checkModCount();
			try {
				ArrayListGaun.this.remove(cursor);
			} catch (Exception e) {
				throw new ConcurrentModificationException();
			}
		}

		private void checkModCount() {
			if (modCount != expectedModCount) {
				throw new ConcurrentModificationException();
			}
		}
	}

	@Override
	public ListIterator<E> listIterator() {
		return new ListIter(0);
	}

	@Override
	public ListIterator<E> listIterator(int index) {
		return new ListIter(index);
	}

	public class ListIter extends InnerIterator implements ListIterator<E> {
		private int cursor;

		public ListIter(int cursor) {
			super();
			if (cursor < 0 || cursor > size()) {
				throw new IndexOutOfBoundsException();
			}
			this.cursor = cursor;
		}

		@Override
		public void add(E e) {
			super.checkModCount();
			if (cursor < 0)
				throw new NoSuchElementException();
			try {
				ArrayListGaun.this.set(cursor, e);
			} catch (Exception ex) {
				throw new ConcurrentModificationException(); // iterator 는 부모의 내용이 변경되지 않는 것을 전제로 한다.
			}
		}

		@Override
		public boolean hasPrevious() {
			return previous() != null;
		}

		@SuppressWarnings("unchecked")
		@Override
		public E previous() {
			if (cursor == 0) {
				return null;
			}
			return (E) arraylist[previousIndex()];
		}

		@Override
		public int nextIndex() {
			return cursor;
		}

		@SuppressWarnings("unchecked")
		@Override
		public E next() {
			super.checkModCount();
			return (E) arraylist[cursor++];
		}

		@Override
		public int previousIndex() {
			super.checkModCount();
			int i = cursor - 1;
			if (i < 0)
				throw new NoSuchElementException();
			if (i >= arraylist.length)
				throw new ConcurrentModificationException();
			cursor = i;
			return cursor;
		}

		@Override
		public void set(E e) {
			super.checkModCount();
			if (cursor < 0)
				throw new NoSuchElementException();
			try {
				ArrayListGaun.this.set(cursor, e);
			} catch (Exception ex) {
				throw new ConcurrentModificationException();
			}
		}
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		Object[] array = c.toArray();

		for (int i = 0; i < c.size(); i++) {
			if (!this.contains(array[i])) {
				return false;
			}
		}
		return true;
	}

	@Override
	public List<E> subList(int fromIndex, int toIndex) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String toString() {
		return Arrays.toString(arraylist);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(arraylist);
		return result;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ArrayListGaun other = (ArrayListGaun) obj;
		if (!Arrays.equals(arraylist, other.arraylist))
			return false;
		return true;
	}
}