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
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.jfree.data.statistics.Statistics;

public class DataProcessingItemAntiLogMedianOfLogs extends AbstractDataProcessingItem {

	public DataProcessingItemAntiLogMedianOfLogs(String columnName) {
		super(columnName);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void processData() {
		resultColumn_ = new HashMap<String, Number>();
		// taking the gene names (the keys)
		Set<String> keys = dataMap_.keySet();
		for (String key : keys) {
			List<Double> values = extractRawValueList(key);
			// calculate the median value from all values retrieved for
			// the key and the specified column
			if (values.size() > 0) {
				resultColumn_.put(key, calculateAntilogMedianOfLogs(values));
			} else {
				resultColumn_.put(key, Double.NaN);
				writeNoElementsMessage(key);
			}
		}
	}

	private Double calculateAntilogMedianOfLogs(List<Double> values) {
		List<Double> logValues = new ArrayList<Double>();
		for (Double singleValue : values) {
			logValues.add(Math.log(singleValue));
		}
		Double logMedian = Statistics.calculateMedian(logValues);
		Double median = Math.pow(Math.E, logMedian);
		return median;
	}
	
	@Override
	public String getResultColumnHeader() {
		return "AntilogOfMedianOfLogs (" + columnName_ + ")";
	}

}
