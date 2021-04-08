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

public abstract class AbstractDataProcessingItem {

	public AbstractDataProcessingItem(String columnName) {
		columnName_ = columnName;
	}

	public abstract void processData();

	public abstract String getResultColumnHeader();

	public Map<String, List<RawDataSet>> getDataMap() {
		return dataMap_;
	}

	public void setDataMap(Map<String, List<RawDataSet>> dataMap) {
		dataMap_ = dataMap;
	}

	public String getColumnName() {
		return columnName_;
	}

	public void setColumnName(String columnName) {
		columnName_ = columnName;
	}

	public Map<String, Number> getResultColumn() {
		return resultColumn_;
	}

	public void setResultColumn(Map<String, Number> resultColumn) {
		resultColumn_ = resultColumn;
	}

	public ArrayList<DataFilter> getFilterList() {
		return filterList_;
	}

	public void setFilterList(ArrayList<DataFilter> filterList) {
		filterList_ = filterList;
	}

	public void setNormalizingBeans(ArrayList<NormalizingBean> normalizingBeans) {
		normalizingBeans_ = normalizingBeans;
	}

	/*
	 * Retrieves all values for the specified column ('columnName_') for one
	 * gene ('key'). Values are parsed to Double and added to list. If this is
	 * not possible, a Double.NaN is added. If the user has set one ore more
	 * filter(s), only datasets passing all filters are used.
	 */
	protected List<Double> extractRawValueList(String key) {
		// get all data sets for a specific key (gene name)
		List<RawDataSet> dataSets = dataMap_.get(key);
		List<Double> values = new ArrayList<Double>();
		// iterate through all data sets for one key (gene name)
		for (RawDataSet rawDataSet : dataSets) {
			// get the value of the specified column (measured variable)
			Double rawValue = ADPUtils.parseValue(key,
					rawDataSet.getDataEntry(columnName_));
			//J.G. 2015-06-22: Normalizing like this will not be done any more. It
			//has been replaced by simply filtering out data sets whose value of
			//the "normalizing column" is lower than the percentile
//			Double normalizedValue = doNormalizing(rawDataSet.getFilename(),
//					rawValue);
			if (passesFilter(rawDataSet)) {
				values.add(rawValue);
			}
		}
		return values;
	}

	protected boolean passesFilter(RawDataSet rawDataSet) {
		if (filterList_ != null && filterList_.size() > 0) {
			for (DataFilter dataFilter : filterList_) {
				Object filterValueObject = rawDataSet.getDataEntry(dataFilter
						.getColumnName());
				try {
					if (dataFilter.getRelation().equalsIgnoreCase("eq")) {
						Integer filterValue = Integer.valueOf(filterValueObject
								.toString());
						if (dataFilter.getLimit().getClass()
								.equals(Integer.class)) {
							Integer limit = (Integer) dataFilter.getLimit();
							if (filterValue.intValue() != limit.intValue()) {
								return false;
							}
						} else {
							String errorMessage = "Testing for equality only possible with integer values. Value was '"
									+ dataFilter.getLimit().toString() + "'.";
							ADPErrorHandler.addErrorEntry(errorMessage);
							continue;
						}
					} else if (dataFilter.getRelation().equalsIgnoreCase("gt")
							|| dataFilter.getRelation().equalsIgnoreCase("lt")) {

						Number limit = dataFilter.getLimit();
						Double filterValueDouble = Double
								.valueOf(filterValueObject.toString());
						if (dataFilter.getNormalizingBean() != null) {
							Double threshold = dataFilter.getNormalizingBean()
									.getThresholdForFilenameMap()
									.get(rawDataSet.getFilename());
							filterValueDouble -= threshold;
						}

						if (dataFilter.getRelation().equals("gt")
								&& filterValueDouble.doubleValue() < limit
										.doubleValue()) {
							// value in filter column is lower than limit, so
							// do not use the corresponding value for
							// calculation
							return false;
						}
						if (dataFilter.getRelation().equals("lt")
								&& filterValueDouble.doubleValue() > limit
										.doubleValue()) {
							// value in filter column is greater than limit, so
							// do not use the corresponding value for
							// calculation
							return false;
						}

					} else {
						String errorMessage = "Unknown filter relation '"
								+ dataFilter.getRelation()
								+ "'. Filter for column '"
								+ dataFilter.getColumnName()
								+ "' cannot be applied!";
						ADPErrorHandler.addErrorEntry(errorMessage);
						continue;
					}
				} catch (NumberFormatException nfe) {
					String message = "Error while filtering dataset. Non parseable filter value '"
							+ filterValueObject
							+ "'.\nException message is: "
							+ nfe.getMessage();
					ADPErrorHandler.addErrorEntry(message);
					continue;
				} catch (Exception e) {
					String message = "Unknown error: " + e.getMessage();
					ADPErrorHandler.addErrorEntry(message);
					continue;
				}
			}
			return true;
		} else {
			return true;
		}
	}

	protected Double doNormalizing(String filename, Double rawValue) {
		if (normalizingBeans_ != null && normalizingBeans_.size() > 0) {
			for (NormalizingBean normalizingBean : normalizingBeans_) {
				if (normalizingBean.getColumnName().equals(columnName_)) {
					Double threshold = normalizingBean
							.getThresholdForFilenameMap().get(filename);
					Double result = rawValue - threshold;
					return result;
				}
			}
			return rawValue;
		} else {
			return rawValue;
		}
	}

	protected void writeNoElementsMessage(String geneName) {
		if (filterList_ != null && filterList_.size() > 0) {
			String conditions = "";
			for (DataFilter dataFilter : filterList_) {
				String limit = dataFilter.getLimit().toString();
				String filterColumnName = dataFilter.getColumnName();
				String relation = "";
				if (dataFilter.getRelation().equals("gt")) {
					relation = ">";
				} else if (dataFilter.getRelation().equals("lt")) {
					relation = "<";
				} else if (dataFilter.getRelation().equals("eq")) {
					relation = "=";
				}
				conditions += filterColumnName + " " + relation + " " + limit
						+ ", ";
			}
			String message = "All values for gene name '" + geneName
					+ "' have been filtered out. Conditions were: "
					+ conditions + ".";
			ADPErrorHandler.addErrorEntry(message);
		}
	}

	protected Map<String, List<RawDataSet>> dataMap_;
	protected String columnName_;
	protected ArrayList<DataFilter> filterList_ = null;
	protected Map<String, Number> resultColumn_;
	protected ArrayList<NormalizingBean> normalizingBeans_ = null;
}
