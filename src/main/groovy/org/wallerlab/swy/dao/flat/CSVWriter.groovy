package org.wallerlab.swy.dao.flat

/**
 * The CSV writer is for storing large data files,
 * e.g. pheromone data. Not recommended for data storage.
 * 
 */
public class CSVWriter {
	
	/**
	 * Writes a complete csv file (or appends data to an existing file).
	 * 
	 * @param file the file to append the output to
	 * @param objectList an array of dataObjects where each dataObject is itself an Array
	 */
	public static void writeCSV(File file, Object[][] objectList) {
		for (Object[] list: objectList) {
			writeCSVLine(file, list)
		}
	}
	
	/**
	 * Writes the string representations of Objects to a file. Seperated by ",", all in one line.
	 * 
	 * @param file the file to append a line to
	 * @param objects the objects to write. All given arguments are written into the same line.
	 */
	public static void writeCSVLine(File file, Object[] objects) {
		String appendString = ""
			for (Object object: objects) {
				appendString += object.toString()+", "
			}
		/* Remove the last , and start a new line. */
		appendString = appendString[0..-3]+"\n"
		
		file.append(appendString)
	}
}