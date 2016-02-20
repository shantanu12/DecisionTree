import java.util.ArrayList;

public class Attribute {
	private String attrName;
	private ArrayList<String> values;
	private boolean attrUsed = false;
	boolean isContinuous;

	public Attribute(String name, ArrayList<String> values, boolean isContinuous) {
		this.attrName = name;
		this.values = new ArrayList<String>();
		for (String v : values) {
			this.values.add(v);
		}
		this.isContinuous = isContinuous;
	}

	public String getAttrName() {
		return this.attrName;
	}

	public String getValue(int i) {
		return this.values.get(i);
	}

	public int getDegree() {
		return this.values.size();
	}

	public void setAttrUsed() {
		this.attrUsed = true;
	}

	public boolean isUsed() {
		return this.attrUsed;
	}

	public boolean isContinuous() {
		return this.isContinuous;
	}
}
