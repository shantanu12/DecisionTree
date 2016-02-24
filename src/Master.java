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
			break;
		case "iris":
			attributes = reader.readAttributes("iris_dataset_attributes.data");
			records = reader.readData("iris_dataset_data.data");
			break;
		}
		int split = records.size() / 10;
		ArrayList<Double> error = new ArrayList<Double>();
		ArrayList<Double> errorPostPruning = new ArrayList<Double>();
		for (int i = 0; i < 10; i++) {
			ArrayList<Record> buildList = new ArrayList<Record>();
			ArrayList<Record> testList = new ArrayList<Record>();
			if ((i != 0) && (i != 9)) {
				for (int j = 0; j < i * split; j++) {
					buildList.add(records.get(j));
				}
				for (int j = (i + 1) * split; j < records.size(); j++) {
					buildList.add(records.get(j));
				}
			} else if (i == 0) {
				for (int j = (i + 1) * split; j < records.size(); j++) {
					buildList.add(records.get(j));
				}
			} else {
				for (int j = 0; j < i * split; j++) {
					buildList.add(records.get(j));
				}
				if ((i + 1) * split < records.size()) {
					for (int j = (i + 1) * split; j < records.size(); j++) {
						buildList.add(records.get(j));
					}
				}
			}
			for (int j = i * split; j < (i + 1) * split; j++) {
				testList.add(records.get(j));
			}
			ID3 tree = new ID3();
			Node root = tree.buildTree(buildList, reader.targetAttributeIndex, attributes);
			if (i == 0) {
				System.out.println("-----BEFORE PRUNING-----");
				tree.printTree(root, reader.targetAttributeIndex, attributes, 0);
			}
			int correct = 0;
			for (Record rec : testList) {
				if (rec.getValue(reader.targetAttributeIndex).equals(tree.traverseTree(root, rec, attributes))) {
					correct++;
				}
			}
			tree.prune(root, reader.targetAttributeIndex, attributes);
			if (i == 0) {
				System.out.println("-----AFTER PRUNING-----");
				tree.printTree(root, reader.targetAttributeIndex, attributes, 0);
			}
			System.out.println("-----BEFORE PRUNING-----");
			double correctness = correct / (double) testList.size();
			error.add(1 - correctness);
			System.out.println("Iteration " + (i + 1) + ": Correctness: " + correctness);
			System.out.println("-----AFTER PRUNING-----");
			correct = 0;
			for (Record rec : testList) {
				if (rec.getValue(reader.targetAttributeIndex).equals(tree.traverseTree(root, rec, attributes))) {
					correct++;
				}
			}
			correctness = correct / (double) testList.size();
			errorPostPruning.add(1 - correctness);
			System.out.println("Iteration " + (i + 1) + ": Correctness: " + correctness);
		}
		System.out.println("-----BEFORE PRUNING-----");
		Utilities.calculateStats(error);
		System.out.println("-----AFTER PRUNING-----");
		Utilities.calculateStats(errorPostPruning);
	}
}
