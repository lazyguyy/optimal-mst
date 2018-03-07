# optimal-mst

In 2002, Seth Pettie and Vijaya Ramachandran described an MST algorithm based
on the complexity of decision trees. We did not manage to find an actual
implementation of the algorithm, so this might be the first one.

## Implementation notes

In the current implementation, the depth of a decision tree is bounded.
In practice, the algorithm should never employ trees with a depth greater than 16.
For further information refer to our <a href="https://lazyguyy.github.io/optimal-mst/">documentation page</a>
