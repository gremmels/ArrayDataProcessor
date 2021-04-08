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

public class DataFilter {

	public DataFilter(String columnName, String relation, String limit)  {
		columnName_ = columnName;
		if ((limit_ = parseInteger(limit)) == null) {
			limit_ = parseDouble(limit); 
		}
		relation_ = relation;
	}
	
	private Double parseDouble(String numberString) {
		Double retValue = null;
		try {
			retValue = Double.parseDouble(numberString);
		} catch (NumberFormatException nfe) {
			//do nothing - this is ok here
		}
		return retValue;
	}
	
	private Integer parseInteger(String numberString) {
		Integer retValue = null;
		try {
			retValue = Integer.parseInt(numberString);
		} catch (NumberFormatException nfe) {
			//do nothing - this is ok here
		}
		return retValue;
	}
	
	public String getColumnName() {
		return columnName_;
	}
	public void setColumnName(String columnName) {
		columnName_ = columnName;
	}
	public Number getLimit() {
		return limit_;
	}
	public void setLimit(Number limit) {
		limit_ = limit;
	}
	
	public String getRelation() {
		return relation_;
	}

	public void setRelation(String relation) {
		relation_ = relation;
	}

	public NormalizingBean getNormalizingBean() {
		return normalizingBean_;
	}

	public void setNormalizingBean(NormalizingBean normalizingBean) {
		normalizingBean_ = normalizingBean;
	}

	private String columnName_;
	private Number limit_;
	private String relation_;
	private NormalizingBean normalizingBean_;
}
