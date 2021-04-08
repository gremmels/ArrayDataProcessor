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
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ADPErrorHandler {

	
	static {
		errorList_ = new ArrayList<String>();
	}
	
	public static void addErrorEntry(String entry) {
		errorList_.add(entry);
	}
	
	public static void writeErrorFileIfNecessary() {
		if (errorList_.size() == 0) {
			return;
		}
		String workingDir = System.getProperty("user.dir");
		String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
		String errorFileName = workingDir + File.separator + "error_" + timeStamp + ".txt";
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(errorFileName));
			for (String entry : errorList_) {
				bw.write(entry);
				bw.newLine();
			}
			bw.close();
			System.out.println("There are errors. See '" + errorFileName + "'.");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static List<String> errorList_;
	
}
