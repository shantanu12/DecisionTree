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
				root.setLinkedAttribute(targetAttributeIndex);
				root.setError(0);
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
			root.setLinkedAttribute(targetAttributeIndex);
			root.setError(root.getData().size() - maxCount);
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
							double midValue = (values.get(a - 1) + values.get(a)) / 2;
							ArrayList<Record> subset1 = new ArrayList<Record>();
							ArrayList<Record> subset2 = new ArrayList<Record>();
							double subset1Entropy = 0.0;
							double subset2Entropy = 0.0;
							ArrayList<Double> localEntropies = new ArrayList<Double>();
							ArrayList<Integer> localSetSizes = new ArrayList<Integer>();
							for (Record rec : root.getData()) {
								if (midValue >= Double.parseDouble(rec.getValue(i))) {
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
			root.setLinkedAttribute(bestAttribute);
			int frequencyCount[] = Utilities.targetCounter(targetDegree, root.getData(), targetAttributeIndex,
					attributes);
			int maximumCount = -1;
			for (int t = 0; t < frequencyCount.length; t++) {
				if (frequencyCount[t] > maximumCount) {
					maximumCount = frequencyCount[t];
				}
			}
			root.setError(root.getData().size() - maximumCount);
			int childCount;
			if (bestAttr.isContinuous()) {
				childCount = 2;
			} else {
				childCount = bestAttr.getDegree();
				attributes.get(bestAttribute).setAttrUsed();
			}
			root.setChildren(new Node[childCount]);
			if (!bestAttr.isContinuous()) {
				for (int j = 0; j < childCount; j++) {
					ArrayList<Record> childRecords = Utilities.subset(root, bestAttribute, bestAttr.getValue(j));
					if ((childRecords.size() == 0) || (childRecords.size() == root.getData().size())) {
						int targetCount[] = Utilities.targetCounter(targetDegree, root.getData(), targetAttributeIndex,
								attributes);
						int maxIndex = -1;
						int maxCount = -1;
						for (int t = 0; t < targetCount.length; t++) {
							if (targetCount[t] > maxCount) {
								maxCount = targetCount[t];
								maxIndex = t;
							}
						}
						root.setLabel(attributes.get(targetAttributeIndex).getValue(maxIndex));
						root.setLinkedAttribute(targetAttributeIndex);
						root.setError(root.getData().size() - maxCount);
						root.setChildren(null);
						return root;
					} else {
						ArrayList<Attribute> childAttributes = new ArrayList<Attribute>();
						for (Attribute attr : attributes) {
							childAttributes.add(new Attribute(attr));
						}
						root.getChildren()[j] = buildTree(childRecords, targetAttributeIndex, childAttributes);
						root.getChildren()[j].setParent(root);
						root.getChildren()[j].setParentDecision(bestAttr.getValue(j));
					}
				}
			} else {
				ArrayList<Record> subset1 = new ArrayList<Record>();
				ArrayList<Record> subset2 = new ArrayList<Record>();
				for (Record rec : root.getData()) {
					if (bestContinuousValue >= Double.parseDouble(rec.getValue(bestAttribute))) {
						subset1.add(rec);
					} else {
						subset2.add(rec);
					}
				}
				ArrayList<Attribute> childAttributes = new ArrayList<Attribute>();
				for (Attribute attr : attributes) {
					childAttributes.add(new Attribute(attr));
				}
				root.getChildren()[0] = buildTree(subset1, targetAttributeIndex, childAttributes);
				root.getChildren()[0].setParent(root);
				root.getChildren()[0].setParentDecision("< " + bestContinuousValue);
				root.getChildren()[0].setContinuousThreshold(bestContinuousValue);
				root.getChildren()[1] = buildTree(subset2, targetAttributeIndex, childAttributes);
				root.getChildren()[1].setParent(root);
				root.getChildren()[1].setParentDecision("> " + bestContinuousValue);
				root.getChildren()[1].setContinuousThreshold(bestContinuousValue);
			}
		} else {
			return root;
		}
		return root;
	}

	public int printTree(Node root, int targetAttributeIndex, ArrayList<Attribute> attributes, int spaceFactor) {
		int size = 0;
		if (root.getChildren() != null) {
			for (int i = 0; i < root.getChildren().length; i++) {
				int targetDegree = attributes.get(targetAttributeIndex).getDegree();
				int[] countKeeper = Utilities.targetCounter(targetDegree, root.getChildren()[i].getData(),
						targetAttributeIndex, attributes);
				String dataDistribution = " [";
				for (int j = 0; j < countKeeper.length; j++) {
					dataDistribution += countKeeper[j] + " " + attributes.get(targetAttributeIndex).getValue(j) + ", ";
				}
				dataDistribution = dataDistribution.substring(0, dataDistribution.length() - 2);
				dataDistribution += "]";
				for (int k = 0; k < spaceFactor; k++) {
					System.out.print("\t");
				}
				System.out.print(root.getChildren()[i].getParent().getLabel() + " : "
						+ root.getChildren()[i].getParentDecision() + dataDistribution);
				System.out.print("\n");
				spaceFactor++;
				size += printTree(root.getChildren()[i], targetAttributeIndex, attributes, spaceFactor);
				spaceFactor--;
			}
		} else {
			int targetDegree = attributes.get(targetAttributeIndex).getDegree();
			int[] countKeeper = Utilities.targetCounter(targetDegree, root.getData(), targetAttributeIndex, attributes);
			String dataDistribution = " [";
			for (int j = 0; j < countKeeper.length; j++) {
				dataDistribution += countKeeper[j] + " " + attributes.get(targetAttributeIndex).getValue(j) + ", ";
			}
			dataDistribution = dataDistribution.substring(0, dataDistribution.length() - 2);
			dataDistribution += "]";
			for (int k = 0; k < spaceFactor; k++) {
				System.out.print("\t");
			}
			System.out.print(
					attributes.get(targetAttributeIndex).getAttrName() + " : " + root.getLabel() + dataDistribution);
			System.out.print("\n");
			return 1;
		}
		return size+1;
	}

	public String traverseTree(Node root, Record record, ArrayList<Attribute> attributes) {
		String decision = "";
		int attributeUnderTest = root.getLinkedAttribute();
		if (root.getChildren() != null) {
			if (!attributes.get(attributeUnderTest).isContinuous()) {
				for (int i = 0; i < root.getChildren().length; i++) {
					if (record.getValue(attributeUnderTest).equals(root.getChildren()[i].getParentDecision())) {
						decision = traverseTree(root.getChildren()[i], record, attributes);
					}
				}
			} else {
				if (Double.parseDouble(record.getValue(attributeUnderTest)) <= root.getChildren()[0]
						.getContinuousThreshold()) {
					decision = traverseTree(root.getChildren()[0], record, attributes);
				} else {
					decision = traverseTree(root.getChildren()[1], record, attributes);
				}
			}
		} else {
			decision = root.getLabel();
		}
		return decision;
	}

	public void prune(Node root, int targetAttributeIndex, ArrayList<Attribute> attributes) {
		if (root.getChildren() == null) {
			return;
		} else {
			for (int i = 0; i < root.getChildren().length; i++) {
				prune(root.getChildren()[i], targetAttributeIndex, attributes);
			}
			int childErrorSum = 0;
			for (int i = 0; i < root.getChildren().length; i++) {
				childErrorSum += root.getChildren()[i].getError();
			}
			double nt = root.getError() + 0.5;
			double nTt = childErrorSum + root.getChildren().length / 2;
			double standardError = Math.sqrt(nTt * (root.getData().size() - nTt) / root.getData().size());
			if (nt > (nTt + standardError)) {
				// do not prune the node - set error of this node as sum of
				// error of children nodes
				root.setError(childErrorSum);
			} else {
				// prune the node - remove children, change label to a decision
				// value and set linked attribute as target attribute
				root.setChildren(null);
				int targetDegree = attributes.get(targetAttributeIndex).getDegree();
				int[] countKeeper = Utilities.targetCounter(targetDegree, root.getData(), targetAttributeIndex,
						attributes);
				int maxIndex = -1;
				int maxCount = -1;
				for (int t = 0; t < countKeeper.length; t++) {
					if (countKeeper[t] > maxCount) {
						maxCount = countKeeper[t];
						maxIndex = t;
					}
				}
				root.setLabel(attributes.get(targetAttributeIndex).getValue(maxIndex));
				root.setLinkedAttribute(targetAttributeIndex);
			}
		}
	}
}
