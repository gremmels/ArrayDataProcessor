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

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.jfree.data.statistics.Statistics;

public class DataProcessingItemMean extends AbstractDataProcessingItem {

	public DataProcessingItemMean(String columnName, boolean includeNullAnNaN) {
		super(columnName);
		includeNullAndNaN_ = includeNullAnNaN;
	}

	@Override
	public void processData() {
		resultColumn_ = new HashMap<String, Number>();
		// taking the gene names (the keys)
		Set<String> keys = dataMap_.keySet();
		for (String key : keys) {
			List<Double> values = extractRawValueList(key);
			// calculate the mean value from all values retrieved for
			// the key and the specified column
			if (values.size() > 0) {
				resultColumn_.put(key, Statistics.calculateMean(values, includeNullAndNaN_));
			} else {
				resultColumn_.put(key, Double.NaN);
				writeNoElementsMessage(key);
			}
		}

	}

	@Override
	public String getResultColumnHeader() {
		return "Mean (" + columnName_ + ")";
	}
	
	private boolean includeNullAndNaN_ = false;

}
