# optimal-mst

In 2002, Seth Pettie and Vijaya Ramachandran described a <a href="https://web.eecs.umich.edu/~pettie/papers/jacm-optmsf.pdf">MST algorithm</a> based
on the complexity of decision trees. We did not manage to find an actual
implementation of the algorithm, so this might be the first one.

## A thing to note

While this algorithm has an optimal asymptotic runtime, it is certainly not optimal for any practical application.  
To be honest, this whole project has only a rather academic purpose; the only parts of interest may be our implementations of the other mst algorithms (for testing purposes we implemented the mst algorithms devised by prim, kruskal, boruvka and fredman / tarjan) as well as the implementation of fibonacci heaps and soft heaps.

## Further notes regarding the implementation of the optimal mst algorithm

In the current implementation, the depth of a decision tree is bounded.
In practice, the algorithm should never employ trees with a depth greater than 16.  
For further information refer to our <a href="https://lazyguyy.github.io/optimal-mst/">documentation page</a>
