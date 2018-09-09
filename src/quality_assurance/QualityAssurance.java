package quality_assurance;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class QualityAssurance {

	public enum Option {
		TOTAL_ALIVE_SINGLE, TOTAL_AREA_SINGLE, BOTH_SINGLE, TOTAL_ALIVE_ALL
	}

	/**
	 * This program allows the user to fetch the specified column of a file.
	 * 
	 * @param file                Data, user wants to fetch the specified column
	 *                            from.
	 * @param columnIndex         Needed for the specific column, user wants to
	 *                            fetch.
	 * @param delimiter           Here: One space.
	 * @param numberOfLinesToSkip Used for the method 'getTotalArea'.
	 * @return Double values from the specified column in a list.
	 * @throws IOException
	 */
	private static List<Double> loadValuesFromColumn(File file, int columnIndex, String delimiter,
			int numbersOfLinesToSkip) throws IOException, NumberFormatException {

		List<Double> valuesList = new ArrayList<>();
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String line = null;
		int lineCounter = 0;

		// Skip first two lines.
		for (int i = 0; i < 2; i++) {
			reader.readLine();
		}

		// This step is used for the computation of the total area.
		for (int i = 0; i < numbersOfLinesToSkip; i++) {
			reader.readLine();
		}

		// Read line by line.
		while ((line = reader.readLine()) != null) {
			lineCounter++;
			// Check for completeness regarding existing columns.
			String[] split = line.split(delimiter);
			if (split.length <= columnIndex) {
				reader.close();
				throw new IndexOutOfBoundsException(
						"In Zeile " + lineCounter + " gibt es keine " + columnIndex + " Spalten!");
			}
			double doubleValue = 0.0;
			try {
				doubleValue = Double.parseDouble(split[columnIndex]);
				valuesList.add(doubleValue);
			} catch (NumberFormatException e) {
				reader.close();
				throw new NumberFormatException(e.getMessage());
			}
		}
		reader.close();

		return valuesList;
	}

	public static int getTotalAlive(File file, int columnIndexForEliminationTime, String delimiter, double timeLimit,
			List<Double> eliminationTimeList) throws NumberFormatException, IOException {

		eliminationTimeList = loadValuesFromColumn(file, columnIndexForEliminationTime, delimiter, 0);

		int totalAlive = 0;
		for (double time : eliminationTimeList) {
			if (time >= timeLimit) {
				totalAlive++;
			}
		}
		return totalAlive;
	}

	public static double getTotalArea(File file, int columnIndexForEliminationTime, int columnIndexForRadius,
			String delimiter, double timeLimit, List<Double> eliminationTimeList, List<Double> radiiList,
			int sumOfCenters) throws NumberFormatException, IOException {

		int totalAlive = getTotalAlive(file, columnIndexForEliminationTime, " ", timeLimit, eliminationTimeList);

		int numbersOfLinesToSkip = sumOfCenters - totalAlive;

		radiiList = loadValuesFromColumn(file, columnIndexForRadius, delimiter, numbersOfLinesToSkip);

		double area = 0.0;
		double PI = Math.PI;

		for (double radius : radiiList) {
			area = area + 2 * PI * Math.pow(radius, 2);
		}
		return area;
	}

	public static void main(String[] args) {

		File inputFile = new File("result_default.ce");
		File resultFile = new File("result_for_default.csv");

		List<Double> valuesList = null;

		final int columnIndexForEliminationTime = 4;
		final int columnIndexForRadius = 5;
		int totalAlive = 0;
		double totalArea = 0.0;

		final int sumOfCenters = 184;

		// Choose the property, you want to calculate.
		Option option = Option.BOTH_SINGLE;

		// Set the total number of time limits.
		Scanner input = new Scanner(System.in);
		System.out.println("Please enter the maximum number of time limits.");
		int j = input.nextInt();
		double[] timeLimits = new double[j];

		int count = 1;

		// Set the time limits.
		for (int i = 0; i < j; i++) {

			System.out.println("Please enter the " + count + "." + " time limit.");
			timeLimits[i] = input.nextDouble();
			if (i > 0 && timeLimits[i] <= timeLimits[i - 1]) {
				System.out.println("Time limit already exists.");
				i--;
			} else {
				count++;
			}
		}

		input.close();

		try {
			String saveResult = " ";
			BufferedWriter writer = new BufferedWriter(new FileWriter(resultFile));

			if (option == Option.TOTAL_ALIVE_SINGLE) {
				saveResult = "Total_Alive" + "\n" + "Time_Limit;Total_Alive";

				for (double timeLimit : timeLimits) {

					try {
						totalAlive = getTotalAlive(inputFile, columnIndexForEliminationTime, " ", timeLimit,
								valuesList);

						saveResult = saveResult + "\n" + timeLimit + ";" + totalAlive;

					} catch (Exception e) {
						System.err.println("Da ist was schiefgegangen: " + e.getMessage());
					}
				}
			}

			else if (option == Option.TOTAL_ALIVE_ALL) {
				saveResult = "Total_Alive" + "\n" + "Time_Limit;Total_Alive";

				for (double timeLimit : timeLimits) {

					try {
						totalAlive = getTotalAlive(inputFile, columnIndexForEliminationTime, " ", timeLimit,
								valuesList);

						saveResult = saveResult + "\n" + timeLimit + ";" + totalAlive;

					} catch (Exception e) {
						System.err.println("Da ist was schiefgegangen: " + e.getMessage());
					}
				}
			}

			else if (option == Option.TOTAL_AREA_SINGLE) {
				saveResult = "Total_Area" + "\n" + "Time_Limit;Total_Area";

				for (double timeLimit : timeLimits) {

					try {
						totalArea = getTotalArea(inputFile, columnIndexForEliminationTime, columnIndexForRadius, " ",
								timeLimit, valuesList, valuesList, sumOfCenters);

						saveResult = saveResult + "\n" + timeLimit + ";" + totalArea;

					} catch (Exception e) {
						System.err.println("Da ist was schiefgegangen: " + e.getMessage());
					}
				}
			}

			else {
				saveResult = "Results" + "\n" + "Time_Limit;Total_Alive;Total_Area";

				for (double timeLimit : timeLimits) {

					try {
						totalAlive = getTotalAlive(inputFile, columnIndexForEliminationTime, " ", timeLimit,
								valuesList);
					} catch (Exception e) {
						System.err.println("Da ist was schiefgegangen: " + e.getMessage());
					}

					try {
						totalArea = getTotalArea(inputFile, columnIndexForEliminationTime, columnIndexForRadius, " ",
								timeLimit, valuesList, valuesList, sumOfCenters);
					} catch (Exception e) {
						System.err.println("Da ist was schiefgegangen: " + e.getMessage());
					}
					saveResult = saveResult + "\n" + timeLimit + ";" + totalAlive + ";" + totalArea;

				}
			}

			writer.write(saveResult);
			writer.close();
		} catch (IOException e) {
			System.out.println("File I/O error!");
		}
	}
}