General usage:
java -jar ArrayDataProcessor.jar --inFile <input filename>[<input filename 2><input filename 3>...] --outFile <output filename>
 [--median <column>  --medianLog <column>  --mean <column> <true|false> --max <column>  --min <column> --filter <column> <lt|gt> <limit>  
 --outputFormat <csv|xls|xlsx>] --normalize <column> <percentile>


Command line options; order of options does not matter, but an option (preceeded by --) has to be followed by at least one argument:
--inFile (mandatory)
	one or more input files (gpr format); if more than one file is used, all data belonging to a gene with the same name are treated as one dataset
	arguments (1..n): list of file names separated by blanc space
--outFile (optional)
	name of output file
	argument (1): name of output file
	default: out
--filter (optional)
	arguments (3): name of filter column, relation (lower than: lt or greater than: gt), limit
--outputFormat (optional)
	arguments (1-3): xls, xlsx, csv
	default: csv
 --normalize (optional)
	arguments (2): name of the column used for normalizing, percentile

The following options indicate the operations to be performed. All options are optional and each option may occur more than one time. Each option has to be followed by the name of the column (exactly like in the gpr file) containing the data to be processed and in case of --mean a flag indicating if null and NaN should be included. Each calculation will be performed on the set of result data with the same gene name (column "Name" in gpr format). If a filter is applied and none of the datasets for a certain gene name fulfils the filter criteria a NaN will be returned. Values in the gpr file(s) which cannot be read as numbers will be treated as NaN.
 
--median
	Returns the median of the dataset.
--medianLog
	Returns the inverse logarithm of the median calculated from the natural logarithm values of the dataset.
--mean
	Returns the arithmetic mean of the dataset (if null or NaN are not ignored and either is present in the dataset the result will be NaN).
--max
	Returns the maximum of the dataset.
--min
	Returns the minimum of the dataset.
--normalize
	A distribution for all values in the specified column is calculated. Then all datasets whose value in this column is below the calculated percentile are filterd out.
	The values of the following control spots are not used for calculation of the percentile: MY-QC?? ??Ctrl-Pos-555???? Ctrl-Pos-647???? Ctrl-Stringent-555??????Ctrl-Stringent-647???? Empty_NCTRL

If any error occurs during data processing (e.g. non readable numbers, wrong file format, all values of a dataset are filtered...) an error file is generated which contains some error descriptions.
