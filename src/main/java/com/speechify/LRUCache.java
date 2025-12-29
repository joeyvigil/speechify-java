package com.speechify;

import java.util.HashMap;
import java.util.Map;

public class LRUCache<T> {

    private final int maxItemsCount;
    private final Map<String, Node<T>> cache;

    private Node<T> head;
    private Node<T> tail;

    public LRUCache(CacheLimits limits) {
        this.maxItemsCount = limits.getMaxItemsCount();
        this.cache = new HashMap<>();
    }

    public T get(String key) {
        Node<T> node = cache.get(key);
        if (node == null) {
            return null;
        }

        moveToHead(node);
        return node.value;
    }

    public void set(String key, T value) {
        Node<T> existing = cache.get(key);

        if (existing != null) {
            existing.value = value;
            moveToHead(existing);
            return;
        }

        if (cache.size() >= maxItemsCount) {
            evictLeastRecentlyUsed();
        }

        Node<T> node = new Node<>(key, value);
        cache.put(key, node);
        addToHead(node);
    }

    private void evictLeastRecentlyUsed() {
        if (tail == null) {
            return;
        }

        cache.remove(tail.key);
        removeNode(tail);
    }

    private void moveToHead(Node<T> node) {
        removeNode(node);
        addToHead(node);
    }

    private void addToHead(Node<T> node) {
        node.prev = null;
        node.next = head;

        if (head != null) {
            head.prev = node;
        }

        head = node;

        if (tail == null) {
            tail = node;
        }
    }

    private void removeNode(Node<T> node) {
        if (node.prev != null) {
            node.prev.next = node.next;
        } else {
            head = node.next;
        }

        if (node.next != null) {
            node.next.prev = node.prev;
        } else {
            tail = node.prev;
        }

        node.prev = null;
        node.next = null;
    }

    private static final class Node<T> {
        private final String key;
        private T value;
        private Node<T> prev;
        private Node<T> next;

        private Node(String key, T value) {
            this.key = key;
            this.value = value;
        }
    }
}
