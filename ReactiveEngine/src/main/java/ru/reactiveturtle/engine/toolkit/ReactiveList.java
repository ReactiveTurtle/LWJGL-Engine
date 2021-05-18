package ru.reactiveturtle.engine.toolkit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.function.Function;
import java.util.function.Predicate;

public class ReactiveList<T> extends ArrayList<T> {
    public ReactiveList() {
    }

    public ReactiveList(T[] array) {
        addAll(Arrays.asList(array));
    }

    public T first(Predicate<T> predicate) {
        T result = null;
        for (T element : this) {
            if (predicate.test(element)) {
                result = element;
                break;
            }
        }
        return result;
    }

    public boolean contains(Predicate<T> predicate) {
        boolean result = false;
        Iterator<T> iterator = iterator();
        while (iterator.hasNext() && !result) {
            T element = iterator.next();
            result = predicate.test(element);
        }
        return result;
    }

    public int indexOf(Predicate<T> predicate) {
        int result = -1;
        for (int i = 0; i < size(); i++) {
            T element = get(i);
            if (predicate.test(element)) {
                result = i;
                break;
            }
        }
        return result;
    }

    public void removeAll(Predicate<T> predicate) {
        for (int i = 0; i < size(); i++) {
            T element = get(i);
            if (predicate.test(element)) {
                remove(i);
                i--;
            }
        }
    }

    public ReactiveList<T> filter(Predicate<T> predicate) {
        ReactiveList<T> resultList = new ReactiveList<>();
        for (T element : this) {
            if (predicate.test(element)) {
                resultList.add(element);
            }
        }
        return resultList;
    }

    public <R> ReactiveList<R> map(Function<T, R> function) {
        ReactiveList<R> resultList = new ReactiveList<>();
        for (T element : this) {
            resultList.add(function.apply(element));
        }
        return resultList;
    }
}
