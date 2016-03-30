package com.absg

import groovy.io.FileType
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

import static com.absg.Utils.*

class SummaryWorker {
	static void main(String[] args) {
		def metaDirName
		if (args.size() > 0) {
			metaDirName = args[0]
		}

		start(metaDirName)
	}

	static SUMMARY_FEEDER_FILE_NAME = "feeder.txt"
	static SUMMARY_ANT_FILE_NAME = "ant.txt"

	static start(metaDirName) {
		def metaFile = new MetaFile(metaDirName, META_FILE_NAME, META_FILE_PATH, BUILD_DIR_NAME, INPUT_DAT_DIR)

		def metaProperties = Utils.loadMetaProperties(metaFile, null)

		Utils.forEachSubfolder(metaFile.buildDirPath) { directory ->
			def feederTextFilePath = "${metaFile.buildDirPath}/${directory.name}/${SUMMARY_FEEDER_FILE_NAME}"
			def antTextFilePath = "${metaFile.buildDirPath}/${directory.name}/${SUMMARY_ANT_FILE_NAME}"
			
			def summaryFeederFile = new File(feederTextFilePath)
			def summaryAntFile = new File(antTextFilePath)
			summaryFeederFile.text = ""
			summaryAntFile.text = ""

			// Read specific (sub) meta properties for each sub folder
			def subMetaProperties = Utils.loadSubMetaProperties(metaFile, directory)
			// then merge with host meta properties
			def mergeProperties = [:]
			mergeProperties.putAll(metaProperties)
			mergeProperties.putAll(subMetaProperties)

			def feederTestResults = [:]
			def antennaTestResults = [:]

			println ""
			println "summarizing directory: ${directory.name}, operator: ${mergeProperties.operator}"

			Utils.forEachFileExtension(directory.path, ".csv") { file ->
				def summaryType = getSummaryType(Utils.getFileTitle(file)) // FEEDER or ANTENNA
				def measurementTypeForLength = 
					getMeasurementTypeForLength(mergeProperties.operator, summaryType)
				def measurementTypeForVSWR = 
					getMeasurementTypeForVSWR(mergeProperties.operator, summaryType)

				println "\tread data from ${file.name}"			
				switch(summaryType) {
					case FEEDER_TYPE:
						def feederNumber = Utils.subtitle(Utils.getFileTitle(file))
						if (!feederTestResults[feederNumber]) {
							def items = feederNumber.split(DAT_FILE_SECTION_SEPARATOR)
							feederTestResults[feederNumber] = [
								feederNumber: feederNumber,
								startPoint: items[0],
								endPoint: items[1]
							]
						}

						def markerData = getMarkerData(file, "M1")

						if (Utils.getMeasurementType(Utils.getFileTitle(file)) == measurementTypeForLength) {
							feederTestResults[feederNumber].length = markerData.freq
						} else if (Utils.getMeasurementType(Utils.getFileTitle(file)) == measurementTypeForVSWR) {
							feederTestResults[feederNumber].vswr = markerData.value
						} else if (Utils.getMeasurementType(Utils.getFileTitle(file)) == MT_RL) {
							feederTestResults[feederNumber].returnLoss = markerData.value
						}
					break
					case ANT_TYPE:
						def antennaNumber = Utils.subtitle(Utils.getFileTitle(file))
						if (!antennaTestResults[antennaNumber]) {
							def items = antennaNumber.split(DAT_FILE_SECTION_SEPARATOR)
							antennaTestResults[antennaNumber] = [
								antennaNumber: items[1],
								connectionPoint: items[0]
							]
						}

						def markerData = getMarkerData(file, "M1")
						if (Utils.getMeasurementType(Utils.getFileTitle(file)) == measurementTypeForVSWR) {
							// antennaTestResults[antennaNumber].length = markerData.freq
							antennaTestResults[antennaNumber].vswr = markerData.value
						} else  if (Utils.getMeasurementType(Utils.getFileTitle(file)) == MT_RA) {
							antennaTestResults[antennaNumber].returnLoss = markerData.value
						}
					break
				}
			}

			feederTestResults.each { feederNumber, data ->
				writeLine(summaryFeederFile, String.format("%s %s %s %s %s %s",
					data.feederNumber, 
					data.length ?: "", 
					data.returnLoss ?: "", 
					data.startPoint, 
					data.endPoint, 
					data.vswr ?: "")
				)
			}

			antennaTestResults.each { antennaNumber, data ->
				writeLine(summaryAntFile, String.format("%s %s %s %s",
					data.antennaNumber, 
					data.returnLoss ?: "", 
					data.connectionPoint, 
					data.vswr ?: "")
				)
			}
		}
		
		
		// writeLine(summaryAntFile, "${feederNumber}")
	}

	static getMarkerData(file, marker) {
		def line = findLineCSVforKey(file, marker)
		def items = line.split(",")
		// getting VSRW data
		def freq = removeDoubleQuote(items[1]) // remove double quote
		def value = removeDoubleQuote(items[2]) // remove double quote
		[freq: freq, value: value]
	}

	static findLineCSVforKey(file, key) {
		def result
		file.eachLine { line ->
			def k = line.split(",")[0]
			def keyLine = removeDoubleQuote(k) // remove double quote out
			if (key == keyLine) {
				result = line
				return
			}
		}

		return result
	}

	static removeDoubleQuote(text) {
		// text sample "text"
		return text[1..-2]
	}

	static writeLine(file, text) {
		file << "${text}\n"
	}

	static listCSVFile(csvFilesDir) {
		def fileList = []
		new File(csvFilesDir).eachFile (FileType.FILES) { file ->
			if (Utils.isFileHasExtension(file, ".csv"))
				fileList << file
		}
		fileList
	}

	static FEEDER_TYPE = "feeder"
	static ANT_TYPE = "ant"

	static getSummaryType(fileTitle) {
		def items = fileTitle.split(DAT_FILE_SECTION_SEPARATOR)
		def type = items[items.size() - 1].toLowerCase()
		def lastTypeLetter = type[-1]
		
		// RL VL DL for feeder 
		// DA RA for ant
		lastTypeLetter == "l" ? FEEDER_TYPE : ANT_TYPE
	}

	static getMeasurementTypeForLength(operator, summaryType) {
		if (operator == OPERATOR_AIS)
			return summaryType == FEEDER_TYPE ? MT_DL : MT_DA

		return summaryType == FEEDER_TYPE ? MT_DL : MT_DA
	}

	static getMeasurementTypeForVSWR(operator, summaryType) {
		if (operator == OPERATOR_AIS)
			return summaryType == FEEDER_TYPE ? MT_DL : MT_DA

		return summaryType == FEEDER_TYPE ? MT_VL : MT_VA
	}
}