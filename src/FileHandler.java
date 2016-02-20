import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class FileHandler {
	public String missingDataChar;
	public String targetAttribute;
	public int targetAttributeIndex;
	public int attributeCount;
	public int recordCount;
	ArrayList<Integer> valuesPerAttribute;
	private ArrayList<Attribute> attributes;
	private ArrayList<Record> records;

	public ArrayList<Attribute> readAttributes(String fileName) {
		attributes = new ArrayList<Attribute>();
		valuesPerAttribute = new ArrayList<Integer>();
		attributeCount = 0;
		int lineNumber = 0;
		File file = new File(fileName);
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line;
			while ((line = br.readLine()) != null) {
				String[] parts = line.split(",");
				if (parts[1].equals("target-attribute")) {
					targetAttribute = parts[2];
				} else if (parts[1].equals("missing-data")) {
					missingDataChar = parts[2];
				} else {
					if (parts[0].equals("discrete")) {
						valuesPerAttribute.add(parts.length - 2);
						ArrayList<String> values = new ArrayList<String>();
						for (int j = 2; j < parts.length; j++) {
							values.add(parts[j]);
						}
						attributes.add(new Attribute(parts[1], values, false));
						attributeCount++;
						if (parts[1].equals(targetAttribute)) {
							targetAttributeIndex = lineNumber - 2;
						}
					} else {
						valuesPerAttribute.add(1);
						ArrayList<String> values = new ArrayList<String>();
						values.add("number");
						attributes.add(new Attribute(parts[1], values, false));
						attributeCount++;
					}

				}
				lineNumber++;
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return attributes;
	}

	public ArrayList<Record> readData(String fileName) {
		records = new ArrayList<Record>();
		ArrayList<ArrayList<Integer>> missingValueMap = new ArrayList<ArrayList<Integer>>();
		for (int x = 0; x < attributeCount; x++) {
			missingValueMap.add(new ArrayList<Integer>());
		}
		recordCount = 0;
		int lineNumber = 0;
		File file = new File(fileName);
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line;
			while ((line = br.readLine()) != null) {
				String[] parts = line.split(",");
				for (int i = 0; i < parts.length; i++) {
					if (parts[i].equals(missingDataChar)) {
						missingValueMap.get(i).add(lineNumber);
					}
				}
				ArrayList<String> values = new ArrayList<String>();
				for (int j = 0; j < parts.length; j++) {
					values.add(parts[j]);
				}
				records.add(new Record(lineNumber, values));
				lineNumber++;
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Handle missing values
		ArrayList<ArrayList<String>> approximationMap = new ArrayList<ArrayList<String>>();
		for (int i = 0; i < valuesPerAttribute.get(targetAttributeIndex); i++) {
			ArrayList<String> temp = new ArrayList<String>(attributeCount);
			for (int j = 0; j < attributeCount; j++) {
				temp.add("?");
			}
			approximationMap.add(temp);
		}
		for (int x = 0; x < missingValueMap.size(); x++) {
			if (x == targetAttributeIndex) { // assuming the target index will
												// always have a value
				continue;
			} else {

				ArrayList<Integer> attributeXsMissingList = missingValueMap.get(x);
				for (int a = 0; a < attributeXsMissingList.size(); a++) {
					int recordIndex = attributeXsMissingList.get(a);
					Record r = records.get(recordIndex);
					String target = r.getValue(targetAttributeIndex);
					for (int b = 0; b < valuesPerAttribute.get(targetAttributeIndex); b++) {
						if (target.equals(attributes.get(targetAttributeIndex).getValue(b))) {
							if (!(approximationMap.get(b).get(x).equals("?"))) {
								// we must have found the replacement value
								// before
								r.setValue(x, approximationMap.get(b).get(x));
							} else {
								// find the approximate value by calculating
								// frequency/average
								if (!attributes.get(x).isContinuous()) {
									// frequency approximation
									int[] countKeeper = new int[valuesPerAttribute.get(x)];
									for (int t = 0; t < countKeeper.length; t++) {
										countKeeper[t] = 0;
									}
									for (Record rec : records) {
										if (rec.getValue(targetAttributeIndex).equals(target)) {
											if (!rec.getValue(x).equals("?")) {
												for (int in = 0; in < valuesPerAttribute.get(x); in++) {
													if (rec.getValue(x).equals(attributes.get(x).getValue(in))) {
														countKeeper[in]++;
													}
												}
											}
										}
									}
									int maxIndex = -1;
									int maxCount = -1;
									for (int t = 0; t < countKeeper.length; t++) {
										if (countKeeper[t] > maxCount) {
											maxCount = countKeeper[t];
											maxIndex = t;
										}
									}
									r.setValue(x, attributes.get(x).getValue(maxIndex));
									approximationMap.get(b).set(x, attributes.get(x).getValue(maxIndex));
								} else {
									// average approximation
									double sum = 0.0;
									int count = 0;
									for (Record rec : records) {
										if (rec.getValue(targetAttributeIndex).equals(target)) {
											if (!rec.getValue(x).equals("?")) {
												sum += Double.parseDouble(rec.getValue(x));
												count++;
											}
										}
									}
									double average = sum / count;
									r.setValue(x, String.valueOf(average));
									approximationMap.get(b).set(x, String.valueOf(average));
								}
							}
						}
					}
				}
			}
		}
		Collections.shuffle(records);
		return records;
	}
}
