package com.ang.peLib.utils;

import java.util.Comparator;

public class PCopyableSorter<T extends PCopyable> {
	private Comparator<? super T> comparator;

	public PCopyableSorter(Comparator<? super T> comparator) {
		this.comparator = comparator;
	}

	public void bubblesort(T[] arr) {
		int n = arr.length;
		for (int i = 0; i < n - 1; i++) {
			boolean swapped = false;
			for (int j = 0; j < n - i - 1; j++) {
				if (comparator.compare(arr[j], arr[j + 1]) == 1) {
					swap(arr, j, j + 1);
					swapped = true;
				}
			}
			if (!swapped) break;

		}
	}

	public void quicksort(T[] arr, int left, int right) {
		if (left < right) {
			int mid = partition(arr, left, right);
			quicksort(arr, left, mid - 1);
			quicksort(arr, mid + 1, right);
		}
	}

	private int partition(T[] arr, int left, int right) {
		int i = left;
		for (int j = left; j < right; j++) {
			if (comparator.compare(arr[j], arr[right]) <= 0) {
				swap(arr, i, j);
				i++;
			}
		}
		swap(arr, i, right);
		return i;

	}

	private void swap(T[] arr, int i, int j) {
		T temp = arr[i].copy();
		arr[i] = arr[j];
		arr[j] = temp;
	}
}
