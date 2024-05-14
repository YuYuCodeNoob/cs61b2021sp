package deque;

import org.junit.Test;

import java.util.Iterator;
public class ArrayDeque<T> implements Deque<T>,Iterable<T>{
    private int size;
    private int head;
    private int rear;
    private int InitialCapacity = 8;
    private T[] items;
    public ArrayDeque(){
        items = (T[]) new Object[8];
        size = 0;
        head = 7;
        rear = 0;
    }
    @Override
    public void addFirst(T item) {
        items[head] = item;
        head = (head - 1) & (items.length - 1);
        size +=1;
        if (size == items.length){
            resize(items.length * 2);
        }
    }

    private void resize(int capacity){
        T[] array = (T[]) new Object[capacity];
        int p = (head + 1) & (items.length - 1);
        int n = items.length;
        int CountOfRight = n - p;
        System.arraycopy(items, p, array, 0, CountOfRight);
        System.arraycopy(items, 0, array, CountOfRight, p);
        items = array;
        head = items.length - 1;
        rear = n;
    }
    private void shrink(){
        if (this.size() > items.length / 4 | items.length == InitialCapacity){
            return;
        }
        T[] array = (T[]) new Object[items.length / 2];
        int p = (head + 1) & (items.length - 1);
        if (p <= rear){
            System.arraycopy(items, p, array, 0, this.size());
        }else {
            System.arraycopy(items, p, array, 0, items.length - p);
            System.arraycopy(items, 0, array, items.length - p, rear);
        }
        items = array;
        items = array;
        head = items.length - 1;
        rear = size;
    }
    @Override
    public void addLast(T item) {
        items[rear] = item;
        rear = (rear + 1) & (items.length -1 );
        size +=1;
        if (size == items.length ){
            resize(items.length * 2);
        }
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public int size() {
        return size;
    }
    @Override
    public void printDeque() {
        if (isEmpty()){
            return;
        }
        for (int i = 0; i < size; i++) {
            if (i == size - 1){
                System.out.println(get(i));
            }
            else {
                System.out.print(get(i) + " ");
            }
        }
    }

    @Override
    public T removeFirst() {
        if (isEmpty()){
            return  null;
        }
/*
* when head == items.length head + 1 == 2 ** n its binary system code 0b1...0
* items.length binary system code ob01111...111
* & -> 0b000000000...000
*
* */
        int h = (head + 1) & (items.length -1);
        T item = items[h];
        if (item != null){
            items[h] = null;
            head = h;
        }
        size -= 1;
        this.shrink();
        return item;
    }

    @Override
    public T removeLast() {
        if (isEmpty()){
            return null;
        }
        int t = (rear - 1) & (items.length - 1);
        T item = items[t];
        if (item != null){
            items[t] = null;
            rear = t;
        }
        size -= 1;
        this.shrink();
        return item;
    }

    @Override
    public T get(int index) {
        if (index < 0 || index >= size){
            return null;
        }
        return items[(head + index + 1) % (items.length)];
    }

    @Override
    public Iterator<T> iterator() {
        return new ArrayDequeIterator();
    }

    private class ArrayDequeIterator implements Iterator<T>{
        int h;
        ArrayDequeIterator(){
            h = 0;
        }
        public boolean hasNext(){
            return h < size;
        }

        @Override
        public T next() {
            T result = get(h);
            h +=1;
            return result;
        }
    }
    @Override
    public boolean equals(Object o){
        if (o == null | !(o instanceof Deque)){
            return false;
        }
        if (o == this){
            return true;
        }
        Deque<T> other = (Deque<T>) o;
        if (other.size() != size){
            return false;
        }
        for (int i = 0; i < this.size() ; i++) {
            if (!other.get(i).equals(this.get(i)))
                return false;
        }
        return true;
    }
}
