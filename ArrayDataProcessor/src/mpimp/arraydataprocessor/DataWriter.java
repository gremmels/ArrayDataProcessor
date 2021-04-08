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

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import mpimp.arraydataprocessor.poi.ADPWorkbook;
import mpimp.arraydataprocessor.poi.ADPWorksheet;

public class DataWriter {

	public DataWriter(String outputFile, List<List<Object>> dataMatrix,
			List<String> headerLine) {
		if (outputFile.equals("")) {
			outputFile_ = "out";
		} else {
			outputFile_ = outputFile;
		}
		dataMatrix_ = dataMatrix;
		headerLine_ = headerLine;
	}

	public void writeOutputCSVFile() {
		String fileName = outputFile_;
		if (!fileName.endsWith(".csv")) {
			fileName = fileName + ".csv";
		}
		int columnCounter = 0;
		int columnCount = headerLine_.size();
		int rowCounter = 0;
		int rowCount = dataMatrix_.size();
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(fileName));
			for (String header : headerLine_) {
				bw.write(header);
				if (columnCounter < columnCount) {
					bw.write("\t");
				}
				columnCounter++;
			}
			bw.newLine();
			for (List<Object> line : dataMatrix_) {
				columnCounter = 0;
				for (Object cell : line) {
					bw.write(cell.toString());
					if (columnCounter < columnCount) {
						bw.write("\t");
					}
					columnCounter++;
				}
				if (rowCounter < rowCount) {
					bw.newLine();
				}
				rowCounter++;
			}
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void writeOutpuExcelFile(String fileFormat) {
		ADPWorkbook adpWorkbook = new ADPWorkbook(fileFormat);
		ADPWorksheet adpWorksheet = adpWorkbook.createADPWorksheet(headerLine_);
		String fileName = outputFile_;
		if (!fileName.endsWith(fileFormat)) {
			fileName = fileName + fileFormat;
		}
		adpWorksheet.writeHeader();
		adpWorksheet.writeBody(dataMatrix_);
		adpWorkbook.write(fileName);
	}

	private List<List<Object>> dataMatrix_;
	private List<String> headerLine_;
	private String outputFile_;

}
