package jp.co.worksap.global;

import java.util.NoSuchElementException;

/**
 * @author T.Thanh
 */

/**
 * The Queue class represents an immutable first-in-first-out (FIFO) queue of
 * objects.
 *
 * @param <E>
 */
public class ImmutableQueue<E> {

    //implementation for an innner class of an immutable list.
    private static class PersistentList<E> {

        private E head;
        private PersistentList<E> tail;
        private int size;

        private PersistentList(E e, PersistentList<E> tail) {
            this.head = e;
            this.tail = tail;
            this.size = tail.size + 1;
        }

        private PersistentList() {
            this.head = null;
            this.tail = null;
        }

        public PersistentList<E> add(E e) {
            return new PersistentList<E>(e, this);
        }

        public boolean isEmpty() {
            return this.size == 0;
        }

    }
    
    //The idea is to represent a queue by two lists, front and back.
    private PersistentList<E> front;
    private PersistentList<E> back;
    /**
      * requires default constructor. 
      */
    private ImmutableQueue(PersistentList<E> front, PersistentList<E> back) {
        this.front = front;
        this.back = back;
    }

    public ImmutableQueue() {
        this.front = new PersistentList();
        this.back = new PersistentList();
    }

    /**
     * *
     * Returns the queue that adds an item into the tail of this queue without
     * modifying this queue.
     * <pre>
     * e.g.
     * When this queue represents the queue (2, 1, 2, 2, 6) and we enqueue the value 4 into this queue,
     * this method returns a new queue (2, 1, 2, 2, 6, 4)
     * and this object still represents the queue (2, 1, 2, 2, 6) .
     * </pre> If the element e is null, throws IllegalArgumentException.
     *
     * @param e
     * @return
     * @throws IllegalArgumentException
     */
    public ImmutableQueue<E> enqueue(E e) {

        if (e == null) {
            throw new IllegalArgumentException();
        }

        return new ImmutableQueue<E>(this.front, this.back.add(e));
    }

    // reverse copy from back to front
    public void reverse() {
        if (front.isEmpty()) {
            while (!back.isEmpty()) {
                this.front = front.add(back.head);
                back = back.tail;
            }
        }
    }

    /**
     * *
     * Returns the queue that removes the object at the head of this queue
     * without modifying this queue.
     * <pre>
     * e.g.
     * When this queue represents the queue (7, 1, 3, 3, 5, 1) ,
     * this method returns a new queue (1, 3, 3, 5, 1)
     * and this object still represents the queue (7, 1, 3, 3, 5, 1) .
     * 
     * </pre> If this queue is empty, throws java.util.NoSuchElementException.
     *
     * @return
     * @throws java.util.NoSuchElementException
     */
    public ImmutableQueue<E> dequeue() {
        if (isEmpty()) {
            throw new NoSuchElementException();
        }
        reverse();
        return new ImmutableQueue<E>(this.front.tail, this.back);

    }

    /**
     * Looks at the object which is the head of this queue without removing it
     * from the queue.
     * <pre>
     * e.g.
     * When this queue represents the queue (7, 1, 3, 3, 5, 1),
     * this method returns 7 and this object still represents the queue (7, 1, 3, 3, 5, 1)
     * </pre> If the queue is empty, throws java.util.NoSuchElementException.
     *
     * @return
     * @throws java.util.NoSuchElementException
     */
    public E peek() {
        reverse();
        return this.front.head;
    }

    /**
     * Check if the queue is empty.
     *
     * @return
     */
    public boolean isEmpty() {
        return (this.front.size == 0 && this.back.size == 0);
    }

    /**
     * Returns the number of objects in this queue.
     *
     * @return
     */
    public int size() {
        return this.front.size + this.back.size;
    }
}
