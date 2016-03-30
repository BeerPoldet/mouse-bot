'use strict';
require('./utils');
var fs = require('fs');
var XLSX = require('xlsx');
var Workbook = require('workbook');
var PropertiesReader = require('properties-reader');

var SITE_PATH = "C:/Users/Poldet/dev/workspaces/mobicat/mouse-bot";
var TYPE_ANT = "ant";
var TYPE_FEEDER = "feeder";
var args = process.argv.slice(2);
var metaDirName = args[0];

//var workbook = XLSX.readFile('test.xlsx');
var templatePath = "{0}/{1}/template".format(SITE_PATH, metaDirName);
var buildPath = "{0}/{1}/build".format(SITE_PATH, metaDirName);
var resuiltPath = "{0}/{1}/result".format(SITE_PATH, metaDirName);
var metaProperties = PropertiesReader("{0}/{1}/meta.properties".format(SITE_PATH, metaDirName));

fs.readdir(templatePath, function(err, files) {
    for (var i = 0; i < files.length; i++) {
    	var fileName = files[i];
  		// only .xlsm file could go on...
    	if (fileName.indexOf(".xlsm") !== -1) {
    		console.log("filling data info report " + fileName);
	    	// template file path
	    	var filePath = "{0}/{1}".format(templatePath, fileName);
	    	var workbook = XLSX.readFile(filePath, {cellStyles: true});
	    	var sheetNames = workbook.SheetNames;
	    	var sheetList = listSheetByTypeAndFreq(workbook.SheetNames);
	    	
	    	sheetList.forEach(function(sheetItem) {
	    		console.log("\tprocessing in sub folder " + sheetItem.subDir);
	    		var inputFile = "{0}/{1}/{2}.txt".format(buildPath, sheetItem.subDir, sheetItem.type);
	    		fs.readFileSync(inputFile).toString().split('\n').forEach(function (line) {
	    			if (line)
		    			updateCellByLine(workbook, sheetItem.type, sheetItem.subDir, line)
	    		});
	    	});

	    	for (var name in workbook.Sheets) {
	    		var sheet = workbook.Sheets[name];
	    		sheet["B5"] = { v: metaProperties.get('buildingName') };
	    		var items = name.split(" ");
	    		if (items.length >= 1) {
	    			var subDir = items[0].toLowerCase();
	    			if (subDir === TYPE_FEEDER || subDir === "antenna") {
						sheet[subDir === TYPE_FEEDER ? "P5" : "N5"] = 
							{ v: metaProperties.get('patDate') };
	    			}
			    }
	    	}

	    	XLSX.writeFile(workbook, "{0}/{1}".format(resuiltPath, fileName), {cellStyles: true});
	    	console.log("");
    	}
    }
    
});

function updateCellByLine(workbook, type, subDir, line) {
	var items = line.split(" ");
	var key = items[0];

	console.log("\t\tprocessing " + key);

	listWorksheet(workbook, type, subDir).forEach(function(name) {
		var worksheet = workbook.Sheets[name];
		var cellEncodeAddress = findTargetCell(worksheet, key);
		if (cellEncodeAddress != null) {
			
			var keyCellAddress = XLSX.utils.decode_cell(cellEncodeAddress);
			fillData(worksheet, type, keyCellAddress, items);
		}
	});
}

function fillData(worksheet, type, keyCellAddress, items) {
	if (type === TYPE_FEEDER) {
		var length = items[1];
		fillDataAt(worksheet, rightShiftFromAddress(keyCellAddress, 4), length);

		var returnLoss = "-{0}".format(items[2]);
		fillDataAt(worksheet, rightShiftFromAddress(keyCellAddress, 5), returnLoss);

		var startPoint = items[3];
		fillDataAt(worksheet, rightShiftFromAddress(keyCellAddress, 6), startPoint);

		var endPoint = items[4];
		fillDataAt(worksheet, rightShiftFromAddress(keyCellAddress, 7), endPoint);

		var vswr = items[5];
		fillDataAt(worksheet, rightShiftFromAddress(keyCellAddress, 8), vswr);
	} else {
		var returnLoss = "-{0}".format(items[1]);
		fillDataAt(worksheet, rightShiftFromAddress(keyCellAddress, 4), returnLoss);

		var connectionPoint = items[2];
		fillDataAt(worksheet, rightShiftFromAddress(keyCellAddress, 5), connectionPoint);

		var vswr = items[3];
		fillDataAt(worksheet, rightShiftFromAddress(keyCellAddress, 6), vswr);
	}
}

function rightShiftFromAddress(cellAddress, shiftTime) {
	return { c: (cellAddress.c + shiftTime), r: cellAddress.r };
}

function fillDataAt(worksheet, targetCellAddress, data) {
	var targetCell = { v: data };
	worksheet[XLSX.utils.encode_cell(targetCellAddress)] = targetCell;
}

function findTargetCell(worksheet, findKey) {
	for (var key in worksheet) {
		var cellData = worksheet[key];
		if (cellData.v === findKey) {
			return key;
		}
	}
	return null;
}

function listWorksheet(workbook, type, subDir) {
	return workbook.SheetNames.filter(function(name) {
		var sheetItem = sheetItemFromName(name);
		return type === sheetItem.type && subDir === sheetItem.subDir;
	});
}

function listSheetByTypeAndFreq(sheetNames) {
	var list = [];
	sheetNames.forEach(function(name) {
		var sheetItem = sheetItemFromName(name);
		if (sheetItem != null) {
			if (list.find(function(item) { 
				return  (item.type === sheetItem.type && 
				item.subDir === sheetItem.subDir) } ) == undefined)

				list.push(sheetItem);
		}
	});

	return list;
}

function sheetItemFromName(name) {
	var items = name.split(" ");
	var type = items[0].toLowerCase();
	var subDir = items[1];
	if (type === "feeder" || type === "antenna") {
		if (type === "antenna") {
			type = TYPE_ANT;
		}
		return {type: type, subDir: subDir};
	}
	return null;
}

function listCellByRange(start, end, worksheet) {
	var list = [];
	/* Iterate through each element in the structure */
	iterateCellRange(start, end, worksheet, function(cellAddress, cellEncodeAddress, cellData) {
		list.push({ address: cellAddress, encodeAddress: cellEncodeAddress, data: cellData });
	});

	return list;
}

function iterateCellRange(start, end, worksheet, closure) {
	var range = { s: start, e: end };//A1:A5
	for (var R = range.s.r; R <= range.e.r; ++R) {
		for (var C = range.s.c; C <= range.e.c; ++C) {
			var cellAddress = { c: C, r: R };
			var cellEncodeAddress = XLSX.utils.encode_cell(cellAddress);
			var cellData
			if (worksheet != null)
				cellData = cellDataByAddress(cellAddress, worksheet);
			closure(cellAddress, cellEncodeAddress, cellData);
		}
	}
}

function cellDataByAddress(cellAddress, worksheet) {
	var cellEncodeAddress = XLSX.utils.encode_cell(cellAddress);
	return worksheet[cellEncodeAddress];
}

function createSheet(cellList) {
	var sheet = {};
	cellList.forEach(function(cell) { 
		var shiftedAddress = cell.address
		shiftedAddress.c += 4
		var shiftedEncodeAddress = XLSX.utils.encode_cell(shiftedAddress);
		sheet[shiftedEncodeAddress] = cell.data
	});

	var range = {s: {c:0, r:0 }, e: {c:500, r:500}};
	sheet['!ref'] = XLSX.utils.encode_range(range);

	return sheet;
}