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

public class NormalizingBean {

	public NormalizingBean(String columnName, Double percentile) {
		columnName_ = columnName;
		percentile_ = percentile;
		thresholdForFilenameMap_ = new HashMap<String, Double>();
	}
	
	public String getColumnName() {
		return columnName_;
	}
	
	public Double getPercentile() {
		return percentile_;
	}
	
	public Map<String, Double> getThresholdForFilenameMap() {
		return thresholdForFilenameMap_;
	}
	
	private String columnName_;
	private Double percentile_;
	private Map<String, Double> thresholdForFilenameMap_;
}
