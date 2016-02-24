import java.util.ArrayList;

public class Node {
	private String label;
	private Node parent;
	private Node[] children;
	private ArrayList<Record> data;
	private String parentDecision;
	private int linkedAttribute;
	private double continuousThreshold;
	private int error;

	public Node() {
		setLabel("");
		setParent(null);
		setChildren(null);
		this.data = new ArrayList<Record>();
		setParentDecision("");
		this.linkedAttribute = -1;
		this.continuousThreshold = 0.0;
		this.error = 0;
	}

	public void setLabel(String s) {
		this.label = s;
	}

	public String getLabel() {
		return this.label;
	}

	public void setParent(Node parent) {
		this.parent = parent;
	}

	public Node getParent() {
		return parent;
	}

	public void setData(ArrayList<Record> data) {
		this.data = data;
	}

	public ArrayList<Record> getData() {
		return data;
	}

	public void setChildren(Node[] children) {
		this.children = children;
	}

	public Node[] getChildren() {
		return children;
	}

	public void setParentDecision(String s) {
		this.parentDecision = s;
	}

	public String getParentDecision() {
		return this.parentDecision;
	}

	public void setLinkedAttribute(int a) {
		this.linkedAttribute = a;
	}

	public int getLinkedAttribute() {
		return this.linkedAttribute;
	}

	public void setContinuousThreshold(double d) {
		this.continuousThreshold = d;
	}

	public double getContinuousThreshold() {
		return this.continuousThreshold;
	}
	
	public void setError(int e){
		this.error = e;
	}
	
	public int getError(){
		return this.error;
	}
}
