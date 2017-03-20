package guideMeGraphResources;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Agile 2016 on 3/5/2017.
 */

public class GraphGuideMe {
    private ArrayList<VertexGuideMe> vertices = new ArrayList<>();
    private EdgeGuideMe[][] relations = null;

    public GraphGuideMe(String[] vertexNames) {
        initializeElements(vertexNames);
    }

    public void initializeElements(String[] vertexNames) {
        relations = new EdgeGuideMe[vertexNames.length][vertexNames.length];
        for (int name = 0; name < vertexNames.length; name++) {
            vertices.add(new VertexGuideMe(vertexNames[name]));
        }
    }

    public void addEdge(String originName, String destinationName, int distance, String direction) {
        int originIndex = -1, destinationIndex = -1;
        for (int v = 0; v < vertices.size(); v++) {
            if (vertices.get(v).isPlaceVertex(originName)) {
                originIndex = v;
            } else if (vertices.get(v).isPlaceVertex(destinationName)) {
                destinationIndex = v;
            }
            if (originIndex != -1 && destinationIndex != -1) {
                break;
            }
        }
        relations[originIndex][destinationIndex] = new EdgeGuideMe(distance, direction);

    }

    public int getVertexCount() {
        return vertices.size();
    }

    public int getOutdegree(VertexGuideMe vertex) {
        int n = 0;
        if (!vertices.contains(vertex))
            return 0;
        int index = vertices.indexOf(vertex);
        for (int i = 0; i < vertices.size(); i++)
            if (relations[index][i] != null)
                index++;
        return n;
    }

    public boolean isAdjacent(VertexGuideMe vertex1, VertexGuideMe vertex2) {
        if (!vertices.contains(vertex1)) {
            return false;
        }
        if (!vertices.contains(vertex2)) {
            return false;
        }
        if (vertex1.equals(vertex2)) {
            return false;
        }
        int index1 = vertices.indexOf(vertex1);
        int index2 = vertices.indexOf(vertex2);
        return relations[index1][index2] != null;
    }

    public EdgeGuideMe getWeight(VertexGuideMe vertex1, VertexGuideMe vertex2) {
        if (!vertices.contains(vertex1)) {
            return null;
        }
        if (!vertices.contains(vertex2)) {
            return null;
        }
        if (vertex1.equals(vertex2)) {
            return null;
        }
        int index1 = vertices.indexOf(vertex1);
        int index2 = vertices.indexOf(vertex2);
        return relations[index1][index2];
    }

    public String getShortestPath(String originPlace, String destinationPlace) {
        int originIndex = -1, destinationIndex = -1;
        ArrayList<VertexGuideMe> route = new ArrayList<>();
        for (int v = 0; v < vertices.size(); v++) {
            if (vertices.get(v).isPlaceVertex(originPlace)) {
                originIndex = v;
            } else if (vertices.get(v).isPlaceVertex(destinationPlace)) {
                destinationIndex = v;
            }
            if (originIndex != -1 && destinationIndex != -1) {
                break;
            }
        }
        if (originIndex != -1 && destinationIndex != -1) {
            route = Dijkstra(vertices.get(originIndex), vertices.get(destinationIndex));
        }

        String text = "";
        VertexGuideMe verticeO = vertices.get(originIndex);
        System.out.println(verticeO);
        VertexGuideMe verticeD = vertices.get(destinationIndex);
        VertexGuideMe lastVertex = null;
        for(int i = 0; i < route.size(); i++){
            if(i == 0){
                System.out.println(route.get(i));
                if(this.getWeight(verticeO, route.get(i)) != null){
                    text = "Diríjase " + this.getWeight(verticeO, route.get(i)).getDistance() + "metros al " + this.getWeight(verticeO, route.get(i)).getDirection() + " para llegar a ";
                    text += route.get(i).getVertexName();
                }

            }else{
                text += " luego diríjase "+ this.getWeight(lastVertex, route.get(i)).getDistance() + "metros al " + this.getWeight(lastVertex, route.get(i)).getDirection() + " para llegar a ";
                text += route.get(i).getVertexName();
            }
            lastVertex = route.get(i);
        }
        text  += " finalmente dirígase "+ this.getWeight(lastVertex, verticeD).getDistance() + "metros al " + this.getWeight(lastVertex, verticeD).getDirection() + " para llegar a ";
        text += verticeD.getVertexName();
        return text;
    }

    public ArrayList Dijkstra(VertexGuideMe origin, VertexGuideMe destiny) {

        Map<VertexGuideMe, Integer> distances = new HashMap();
        Map<VertexGuideMe, ArrayList<VertexGuideMe>> routes = new HashMap<>();
        distances.put(origin, 0);
        routes.put(origin, null);
        for (VertexGuideMe vertex : vertices) {
            if (vertex != origin) {
                distances.put(vertex, -1);
                routes.put(vertex, new ArrayList<VertexGuideMe>());
            }
        }
        ArrayList<VertexGuideMe> visited = new ArrayList<>();//S
        ArrayList<VertexGuideMe> notVisited = (ArrayList<VertexGuideMe>) vertices.clone();//Q
        while (!notVisited.isEmpty()) {
            VertexGuideMe u = this.minDistance(notVisited, distances);
            visited.add(u);

            ArrayList<VertexGuideMe> adjacent = this.getAdjacent(u);
            for (VertexGuideMe vertex : adjacent) {
                if (distances.get(vertex) == -1) {
                    distances.put(vertex, distances.get(u) + (this.getWeight(u, vertex) != null ? this.getWeight(u, vertex).getDistance() : 0));
                    routes.get(vertex).add(u);
                } else if (distances.get(u) + (this.getWeight(u, vertex) != null ? this.getWeight(u, vertex).getDistance() : 0) < distances.get(vertex)) {
                    distances.put(vertex, distances.get(u) + (this.getWeight(u, vertex) != null ? this.getWeight(u, vertex).getDistance() : 0));
                    routes.get(vertex).add(u);
                }
            }
        }
        ArrayList result = new ArrayList();
        result.add(distances.get(destiny));
        result.add(routes.get(destiny));
        return routes.get(destiny);
    }

    public ArrayList getAdjacent(VertexGuideMe vertex) {
        ArrayList<VertexGuideMe> adjacentVertices = new ArrayList<>();
        int index = vertices.indexOf(vertex);
        for (int i = 0; i < vertices.size(); i++) {
            if (vertices.get(i).isPlaceVertex(vertex.getVertexName())) {
                index = i;
                break;
            }
        }
        if (index == -1) {
            return adjacentVertices;
        }
        for (int i = 0; i < relations[index].length; i++) {
            if (i != index && relations[index][i] != null) {
                adjacentVertices.add(vertices.get(i));
            }
        }
        return adjacentVertices;
    }

    public VertexGuideMe minDistance(ArrayList<VertexGuideMe> Q, Map<VertexGuideMe, Integer> distances) {
        int min = -1;
        VertexGuideMe vertex = null;
        ArrayList<Integer> valores = new ArrayList();

        for (VertexGuideMe entry : distances.keySet()) {
            if (Q.contains(entry) && distances.get(entry) != -1) {
                if (min == -1) {
                    min = distances.get(entry);
                    vertex = entry;
                } else {
                    if (min > distances.get(entry)) {
                        min = distances.get(entry);
                        vertex = entry;
                    }
                }
            }
        }
        Q.remove(vertex);
        return vertex;
    }
}
