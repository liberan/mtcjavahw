package com.mipt.angelikaliber.collections;

public interface CustomList<A> {
    public void add(A element);
    public A get(int index);
    public void remove(int index);
    public int size();
    public boolean isEmpty();
}
