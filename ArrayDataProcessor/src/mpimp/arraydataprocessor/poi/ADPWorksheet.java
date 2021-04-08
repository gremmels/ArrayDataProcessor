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
package mpimp.arraydataprocessor.poi;

import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

public class ADPWorksheet {

	public ADPWorksheet(Sheet sheet, List<String> header) {
		workSheet_ = sheet;
		header_ = header;
	}
	
	
	public void writeHeader() {
		Row row = workSheet_.createRow(0);
		for (int i = 0; i < header_.size(); i++) {
			Cell cell = row.createCell(i);
			cell.setCellValue(header_.get(i));
		}
	}
	
	public void writeBody(List<List<Object>> body) {
		for (int rowIndex = 0; rowIndex < body.size(); rowIndex++) {
			List<Object> line = body.get(rowIndex);
			Row row = workSheet_.createRow(rowIndex + 1);
			Cell cell = null;
			for (int colIndex = 0; colIndex < header_.size(); colIndex++) {
				if (line.get(colIndex) instanceof Integer) {
					cell = row.createCell(colIndex, Cell.CELL_TYPE_NUMERIC);
					Integer cellContent = (Integer)(line.get(colIndex));
					cell.setCellValue(cellContent);
				} else if (line.get(colIndex) instanceof Double) {
					cell = row.createCell(colIndex, Cell.CELL_TYPE_NUMERIC);
					Double cellContent = (Double)(line.get(colIndex));
					cell.setCellValue(cellContent);
				} else {
					cell = row.createCell(colIndex, Cell.CELL_TYPE_STRING);
					String cellContent = (String)line.get(colIndex);
					if (cellContent.startsWith("\"") && cellContent.endsWith("\"")) {
						cellContent = cellContent.substring(1, cellContent.length() - 1);
					}
					cell.setCellValue(cellContent);
				}
			}
		}
	}
	
	private Sheet workSheet_;
	private List<String> header_;
}
