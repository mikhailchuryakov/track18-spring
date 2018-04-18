package ru.track.list;

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
    private int length;

    public MyArrayList() {
        array = new int[10];
    }

    public MyArrayList(int capacity) {
        array = new int[capacity];
    }

    @Override
    void add(int item) {
        if(length < array.length){
            array[length] = item;
        }
        else {
            int[] arr = new int[length * 2 + 1]; // 1 - in case capacity = 0
            System.arraycopy(array, 0, arr, 0, length);
            arr[length] = item;
            array = arr;
        }
        length++;
    }

    @Override
    int remove(int idx) throws NoSuchElementException {
        if (idx > length) throw new NoSuchElementException();
        int item = array[idx];
        System.arraycopy(array, idx +  1, array, idx, length - idx);
        length--;
        return item;
    }

    @Override
    int get(int idx) throws NoSuchElementException {
        if (idx >= length) throw new NoSuchElementException();
        return array[idx];
    }

    @Override
    int size() {
        return length;
    }
}