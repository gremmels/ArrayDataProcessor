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

import org.jfree.data.statistics.Statistics;

public class DataProcessor {

	public DataProcessor(Map<String, List<RawDataSet>> dataMap) {
		dataMap_ = dataMap;
		headerLine_ = new ArrayList<String>();
		writeHeaderList();
	}

	public List<List<Object>> getResultMatrix() {
		return resultMatrix_;
	}

	public List<String> getHeaderLine() {
		return headerLine_;
	}

	public void processData() {
		resultMatrix_ = new ArrayList<List<Object>>();
		Set<String> keys = dataMap_.keySet();
		for (String key : keys) {
			List<Object> resultLine = new ArrayList<Object>();
			resultLine.add(key);
			List<RawDataSet> dataSets = dataMap_.get(key);
			String count = String.valueOf(dataSets.size());
			resultLine.add(count);
			resultLine.add(String.valueOf(medianOfRatios_635_532(dataSets)));
			resultLine.add(String.valueOf(median_F635_Median_B635(dataSets)));
			resultLine.add(String.valueOf(median_F532_Median_B532(dataSets)));
			resultLine.add(String.valueOf(medianOfRatios_532_635(dataSets)));
			
			resultMatrix_.add(resultLine);
		}
	}

	public double medianOfRatios_635_532(List<RawDataSet> dataSets) {
		List<Double> values = new ArrayList<Double>();
		for (RawDataSet rawDataSet : dataSets) {
			try {
				Double value = Double.valueOf(rawDataSet
						.getDataEntry("Median of Ratios (635/532)").toString());
				values.add(value);
			} catch (NumberFormatException nfe) {

			}
		}
		return Statistics.calculateMedian(values);
	}

	public double median_F635_Median_B635(List<RawDataSet> dataSets) {
		List<Double> values = new ArrayList<Double>();
		for (RawDataSet rawDataSet : dataSets) {
			try {
				Double value = Double.valueOf(rawDataSet
						.getDataEntry("F635 Median - B635").toString());
				values.add(value);
			} catch (NumberFormatException nfe) {

			}
		}
		return Statistics.calculateMedian(values);
	}

	public double median_F532_Median_B532(List<RawDataSet> dataSets) {
		List<Double> values = new ArrayList<Double>();
		for (RawDataSet rawDataSet : dataSets) {
			try {
				Double value = Double.valueOf(rawDataSet
						.getDataEntry("F532 Median - B532").toString());
				values.add(value);
			} catch (NumberFormatException nfe) {

			}
		}
		return Statistics.calculateMedian(values);
	}

	public double medianOfRatios_532_635(List<RawDataSet> dataSets) {
		List<Double> values = new ArrayList<Double>();
		for (RawDataSet rawDataSet : dataSets) {
			try {
				Double value = Double.valueOf(rawDataSet
						.getDataEntry("Median of Ratios (635/532)").toString());
				values.add(value);
			} catch (NumberFormatException nfe) {

			}
		}
		double median = Statistics.calculateMedian(values); 
		return 1/median;
	}

	
	
	private void writeHeaderList() {
		headerLine_.add("Name");
		// headerLine_.add("No.");
		headerLine_.add("Count");
		headerLine_.add("Median of Ratios (635/532)");
		//headerLine_.add("Position");
		headerLine_.add("Median(\"F635 Median - B635\")");
		headerLine_.add("Median(\"F532 Median - B532\")");
		headerLine_.add("Median of Ratios (532/635)");
	}

	private Map<String, List<RawDataSet>> dataMap_;
	private List<List<Object>> resultMatrix_;
	private List<String> headerLine_;
}
