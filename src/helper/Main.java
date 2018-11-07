package helper;

import java.io.File;
import java.io.IOException;

public class Main {

	public static void main(String[] args) {

		// ################## PROPERTY SETTINGS - BEGIN ##################

//		File inputFile = new File("total_alive.csv");
//		String outputFile = "count_total_alive.csv";

		File inputFile = new File("total_area.csv");
		String outputFile = "count_total_area.csv";

		// ################## PROPERTY SETTINGS - END ####################

		final int totalNumberOfColumns = 4;
		final int columnIndexForDefault = 1;
		final int columnIndexForRadius = 2;
		final int columnIndexForInRange = 3;
		final int columnIndexForRandom = 4;

		try {
			QualityMeasurementsForHeuristicsHelper.compareHeuristicsByGettingHighestValue(inputFile, outputFile,
					totalNumberOfColumns, columnIndexForDefault, columnIndexForRadius, columnIndexForInRange,
					columnIndexForRandom, ";");
			System.out.println("Computation finished.");
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}