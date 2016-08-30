import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;
import java.util.Random;
import java.util.*;
import java.lang.StringBuilder;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.PrintWriter;


public class Annealing {
    static int num_edge;
    static int num_vertice;
    public static int calc_sum(Answer ans, boolean[] children, int[][] edges) {
        // for (int i = 0; i < n; i++) {
        //     System.out.print(perm[i] + " ");
        // }
        // System.out.println();
        int sum = 0;
        for (Integer[] c : ans.circArray) {
            for (int v: c) {
                if (children[v] == true) {
                    sum += 2;
                } else {
                    sum += 1;
                }
            }
        }
        return sum;
    }
    
    public static double calc_prob(double temp, int delta) {
        if (delta > 0) return 1;
        double x = (delta) * 0.2;
        double sig = 1 / (1 + Math.exp(-x));
        return sig * temp;
    }
    public static ArrayList<Integer[]> run(Answer ans, int num_iter) {
        int bestSum = ans.sum;
        int currentSum = ans.sum;
        int newSum = 0;
        int delta;
        ArrayList<Integer[]> bestCirc = ans.circArray;
        ArrayList<Integer[]> currentCirc = ans.circArray;
        ArrayList<Integer[]> newCirc;
        Random rnd = new Random();
        for (int i = num_iter; i >= 0; i--) {
            if ((i % 1000) == 0) {
                System.out.println(i);
            }
            int circNum = currentCirc.size();
            if (ans.singleVertex.size() == 0) {
                break;
            }
            if (ans.tot_edges < ans.num_ver * 3) {
                ans.evolve();
            } else {
                if (circNum <= 2) {
                    return currentCirc;
                }
                int x = rnd.nextInt(circNum - 1);
                int y = rnd.nextInt(circNum - 1);
                while (x == y) {
                    y = rnd.nextInt(circNum - 1);
                }
                ans.permute(x, y);
            }
            newCirc = ans.circArray;
            newSum = ans.sum;
            if (newSum > bestSum) {
                bestSum = newSum;
                bestCirc = newCirc;
            }
            delta = newSum - currentSum;
            double temp = ((double) i) / ((double) num_iter);
            if (rnd.nextDouble() < calc_prob(temp, delta)) {
                currentSum = newSum;
                currentCirc = newCirc;
            } else {
                ans.recover(currentCirc, currentSum);
            }
        }
        // System.out.println("Best" + bestSum);

        return new ArrayList<Integer[]>(bestCirc);
    }
    public static String ansToStr(ArrayList<Integer[]> circArray) {
        if (circArray.size() == 0) {
            return "None";
        }
        StringBuilder sb = new StringBuilder();
        String prefix1 = "";
        for (Integer[] circ: circArray) {
            String prefix2 = "";
            sb.append(prefix1);
            prefix1 = ";";
            for (int i = 0; i < circ.length; i++) {
                sb.append(prefix2);
                prefix2 = " ";
                sb.append(circ[i]);
            }
        }
        return sb.toString();
    }
    public static void main(String[] args) throws FileNotFoundException, IOException {
        //Scanner input = new Scanner(System.in);
        int num_data = 492;
        int num_iter = 10000;
        BufferedWriter solution = new BufferedWriter(new FileWriter("./solution.out"));
        
        for (int i = 1; i <= num_data; i++) {
            if ((i % 1000) == 0) {
                System.out.println(i);
            }
            File file = new File("./instances/" + i + ".in");
            Scanner input = new Scanner(file);
            int num_ver = Integer.parseInt(input.nextLine().trim());
            String[] cs = input.nextLine().split("\\s+");
            int[] cn = new int[cs.length];
            if (cs.length == 0) {
                for (int j = 0; j < cs.length; j++){
                    cn[j] = Integer.parseInt(cs[j]);
                }
            }
            int tot_edges = 0;
            boolean[] children = new boolean[num_ver];
            int[][] edges = new int[num_ver][num_ver];
            HashMap<Integer, HashSet<Integer>> vp = new HashMap<Integer, HashSet<Integer>>();
            HashMap<Integer, HashSet<Integer>> vc = new HashMap<Integer, HashSet<Integer>>();
            for (int v: cn) {
                children[v] = true;
            }
            for (int x = 0; x < num_ver; x++) {
                for (int y = 0; y < num_ver; y++) {
                    int e = input.nextInt();
                    if (vc.get(x) == null) {
                       vc.put(x, new HashSet<Integer>());
                    }
                    if (vp.get(y) == null) {
                        vp.put(y, new HashSet<Integer>());
                    }
                    if (e == 1) {
                        edges[x][y] = 1;
                        tot_edges += 1;
                        vc.get(x).add(y);
                        vp.get(y).add(x);
                    }
                }
            }
            System.out.println("instance" + i);
            Answer ans = new Answer(edges, children, num_ver, vp, vc, tot_edges);
            solution.write(ansToStr(run(ans, num_iter)));
            solution.newLine();
            input.close();
        }
        solution.close();
    }
}