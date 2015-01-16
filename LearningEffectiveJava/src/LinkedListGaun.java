import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

public class LinkedListGaun<E> implements List<E>, Deque<E>, Cloneable,
		java.io.Serializable {

	private static final long serialVersionUID = -2692658279211727342L;
	private transient int size = 0;
	private transient int modCount = 0;

	private transient Node<E> first;
	private transient Node<E> last;

	private static class Node<E> {
		E item;
		Node<E> next;
		Node<E> prev;

		Node(Node<E> prev, E element, Node<E> next) {
			this.item = element;
			this.next = next;
			this.prev = prev;
		}
	}

	public LinkedListGaun() {
	}

	public LinkedListGaun(Collection<? extends E> c) {
		// Constructs a list containing the elements of the specified
		// collection,
		// in the order they are returned by the collection's iterator.
		addAll(c);
	}

	/* e를 첫번째 원소로 link한다 */
	private void linkFirst(E e) {
		final Node<E> f = first;
		final Node<E> newNode = new Node<E>(null, e, f);
		first = newNode;
		if (f == null) {
			last = newNode; // f == null <-> size == 0
		} else {
			f.prev = newNode;
		}
		size++;
		modCount++;
	}

	private void linkLast(E e) {
		final Node<E> l = last;
		final Node<E> newNode = new Node<E>(l, e, null);
		last = newNode;
		if (l == null) {
			first = newNode;
		} else {
			l.next = newNode;
		}
		size++;
		modCount++;
	}

	private void linkBefore(E e, Node<E> node) {
		final Node<E> newNode = new Node<E>(node.prev, e, node);
		final Node<E> f = first;
		if (node == f) {
			first = newNode;
			f.prev = newNode;
		} else {
			node.prev.next = newNode;
			node.prev = newNode;
		}

		size++;
		modCount++;
	}

	private E unlinkFirst() {
		Node<E> f = first;
		if (f == null) {
			return null;
		}

		E result = f.item;
		first = f.next;
		if (first != null) {
			first.prev = null;
		}
		f = null;

		size--;
		modCount++;
		return result;
	}

	private E unlinkLast() {
		Node<E> l = last;
		if (l == null) {
			return null;
		}
		E result = l.item;
		last = l.prev;
		l = null;

		size--;
		modCount++;
		return result;
	}

	private E unlinkNode(Node<E> node) {
		if (node == null) {
			return null;
		}

		Node<E> prev = node.prev;
		Node<E> next = node.next;

		if (node == first) {
			if(next != null) {
				next.prev = null;
			}
			first = node.next;
		} else if (node == last) {
			node.prev.next = null;
			last = node.prev;
		} else {
			node.next.prev = prev;
			node.prev.next = next;
		}

		E result = node.item;
		node.item = null;
		size--;
		modCount++;

		return result;
	}

	@Override
	public void addFirst(E e) {
		linkFirst(e);
	}

	@Override
	public void addLast(E e) {
		linkLast(e);
	}

	@Override
	public boolean offerFirst(E e) {
		addFirst(e);
		return true;
	}

	@Override
	public boolean offerLast(E e) {
		addLast(e);
		return true;
	}

	@Override
	public E removeFirst() {
		return unlinkFirst();
	}

	@Override
	public E removeLast() {
		return unlinkLast();
	}

	/*
	 * Retrieves and removes the first element of this list, or returns null if
	 * this list is empty
	 */
	@Override
	public E pollFirst() {
		Node<E> f = first;
		if (f == null) {
			return null;
		}
		return removeFirst();
	}

	@Override
	public E pollLast() {
		Node<E> f = first;
		if (f == null) {
			return null;
		}
		return removeLast();
	}

	/* Returns the first element in this list. */
	@Override
	public E getFirst() {
		Node<E> f = first;
		if (f == null) {
			throw new NoSuchElementException();
		}

		return f.item;
	}

	@Override
	public E getLast() {
		Node<E> l = last;
		if (l == null) {
			throw new NoSuchElementException();
		}

		return l.item;
	}

	@Override
	public E peekFirst() {
		Node<E> f = first;
		if (f == null) {
			return null;
		}

		return f.item;
	}

	@Override
	public E peekLast() {
		Node<E> l = last;
		if (l == null) {
			return null;
		}

		return l.item;
	}

	/*
	 * Removes the first occurrence of the specified element in this list (when
	 * traversing the list from head to tail). If the list does not contain the
	 * element, it is unchanged.
	 */
	@Override
	public boolean removeFirstOccurrence(Object o) {
		return remove(o);
	}

	@Override
	public boolean removeLastOccurrence(Object o) { // indexof node() searchr가많
		if (o == null) {
			return false;
		}

		for (Node<E> x = last; x != null; x = x.prev) {
			if (x.item.equals(o)) {
				unlinkNode(x);
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean offer(E e) {
		linkLast(e);
		return true;
	}

	@Override
	public E remove() {
		return unlinkFirst();
	}

	@Override
	public E poll() {
		Node<E> f = first;
		if (f == null) {
			return null;
		}
		return unlinkFirst();
	}

	@Override
	public E element() {
		Node<E> f = first;
		if (f == null) {
			throw new NoSuchElementException();
		}
		return f.item;
	}

	@Override
	public E peek() {
		return peekFirst();
	}

	@Override
	public void push(E e) {
		addFirst(e);
	}

	@Override
	public E pop() {
		return removeFirst();
	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public boolean isEmpty() {
		return (first == null);
	}

	@Override
	public boolean contains(Object o) {
		return (indexOf(o) != -1);
	}

	@Override
	public Object[] toArray() {
		Object[] items = new Object[size];
		int i = 0;
		for (Node<E> x = first; x != null; x = x.next) {
			items[i] = x.item;
			i++;
		}
		return items;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T[] toArray(T[] a) {
		int length = a.length;
		int i = 0;
		if (length < size)
			a = (T[]) java.lang.reflect.Array.newInstance(a.getClass()
					.getComponentType(), size);
		/* getComponentType : 배열 컴포넌트의 클래스타입 */

		for (Node<E> x = first; x != null; x = x.next) {
			a[i] = (T) x.item;
			i++;
		}
		if (a.length > size)
			a[size] = null;
		return a;
	}

	@Override
	public boolean add(E e) {
		linkLast(e);
		return true;
	}

	@Override
	public boolean remove(Object o) {
		if (o == null) {
			return false;
		}

		for (Node<E> x = first; x != null; x = x.next) {
			if (x.item.equals(o)) {
				unlinkNode(x);
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean addAll(Collection<? extends E> c) {
		return addAll(size, c);
	}

	@Override
	public boolean addAll(int index, Collection<? extends E> c) {
		checkIndexForAdd(index);

		Object[] array = c.toArray();
		int arrayLength = array.length;
		Node<E> prevNode, nextNode;

		if (arrayLength == 0)
			return false;

		if (index == 0) {
			prevNode = null;
			nextNode = first;
		} else if (index == size) {
			prevNode = last;
			nextNode = null;
		} else {
			prevNode = getNode(index - 1);
			nextNode = getNode(index);
		}

		for (Object o : array) {
			@SuppressWarnings("unchecked")
			Node<E> newNode = new Node<E>(prevNode, (E) o, null);

			if (prevNode == null) {
				first = newNode;
			} else if (nextNode == null) {
				last.next = newNode;
				last = newNode;
			} else {
				prevNode.next = newNode;
				nextNode.prev = newNode;
			}
			newNode.next = nextNode;
			prevNode = newNode;
		}

		size += arrayLength;
		modCount++;

		return true;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return removeComperator(c, false);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return removeComperator(c, true);
	}

	private boolean removeComperator(Collection<?> c, boolean condition) {
		int j = 0;
		Node<E> node = first;

		for (Node<E> x = first; x != null; x = x.next) {
			if (c.contains(x.item) == condition) {
				node.item = x.item;
				node = node.next;
				j++;
			}
		}

		while (j < size) {
			remove(j);
		}

		if (node == first) {
			return false;
		}

		return true;
	}

	@Override
	public void clear() {
		for (Node<E> x = last; x != null;) {
			Node<E> prev = x.prev;
			x.item = null;
			x.prev = null;
			x.next = null;
			x = prev;
		}
		first = null;
		last = null;
		size = 0;
		modCount++;
	}

	Node<E> node(int index) {
		if (index < (size >> 1)) {
			Node<E> x = first;
			for (int i = 0; i < index; i++)
				x = x.next;
			return x;
		} else {
			Node<E> x = last;
			for (int i = size - 1; i > index; i--)
				x = x.prev;
			return x;
		}
	}

	@Override
	public E get(int index) {
		checkIndexForGet(index);

		int i = 0;
		for (Node<E> x = first; x != null; x = x.next) {
			if (index == i) {
				return x.item;
			}
			i++;
		}
		return null;
	}

	private Node<E> getNode(int index) {
		checkIndexForGet(index);
		if (index < (size >> 1)) {
			int i = 0;
			for (Node<E> x = first; x != null; x = x.next) {
				if (i == index) {
					return x;
				}
				i++;
			}
		} else {
			int i = size - 1;
			for (Node<E> x = last; x != null; x = x.prev) {
				if (i == index) {
					return x;
				}
				i--;
			}
		}
		return null;
	}

	@Override
	public E set(int index, E e) {
		checkIndexForGet(index);
		E originItem = null;

		if (index < (size >> 1)) {
			int i = 0;
			for (Node<E> x = first; x != null; x = x.next) {
				if (i == index) {
					originItem = x.item;
					x.item = e;
				}
				i++;
			}
		} else {
			int i = size - 1;
			for (Node<E> x = last; x != null; x = x.prev) {
				if (i == index) {
					originItem = x.item;
					x.item = e;
				}
				i--;
			}
		}
		modCount++;

		return originItem;
	}

	@Override
	public void add(int index, E e) {
		checkIndexForAdd(index);
		if (index < (size >> 1)) {
			int i = 0;
			for (Node<E> x = first; x != null; x = x.next) {
				if (i == index) {
					linkBefore(e, x);
				}
				i++;
			}
		} else if (index == size) {
			linkLast(e);
		} else {
			int i = size - 1;
			for (Node<E> x = last; x != null; x = x.prev) {
				if (i == index) {
					linkBefore(e, x);
				}
				i--;
			}
		}
		modCount++;
	}

	@Override
	public E remove(int index) {
		checkIndexForGet(index);
		if (index < (size >> 1)) {
			int i = 0;
			for (Node<E> x = first; x != null; x = x.next) {
				if (i == index) {
					return unlinkNode(x);
				}
				i++;
			}
		} else {
			int i = size - 1;
			for (Node<E> x = last; x != null; x = x.prev) {
				if (i == index) {
					return index == 0? unlinkFirst() : unlinkNode(x);
				}
				i--;
			}
		}
		return null;
	}

	@Override
	public int indexOf(Object o) {
		int index = 0;
		/*
		 * LinkedList를 node(index)로 접근하면 최소 index time이 걸린다. node를 순회
		 */
		if (o == null) {
			for (Node<E> x = first; x != null; x = x.next) {
				if (x.item == null)
					return index;
				index++;
			}
		} else {
			for (Node<E> x = first; x != null; x = x.next) {
				if (x.item.equals(o))
					return index;
				index++;
			}
		}

		return -1;
	}

	@Override
	public int lastIndexOf(Object o) {
		int index = size - 1;
		if (o == null) {
			for (Node<E> x = last; x != null; x = x.prev) {
				if (x.item == null)
					return index;
				index--;
			}
		} else {
			for (Node<E> x = last; x != null; x = x.prev) {
				if (x.item.equals(o))
					return index;
				index--;
			}
		}

		return -1;
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		Object[] array = c.toArray();
		int i = 0;

		for (Node<E> x = first; x != null; x = x.next) {
			if (!this.contains(array[i])) {
				return false;
			}
			i++;
		}
		return true;
	}

	@Override
	public Iterator<E> iterator() {
		return new ListIter(0);
	}

	@Override
	public Iterator<E> descendingIterator() {
		return null;
	}

	@Override
	public ListIterator<E> listIterator() {
		return new ListIter(0);
	}

	@Override
	public ListIterator<E> listIterator(int index) {
		return new ListIter(index);
	}

	private class ListIter implements ListIterator<E> {
		private Node<E> next;
		private int nextIndex;
		private int expectedModCount = modCount;

		public ListIter(int index) {
			next = (index == size) ? null : getNode(index);
			nextIndex = index;
		}

		@Override
		public boolean hasNext() {
			return (nextIndex < size);
		}

		@Override
		public E next() {
			if (nextIndex > size - 1) {
				throw new NoSuchElementException();
			}
			Node<E> now = next;
			next = next.next;
			nextIndex++;

			return now.item;
		}

		@Override
		public boolean hasPrevious() {
			return (nextIndex != 0);
		}

		@Override
		public E previous() {
			if (nextIndex == 0) {
				throw new NoSuchElementException();
			}
			next = next.prev;
			nextIndex--;
			return next.item;
		}

		@Override
		public int nextIndex() {
			return nextIndex;
		}

		@Override
		public int previousIndex() {
			return nextIndex - 1;
		}

		@Override
		public void remove() {
			checkForComodification();
			if (nextIndex > size - 1) {
				throw new IndexOutOfBoundsException();
			}

			if (nextIndex == size - 1) {
				unlinkLast();
			} else {
				Node<E> now = next;
				next = now.next;
				unlinkNode(now);
			}

			expectedModCount++;
		}

		@Override
		public void set(E e) {
			checkForComodification();
			LinkedListGaun.this.set(nextIndex, e);
			expectedModCount++;
		}

		@Override
		public void add(E e) {
			checkForComodification();
			LinkedListGaun.this.add(nextIndex, e);
			nextIndex++;
			expectedModCount++;
		}

		final void checkForComodification() {
			if (modCount != expectedModCount)
				throw new ConcurrentModificationException();
		}
	}

	@Override
	public List<E> subList(int fromIndex, int toIndex) {
		return null;
	}

	private void checkIndexForAdd(int index) {
		if (index < 0 || index > size) {
			throw new IndexOutOfBoundsException();
		}
	}

	private void checkIndexForGet(int index) {
		if (index < 0 || index >= size) {
			System.err.println(index);
			throw new IndexOutOfBoundsException();
		}
	}

	@Override
	public String toString() {
		String list = "[";
		if (size != 0) {
			Node<E> y = first.next;
			list += first.item;
			for (Node<E> x = y; x != null; x = x.next) {
				list += ", " + x.item;
			}
		}
		return list + "]";
	}
}