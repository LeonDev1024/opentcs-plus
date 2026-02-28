package org.opentcs.algorithm.path;

/**
 * 路径边类
 */
public class Edge {
    private Node source;
    private Node target;
    private double weight;

    public Edge(Node source, Node target, double weight) {
        this.source = source;
        this.target = target;
        this.weight = weight;
    }

    // Getters and Setters
    public Node getSource() {
        return source;
    }

    public void setSource(Node source) {
        this.source = source;
    }

    public Node getTarget() {
        return target;
    }

    public void setTarget(Node target) {
        this.target = target;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    @Override
    public String toString() {
        return "Edge{" +
                "source=" + source.getId() +
                ", target=" + target.getId() +
                ", weight=" + weight +
                '}';
    }
}
