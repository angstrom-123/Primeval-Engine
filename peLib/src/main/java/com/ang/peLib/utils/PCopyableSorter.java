package com.ang.peLib.utils;

import java.util.Comparator;

/**
 * Allows for various sorting techniques on copyable objects by various comparators.
 */
public class PCopyableSorter<T extends PCopyable> {
	private Comparator<? super T> comparator;

	/**
	 * Constructs a new sorter with a comparator.
	 * @param comparator a way to compare the objects to be sorted
	 */
	public PCopyableSorter(Comparator<? super T> comparator) {
		this.comparator = comparator;
	}

	/**
	 * Performs a bubble sort on the given array.
	 * @param arr the array to sort
	 */
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

	/**
	 * Performs a quick sort on the given array.
	 * @param arr the array to sort
	 */
	public void quicksort(T[] arr, int left, int right) {
		if (left < right) {
			int mid = partition(arr, left, right);
			quicksort(arr, left, mid - 1);
			quicksort(arr, mid + 1, right);
		}
	}

	/**
	 * Performs a quick sort partition.
	 * @param  arr   array to partition
	 * @param  left  left pointer for the partition
	 * @param  right right pointer for the partition
	 * @return 		 midpoint of the partition
	 */
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

	/**
	 * Swaps objects at 2 indices in the given array.
	 * @param arr the array to swap in 
	 * @param i   first swap index
	 * @param j   second swap index
	 */
	private void swap(T[] arr, int i, int j) {
		T temp = arr[i].copy();
		arr[i] = arr[j];
		arr[j] = temp;
	}
}
