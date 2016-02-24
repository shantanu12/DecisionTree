import java.util.ArrayList;

public class Utilities {
	public static double calcEntropy(int[] count, double total) {
		double entropy = 0;
		for (int i = 0; i < count.length; i++) {
			double probability = count[i] / total;
			if (count[i] > 0) {
				entropy += -probability * (Math.log(probability) / Math.log(2));
			}
		}
		return entropy;
	}

	public static ArrayList<Record> subset(Node root, int attributeIndex, String value) {
		ArrayList<Record> subset = new ArrayList<Record>();
		for (Record rec : root.getData()) {
			if (rec.getValue(attributeIndex).equals(value)) {
				subset.add(rec);
			}
		}
		return subset;
	}

	public static int[] targetCounter(int targetDegree, ArrayList<Record> records, int targetAttributeIndex,
			ArrayList<Attribute> attributes) {
		int[] countKeeper = new int[targetDegree];
		for (int t = 0; t < countKeeper.length; t++) {
			countKeeper[t] = 0;
		}
		for (Record rec : records) {
			for (int i = 0; i < targetDegree; i++) {
				if (rec.getValue(targetAttributeIndex).equals(attributes.get(targetAttributeIndex).getValue(i))) {
					countKeeper[i]++;
				}
			}
		}
		return countKeeper;
	}

	public static double calcGain(double rootEntropy, ArrayList<Double> subsetEntopies, ArrayList<Integer> subsetSizes,
			int recordCount) {
		double gain = rootEntropy;
		for (int i = 0; i < subsetEntopies.size(); i++) {
			gain += -((subsetSizes.get(i) / (double) recordCount) * subsetEntopies.get(i));
		}
		return gain;
	}
	
	public static void calculateStats(ArrayList<Double> error){
		double sum = 0.0;
		for (Double d : error) {
			sum += d;
		}
		double meanError = sum / 10;
		System.out.println("Mean Error: " + meanError);
		for (Double d : error) {
			sum += Math.pow((d - meanError), 2);
		}
		double standardDev = Math.sqrt(sum / 10);
		System.out.println("Standard Deviation: " + standardDev);
		double standardError = standardDev/Math.sqrt(10);
		System.out.println("Standard Error: " + standardError);
	}
}
