package ru.track.list;

import java.util.NoSuchElementException;

/**
 * Должен наследовать List
 * Односвязный список
 */
public class MyLinkedList extends List {

    /**
     * private - используется для сокрытия этого класса от других.
     * Класс доступен только изнутри того, где он объявлен
     * <p>
     * static - позволяет использовать Node без создания экземпляра внешнего класса
     */
    private static class Node {
        Node prev;
        Node next;
        int val;

        Node(Node prev, Node next, int val) {
            this.prev = prev;
            this.next = next;
            this.val = val;
        }
    }

    private static Node head;
    private int count = 0;

    @Override
    void add(int item) {
        count++;
        if (head == null) {
            head = new Node(null, null, item);
            return;
        }
        Node node = head;
        while (node.next != null){
            node = node.next;
        }
        node.next = new Node (node, null, item);
    }

    @Override
    int remove(int idx) throws NoSuchElementException {
        int item;
        if (head == null) throw new NoSuchElementException();
        Node node = head;
        if (head.next == null && idx == 0){
            item = head.val;
            head = null;
            count--;
            return item;
        }
        for (int i = 1; i < idx; i++){
            if (node.next == null) throw new NoSuchElementException();
            node = node.next;
        }
        count--;
        item = node.val;
        node.next = node.next.next;
        return item;
    }

    @Override
    int get(int idx) throws NoSuchElementException {
        if (head == null) throw new NoSuchElementException();
        Node node = head;
        for (int i = 0; i < idx; i++){
            if (node == null) throw new NoSuchElementException();
            node = node.next;
        }
        return node.val;
    }

    @Override
    int size() {
        return count;
    }
}
