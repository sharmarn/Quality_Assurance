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
		TOTAL_ALIVE, TOTAL_AREA, BOTH
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

	private static List<Double> loadTimeLimitsFromColumn(File file, int columnIndex, String delimiter, int sumOfCentres)
			throws IOException, NumberFormatException {

		List<Double> valuesList = new ArrayList<>();
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String line = null;
		int lineCounter = 1;

		// Skip first two lines.
		for (int i = 0; i < 2; i++) {
			reader.readLine();
		}

		// Read line by line.
		while ((line = reader.readLine()) != null) {
			lineCounter++;
			if (lineCounter == 2 || lineCounter == sumOfCentres) {
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

		String[] fileNames = { "result_default.ce", "result_radius.ce", "result_in_range.ce", "result_osm_id.ce",
				"result_random.ce" };
		File resultFile = new File("result_for_total_alive.csv");

		// TODO:
		final int sumOfCenters = 184;

		// Choose the property, you want to calculate.
		Option option = Option.TOTAL_ALIVE;

		// Construct understandable names for the used heuristics.
		ArrayList<String> heuristics = new ArrayList<String>();

		for (String fileName : fileNames) {
			fileName = fileName.replace("result_", "").replace(".ce", "").toUpperCase();
			heuristics.add(fileName);
		}

		// Construct the header of the used heuristics for the .csv file.
		String stringOfHeuristics = "";

		for (int i = 0; i < heuristics.size(); i++) {
			if (i < heuristics.size() - 1) {
				stringOfHeuristics += heuristics.get(i) + ";";
			} else {
				stringOfHeuristics += heuristics.get(i);
			}
		}

		List<Double> valuesList = null;

		final int columnIndexForEliminationTime = 4;
		final int columnIndexForRadius = 5;
		int totalAlive = 0;
		double totalArea = 0.0;

		// Set the total number of time limits.
		Scanner input = new Scanner(System.in);
		System.out.println("Please enter the maximum number of time limits.");
		int numberOfTimeLimits = input.nextInt();
		double[] arrayOfTimeLimits = new double[numberOfTimeLimits + 1];

		File inputFileForTimeLimits = new File("result_default.ce");

		// Get time limits, dynamically.
		try {
			List<Double> timeLimitsList = loadTimeLimitsFromColumn(inputFileForTimeLimits,
					columnIndexForEliminationTime, " ", sumOfCenters);
			double intervall = (timeLimitsList.get(1) - timeLimitsList.get(0)) / numberOfTimeLimits;
			for (int i = 1; i <= numberOfTimeLimits; i++) {
				double timeLimit = intervall * i;
				arrayOfTimeLimits[i] = timeLimit;
			}
		} catch (NumberFormatException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		input.close();

		// From here on, the options are specified.
		if (option == Option.TOTAL_ALIVE) {

			try {
				String saveResult = "";
				BufferedWriter writer = new BufferedWriter(new FileWriter(resultFile));
				saveResult = "TOTAL_ALIVE" + "\n" + "TIME_LIMIT;" + stringOfHeuristics;

				for (double timeLimit : arrayOfTimeLimits) {
					saveResult = saveResult + "\n" + timeLimit;
					for (String fileName : fileNames) {
						File inputFile = new File(fileName);

						try {
							totalAlive = getTotalAlive(inputFile, columnIndexForEliminationTime, " ", timeLimit,
									valuesList);

							saveResult = saveResult + ";" + totalAlive;

						} catch (Exception e) {
							System.err.println("Da ist was schiefgegangen: " + e.getMessage());
						}
					}
				}
				writer.write(saveResult);
				writer.close();
				System.out.println("Calculation of total alive has been finished.");
			} catch (IOException e) {
				System.out.println("File I/O error!");
			}
		}

		else if (option == Option.TOTAL_AREA) {

			try {
				String saveResult = "";
				BufferedWriter writer = new BufferedWriter(new FileWriter(resultFile));
				saveResult = "TOTAL_AREA" + "\n" + "TIME_LIMIT;" + stringOfHeuristics;

				for (double timeLimit : arrayOfTimeLimits) {
					saveResult = saveResult + "\n" + timeLimit;
					for (String fileName : fileNames) {
						File inputFile = new File(fileName);

						try {
							totalArea = getTotalArea(inputFile, columnIndexForEliminationTime, columnIndexForRadius,
									" ", timeLimit, valuesList, valuesList, sumOfCenters);

							saveResult = saveResult + ";" + totalArea;

						} catch (Exception e) {
							System.err.println("Da ist was schiefgegangen: " + e.getMessage());
						}
					}
				}
				writer.write(saveResult);
				writer.close();
				System.out.println("Calculation of total area has been finished.");
			} catch (IOException e) {
				System.out.println("File I/O error!");
			}
		}

		else {
			if (fileNames.length == 1) {
				for (String fileName : fileNames) {
					File inputFile = new File(fileName);

					try {
						String saveResult = "";
						BufferedWriter writer = new BufferedWriter(new FileWriter(resultFile));
						saveResult = "TOTAL_ALIVE AND TOTAL_AREA" + "\n" + "TIME_LIMIT;TOTAL_ALIVE;TOTAL_AREA";

						for (double timeLimit : arrayOfTimeLimits) {

							try {
								totalAlive = getTotalAlive(inputFile, columnIndexForEliminationTime, " ", timeLimit,
										valuesList);
							} catch (Exception e) {
								System.err.println("Da ist was schiefgegangen: " + e.getMessage());
							}

							try {
								totalArea = getTotalArea(inputFile, columnIndexForEliminationTime, columnIndexForRadius,
										" ", timeLimit, valuesList, valuesList, sumOfCenters);
							} catch (Exception e) {
								System.err.println("Da ist was schiefgegangen: " + e.getMessage());
							}
							saveResult = saveResult + "\n" + timeLimit + ";" + totalAlive + ";" + totalArea;
						}
						writer.write(saveResult);
						writer.close();
						System.out.println("Calculation of total alive and total area has been finished.");
					} catch (IOException e) {
						System.out.println("File I/O error!");
					}
				}
			} else
				System.out.println("Error: More than one file !");
		}
	}
}