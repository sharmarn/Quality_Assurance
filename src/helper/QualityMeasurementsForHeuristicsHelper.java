package helper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * This is solely a helper class for evaluating the total_alive.csv and
 * total_area.csv files.
 *
 */
public class QualityMeasurementsForHeuristicsHelper {

	public static void compareHeuristicsByGettingHighestValue(File file, String evaluationFile,
			int totalNumberOfColumns, int columnIndexForDefault, int columnIndexForRadius, int columnIndexForInRange,
			int columnIndexForRandom, String delimiter) throws IOException, NumberFormatException {

		BufferedReader reader = new BufferedReader(new FileReader(file));
		File resultFile = new File(evaluationFile);
		BufferedWriter writer = new BufferedWriter(new FileWriter(resultFile));
		String line = null;
		int lineCounter = 0;

		String saveResult = "time_limit;default;radius;in_range;random" + "\n";

		// Skip the header.
		reader.readLine();

		// Create counters for each heuristic.
		int countDefault = 0;
		int countRadius = 0;
		int countInRange = 0;
		int countRandom = 0;

		// Read line by line.
		while ((line = reader.readLine()) != null) {
			lineCounter++;
			// Check for completeness regarding existing columns.
			String[] split = line.split(delimiter);
			if (split.length <= totalNumberOfColumns) {
				reader.close();
				throw new IndexOutOfBoundsException(
						"In Zeile " + lineCounter + " gibt es keine " + totalNumberOfColumns + " Spalten!");
			}

			// Fetch the values for time and all the heuristics from the .csv file.
			double timeLimit = 0.0;
			double doubleValueOfDefault = 0.0;
			double doubleValueOfRadius = 0.0;
			double doubleValueOfInRange = 0.0;
			double doubleValueOfRandom = 0.0;
			try {
				timeLimit = Double.parseDouble(split[0]);
				doubleValueOfDefault = Double.parseDouble(split[columnIndexForDefault]);
				doubleValueOfRadius = Double.parseDouble(split[columnIndexForRadius]);
				doubleValueOfInRange = Double.parseDouble(split[columnIndexForInRange]);
				doubleValueOfRandom = Double.parseDouble(split[columnIndexForRandom]);

				// Convert double values into integer values.
				int valueOfDefault = (int) Math.round(doubleValueOfDefault);
				int valueOfRadius = (int) Math.round(doubleValueOfRadius);
				int valueOfInRange = (int) Math.round(doubleValueOfInRange);
				int valueOfRandom = (int) Math.round(doubleValueOfRandom);

				// Look for possibility of only one maximum - BEGIN
				if (valueOfDefault > valueOfRadius && valueOfDefault > valueOfInRange
						&& valueOfDefault > valueOfRandom) {

					saveResult = saveResult + timeLimit + ";" + countDefault++ + ";" + countRadius + ";" + countInRange
							+ ";" + countRandom + "\n";
				}

				else if (valueOfRadius > valueOfDefault && valueOfRadius > valueOfInRange
						&& valueOfRadius > valueOfRandom) {

					saveResult = saveResult + timeLimit + ";" + countDefault + ";" + countRadius++ + ";" + countInRange
							+ ";" + countRandom + "\n";
				}

				else if (valueOfInRange > valueOfDefault && valueOfInRange > valueOfRadius
						&& valueOfInRange > valueOfRandom) {

					saveResult = saveResult + timeLimit + ";" + countDefault + ";" + countRadius + ";" + countInRange++
							+ ";" + countRandom + "\n";
				}

				else if (valueOfRandom > valueOfDefault && valueOfRandom > valueOfRadius
						&& valueOfRandom > valueOfInRange) {

					saveResult = saveResult + timeLimit + ";" + countDefault + ";" + countRadius + ";" + countInRange
							+ ";" + countRandom++ + "\n";
				}

				// Look for possibility of only one maximum - END

				// Look for possibility of three max. values - BEGIN

				else if (valueOfDefault == valueOfRadius && valueOfDefault == valueOfInRange
						&& valueOfRadius == valueOfInRange && valueOfDefault > valueOfRandom
						&& valueOfRadius > valueOfRandom && valueOfInRange > valueOfRandom) {

					saveResult = saveResult + timeLimit + ";" + countDefault++ + ";" + countRadius++ + ";"
							+ countInRange++ + ";" + countRandom + "\n";
				}

				else if (valueOfDefault == valueOfRadius && valueOfDefault == valueOfRandom
						&& valueOfRadius == valueOfRandom && valueOfDefault > valueOfInRange
						&& valueOfRadius > valueOfInRange && valueOfRandom > valueOfInRange) {

					saveResult = saveResult + timeLimit + ";" + countDefault++ + ";" + countRadius++ + ";"
							+ countInRange + ";" + countRandom++ + "\n";
				}

				else if (valueOfDefault == valueOfInRange && valueOfDefault == valueOfRandom
						&& valueOfInRange == valueOfRandom && valueOfDefault > valueOfRadius
						&& valueOfInRange > valueOfRadius && valueOfRandom > valueOfRadius) {

					saveResult = saveResult + timeLimit + ";" + countDefault++ + ";" + countRadius + ";"
							+ countInRange++ + ";" + countRandom++ + "\n";
				}

				else if (valueOfRadius == valueOfInRange && valueOfRadius == valueOfRandom
						&& valueOfInRange == valueOfRandom && valueOfRadius > valueOfDefault
						&& valueOfInRange > valueOfDefault && valueOfRandom > valueOfDefault) {

					saveResult = saveResult + timeLimit + ";" + countDefault + ";" + countRadius++ + ";"
							+ countInRange++ + ";" + countRandom++ + "\n";
				}

				// Look for possibility of tree max. values - END

				// Look for possibility of two max. values - BEGIN

				else if (valueOfDefault == valueOfRadius && valueOfDefault > valueOfInRange
						&& valueOfDefault > valueOfRandom && valueOfRadius > valueOfInRange
						&& valueOfRadius > valueOfRandom) {

					saveResult = saveResult + timeLimit + ";" + countDefault++ + ";" + countRadius++ + ";"
							+ countInRange + ";" + countRandom + "\n";
				}

				else if (valueOfDefault == valueOfInRange && valueOfDefault > valueOfRadius
						&& valueOfDefault > valueOfRandom && valueOfInRange > valueOfRadius
						&& valueOfInRange > valueOfRandom) {

					saveResult = saveResult + timeLimit + ";" + countDefault++ + ";" + countRadius + ";"
							+ countInRange++ + ";" + countRandom + "\n";
				}

				else if (valueOfDefault == valueOfRandom && valueOfDefault > valueOfRadius
						&& valueOfDefault > valueOfInRange && valueOfRandom > valueOfRadius
						&& valueOfRandom > valueOfInRange) {

					saveResult = saveResult + timeLimit + ";" + countDefault++ + ";" + countRadius + ";" + countInRange
							+ ";" + countRandom++ + "\n";
				}

				else if (valueOfRadius == valueOfInRange && valueOfRadius > valueOfDefault
						&& valueOfRadius > valueOfRandom && valueOfInRange > valueOfDefault
						&& valueOfInRange > valueOfRandom) {

					saveResult = saveResult + timeLimit + ";" + countDefault + ";" + countRadius++ + ";"
							+ countInRange++ + ";" + countRandom + "\n";
				}

				else if (valueOfRadius == valueOfRandom && valueOfRadius > valueOfDefault
						&& valueOfRadius > valueOfInRange && valueOfRandom > valueOfDefault
						&& valueOfRandom > valueOfInRange) {

					saveResult = saveResult + timeLimit + ";" + countDefault + ";" + countRadius++ + ";" + countInRange
							+ ";" + countRandom++ + "\n";
				}

				else if (valueOfInRange == valueOfRandom && valueOfInRange > valueOfDefault
						&& valueOfInRange > valueOfRadius && valueOfRandom > valueOfDefault
						&& valueOfRandom > valueOfRadius) {

					saveResult = saveResult + timeLimit + ";" + countDefault + ";" + countRadius + ";" + countInRange++
							+ ";" + countRandom++ + "\n";
				}

				// Look for possibility of two max. values - END

				// Else all values are equally great.

				else if (valueOfDefault == valueOfRadius && valueOfDefault == valueOfInRange
						&& valueOfDefault == valueOfRandom && valueOfRadius == valueOfInRange
						&& valueOfRadius == valueOfRandom && valueOfInRange == valueOfRandom) {

					saveResult = saveResult + timeLimit + ";" + countDefault + ";" + countRadius + ";" + countInRange
							+ ";" + countRandom + "\n";
				}

				else {

					saveResult = saveResult + "";
				}

			} catch (NumberFormatException e) {
				reader.close();
				throw new NumberFormatException(e.getMessage());
			}
		}
		reader.close();
		writer.write(saveResult);
		writer.close();
	}
}