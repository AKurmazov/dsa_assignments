package sweep_line;

// Alexander Kurmazov 19BS-574 Group 5

import java.util.ArrayList;
import java.util.Arrays;

public class AVLTree<T extends Comparable<T>> {

    public Node<T> root;

    private int getNodeDepth(Node<T> node) {
        if (node == null) {
            return 0;
        }
        return node.depth;
    }

    private int getDepthDifference(Node<T> node) {
        if (node == null) {
            return 0;
        }
        return getNodeDepth(node.left) - getNodeDepth(node.right);
    }

    private void updateNodeDepth(Node<T> node) {
        node.depth = 1 + Math.max(getNodeDepth(node.left), getNodeDepth(node.right));
    }

    private Node<T> leftRotate(Node<T> node) {
        Node<T> nextRight = node.right;
        Node<T> temp = nextRight.left;

        nextRight.left = node;
        node.right = temp;

        updateNodeDepth(node);
        updateNodeDepth(nextRight);

        return nextRight;
    }

    private Node<T> rightRotate(Node<T> node) {
        Node<T> nextLeft = node.left;
        Node<T> temp = nextLeft.right;

        nextLeft.right = node;
        node.left = temp;

        updateNodeDepth(node);
        updateNodeDepth(nextLeft);

        return nextLeft;
    }

    public Node<T> minNode(Node<T> node) {
        Node<T> temp = node;
        while (temp.left != null) {
            temp = temp.left;
        }
        return temp;
    }

    public ArrayList<Node<T>> findSuccAndPred(Node<T> root, Node<T> succ, Node<T> pred, T value) {
        ArrayList<Node<T>> succAndPred;
        if (root == null) {
            succAndPred = new ArrayList<>(Arrays.asList(succ, pred));
        } else {
            if (root.value.equals(value)) {
                if (root.left != null) {
                    Node<T> temp = root.left;
                    while (temp.right != null) {
                        temp = temp.right;
                    }
                    pred = temp;
                }

                if (root.right != null) {
                    Node<T> temp = root.right;
                    while (temp.left != null) {
                        temp = temp.left;
                    }
                    succ = temp;
                }
                succAndPred = new ArrayList<>(Arrays.asList(succ, pred));
            } else if (root.value.compareTo(value) > 0) {
                succAndPred = findSuccAndPred(root.left, root, pred, value);
            } else {
                succAndPred = findSuccAndPred(root.right, succ, root, value);
            }
        }
        return succAndPred;
    }

    public Node<T> insert(Node<T> node, T value) {
        if (node == null) {
            return (new Node<>(value));
        } else if (value.compareTo(node.value) < 0) {
            node.left = this.insert(node.left, value);
        } else if (value.compareTo(node.value) > 0) {
            node.right = this.insert(node.right, value);
        } else {
            return node;
        }

        updateNodeDepth(node);
        int diff = getDepthDifference(node);

        if (diff > 1) {
            if (value.compareTo(node.left.value) > 0) {
                node.left = this.leftRotate(node.left);
            }
            return this.rightRotate(node);
        } else if (diff < -1) {
            if (value.compareTo(node.right.value) < 0) {
                node.right = this.rightRotate(node.right);
            }
            return this.leftRotate(node);
        }

        return node;
    }

    public Node<T> remove(Node<T> node, T value) {
        if (node == null) {
            return null;
        }

        if (value.compareTo(node.value) < 0) {
            node.left = remove(node.left, value);
        } else if (value.compareTo(node.value) > 0) {
            node.right = remove(node.right, value);
        } else {
            if (node.left == null || node.right == null) {
                Node<T> temp;
                if (node.left == null) {
                    temp = node.right;
                } else {
                    temp = node.left;
                }

                node = temp;
            } else {
                Node<T> temp = minNode(node.right);
                node.value = temp.value;
                node.right = remove(node.right, temp.value);
            }
        }

        if (node == null) {
            return null;
        }

        updateNodeDepth(node);
        int diff = getDepthDifference(node);

        if (diff > 1) {
            if (getDepthDifference(node.left) < 0) {
                node.left = this.leftRotate(node.left);
            }
            return this.rightRotate(node);
        } else if (diff < -1) {
            if (getDepthDifference(node.right) > 0) {
                node.right = this.rightRotate(node.right);
            }
            return this.leftRotate(node);
        }

        return node;
    }

}

class Node<T extends Comparable<T>> {
    Node<T> left;
    Node<T> right;
    T value;
    int depth;

    Node(T value) {
        this.value = value;
        this.depth = 1;
    }
}
