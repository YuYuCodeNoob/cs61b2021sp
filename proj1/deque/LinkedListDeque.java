package deque;

import java.util.Iterator;

public class LinkedListDeque<T> implements Deque<T>,Iterable<T>{
    private class Node<N>{
        private N item;
        private Node<N> next;
        private Node<N> pre;
        private Node(N item,Node<N> next,Node<N> pre){
            this.item = item;
            this.next = next;
            this.pre = pre;
        }
    }
    private Node<T> sentinel = new Node<>(null,null,null);
    private int size;
    public LinkedListDeque(){
        size = 0;
        sentinel.next = sentinel;
        sentinel.pre = sentinel;
    }
    @Override
    public void addFirst(T item) {
        Node<T> first = new Node<>(item,sentinel.next,sentinel);
        sentinel.next.pre = first;
        sentinel.next = first;
        size +=1;
    }
    @Override
    public void addLast(T item) {
        Node<T> last = new Node<>(item,sentinel,sentinel.pre);
        last.pre.next = last;
        sentinel.pre = last;
        size +=1;
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
        Node<T> head = sentinel.next;
        while (!head.equals(sentinel)){
            if (head.next.equals(sentinel)){
                System.out.println(head.item);
            }else {
                System.out.println(head.item + " ");
            }
            head = head.next;
        }
        System.out.println();
    }

    @Override
    public T removeFirst() {
        if(isEmpty()){
            return null;
        }
        Node<T> first = sentinel.next;
        sentinel.next = first.next;
        sentinel.next.pre = sentinel;
        size-=1;
        return first.item;
    }

    @Override
    public T removeLast() {
        if (isEmpty()){
            return null;
        }
        Node<T> last = sentinel.pre;
        sentinel.pre = last.pre;
        last.pre.next = sentinel;
        size-=1;
        return last.item;
    }

    @Override
    public T get(int index) {
        if (index >= size || index < 0){
            return null;
        }
        Node<T> head = sentinel.next;
        for (int i = 0; i < index ; i++) {
            head = head.next;
        }
        return head.item;
    }
    public T getRecursive(int index) {
        if (index >= size || index < 0){
            return null;
        }
        return getRecursiveHelper(index,sentinel.next);
}

    private T getRecursiveHelper(int index, Node<T> next) {
        if (index == 0){
            return next.item;
        }else {
            return getRecursiveHelper(index - 1,next.next);
        }
    }

    public Iterator<T> iterator(){
        return new LinkedListDequeIterator();
    }
//    @Override
//    public String toString(){
//        StringBuilder returnString = new StringBuilder("(");
//        for (int i = 0; i < this.size() - 1; i++) {
//            returnString.append(this.get(i).toString() + ",");
//        }
//        returnString.append(this.get(this.size() -1));
//        returnString.append(")");
//        return returnString.toString();
//    }
    private class LinkedListDequeIterator implements Iterator<T> {
        private Node<T> head;

        LinkedListDequeIterator() {
            head = sentinel.next;
        }

        public boolean hasNext() {
            return head != sentinel;
        }

        public T next() {
            T item = head.item;
            head = head.next;
            return item;
        }
    }
    public boolean equals(Object o){
        if (o == null){
            return false;
        }
        if (o == this){
            return true;
        }
        if (!(o instanceof Deque)){
            return false;
        }
        Deque<T> other = (Deque<T>) o;
        if (other.size() != this.size()){
            return false;
        }
        for (int i = 0; i < this.size(); i++) {
            if (!this.get(i).equals(other.get(i))){
                return false;
            }
        }
        return true;
    }
}
