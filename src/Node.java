import java.util.ArrayList;

public class Node {
	String label;
	private Node parent;
	private Node[] children;
	private ArrayList<Record> data;

	public Node() {
		setLabel("");
		setParent(null);
		setChildren(null);
		this.data = new ArrayList<Record>();
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
}
