package quality_assurance;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

	public enum Option {
		TOTAL_ALIVE, TOTAL_AREA, BOTH
	}

	public static void main(String[] args) {

		// ################## PROPERTY SETTINGS - BEGIN ##################

		String[] fileNames = { "default.ce", "radius.ce", "in_range.ce", "osm_id.ce", "random.ce" };

		// Set the file, you want to get the time intervals from.
		File inputFileForTimeLimitIntervals = new File("default.ce");

		// Choose the property, you want to calculate.
		Option option = Option.TOTAL_ALIVE;

		// ################## PROPERTY SETTINGS - END ####################

		// Construct understandable names for the used heuristics.
		ArrayList<String> heuristics = new ArrayList<String>();

		for (String fileName : fileNames) {
			fileName = fileName.replace(".ce", "").toLowerCase();
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

		// Set the total number of time intervals.
		Scanner input = new Scanner(System.in);
		System.out.println("Please enter the maximum number of time intervals.");
		int numberOfTimeIntervals = input.nextInt();

		// Initialize an array for keeping the intervals of the time limits.
		double[] arrayOfTimeIntervals = new double[numberOfTimeIntervals + 1];

		// Get the total number of centers.
		int sumOfCenters = 0;

		try {
			sumOfCenters = HeuristicEvaluation.getSumOfCenters(inputFileForTimeLimitIntervals);
			System.out.println(sumOfCenters);
		} catch (NumberFormatException e2) {
			e2.printStackTrace();
		} catch (IOException e2) {
			e2.printStackTrace();
		}

		// Get time limits in intervals.
		try {
			List<Double> timeIntervalsList = HeuristicEvaluation.loadFirstAndSecondLastEliminationTimesFromColumn(
					inputFileForTimeLimitIntervals, columnIndexForEliminationTime, " ", sumOfCenters);
			double interval = (timeIntervalsList.get(1) - timeIntervalsList.get(0)) / numberOfTimeIntervals;
			for (int i = 0; i < numberOfTimeIntervals + 1; i++) {
				double timeInterval = interval * i;
				arrayOfTimeIntervals[i] = timeInterval;
			}
		} catch (NumberFormatException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		input.close();

		// ################## OPTIONS - BEGIN ##################

		if (option == Option.TOTAL_ALIVE) {
			File resultFile = new File("total_alive.csv");

			try {
				String saveResult = "";
				BufferedWriter writer = new BufferedWriter(new FileWriter(resultFile));
				saveResult = "time_limit;" + stringOfHeuristics;

				for (double timeInterval : arrayOfTimeIntervals) {
					saveResult = saveResult + "\n" + timeInterval;
					for (String fileName : fileNames) {
						File inputFile = new File(fileName);

						try {
							totalAlive = HeuristicEvaluation.getTotalAlive(inputFile, columnIndexForEliminationTime, " ",
									timeInterval, valuesList);

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
			File resultFile = new File("total_area.csv");

			try {
				String saveResult = "";
				BufferedWriter writer = new BufferedWriter(new FileWriter(resultFile));
				saveResult = "time_limit;" + stringOfHeuristics;

				for (double timeInterval : arrayOfTimeIntervals) {
					saveResult = saveResult + "\n" + timeInterval;
					for (String fileName : fileNames) {
						File inputFile = new File(fileName);

						try {
							totalArea = HeuristicEvaluation.getTotalArea(inputFile, columnIndexForEliminationTime,
									columnIndexForRadius, " ", timeInterval, valuesList, valuesList, sumOfCenters);

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
			File resultFile = new File("total_alive_and_area.csv");
			if (fileNames.length == 1) {
				for (String fileName : fileNames) {
					File inputFile = new File(fileName);

					try {
						String saveResult = "";
						BufferedWriter writer = new BufferedWriter(new FileWriter(resultFile));
						saveResult = "time_limit;total_alive;total_area";

						for (double timeInterval : arrayOfTimeIntervals) {

							try {
								totalAlive = HeuristicEvaluation.getTotalAlive(inputFile, columnIndexForEliminationTime,
										" ", timeInterval, valuesList);
							} catch (Exception e) {
								System.err.println("Da ist was schiefgegangen: " + e.getMessage());
							}

							try {
								totalArea = HeuristicEvaluation.getTotalArea(inputFile, columnIndexForEliminationTime,
										columnIndexForRadius, " ", timeInterval, valuesList, valuesList, sumOfCenters);
							} catch (Exception e) {
								System.err.println("Da ist was schiefgegangen: " + e.getMessage());
							}
							saveResult = saveResult + "\n" + timeInterval + ";" + totalAlive + ";" + totalArea;
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

		// ################## OPTIONS - END ##################
	}
}