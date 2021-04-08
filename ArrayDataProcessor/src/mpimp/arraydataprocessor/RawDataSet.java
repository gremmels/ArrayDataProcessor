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
import java.util.Map;

/*
 * A raw data set represents exactly one data line and the header line where the header items are the 
 * keys in 'datasetMap_' and the data entries are the values. Do make it distinguishable to which
 * raw file the dataset belongs (in case of more than one input file), the filename is also stored.
 */

public class RawDataSet {
	
	public RawDataSet(String filename) {
		datasetMap_ = new HashMap<String, Object>();
		filename_ = filename;
	}

	public void addDataEntry(String key, Object value) {
		datasetMap_.put(key, value);
	}
	
	public Object getDataEntry(String key) {
		return datasetMap_.get(key);
	}
	
	public String getFilename() {
		return filename_;
	}

	public void setFilename(String filename) {
		filename_ = filename;
	}

	private Map<String, Object> datasetMap_;
	private String filename_;
}
