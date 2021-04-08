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

public class DataProcessingItemCount extends AbstractDataProcessingItem {

	public DataProcessingItemCount(String columnName) {
		super(columnName);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void processData() {
		resultColumn_ = new HashMap<String, Number>();
		// taking the gene names (the keys)
		Set<String> keys = dataMap_.keySet();
		for (String key : keys) {
			List<String> values = extractStringRawValueList(key);
			if (values.size() == 0) {
				writeNoElementsMessage(key);
			}
			resultColumn_.put(key, values.size());
		}
	}

	protected List<String> extractStringRawValueList(String key) {
		// get all data sets for a specific key (gene name)
		List<RawDataSet> dataSets = dataMap_.get(key);
		List<String> values = new ArrayList<String>();
		// iterate through all data sets for one key (gene name)
		for (RawDataSet rawDataSet : dataSets) {
			// get the value of the specified column (measured variable)
			String valueString = rawDataSet.getDataEntry(columnName_).toString();
			if (passesFilter(rawDataSet)) {
				// add value to value collection
				values.add(valueString);
			}
		}
		return values;
	}

	@Override
	public String getResultColumnHeader() {
		return "Count";
	}

}
