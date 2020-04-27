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