package util.graph;

public class Graphs {

    public static EdgeList[] adjacencyList(int vertices, Iterable<WeightedEdge> edges) {
        EdgeList[] adjacency = new EdgeList[vertices];
        for (int i = 0; i < vertices; i++)
            adjacency[i] = new EdgeList();

        for (WeightedEdge e : edges) {
            adjacency[e.from].append(e);
            adjacency[e.to].append(e.reversed());
        }
        return adjacency;
    }

    public static EdgeList removeDuplicates(int vertices, Iterable<WeightedEdge> edges) {

        // first pass: bucket by e.to
        EdgeList[] firstPass = new EdgeList[vertices];
        for (int i = 0; i < vertices; i++)
            firstPass[i] = new EdgeList();

        // second pass: bucket by e.from
        EdgeList[] secondPass = new EdgeList[vertices];
        for (int i = 0; i < vertices; i++)
            secondPass[i] = new EdgeList();

        for (WeightedEdge e : edges) {
            if (e.from > e.to)
                e = e.reversed();
            firstPass[e.to].append(e);
        }

        for (int v = 0; v < vertices; v++)
            for (WeightedEdge e : firstPass[vertices - v - 1])
                secondPass[e.from].append(e);

        EdgeList result = new EdgeList();
        for (int v = 0; v < vertices; v++) {
            WeightedEdge lightest = null;
            // find lightest edge from v to all connected vertices
            for (WeightedEdge e : secondPass[v]) {
                // skip self-loops
                if (e.to == v)
                    continue;
                // first iteration: initialize candidate for lightest edge
                if (lightest == null)
                    lightest = e;
                // different target: save current best
                if (lightest.to != e.to) {
                    result.append(lightest);
                    lightest = e;
                }
                // update lightest if we find a better candidate
                if (lightest.weight > e.weight)
                    lightest = e;
            }
            // append last result
            if (lightest != null)
                result.append(lightest);
        }
        return result;
    }
}
