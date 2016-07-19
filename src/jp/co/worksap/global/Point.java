/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.co.worksap.global;

import java.util.NoSuchElementException;

/**
 *
 * @author thanh
 */
//this class represent a point in the coordination
public class Point implements Comparable {

    public static enum Type {

        START,
        GOAL,
        CHECKPOINT,
        OPEN,
        OBSTACLE
    };
    private final int x;
    private final int y;
    private final Point.Type type;
    private int gCosts;
    private int hCosts;
    private int fCosts;
    private Point previous;

    public int getfCosts() {
        return hCosts + gCosts;
    }

    public void setfCosts(int costs) {
        this.fCosts = costs;
    }

    public Point getPrevious() {
        return previous;
    }

    public void setPrevious(Point previous) {
        this.previous = previous;
    }

    public int getgCosts() {
        return gCosts;
    }

    public void setgCosts(int costs) {
        this.gCosts = costs;
    }

    public int gethCosts() {
        return hCosts;
    }

    public void sethCosts(int costs) {
        this.hCosts = costs;
    }

    public void sethCosts(Point point) {
        sethCosts(Math.abs(this.getX() - this.getX()) + Math.abs(point.getY() - point.getY()));
    }

    private Point.Type checkType(char c) {
        switch (c) {
            case '#':
                return Point.Type.OBSTACLE;
            case '.':
                return Point.Type.OPEN;
            case '@':
                return Point.Type.CHECKPOINT;
            case 'S':
                return Point.Type.START;
            case 'G':
                return Point.Type.GOAL;
            default:
                throw new NoSuchElementException();
        }
    }

    public Point(int x, int y, char c) {
        this.x = x;
        this.y = y;
        this.type = checkType(c);
    }

    public Point(Point point) {
        this.x = point.x;
        this.y = point.y;
        this.type = point.type;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Point.Type getType() {
        return type;
    }

    @Override
    public int compareTo(Object other) {
        int value = this.getfCosts();
        int otherValue = ((Point) other).getfCosts();
        int diff = value - otherValue;
        return (diff > 0) ? 1 : (diff < 0) ? -1 : 0;
    }
}
