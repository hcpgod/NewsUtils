package com.tools.utils;

import java.util.LinkedList;

/**
 * @author hcp
 */
public class FixSizeLinkedList<T> extends LinkedList<T> {

  private static final long serialVersionUID = 3292612616231532364L;

  private int capacity;

  public FixSizeLinkedList(int capacity) {
    super();
    this.capacity = capacity;
  }

  @Override
  public boolean add(T e) {
    // 超过长度，移除最后一个
    if (size() + 1 > capacity) {
      super.removeFirst();
    }
    return super.add(e);
  }
}
