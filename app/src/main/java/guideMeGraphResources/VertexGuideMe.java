package guideMeGraphResources;

/**
 * Created by Agile 2016 on 3/5/2017.
 */

public class VertexGuideMe {
    private String vertexName = "";

    public VertexGuideMe(String vertexName) {
        this.vertexName = vertexName;
    }
    public boolean isPlaceVertex(String placeName){
        return placeName.equals(this.vertexName);
    }

    public String getVertexName() {
        return vertexName;
    }

    public void setVertexName(String vertexName) {
        this.vertexName = vertexName;
    }

    @Override
    public String toString() {
        return "VertexGuideMe{" +
                "vertexName='" + vertexName + '\'' +
                '}';
    }
}
