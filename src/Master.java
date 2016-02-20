import java.util.ArrayList;

public class Master {

	public static void main(String[] args) {
		String dataset = "voting"; // later change to args[0]
		FileHandler reader = new FileHandler();
		ArrayList<Attribute> attributes = new ArrayList<Attribute>();
		ArrayList<Record> records = new ArrayList<Record>();
		switch (dataset) {
		case "voting":
			attributes = reader.readAttributes("voting_dataset_attributes.data");
			records = reader.readData("voting_dataset_data.data");
		}
		/*
		 * Node root = new Node();
		 *
		 * for (Record rec : records) { root.getData().add(rec); }
		 */
		ID3 tree = new ID3();
		Node root = tree.buildTree(records, reader.targetAttributeIndex, attributes);
	}
}
