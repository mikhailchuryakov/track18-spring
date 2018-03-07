package ru.track.list;

import java.util.Arrays;
import java.util.NoSuchElementException;

/**
 * Должен наследовать List
 *
 * Должен иметь 2 конструктора
 * - без аргументов - создает внутренний массив дефолтного размера на ваш выбор
 * - с аргументом - начальный размер массива
 */
public class MyArrayList extends List {
    private int[] array;

    public MyArrayList() {
        array = new int[0];
    }

    public MyArrayList(int capacity) {
        array = new int[capacity];
    }

    @Override
    void add(int item) {
        int[] arr = new int[array.length + 1];
        System.arraycopy(array, 0, arr, 0, array.length);
        arr[array.length] = item;
        array = arr;
    }

    @Override
    int remove(int idx) throws NoSuchElementException {
        if (idx > array.length - 1) throw new NoSuchElementException();
        int[] arr = new int[array.length - 1];
        int item = array[idx];
        System.arraycopy(array, 0, arr, 0, idx);
        System.arraycopy(array, idx + 1, arr, idx, array.length - 1 - idx);
        array = arr;
        return item;
    }

    @Override
    int get(int idx) throws NoSuchElementException {
        if (idx > array.length - 1) throw new NoSuchElementException();
        return array[idx];
    }

    @Override
    int size() {
        return array.length;
    }
}
