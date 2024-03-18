package deque;

import org.junit.Test;

import java.util.Iterator;

public class ArrayDeque<T> implements Deque<T>,Iterable<T>{
    private int size;
    private int head;
    private int rear;
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
        if (size == items.length - 2){
            resize(items.length * 2);
        }
    }

    private void resize(int capacity){
        T[] array = (T[]) new Object[capacity];
        System.arraycopy(items,0,array,0,rear);
        System.arraycopy(items,head,array,array.length - 1 + rear - size,size - rear + 1);
        head = array.length - 1 + rear - size;
        items = array;
    }
    @Override
    public void addLast(T item) {
        items[rear] = item;
        rear = (rear + 1) & (items.length -1 );
        size +=1;
        if (size == items.length - 2 ){
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
        return item;
    }

    @Override
    public T get(int index) {
        if (index < 0 || index >= size){
            return null;
        }
        return items[(head + index + 1) % items.length];
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
}
