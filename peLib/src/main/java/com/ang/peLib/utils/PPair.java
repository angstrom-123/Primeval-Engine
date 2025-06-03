package com.ang.peLib.utils;

public class PPair<T> {
	private T object1;
	private T object2;

	public T getFirst() { return object1; }

	public T getSecond() { return object2; }

	public PPair(T object1, T object2) {
		this.object1 = object1;
		this.object2 = object2;
	}
}
