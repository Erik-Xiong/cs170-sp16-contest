import java.util.*;
import java.lang.Math;
import java.lang.StringBuilder;
import java.io.PrintWriter;

public class Answer {
    public ArrayList<Integer[]> circArray = new ArrayList<Integer[]>();
    public HashSet<Integer> singleVertex = new HashSet<Integer>();
    public HashSet<Integer> brokenCirc = new HashSet<Integer>();
    public HashSet<Integer> newCirc= new HashSet<Integer>();
    public HashMap<Integer, HashSet<Integer>> vp; // parents of vertices
    public HashMap<Integer, HashSet<Integer>> vc; // children of vertices
    private int[][] edges;
    private boolean[] children;
    public int num_ver;
    public int sum;
    public int tot_edges;

    public Answer (int[][] e, boolean[] c, int n, HashMap<Integer, HashSet<Integer>> ps, HashMap<Integer, HashSet<Integer>> cs, int tot) {
        num_ver = n;
        children = c;
        edges = e;
        vp = ps;
        vc = cs;
        tot_edges = tot;
        this.singleVertex = new  HashSet<Integer>();
        for (int i = 0; i < n; i++) {
            singleVertex.add(i);
        }
        HashSet<Integer> tempVertex = new HashSet<Integer>(singleVertex);
        while (tempVertex.size() != 0) {
            ArrayList<Integer> circ = this.search(tempVertex, false);
            tempVertex.removeAll(circ);
            if (circ.size() == 0) {
                break;
            }
            if (circ.size() > 1) {
                Integer[] tempArray = new Integer[circ.size()];
                tempArray = circ.toArray(tempArray);
                circArray.add(tempArray);
                singleVertex.removeAll(circ);
            }
        }
        for (Integer[] a: circArray) {
            sum += calc_sum(Arrays.asList(a));
        }
    }
    public void evolve() {
        sum = 0;
        circArray = new ArrayList<Integer[]>();
        HashSet<Integer> tempVertex = new HashSet<Integer>();
        for (int i = 0; i < num_ver; i++) {
            tempVertex.add(i);
        }
        while (tempVertex.size() != 0) {
            ArrayList<Integer> circ = this.search(tempVertex, true);
            tempVertex.removeAll(circ);
            if (circ.size() == 0) {
                break;
            }
            if (circ.size() > 1) {
                Integer[] tempArray = new Integer[circ.size()];
                tempArray = circ.toArray(tempArray);
                circArray.add(tempArray);
                singleVertex.removeAll(circ);
            }
        }
        for (Integer[] a: circArray) {
            sum += calc_sum(Arrays.asList(a));
        }
    }

    public void permute(int i, int j) {
        Integer[] b1 = circArray.remove(i);
        Integer[] b2 = circArray.remove(j);
        brokenCirc = new HashSet<Integer>(Arrays.asList(b1));
        brokenCirc.addAll(Arrays.asList(b2));
        newCirc = new HashSet<Integer>();
        singleVertex.addAll(brokenCirc);
        HashSet<Integer> tempVertex = new HashSet<Integer>(singleVertex);
        while (tempVertex.size() != 0) {
            ArrayList<Integer> circ = search(tempVertex, true);
            tempVertex.removeAll(circ);
            if (circ.size() == 0) {
                break;
            }
            if (circ.size() > 1) {
                singleVertex.removeAll(circ);
                newCirc.addAll(circ);
                Integer[] tempArray = new Integer[circ.size()];
                tempArray = circ.toArray(tempArray);
                circArray.add(tempArray);
            }
        }
        sum += calc_sum(newCirc) - calc_sum(brokenCirc);
    }
    public void recover(ArrayList<Integer[]> oldCircArray, int oldSum) {
        circArray = oldCircArray;
        sum = oldSum;
        singleVertex.addAll(brokenCirc);
        singleVertex.removeAll(newCirc);
    }
    private int debug(ArrayList<Integer[]> al, HashSet<Integer> sv) {
        int s = 0;
        for (Integer[] v: al) {
            s += v.length;
        }
        return s + sv.size();
    }
    public ArrayList<Integer> search(HashSet<Integer> vertices, boolean randomized) {
        ArrayList<Integer> rv = new ArrayList<Integer>();
        Queue<Tuple> pq = rank(vertices, randomized);
        if (pq.peek() == null) {
            return rv;
        }
        int start = pq.poll().vertex;
        HashSet<Integer> d1 = new HashSet<Integer>(vc.get(start));
        d1.retainAll(vertices);
        HashSet<Integer> d2 = new HashSet<Integer>();
        for (int x: d1) {
            d2.addAll(vc.get(x));
            d2.remove(start);
        }
        d2.retainAll(vertices);
        HashSet<Integer> d3 = new HashSet<Integer>();
        for (int x: d2) {
            d3.addAll(vc.get(x));
            d3.remove(start);
        }
        d3.retainAll(vertices);
        HashSet<Integer> d4 = new HashSet<Integer>();
        for (int x: d3) {
            d4.addAll(vc.get(x));
            d4.remove(start);
        }
        d4.retainAll(vertices);

        HashSet<Integer> temp;
        d4.retainAll(vp.get(start));
        if (d4.size() != 0) {
            HashSet<Integer> b3 = new HashSet<Integer>();
            HashSet<Integer> b2 = new HashSet<Integer>();
            HashSet<Integer> b1 = new HashSet<Integer>();
            Queue<Tuple> pq4 = rank(d4, randomized);
            for (int i = 0; i < pq4.size(); i++) {
                int v4 = pq4.poll().vertex;
                temp = new HashSet<Integer>(b3);
                rv.add(v4);
                temp.retainAll(vp.get(v4));
                temp.removeAll(rv);
                if (temp.size() != 0) {
                    b3 = new HashSet<Integer>(temp);
                    b2.removeAll(rv);
                    b1.removeAll(rv);
                    break;
                }
                rv.remove(rv.size() - 1);
            }
            if (b3.size() != 0) {
                Queue<Tuple> pq3 = rank(b3, randomized);
                for (int i = 0; i < pq3.size(); i++) {
                    int v3 = pq3.poll().vertex;
                    temp = new HashSet<Integer>(b2);
                    rv.add(v3);
                    temp.retainAll(vp.get(v3));
                    temp.removeAll(rv);
                    if (temp.size() != 0) {
                        b2 = new HashSet<Integer>(temp);
                        b1.removeAll(rv);
                        break;
                    }
                    rv.remove(rv.size() - 1);
                }
                if (b2.size() != 0) {
                    Queue<Tuple> pq2 = rank(b2, randomized);
                    for (int i = 0; i < pq2.size(); i++) {
                        int v2 = pq2.poll().vertex;
                        temp = new HashSet<Integer>(b1);
                        rv.add(v2);
                        temp.retainAll(vp.get(v2));
                        temp.removeAll(rv);
                        if (temp.size() != 0) {
                            b1 = new HashSet<Integer>(temp);
                            break;
                        }
                        rv.remove(rv.size() - 1);
                    }
                    Queue<Tuple> pq1 = rank(b1, randomized);
                    if (pq1.size() != 0) {
                        int v1 = pq1.poll().vertex;
                        rv.add(v1);
                        rv.add(start);
                        Collections.reverse(rv);
                        if (check(rv) == false) {
                            System.out.println("Error");
                        }
                        return rv;
                    }
                }
            }
        }

        rv.clear();
        d3.retainAll(vp.get(start));
        if (d3.size() != 0) {
            HashSet<Integer> b2 = new HashSet<Integer>();
            HashSet<Integer> b1 = new HashSet<Integer>();
            Queue<Tuple> pq3 = rank(d3, randomized);
            for (int i = 0; i < pq3.size(); i++) {
                int v3 = pq3.poll().vertex;
                temp = new HashSet<Integer>(b2);
                rv.add(v3);
                temp.retainAll(vp.get(v3));
                temp.removeAll(rv);
                if (temp.size() != 0) {
                    b2 = new HashSet<Integer>(temp);
                    b1.removeAll(rv);
                    break;
                }
                rv.remove(rv.size() - 1);
            }
            if (b2.size() != 0) {
                Queue<Tuple> pq2 = rank(b2, randomized);
                for (int i = 0; i < pq2.size(); i++) {
                    int v2 = pq2.poll().vertex;
                    temp = new HashSet<Integer>(b1);
                    rv.add(v2);
                    temp.retainAll(vp.get(v2));
                    temp.removeAll(rv);
                    if (temp.size() != 0) {
                        b1 = new HashSet<Integer>(temp);
                        break;
                    }
                    rv.remove(rv.size() - 1);
                }
                Queue<Tuple> pq1 = rank(b1, randomized);
                if (pq1.size() != 0) {
                    int v1 = pq1.poll().vertex;
                    rv.add(v1);
                    rv.add(start);
                    Collections.reverse(rv);
                    if (check(rv) == false) {
                        System.out.println("Error");
                    }
                    return rv;
                }
            }
        }
        rv.clear();
        d2.retainAll(vp.get(start));
        if (d2.size() != 0) {
            HashSet<Integer> b1 = new HashSet<Integer>();
            Queue<Tuple> pq2 = rank(d2, randomized);
            for (int i = 0; i < pq2.size(); i++) {
                int v2 = pq2.poll().vertex;
                temp = new HashSet<Integer>(b1);
                rv.add(v2);
                temp.retainAll(vp.get(v2));
                temp.removeAll(rv);
                if (temp.size() != 0) {
                    b1 = new HashSet<Integer>(temp);
                    break;
                }
                rv.remove(rv.size() - 1);
            }
            if (b1.size() != 0) {
                int v1 = rank(b1, randomized).poll().vertex;
                rv.add(v1);
                rv.add(start);
                Collections.reverse(rv);
                if (check(rv) == false) {
                    System.out.println("Error");
                }
                return rv;
            }
        }
        rv.clear();
        d1.retainAll(vp.get(start));
        Queue<Tuple> pq1 = rank(d1, randomized);
        if (pq1.size() != 0) {
            int v1 = pq1.poll().vertex;
            rv.add(v1);
            rv.add(start);
            Collections.reverse(rv);
            if (check(rv) == false) {
                System.out.println("Error");
            }
            return rv;
        }

        rv.add(start);
        return rv;

    }
    public boolean check(ArrayList<Integer> rv) {
        for (int i = 0; i < rv.size()-1; i++) {
            if (edges[rv.get(i)][rv.get(i+1)] == 0) {
                return false;
            }
        }
        if (edges[rv.get(rv.size()-1)][rv.get(0)] == 0) {
            return false;
        }
        return true;
    }
    public Queue<Tuple> rank(Collection<Integer> vertices, boolean randomized) {
        if (randomized == true) {
            LinkedList<Tuple> rv = new LinkedList<Tuple>();
            for (int v: vertices) {
                rv.add(new Tuple(v, 0));
            }
            Collections.shuffle(rv);
            return rv;
        }
        PriorityQueue<Tuple> pq = new PriorityQueue<Tuple>();
        for (int x: vertices) {
            int in = vp.get(x).size();
            int out = vc.get(x).size();
            if (in != 0 && out != 0) {
                if (children[x] == true) {
                    pq.add(new Tuple(x, 0));
                } else {
                    pq.add(new Tuple(x, Math.min(in, out)));
                }
            }
        }
        return pq;
    }  

    public int calc_sum(Collection<Integer> vertices) {
        int s = 0;
        for (int v: vertices) {
            if (children[v] == true) {
                s += 2;
            } else {
                s += 1;
            }
        }
        return s;
    }

    private static class Tuple implements Comparable<Tuple> {
        public int vertex;
        public int key;
        public Tuple(int v, int k) {
            vertex = v;
            key = k;
        }
        @Override
        public boolean equals(Object o) {
            if (o == null) {
                return false;
            }
            Tuple other = (Tuple) o;
            return vertex == other.vertex;
        }
        @Override
        public int compareTo(Tuple other) {
            return key- other.key; // ?
        }
    }

} 