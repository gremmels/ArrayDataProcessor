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

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

public class DataReader {

	static {
		DataReader.controlNames_ = new HashMap<String, String>();
		DataReader.controlNames_.put("MY-QC", "MY-QC");
		DataReader.controlNames_.put("Ctrl-Pos-555", "Ctrl-Pos-555");
		DataReader.controlNames_.put("Ctrl-Pos-647", "Ctrl-Pos-647");
		DataReader.controlNames_.put("Ctrl-Stringent-555", "Ctrl-Stringent-555");
		DataReader.controlNames_.put("Ctrl-Stringent-647", "Ctrl-Stringent-647");
		DataReader.controlNames_.put("Empty_NCTRL", "Empty_NCTRL");
	}
	
	public DataReader(String inputFile) {
		inputFile_ = inputFile;
		initContainers();
	}

	public DataReader(List<String> inputFileList) {
		inputFileList_ = inputFileList;
		initContainers();
	}

	public void readRawFile() {
		readRawFile(inputFile_);
	}

	public void readRawFile(String fileName) {
		BufferedReader br = null;
		inDataBlock_ = false;
		lineCounter_ = 0;
		dataLineCounter_ = 0;
		if (normalizingBeans_ != null && normalizingBeans_.size() > 0) {
			normalizingMap_ = new HashMap<String, List<Double>>();
		}
		try {
			br = new BufferedReader(new FileReader(fileName));
			String line;
			while ((line = br.readLine()) != null) {
				parseLine(fileName, line);
			}
			br.close();
			if (inDataBlock_ == false) {
				String message = "No data block found. Please check if file '"
						+ fileName
						+ "' has the correct gpr format or contains any data at all.";
				ADPErrorHandler.addErrorEntry(message);
			}
			lineCountMap_.put(fileName, lineCounter_);
			dataLineCountMap_.put(fileName, dataLineCounter_);
			if (normalizingMap_ != null) {
				calulateNormalizingThresholds(fileName);
			}
		} catch (FileNotFoundException e) {
			String message = "File " + fileName
					+ " could not be found. Exception message: "
					+ e.getMessage();
			ADPErrorHandler.addErrorEntry(message);
		} catch (IOException e) {
			String message = "Error while reading file " + fileName
					+ " Exception message: " + e.getMessage();
			ADPErrorHandler.addErrorEntry(message);
		} catch (HeaderFormatException hfe) {
			String message = "Header format does not fulfil requirements. Exception message: "
					+ hfe.getMessage();
			ADPErrorHandler.addErrorEntry(message);
		} catch (Exception e) {
			String message = "Unknown error: " + e.getMessage();
			ADPErrorHandler.addErrorEntry(message);
		} finally {
			try {
				br.close();
			} catch (IOException ioe) {
				String message = "Closing of data stream after error not possible. Exception message: "
						+ ioe.getMessage();
				ADPErrorHandler.addErrorEntry(message);
			} catch (Exception e) {
				String message = "Unknown error: " + e.getMessage();
				ADPErrorHandler.addErrorEntry(message);
			}
		}
		return;
	}

	public void readRawFileList() {
		if (inputFileList_ != null) {
			for (String fileName : inputFileList_) {
				readRawFile(fileName);
			}
		} else {
			String message = "No input file specified.";
			ADPErrorHandler.addErrorEntry(message);
		}
	}

	public Map<String, List<RawDataSet>> getDataMap() {
		return dataMap_;
	}

	public Map<String, Integer> getLineCountMap() {
		return lineCountMap_;
	}

	public Map<String, Integer> getDataLineCountMap() {
		return dataLineCountMap_;
	}

	public void setNormalizingBeans(List<NormalizingBean> normalizingBeans) {
		normalizingBeans_ = normalizingBeans;
	}

	public List<DataFilter> getFilterList() {
		return filterList_;
	}

	public void setFilterList(List<DataFilter> filterList) {
		filterList_ = filterList;
	}

	private void initContainers() {
		dataMap_ = new HashMap<String, List<RawDataSet>>();
		lineCountMap_ = new HashMap<String, Integer>();
		dataLineCountMap_ = new HashMap<String, Integer>();
	}

	private void parseLine(String filename, String line)
			throws HeaderFormatException {
		String[] lineArray = line.split("\t");
		if (lineArray.length < 3) {
			return;
		}
		Pattern blockPattern = Pattern.compile("\"?Block\"?");
		Pattern columnPattern = Pattern.compile("\"?Column\"?");
		Pattern rowPattern = Pattern.compile("\"?Row\"?");
		lineCounter_++;
		if (inDataBlock_ == false) {
			Matcher blockMatcher = blockPattern.matcher(lineArray[0]);
			Matcher columnMatcher = columnPattern.matcher(lineArray[1]);
			Matcher rowMatcher = rowPattern.matcher(lineArray[2]);
			if (blockMatcher.matches() && columnMatcher.matches()
					&& rowMatcher.matches()) {
				inDataBlock_ = true;
				headerLine_ = lineArray;
				return;
			}
		} else {
			dataLineCounter_++;
			RawDataSet rawDataSet = new RawDataSet(filename);
			for (int i = 0; i < headerLine_.length; i++) {
				String key = headerLine_[i].replace("\"", ""); //sometimes text entries are in quotes, sometimes not
				if (i < lineArray.length) {
					rawDataSet.addDataEntry(key, lineArray[i]);
					// check if normalizing is required
					if (normalizingBeans_ != null
							&& normalizingBeans_.size() > 0) {
						// ...and collect the necessary data
						collectDataForNormalizing(key, lineArray[3], lineArray[i]);
					}
				} else {
					rawDataSet.addDataEntry(key, Double.NaN);
				}
			}
			if (dataMap_.containsKey(lineArray[3])) {
				dataMap_.get(lineArray[3]).add(rawDataSet);
			} else {
				List<RawDataSet> dataList = new ArrayList<RawDataSet>();
				dataList.add(rawDataSet);
				dataMap_.put(lineArray[3], dataList);
			}
		}
	}
	//controls (not used for calculating distribution): MY-QC   Ctrl-Pos-555   Ctrl-Pos-647   Ctrl-Stringent-555   Ctrl-Stringent-647   Empty_NCTRL
	private void collectDataForNormalizing(String columnName, String spotName, Object objectValue) {
		spotName = spotName.replace("\"", "");//sometimes text entries are in quotes, sometimes not
		if (controlNames_.containsKey(spotName)) {
			return;
		}
		for (NormalizingBean normalizingBean : normalizingBeans_) {
			if (columnName.equals(normalizingBean.getColumnName())) {
				Double doubleValue = ADPUtils.parseValue(columnName,
						objectValue);
				if (normalizingMap_.containsKey(columnName)) {
					normalizingMap_.get(normalizingBean.getColumnName()).add(
							doubleValue);
				} else {
					List<Double> valuesToNormalize = new ArrayList<Double>();
					valuesToNormalize.add(doubleValue);
					normalizingMap_.put(columnName, valuesToNormalize);
				}
			}
		}
	}

	private void calulateNormalizingThresholds(String fileName) {
		for (NormalizingBean normalizingBean : normalizingBeans_) {
			List<Double> doubleList = normalizingMap_.get(normalizingBean
					.getColumnName());
			Double[] doubleObjectArray = doubleList
					.toArray(new Double[doubleList.size()]);
			double[] doubleArray = ArrayUtils.toPrimitive(doubleObjectArray);
			DescriptiveStatistics descriptiveStatistics = new DescriptiveStatistics(
					doubleArray);
			double threshold = descriptiveStatistics
					.getPercentile(normalizingBean.getPercentile());
			normalizingBean.getThresholdForFilenameMap().put(fileName,
					threshold);
			DataFilter dataFilter = new DataFilter(
					normalizingBean.getColumnName(), "gt", "0");
			dataFilter.setNormalizingBean(normalizingBean);
			filterList_.add(dataFilter);
		}
	}

	private String inputFile_;
	private List<String> inputFileList_;
	private Map<String, List<RawDataSet>> dataMap_;
	private int lineCounter_ = 0;
	private int dataLineCounter_ = 0;
	private boolean inDataBlock_ = false;
	private Map<String, Integer> lineCountMap_;
	private Map<String, Integer> dataLineCountMap_;
	private Map<String, List<Double>> normalizingMap_;
	private List<NormalizingBean> normalizingBeans_;
	private List<DataFilter> filterList_;
	private static Map<String, String> controlNames_;

	private String[] headerLine_ = null;

}

/*
 * Name No. Count Median of Ratios (635/532) => SNR635, SNR532 Position => ?
 * Median("F635 Median - B635") Median("F532 Median - B532")
 * 
 * Median of Ratios (532/635) => SNR635, SNR532
 */
