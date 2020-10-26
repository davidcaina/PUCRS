package models;

public class VAO {
	
	private int vaoID;
	private int vertices;
	
	public VAO(int vaoID, int vertexCount){
		this.vaoID = vaoID;
		this.vertices = vertexCount;
	}

	public int getVaoID() {
		return vaoID;
	}

	public int getVertices() {
		return vertices;
	}
	
	

}
