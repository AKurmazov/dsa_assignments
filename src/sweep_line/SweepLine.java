package sweep_line;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.StringTokenizer;


// Alexander Kurmazov 19BS-574 Group 5

public class SweepLine {

    public static void main(String[] args) {
        // Fast I/O BufferedReader
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        try {
            StringTokenizer st = new StringTokenizer(br.readLine());
            int n = Integer.parseInt(st.nextToken());
            Segment[] segments = new Segment[n];

            ArrayList<Event> events = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                st = new StringTokenizer(br.readLine());
                long xA = Long.parseLong(st.nextToken());
                long yA = Long.parseLong(st.nextToken());
                long xB = Long.parseLong(st.nextToken());
                long yB = Long.parseLong(st.nextToken());

                events.add(new Event(Math.min(xA, xB), true, i));
                events.add(new Event(Math.max(xA, xB), false, i));

                segments[i] = new Segment(new Point(xA, yA), new Point(xB, yB), i);
            }

            // Sort events with respect to X values
            MergeSort<Event> ms = new MergeSort<>();
            ms.sort(events, 0, events.size() - 1);


            IntersectionChecker SI = new IntersectionChecker();
            AVLTree<Segment> tree = new AVLTree<>(); // Storage for segments sorted wrt. Y values
            for (int i = 0; i < events.size(); i++) {
                Event event = events.get(i);
                int index = event.index;
                Segment s = segments[index];

                // Get the successor and predecessor of a segment
                ArrayList<Node<Segment>> succAndPred = tree.findSuccAndPred(tree.root, null, null, s);
                Node<Segment> succNode = succAndPred.get(0);
                Node<Segment> predNode= succAndPred.get(1);

                // Check the intersections using Sweep Line Algorithm
                if (event.isStart) {
                    tree.root = tree.insert(tree.root, s);

                    if (predNode != null && SI.intersect(s.p, s.q, predNode.value.p, predNode.value.q)) {
                        Segment t = predNode.value;
                        System.out.println("INTERSECTION");
                        System.out.println(t.p.x + " " + t.p.y + " " + t.q.x + " " + t.q.y);
                        System.out.println(s.p.x + " " + s.p.y + " " + s.q.x + " " + s.q.y);
                        return;
                    } else if (succNode != null && SI.intersect(s.p, s.q, succNode.value.p, succNode.value.q)) {
                        Segment t = succNode.value;
                        System.out.println("INTERSECTION");
                        System.out.println(s.p.x + " " + s.p.y + " " + s.q.x + " " + s.q.y);
                        System.out.println(t.p.x + " " + t.p.y + " " + t.q.x + " " + t.q.y);
                        return;
                    }
                } else {
                    if (predNode != null && succNode != null && SI.intersect(predNode.value.p, predNode.value.q, succNode.value.p, succNode.value.q)) {
                        Segment t = succNode.value;
                        Segment f = predNode.value;
                        System.out.println("INTERSECTION");
                        System.out.println(f.p.x + " " + f.p.y + " " + f.q.x + " " + f.q.y);
                        System.out.println(t.p.x + " " + t.p.y + " " + t.q.x + " " + t.q.y);
                        return;
                    }
                    tree.root = tree.remove(tree.root, segments[index]);
                }
            }
            System.out.println("NO INTERSECTIONS");
        } catch (IOException e) {
        }

    }

}

class Segment implements Comparable<Segment> {
    Point p, q;
    int index;

    double getY(double x) {
        if (p.x == q.x) {
            return p.y;
        }
        return p.y + (q.y - p.y) * (x - p.x) / (q.x - p.x);
    }

    @Override
    public int compareTo(Segment other) {
        double x = Math.max(Math.min(this.p.x, this.q.x), Math.min(other.p.x, other.q.x));
        return (int) (this.getY(x) - other.getY(x));
    }

    public Segment() {}
    public Segment(Point p, Point q, int index) {
        this.p = p;
        this.q = q;
        this.index = index;
    }

    public String toString() {
        return this.index + " ";
    }
}

class Event implements Comparable<Event> {

    int index;
    long xVal;
    boolean isStart;

    public Event() {}
    public Event(long xVal, boolean isStart, int index) {
        this.xVal = xVal;
        this.isStart = isStart;
        this.index = index;
    }

    @Override
    public int compareTo(Event other) {
        return (int) (this.xVal - other.xVal);
    }

}

class IntersectionChecker {

    private int orientation(Point p, Point q, Point r) {
        /*
        Return types:
          -1 - counterclockwise
           0 - collinear
           1 - clockwise
        */
        double slope = (q.y - p.y) * (r.x - q.x) - (r.y - q.y) * (q.x - p.x);

        if (slope > 0) {
            return 1;
        } else if (slope == 0) {
            return 0;
        } else {
            return -1;
        }
    }

    public boolean intersect(Point p0, Point q0, Point p1, Point q1) {
        int o1, o2, o3, o4;
        o1 = orientation(p0, q0, p1);
        o2 = orientation(p0, q0, q1);
        o3 = orientation(p1, q1, p0);
        o4 = orientation(p1, q1, q0);

        if (o1 != o2 && o3 != o4) {
            return true;
        }

        if (o1 == 0 && o2 == 0 && o3 == 0 && o4 == 0) {
            if (checkBorders(p0, q0, p1, q1) || checkBorders(p1, q1, p0, q0)) {
                return true;
            }
        }

        return false;
    }

    private boolean checkBorders(Point p0, Point q0, Point p1, Point q1) {
        if (((p0.x <= Math.max(p1.x, q1.x) && p0.x >= Math.min(p1.x, q1.x))
                && (p0.y <= Math.max(p1.y, q1.y) && p0.y >= Math.min(p1.y, q1.y)))
                || ((q0.x <= Math.max(p1.x, q1.x) && q0.x >= Math.min(p1.x, q1.x))
                && (q0.y <= Math.max(p1.y, q1.y) && q0.y >= Math.min(p1.y, q1.y)))) {
            return true;
        }
        return false;
    }

}

class Point {

    long x, y;

    Point(long x, long y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return x + " " + y;
    }

}

//class AVLTree<T extends Comparable<T>> {
//
//    public Node<T> root;
//
//    private int getNodeDepth(Node<T> node) {
//        if (node == null) {
//            return 0;
//        }
//        return node.depth;
//    }
//
//    private int getDepthDifference(Node<T> node) {
//        if (node == null) {
//            return 0;
//        }
//        return getNodeDepth(node.left) - getNodeDepth(node.right);
//    }
//
//    private void updateNodeDepth(Node<T> node) {
//        node.depth = 1 + Math.max(getNodeDepth(node.left), getNodeDepth(node.right));
//    }
//
//    private Node<T> leftRotate(Node<T> node) {
//        Node<T> nextRight = node.right;
//        Node<T> temp = nextRight.left;
//
//        nextRight.left = node;
//        node.right = temp;
//
//        updateNodeDepth(node);
//        updateNodeDepth(nextRight);
//
//        return nextRight;
//    }
//
//    private Node<T> rightRotate(Node<T> node) {
//        Node<T> nextLeft = node.left;
//        Node<T> temp = nextLeft.right;
//
//        nextLeft.right = node;
//        node.left = temp;
//
//        updateNodeDepth(node);
//        updateNodeDepth(nextLeft);
//
//        return nextLeft;
//    }
//
//    public Node<T> minNode(Node<T> node) {
//        Node<T> temp = node;
//        while (temp.left != null) {
//            temp = temp.left;
//        }
//        return temp;
//    }
//
//    public ArrayList<Node<T>> findSuccAndPred(Node<T> root, Node<T> succ, Node<T> pred, T value) {
//        ArrayList<Node<T>> succAndPred;
//        if (root == null) {
//            succAndPred = new ArrayList<>(Arrays.asList(succ, pred));
//        } else {
//            if (root.value.equals(value)) {
//                if (root.left != null) {
//                    Node<T> temp = root.left;
//                    while (temp.right != null) {
//                        temp = temp.right;
//                    }
//                    pred = temp;
//                }
//
//                if (root.right != null) {
//                    Node<T> temp = root.right;
//                    while (temp.left != null) {
//                        temp = temp.left;
//                    }
//                    succ = temp;
//                }
//                succAndPred = new ArrayList<>(Arrays.asList(succ, pred));
//            } else if (root.value.compareTo(value) > 0) {
//                succAndPred = findSuccAndPred(root.left, root, pred, value);
//            } else {
//                succAndPred = findSuccAndPred(root.right, succ, root, value);
//            }
//        }
//        return succAndPred;
//    }
//
//    public Node<T> insert(Node<T> node, T value) {
//        if (node == null) {
//            return (new Node<>(value));
//        } else if (value.compareTo(node.value) < 0) {
//            node.left = this.insert(node.left, value);
//        } else if (value.compareTo(node.value) > 0) {
//            node.right = this.insert(node.right, value);
//        } else {
//            return node;
//        }
//
//        updateNodeDepth(node);
//        int diff = getDepthDifference(node);
//
//        if (diff > 1) {
//            if (value.compareTo(node.left.value) > 0) {
//                node.left = this.leftRotate(node.left);
//            }
//            return this.rightRotate(node);
//        } else if (diff < -1) {
//            if (value.compareTo(node.right.value) < 0) {
//                node.right = this.rightRotate(node.right);
//            }
//            return this.leftRotate(node);
//        }
//
//        return node;
//    }
//
//    public Node<T> remove(Node<T> node, T value) {
//        if (node == null) {
//            return null;
//        }
//
//        if (value.compareTo(node.value) < 0) {
//            node.left = remove(node.left, value);
//        } else if (value.compareTo(node.value) > 0) {
//            node.right = remove(node.right, value);
//        } else {
//            if (node.left == null || node.right == null) {
//                Node<T> temp;
//                if (node.left == null) {
//                    temp = node.right;
//                } else {
//                    temp = node.left;
//                }
//
//                node = temp;
//            } else {
//                Node<T> temp = minNode(node.right);
//                node.value = temp.value;
//                node.right = remove(node.right, temp.value);
//            }
//        }
//
//        if (node == null) {
//            return null;
//        }
//
//        updateNodeDepth(node);
//        int diff = getDepthDifference(node);
//
//        if (diff > 1) {
//            if (getDepthDifference(node.left) < 0) {
//                node.left = this.leftRotate(node.left);
//            }
//            return this.rightRotate(node);
//        } else if (diff < -1) {
//            if (getDepthDifference(node.right) > 0) {
//                node.right = this.rightRotate(node.right);
//            }
//            return this.leftRotate(node);
//        }
//
//        return node;
//    }
//
//}

//class Node<T extends Comparable<T>> {
//    Node<T> left;
//    Node<T> right;
//    T value;
//    int depth;
//
//    Node(T value) {
//        this.value = value;
//        this.depth = 1;
//    }
//}

//class MergeSort<T extends Comparable<T>> {
//
//    public void sort(ArrayList<T> array, int l, int r) {
//        if (l < r) {
//            int mid = (l + r) / 2;
//
//            sort(array, l, mid);
//            sort(array, mid + 1, r);
//            merge(array, l, r, mid);
//        }
//    }
//
//    private void merge(ArrayList<T> array, int l, int r, int mid) {
//        int size1 = mid - l + 1;
//        int size2 = r - mid;
//
//        ArrayList<T> leftArray = new ArrayList<>();
//        ArrayList<T> rightArray = new ArrayList<>();
//
//        for (int i = l; i <= mid; i++) {
//            leftArray.add(array.get(i));
//        }
//        for (int i = mid + 1; i <= r; i++) {
//            rightArray.add(array.get(i));
//        }
//
//        int i = 0, j = 0;
//        while(i < size1 && j < size2) {
//            if (leftArray.get(i).compareTo(rightArray.get(j)) <= 0) {
//                array.set(l++, leftArray.get(i++));
//            } else {
//                array.set(l++, rightArray.get(j++));
//            }
//        }
//
//        while(i < size1) {
//            array.set(l++, leftArray.get(i++));
//        }
//        while(j < size2) {
//            array.set(l++, rightArray.get(j++));
//        }
//    }
//
//}

