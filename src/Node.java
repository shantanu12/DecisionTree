import java.util.ArrayList;

public class Node {
	String label;
	private Node parent;
	private Node[] children;
	private ArrayList<Record> data;
	private String parentDecision;

	public Node() {
		setLabel("");
		setParent(null);
		setChildren(null);
		this.data = new ArrayList<Record>();
		setParentDecision("");
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
}
