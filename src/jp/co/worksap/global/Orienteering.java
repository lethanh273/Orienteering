package jp.co.worksap.global;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import jp.co.worksap.global.Point;
import jp.co.worksap.global.Point.Type;
import static jp.co.worksap.global.Point.Type.CHECKPOINT;
import static jp.co.worksap.global.Point.Type.GOAL;
import static jp.co.worksap.global.Point.Type.OBSTACLE;
import static jp.co.worksap.global.Point.Type.START;

/**
 * @author thanh
 */
public class Orienteering {

    private int[][] adjacency_matrix;
    private Point start;
    private Point goal;
    private List<Point> checkPoints;
    private int width;
    private int height;
    private Point[][] map;
    private boolean hasPath = true;
    private int length;
    private int[][] path;

    public static void main(String[] args) throws IOException {
        Orienteering or = new Orienteering();
        or.buildMaps();
        int result = or.solve(or.setUpAdjacencyMatrix());
        if (!or.hasPath) {
            System.out.println(-1);
        } else {
            System.out.println(result);
        }

    }

    /**
     * initializes all points in the map.
     */
    private void buildMaps() throws FileNotFoundException, IOException {

        BufferedReader reader;
        reader = new BufferedReader(new InputStreamReader(System.in));
        //reader = new BufferedReader(new FileReader("C:\\Users\\thanh\\Documents\\NetBeansProjects\\worksap\\src\\" + "example1.txt"));
        String line = reader.readLine();
        String[] dimension = line.split(" ");
        this.width = Integer.valueOf(dimension[0]);
        this.height = Integer.valueOf(dimension[1]);

        map = new Point[height][width];
        int row = 0;
        checkPoints = new ArrayList<>();
        while ((line = reader.readLine()) != null) {
            for (int col = 0; col < width; col++) {
                Point p = new Point(row, col, line.charAt(col));
                map[row][col] = p;
                if (p.getType() == START) {
                    start = p;
                } else if (p.getType() == GOAL) {
                    goal = p;
                } else if (p.getType() == CHECKPOINT) {
                    checkPoints.add(p);
                }
            }
            row++;
        }
        reader.close();
    }

    /**
     * Set up the adjacency matrix that stores immediate calculation of minimum
     * cost. We translate given 'orienteering' problem to classic traveling
     * salesman problem.
     */
    private int[][] setUpAdjacencyMatrix() {

        adjacency_matrix = new int[checkPoints.size() + 1][checkPoints.size() + 1];
        if (checkPoints.isEmpty()) {
            adjacency_matrix[0][0] = findPath(start, goal);
            return adjacency_matrix;
        }
        for (int i = 0; i <= checkPoints.size() - 1; i++) {

            Point checkpoint = checkPoints.get(i);
            int dis = findPath(goal, checkpoint);
            adjacency_matrix[i + 1][0] = dis;
            int dis2 = findPath(checkpoint, start);
            adjacency_matrix[0][i + 1] = dis2;

        }
        for (int i = 1; i <= checkPoints.size(); i++) {
            for (int j = 1; j <= checkPoints.size(); j++) {
                if (i != j) {
                    int dis = findPath(checkPoints.get(i - 1), checkPoints.get(j - 1));
                    adjacency_matrix[i][j] = dis;
                }
            }
        }
        return adjacency_matrix;
    }

    /**
     * finds an walkable path from start to end point in the map using A*
     * algorithm.
     */
    private int findPath(Point p1, Point p2) {
        PriorityQueue openList = new PriorityQueue();
        HashSet closedSet = new HashSet();
        openList.offer(map[p1.getX()][p1.getY()]);
        while (!openList.isEmpty()) {
            Point curr = (Point) openList.poll();
            closedSet.add(curr);
            openList.remove(curr);
            if (curr.getX() == p2.getX() && (curr.getY() == p2.getY())) {
                return getDistance(map[p1.getX()][p1.getY()], curr);
            }
            List<Point> neighbor = getAdjacentPoints(curr);
            for (Point adj : neighbor) {

                if (!closedSet.contains(adj)) {
                    if (!openList.contains(adj)) {
                        adj.setPrevious(curr); // 
                        openList.add(adj);
                    }
                }
            }
            if (openList.isEmpty()) {
                hasPath = false;
                return 0;
            }
        }
        hasPath = false;
        return 0;
    }

    /**
     * find distance between 2 points according to path found
     */
    private int getDistance(Point start, Point end) {

        LinkedList<Point> path = new LinkedList<>();
        Point curr = end;
        boolean done = false;
        while (!done) {
            path.addFirst(curr);
            curr = (Point) curr.getPrevious();
            if (curr.equals(start)) {
                done = true;
            }
        }
        return path.size();
    }

    private int solve(int[][] adjacency_matrix) {
        int n = adjacency_matrix[0].length;
        length = (int) Math.pow(2, n);

        //to store intermediate value calculated from each step of dynamic programming
        int[][] storage = new int[n][length];
        path = new int[n][length];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < length; j++) {
                storage[i][j] = -1;
                path[i][j] = -1;
            }
        }

        //initialize the final cost that reaches goal based on adjacency matrix 
        for (int i = 0; i < n; i++) {
            storage[i][0] = adjacency_matrix[i][0];
        }

        String st = Integer.toBinaryString(length);

        int result = tsp(0, length - 2, storage);

        return result;
    }

    private int tsp(int start, int set, int[][] storage) {

        int offset, subset, result = -1, cost;
        if (storage[start][set] > -1) {
            return storage[start][set];
        } else {
            for (int x = 1; x < adjacency_matrix[0].length; x++) {

                // use binary code to represent set .
                subset = set & ~(0001 << x);
                if (subset != set) {

                    cost = adjacency_matrix[start][x] + tsp(x, subset, storage);
                    if (result == -1 || result >= cost) {
                        result = cost;
                        path[start][set] = x;
                    }
                }
            }
            storage[start][set] = result;

            return result;
        }
    }
    private ArrayList<Integer> outputArray = new ArrayList<Integer>();

    private void getPath(int start, int set) {
        if (path[start][set] == -1) {
            return;
        }
        int x = path[start][set];
        int subset = set & ~(0001 << x);
        outputArray.add(x);
        getPath(x, subset);
    }

    /**
     * returns a list of points adjacent to the given point
     */
    private ArrayList<Point> getAdjacentPoints(Point point) {

        int x = point.getX();
        int y = point.getY();
        ArrayList<Point> neighbor = new ArrayList<>();
        if (x > 0) {
            Point p = map[x - 1][y];
            if (p.getType() != OBSTACLE) {
                neighbor.add(p);
            }
        }

        if (x < width - 1) {
            Point p = map[x + 1][y];
            if (p.getType() != OBSTACLE) {
                neighbor.add(p);
            }
        }

        if (y > 0) {
            Point p = map[x][y - 1];
            if (p.getType() != OBSTACLE) {
                neighbor.add(p);
            }
        }

        if (y < height - 1) {
            Point p = map[x][y + 1];
            if (p.getType() != OBSTACLE) {
                neighbor.add(p);
            }
        }

        return neighbor;
    }

    //add according to fullCost order.To optimized path finding.
    class PriorityQueue extends LinkedList {

        public void add(Comparable point) {
            for (int i = 0; i < size(); i++) {
                if (point.compareTo(get(i)) <= 0) {
                    add(i, point);
                    return;
                }
            }
            addLast(point);
        }
    }
}
