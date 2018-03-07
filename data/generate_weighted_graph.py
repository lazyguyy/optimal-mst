import random

# Takes a number of vertices and a floating point number between 0 and 1
# and generates a weighted graph with distinct weights that has that many vertices and an edge between
# any two given vertices with the given probability
# 

vertices = int(input())
edge_prob = float(input())

graph = []
edges = 0

for i in range(vertices - 1):
    for j in range(i + 1 ,vertices):
        if random.random() < edge_prob:
            graph.append((i, j))
            edges += 1


weights = list(range(1, edges + 1))
random.shuffle(weights)
for i in range(edges):
    print(graph[i][0], graph[i][1], weights[i])
