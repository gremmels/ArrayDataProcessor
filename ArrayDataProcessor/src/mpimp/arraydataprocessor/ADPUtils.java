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

public class ADPUtils {

	public static Double parseValue(String dataFieldName, Object objectValue) {
		try {
			Double value = Double.valueOf(objectValue.toString());
			return value;
		} catch (NumberFormatException nfe) {
			String message = "Non parseable value '" + objectValue
					+ "' for " + dataFieldName + ".";
			ADPErrorHandler.addErrorEntry(message);
			return Double.NaN;
		} catch (Exception e) {
			String message = "Unknown error: " + e.getMessage();
			ADPErrorHandler.addErrorEntry(message);
			return Double.NaN;
		}
	}
	
}
