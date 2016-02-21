import java.util.ArrayList;

public class Master {

	public static void main(String[] args) {
		String dataset = "iris"; // later change to args[0]
		FileHandler reader = new FileHandler();
		ArrayList<Attribute> attributes = new ArrayList<Attribute>();
		ArrayList<Record> records = new ArrayList<Record>();
		switch (dataset) {
		case "voting":
			attributes = reader.readAttributes("voting_dataset_attributes.data");
			records = reader.readData("voting_dataset_data.data");
		case "iris":
			attributes = reader.readAttributes("iris_dataset_attributes.data");
			records = reader.readData("iris_dataset_data.data");
		}
		ID3 tree = new ID3();
		Node root = tree.buildTree(records, reader.targetAttributeIndex, attributes);
		tree.printTree(root, reader.targetAttributeIndex, attributes, 0);
	}
}
