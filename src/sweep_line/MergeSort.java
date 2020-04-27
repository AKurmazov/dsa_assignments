package sweep_line;

import java.util.ArrayList;
import java.util.Arrays;


// Alexander Kurmazov 19BS-574 Group 5

public class MergeSort<T extends Comparable<T>> {

    public static void main(String[] args) {
        MergeSort<Double> ms = new MergeSort<>();
        ArrayList<Double> array = new ArrayList<>(Arrays.asList(1.3, 4.5, 0d, 3.3, 4.5));
        ms.sort(array, 0, array.size() - 1);
        ms.printArray(array);
    }

    public void sort(ArrayList<T> array, int l, int r) {
        if (l < r) {
            int mid = (l + r) / 2;

            sort(array, l, mid);
            sort(array, mid + 1, r);
            merge(array, l, r, mid);
        }
    }

    private void merge(ArrayList<T> array, int l, int r, int mid) {
        int size1 = mid - l + 1;
        int size2 = r - mid;

        ArrayList<T> leftArray = new ArrayList<>();
        ArrayList<T> rightArray = new ArrayList<>();

        for (int i = l; i <= mid; i++) {
            leftArray.add(array.get(i));
        }
        for (int i = mid + 1; i <= r; i++) {
            rightArray.add(array.get(i));
        }

        int i = 0, j = 0;
        while(i < size1 && j < size2) {
            if (leftArray.get(i).compareTo(rightArray.get(j)) <= 0) {
                array.set(l++, leftArray.get(i++));
            } else {
                array.set(l++, rightArray.get(j++));
            }
        }

        while(i < size1) {
            array.set(l++, leftArray.get(i++));
        }
        while(j < size2) {
            array.set(l++, rightArray.get(j++));
        }
    }

    public void printArray(ArrayList<T> array) {
        for (T entry: array) {
            System.out.print(entry + " ");
        }
        System.out.println();
    }

}
