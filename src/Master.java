import java.util.ArrayList;

public class Master {

	public static void main(String[] args) {
		String dataset = args[0];
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
		case "tictactoe":
			attributes = reader.readAttributes("tictactoe_dataset_attributes.data");
			records = reader.readData("tictactoe_dataset_data.data");
			break;
		case "banknote":
			attributes = reader.readAttributes("banknote_authentication_dataset_attributes.data");
			records = reader.readData("banknote_authentication_dataset_data.data");
			break;
		case "credit":
			attributes = reader.readAttributes("credit_approval_dataset_attributes.data");
			records = reader.readData("credit_approval_dataset_data.data");
			break;
		}
		int split = records.size() / 10;
		ArrayList<Double> error = new ArrayList<Double>();
		ArrayList<Double> errorPostPruning = new ArrayList<Double>();
		ArrayList<Integer> size = new ArrayList<Integer>();
		ArrayList<Integer> sizePostPruning = new ArrayList<Integer>();
		ArrayList<Double> majError = new ArrayList<Double>();
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
			System.out.println("Iteration " + (i + 1) + ":");
			System.out.println("--------------\n");
			System.out.println("-----BEFORE PRUNING-----");
			size.add(tree.printTree(root, reader.targetAttributeIndex, attributes, 0));
			int targetDegree = attributes.get(reader.targetAttributeIndex).getDegree();
			int[] countKeeper = Utilities.targetCounter(targetDegree, buildList, reader.targetAttributeIndex,
					attributes);
			int maxIndex = -1;
			int maxCount = -1;
			for (int t = 0; t < countKeeper.length; t++) {
				if (countKeeper[t] > maxCount) {
					maxCount = countKeeper[t];
					maxIndex = t;
				}
			}
			String decision = attributes.get(reader.targetAttributeIndex).getValue(maxIndex);
			int correct = 0;
			int majCorrect = 0;
			for (Record rec : testList) {
				if (rec.getValue(reader.targetAttributeIndex).equals(tree.traverseTree(root, rec, attributes))) {
					correct++;
				}
				if (rec.getValue(reader.targetAttributeIndex).equals(decision)) {
					majCorrect++;
				}
			}
			majError.add(100 - Utilities.round((majCorrect / (double) testList.size() * 100), 2));
			tree.prune(root, reader.targetAttributeIndex, attributes);
			System.out.println("\n-----AFTER PRUNING-----");
			sizePostPruning.add(tree.printTree(root, reader.targetAttributeIndex, attributes, 0));
			System.out.print("\nBEFORE PRUNING-----");
			double correctness = Utilities.round((correct / (double) testList.size() * 100), 2);
			error.add(100 - correctness);
			System.out.println("Correctness: " + correctness + "%");
			System.out.print("AFTER PRUNING------");
			correct = 0;
			for (Record rec : testList) {
				if (rec.getValue(reader.targetAttributeIndex).equals(tree.traverseTree(root, rec, attributes))) {
					correct++;
				}
			}
			correctness = Utilities.round((correct / (double) testList.size() * 100), 2);
			errorPostPruning.add(100 - correctness);
			System.out.println("Correctness: " + correctness + "%\n");
		}
		System.out.println("----------OVERALL----------");
		double sum = 0.0;
		for (double d : majError) {
			sum += d;
		}
		double meanError = Utilities.round(sum / 10, 2);
		System.out.println("Mean Majority Classifier Error: " + meanError + "%");
		System.out.println("-----BEFORE PRUNING-----");
		Utilities.calculateStats(error, size);
		System.out.println("-----AFTER PRUNING-----");
		Utilities.calculateStats(errorPostPruning, sizePostPruning);
	}
}
