package comparator;

import java.io.File;
import java.io.IOException;

public class Main {

	public static void main(String[] args) {

//		File inputFile = new File("result_for_total_alive.csv");
//		String outputFile = "evaluate_total_alive.csv";

		File inputFile = new File("result_for_total_area.csv");
		String outputFile = "evaluate_total_area.csv";

		int totalNumberOfColumns = 5;
		int columnIndexForDefault = 1;
		int columnIndexForRadius = 2;
		int columnIndexForInRange = 3;
		int columnIndexForOsmId = 4;
		int columnIndexForRandom = 5;

		try {
			Comparator.loadValuesFromColumn(inputFile, outputFile, totalNumberOfColumns, columnIndexForDefault,
					columnIndexForRadius, columnIndexForInRange, columnIndexForOsmId, columnIndexForRandom, ";");
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
