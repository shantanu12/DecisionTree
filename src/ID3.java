import java.util.ArrayList;
import java.util.Collections;

public class ID3 {
	public Node buildTree(ArrayList<Record> records, int targetAttributeIndex, ArrayList<Attribute> attributes) {
		// 1. create root node
		Node root = new Node();
		for (Record rec : records) {
			root.getData().add(rec);
		}

		// 2. Check for stopping criteria - all +ve, all -ve, all attributes
		// utilized
		int targetDegree = attributes.get(targetAttributeIndex).getDegree();
		int[] countKeeper = Utilities.targetCounter(targetDegree, root.getData(), targetAttributeIndex, attributes);
		for (int t = 0; t < countKeeper.length; t++) {
			if (countKeeper[t] == root.getData().size()) {
				root.setLabel(attributes.get(targetAttributeIndex).getValue(t));
				return root;
			}
		}
		int remainingAttributes = 0;
		for (int i = 0; i < attributes.size(); i++) {
			if (!attributes.get(i).isUsed()) {
				remainingAttributes++;
			}
		}
		if (remainingAttributes == 1) {
			// just the target attribute is left. Return the node with label of
			// best target
			int maxIndex = -1;
			int maxCount = -1;
			for (int t = 0; t < countKeeper.length; t++) {
				if (countKeeper[t] > maxCount) {
					maxCount = countKeeper[t];
					maxIndex = t;
				}
			}
			root.setLabel(attributes.get(targetAttributeIndex).getValue(maxIndex));
			return root;
		}

		// 3. build tree further
		// 3.1 calculate entropy of current system
		double rootEntropy = Utilities.calcEntropy(countKeeper, root.getData().size());

		// 3.2 calculate info gain with all unused attributes
		double bestInfoGain = 0.0;
		int bestAttribute = -1;
		double bestContinuousValue = 0.0;
		for (int i = 0; i < attributes.size(); i++) {
			if (i != targetAttributeIndex) {
				if (!attributes.get(i).isUsed()) {
					if (!attributes.get(i).isContinuous()) {
						// handling info gain of discrete attributes
						double subsetEntropy = 0.0;
						ArrayList<Double> entropies = new ArrayList<Double>();
						ArrayList<Integer> setSizes = new ArrayList<Integer>();
						for (int j = 0; j < attributes.get(i).getDegree(); j++) {
							ArrayList<Record> subset = Utilities.subset(root, i, attributes.get(i).getValue(j));
							setSizes.add(subset.size());
							if (subset.size() != 0) {
								int[] countTarget = Utilities.targetCounter(targetDegree, subset, targetAttributeIndex,
										attributes);
								subsetEntropy = Utilities.calcEntropy(countTarget, subset.size());
								entropies.add(subsetEntropy);
							}
						}
						double infoGain = Utilities.calcGain(rootEntropy, entropies, setSizes, root.getData().size());
						if (infoGain > bestInfoGain) {
							bestAttribute = i;
							bestInfoGain = infoGain;
						}
					} else {
						// handling info gain of continuous attributes
						double bestLocalGain = 0.0;
						double bestLocalContinuousValue = -1;
						ArrayList<Double> values = new ArrayList<Double>();
						for (Record rec : root.getData()) {
							values.add(Double.parseDouble(rec.getValue(i)));
						}
						Collections.sort(values);
						for (int a = 1; a < values.size(); a++) {
							double midValue = (values.get(i - 1) + values.get(i)) / 2;
							ArrayList<Record> subset1 = new ArrayList<Record>();
							ArrayList<Record> subset2 = new ArrayList<Record>();
							double subset1Entropy = 0.0;
							double subset2Entropy = 0.0;
							ArrayList<Double> localEntropies = new ArrayList<Double>();
							ArrayList<Integer> localSetSizes = new ArrayList<Integer>();
							for (Record rec : root.getData()) {
								if (midValue > Double.parseDouble(rec.getValue(i))) {
									subset1.add(rec);
								} else {
									subset2.add(rec);
								}
							}
							localSetSizes.add(subset1.size());
							localSetSizes.add(subset2.size());
							if (subset1.size() != 0) {
								int[] countTarget = Utilities.targetCounter(targetDegree, subset1, targetAttributeIndex,
										attributes);
								subset1Entropy = Utilities.calcEntropy(countTarget, subset1.size());
								localEntropies.add(subset1Entropy);
							}
							if (subset2.size() != 0) {
								int[] countTarget = Utilities.targetCounter(targetDegree, subset2, targetAttributeIndex,
										attributes);
								subset2Entropy = Utilities.calcEntropy(countTarget, subset2.size());
								localEntropies.add(subset2Entropy);
							}
							double localInfoGain = Utilities.calcGain(rootEntropy, localEntropies, localSetSizes,
									root.getData().size());
							if (localInfoGain > bestLocalGain) {
								bestLocalContinuousValue = midValue;
								bestLocalGain = localInfoGain;
							}
						}
						if (bestLocalGain > bestInfoGain) {
							bestAttribute = i;
							bestInfoGain = bestLocalGain;
							bestContinuousValue = bestLocalContinuousValue;
						}
					}
				}
			}
		}

		// we have found the best information gain, best attribute for split and
		// if it is continuous then its best split value
		if (bestAttribute != -1) {
			Attribute bestAttr = attributes.get(bestAttribute);
			root.setLabel(bestAttr.getAttrName());
			int childCount;
			if (bestAttr.isContinuous()) {
				childCount = 2;
			} else {
				childCount = bestAttr.getDegree();
				attributes.get(bestAttribute).setAttrUsed();
			}
			root.setChildren(new Node[childCount]);
		}
		// create child nodes for the best attribute
		return root;
	}
}
