package deque;

import java.util.Comparator;

public class MaxArrayDeque<T> extends ArrayDeque<T>{
    private Comparator<T> comparator;
    public MaxArrayDeque(Comparator<T> c){
        this.comparator = c;
    }
    public T max(){
        if (this.isEmpty()){
            return null;
        }
        T mx = this.get(0);
        for (T item : this) {
            if (comparator.compare(item,mx) > 0){
                mx = item;
            }
        }
        return mx;
    }
    public T max(Comparator<T> c){
        if (this.isEmpty()){
            return null;
        }
        T mx = this.get(0);
        for (T item : this) {
            if (c.compare(item,mx) > 0){
                mx = item;
            }
        }
        return mx;
    }
}
