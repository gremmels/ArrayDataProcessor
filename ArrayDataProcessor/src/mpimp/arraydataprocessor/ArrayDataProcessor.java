/*
ArrayDataProcessor: an evaluation tool which takes one or more files in 
GenPix Results format (*.gpr) and does several statistical operations 
on the data. Output of results either in MS Excel (*.xls, *.xlsx) or *.csv
format.

Copyright (C) 2015,  gremmels(at)mpimp-golm.mpg.de

ArrayDataProcessor is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>

Contributors:
gremmels(at)mpimp-golm.mpg.de - initial API and implementation
*/
package mpimp.arraydataprocessor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ArrayDataProcessor {

	public static void main(String[] args) {
		if (args.length < 2) {
			ArrayDataProcessor
					.printUsage("ArrayDataProcessor takes at least two arguments.");
		} else if (args.length == 2) {
			ArrayDataProcessor.doStandardDataProcessing(args);
		} else {
			ArrayDataProcessor.doComplexDataProcessing(args);
		}

		ADPErrorHandler.writeErrorFileIfNecessary();
	}

	private static void doStandardDataProcessing(String[] commandLine) {
		String inputFile = commandLine[0];
		String outputFile = commandLine[1];

		handleDataSimple(inputFile, outputFile);
	}

	private static void handleDataSimple(String inputFile, String outputFile) {
		DataReader dataReader = new DataReader(inputFile);
		dataReader.readRawFile();
		DataProcessor dataProcessor = new DataProcessor(dataReader.getDataMap());
		dataProcessor.processData();

		List<List<Object>> resultMatrix = dataProcessor.getResultMatrix();
		List<String> headerLine = dataProcessor.getHeaderLine();
		DataWriter dataWriter = new DataWriter(outputFile, resultMatrix,
				headerLine);
		dataWriter.writeOutputCSVFile();
		dataWriter.writeOutpuExcelFile(".xls");
		dataWriter.writeOutpuExcelFile(".xlsx");
	}

	private static void doComplexDataProcessing(String[] commandLine) {
		List<String> inputFileList = new ArrayList<String>();
		List<String> outputFormatList = new ArrayList<String>();
		String outputFile = "";
		ArrayList<DataFilter> filterList = new ArrayList<DataFilter>();
		ArrayList<NormalizingBean> normalizingBeans = new ArrayList<NormalizingBean>();
		ArrayList<AbstractDataProcessingItem> adpList = new ArrayList<AbstractDataProcessingItem>();

		adpList.add(new DataProcessingItemCount("Name"));
		int i = 0;
		while (i < commandLine.length) {
			if (commandLine[i].equals("--inFile")) {
				if (!checkIfCommandLineContinues(commandLine, i + 1)) {
					ArrayDataProcessor
							.printUsage("Option '--inputFile' requires at least one argument (path of input file(s)).");
					return;
				}
				if (commandLine[i + 1].startsWith("--")) {
					ArrayDataProcessor
							.printUsage("Option '--inputFile' requires at least one argument (path of input file(s)).");
					return;
				}
				i++;
				while (i < commandLine.length
						&& !commandLine[i].startsWith("--")) {
					inputFileList.add(commandLine[i]);
					i++;
				}
			} else if (commandLine[i].equals("--outFile")) {
				if (!checkIfCommandLineContinues(commandLine, i + 1)) {
					ArrayDataProcessor
							.printUsage("Option '--outFile' requires an argument (path of output file).");
					return;
				}
				if (commandLine[i + 1].startsWith("--")) {
					ArrayDataProcessor
							.printUsage("Option '--outFile' requires an argument (path of output file).");
					return;
				}
				outputFile = commandLine[i + 1];
				i += 2;
			} else if (commandLine[i].equals("--median")) {
				if (!checkIfCommandLineContinues(commandLine, i + 1)) {
					ArrayDataProcessor
							.printUsage("Option '--median' requires an argument (name of data column)");
					return;
				}
				if (commandLine[i + 1].startsWith("--")) {
					ArrayDataProcessor
							.printUsage("Option '--median' requires an argument (name of data column)");
					return;
				}
				adpList.add(new DataProcessingItemMedian(commandLine[i + 1]));
				i += 2;
			} else if (commandLine[i].equals("--medianLog")) {
				if (!checkIfCommandLineContinues(commandLine, i + 1)) {
					ArrayDataProcessor
							.printUsage("Option '--medianLog' requires an argument (name of data column).");
					return;
				}
				if (commandLine[i + 1].startsWith("--")) {
					ArrayDataProcessor
							.printUsage("Option '--medianLog' requires an argument (name of data column).");
					return;
				}
				adpList.add(new DataProcessingItemAntiLogMedianOfLogs(
						commandLine[i + 1]));
				i += 2;
			} else if (commandLine[i].equals("--mean")) {
				if (!checkIfCommandLineContinues(commandLine, i + 1)) {
					ArrayDataProcessor
							.printUsage("Option '--mean' requires 2 arguments (name of data column and "
									+ "'true' or 'false' for indicating if null and NaN values should be included).");
					return;
				}
				if (commandLine[i + 1].startsWith("--")) {
					ArrayDataProcessor
							.printUsage("Option '--mean' requires  2 arguments (name of data column and "
									+ "'true' or 'false' for indicating if null and NaN values should be included).");
					return;
				}
				if (!checkIfCommandLineContinues(commandLine, i + 2)) {
					ArrayDataProcessor
							.printUsage("Option '--mean' requires  2 arguments (name of data column and "
									+ "'true' or 'false' for indicating if null and NaN values should be included).");
					return;
				}
				if (commandLine[i + 2].equalsIgnoreCase("true")) {
					adpList.add(new DataProcessingItemMean(commandLine[i + 1],
							true));
				} else if (commandLine[i + 2].equalsIgnoreCase("false")) {
					adpList.add(new DataProcessingItemMean(commandLine[i + 1],
							false));
				} else {
					ArrayDataProcessor
							.printUsage("Option '--mean' requires 'true' or 'false' (include null and NaN) as second argument.");
					return;
				}
				i += 3;
			} else if (commandLine[i].equals("--max")) {
				if (!checkIfCommandLineContinues(commandLine, i + 1)) {
					ArrayDataProcessor
							.printUsage("Option '--max' requires an argument (name of data column).");
					return;
				}
				if (commandLine[i + 1].startsWith("--")) {
					ArrayDataProcessor
							.printUsage("Option '--max' requires an argument (name of data column).");
					return;
				}
				adpList.add(new DataProcessingItemMax(commandLine[i + 1]));
				i += 2;
			} else if (commandLine[i].equals("--min")) {
				if (!checkIfCommandLineContinues(commandLine, i + 1)) {
					ArrayDataProcessor
							.printUsage("Option '--min' requires an argument (name of data column.");
					return;
				}
				if (commandLine[i + 1].startsWith("--")) {
					ArrayDataProcessor
							.printUsage("Option '--min' requires an argument (name of data column.");
					return;
				}
				adpList.add(new DataProcessingItemMin(commandLine[i + 1]));
				i += 2;
			} else if (commandLine[i].equals("--filter")) {
				if (!checkIfCommandLineContinues(commandLine, i + 1)) {
					ArrayDataProcessor
							.printUsage("Option '--filter' requires 3 arguments (name of data column, relation, limit)");
					return;
				}
				if (!checkIfCommandLineContinues(commandLine, i + 2)) {
					ArrayDataProcessor
							.printUsage("Option '--filter' requires 3 arguments (name of data column, relation, limit)");
					return;
				}
				if (!checkIfCommandLineContinues(commandLine, i + 3)) {
					ArrayDataProcessor
							.printUsage("Option '--filter' requires 3 arguments (name of data column, relation, limit)");
					return;
				}
				if (commandLine[i + 1].startsWith("--")
						|| commandLine[i + 2].startsWith("--")
						|| commandLine[i + 3].startsWith("--")) {
					ArrayDataProcessor
							.printUsage("Option '--filter' requires 3 arguments (name of data column, relation, limit)");
					return;
				}
				DataFilter dataFilter = new DataFilter(commandLine[i + 1],
						commandLine[i + 2], commandLine[i + 3]);
				filterList.add(dataFilter);
				i += 4;
			} else if (commandLine[i].equals("--outputFormat")) {
				if (!checkIfCommandLineContinues(commandLine, i + 1)) {
					ArrayDataProcessor
							.printUsage("Option '--outputFormat' requires at least one of 'csv', 'xls' or 'xlsx' as argument.");
					return;
				}
				i++;
				while (i < commandLine.length
						&& !commandLine[i].startsWith("--")) {
					if (!commandLine[i].equalsIgnoreCase("csv")
							&& !commandLine[i].equalsIgnoreCase("xls")
							&& !commandLine[i].equalsIgnoreCase("xlsx")) {
						ArrayDataProcessor
								.printUsage("Option '--outputFormat' requires at least one of 'csv', 'xls' or 'xlsx' as argument.");
					}
					outputFormatList.add(commandLine[i]);
					i++;
				}
			} else if (commandLine[i].equals("--normalize")) {
				if (!checkIfCommandLineContinues(commandLine, i + 1)) {
					ArrayDataProcessor
							.printUsage("Option '--normalize' requires 2 arguments (name of data column, percentile)");
					return;
				}
				if (!checkIfCommandLineContinues(commandLine, i + 2)) {
					ArrayDataProcessor
							.printUsage("Option '--normalize' requires 2 arguments (name of data column, percentile)");
					return;
				}
				if (commandLine[i + 1].startsWith("--")
						|| commandLine[i + 2].startsWith("--")) {
					ArrayDataProcessor
							.printUsage("Option '--normalize' requires 2 arguments (name of data column, percentile)");
					return;
				}
				Double percentile = ADPUtils.parseValue("percentile",
						commandLine[i + 2]);
				if (!percentile.equals(Double.NaN)) {
					NormalizingBean normalizingBean = new NormalizingBean(
							commandLine[i + 1], percentile);
					normalizingBeans.add(normalizingBean);
				} else {
					ArrayDataProcessor
							.printUsage("Argument 'percentile' for --normalize must be a double value.");
					return;
				}
				i += 3;
			} else {
				if (commandLine[i].startsWith("--")) {
					ArrayDataProcessor.printUsage("Unknown option '"
							+ commandLine[i] + "'.");
				} else {
					ArrayDataProcessor.printUsage("Unknown argument '"
							+ commandLine[i] + "'.");
				}
				i++;
			}
		}
		if (commandLine[commandLine.length - 1].startsWith("--")) {
			ArrayDataProcessor
					.printUsage("Each option starting with '--' requires at least one argument.");
			return;
		}
		handleDataComplex(inputFileList, outputFormatList, outputFile,
				filterList, adpList, normalizingBeans);
	}

	private static boolean checkIfCommandLineContinues(String[] commandLine,
			int requiredIndex) {
		if (commandLine.length - 1 < requiredIndex) {
			return false;
		}
		return true;
	}

	private static void handleDataComplex(List<String> inputFileList,
			List<String> outputFormatList, String outputFile,
			ArrayList<DataFilter> filterList,
			ArrayList<AbstractDataProcessingItem> adpList, ArrayList<NormalizingBean> normalizingBeans) {
		DataReader dataReader = new DataReader(inputFileList);
		dataReader.setFilterList(filterList);
		dataReader.setNormalizingBeans(normalizingBeans);
		dataReader.readRawFileList();
		List<List<Object>> resultMatrix = new ArrayList<List<Object>>();
		List<String> headerLine = new ArrayList<String>();
		headerLine.add("Name");
		for (AbstractDataProcessingItem adpItem : adpList) {
			adpItem.setDataMap(dataReader.getDataMap());
			adpItem.setFilterList(filterList);
			adpItem.setNormalizingBeans(normalizingBeans);
			adpItem.processData();
			ArrayDataProcessor.addColumnToResultMatrix(
					adpItem.getResultColumn(), adpItem.getResultColumnHeader(),
					headerLine, resultMatrix);
		}
		DataWriter dataWriter = new DataWriter(outputFile, resultMatrix,
				headerLine);

		if (outputFormatList.size() > 0) {
			for (String format : outputFormatList) {
				if (format.equalsIgnoreCase("csv")) {
					dataWriter.writeOutputCSVFile();
				} else if (format.equalsIgnoreCase("xls")) {
					dataWriter.writeOutpuExcelFile(".xls");
				} else if (format.equalsIgnoreCase("xlsx")) {
					dataWriter.writeOutpuExcelFile(".xlsx");
				}
			}
		} else {
			dataWriter.writeOutputCSVFile();
		}
	}

	private static void addColumnToResultMatrix(
			Map<String, Number> resultColumn, String resultColumnHeader,
			List<String> headerLine, List<List<Object>> resultMatrix) {
		headerLine.add(resultColumnHeader);
		if (resultMatrix.size() == 0) {
			Set<String> keys = resultColumn.keySet();
			for (String key : keys) {
				List<Object> resultLine = new ArrayList<Object>();
				resultLine.add(key);
				resultLine.add(resultColumn.get(key));
				resultMatrix.add(resultLine);
			}
		} else {
			for (List<Object> resultLine : resultMatrix) {
				resultLine.add(resultColumn.get(resultLine.get(0)));
			}
		}
	}

	private static void printUsage(String additionalMessage) {
		System.out
				.println(additionalMessage
						+ "\nUsage:\n java -jar ArrayDataProcessor.jar <input file name> <output file name>\nOR "
						+ "--inFile <input filename>[<input filename 2><input filename 3>...] --outFile <output filename> "
						+ "[--median <column> --medianLog <column> --mean <column> <true|false> --max <column> --min <column>"
						+ " --filter <column> <lt|gt|eq> <limit> --outputFormat <csv|xls|xlsx>] --normalize <column> <percentile>");
	}

}
