package deque;

public class ArrayDeque<T> implements Deque<T>{
    private int size;
    private int head;
    private int rear;
    private T[] items;
    public ArrayDeque(){
        T[] items = (T[]) new Object[16];
        size = 0;
        head = 15;
        rear = 0;
    }
    @Override
    public void addFirst(T item) {
        items[head = (head - 1) & (items.length - 1)] = item;
        size +=1;
        if (head == rear){
            resize(items.length * 2);
        }
    }
    private void resize(int capacity){
        T[] array = (T[]) new Object[capacity];
        System.arraycopy(items,0,array,0,rear);
        System.arraycopy(items,head,array,array.length - 1 + rear - size,size - rear);
        head = array.length - 1 + rear - size;
        items = array;
    }
    @Override
    public void addLast(T item) {
        items[rear] = item;
        size +=1;
        if ((rear = (rear + 1) & (items.length - 1)) == head ){
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
        if (head +1 == items.length){
            head = 0;
        }else {
            head = head + 1;
        }
        size -= 1;
        return items[head];
    }

    @Override
    public T removeLast() {
        if (isEmpty()){
            return null;
        }
        if (rear - 1 == 0){
            rear = items.length - 1;
        }else {
            rear -= 1;
        }
        size -= 1;
        return items[rear];
    }

    @Override
    public T get(int index) {
        if (index < 0 || index >= size){
            return null;
        }
        return items[(head + index + 1) % items.length];
    }
}
