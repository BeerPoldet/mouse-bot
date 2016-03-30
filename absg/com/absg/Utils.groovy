package com.absg

import groovy.io.FileType
import static groovy.io.FileType.*

class Utils {
	static OPERATOR_AIS  = "AIS"
	static OPERATOR_DTAC = "DTAC"
	static OPERATOR_TRUE = "TRUE"

	static MT_DL = "dl"
	static MT_VL = "vl"
	static MT_RL = "rl"

	static MT_DA = "da"
	static MT_VA = "va"
	static MT_RA = "ra"

	static DAT_FILE_SECTION_SEPARATOR = "_" // file name example "F9-S-1B_F9-A-4-9_DL.dat"
	static META_FILE_PATH = "C:/Users/Poldet/dev/workspaces/mobicat/mouse-bot"
	static META_FILE_NAME = "meta"
	static BUILD_DIR_NAME = "build"
	static INPUT_DAT_DIR = "line_sweep_tools"
	static RESULT_DIR = "result"

	static loadMetaProperties(MetaFile metaFile, extraConfigProperties) {
		def metaProperties = new Properties()
		metaFile.getFile().withInputStream { inputStream ->
			metaProperties.load(inputStream)
		}
		addProperties(metaProperties, metaFile, extraConfigProperties)

		metaProperties
	}

	static addProperties(metaProperties, MetaFile metaFile, extraConfigProperties) {
		extraConfigProperties?.each { key, value ->
			metaProperties[key] = value
		}
	}

	static loadSubMetaProperties(metaFile, subDir) {
		def metaProperties = new Properties()
		def subMetaFullPath = "${metaFile.dirPath}/${metaFile.title}_${subDir.name}.properties"
		def subMetaFile = new File(subMetaFullPath)
		if (subMetaFile.exists()) {
			subMetaFile.withInputStream { inputStream ->
				metaProperties.load(inputStream)
			}
		}

		metaProperties.inputDir = convertToWindowsPath("${metaFile.inputDirPath}/${subDir.name}")
		metaProperties.outputDir = convertToWindowsPath("${metaFile.buildDirPath}/${subDir.name}")

		metaProperties
	}

	static isFileHasExtension(file, extension) {
		getFileTitle(file, true).endsWith(extension)
	}

	static getFileTitle(file, includeExtension = false) {
		file.name.replaceFirst(includeExtension ? ~/\.+$/ : ~/\.[^\.]+$/, '')
	}

	static convertToWindowsPath(path) {
		path.replace('/', "\\")
	}

	static subtitle(fileTitle) {
		def items = fileTitle.split(DAT_FILE_SECTION_SEPARATOR)
		items[0..items.size() - 2].join(DAT_FILE_SECTION_SEPARATOR)
	}

	static getMeasurementType(fileName) {
		def items = fileName.split(DAT_FILE_SECTION_SEPARATOR)
		def type = items[items.size() - 1].toLowerCase()
	}

	static forEachSubfolder(dirPath, closure) {
		new File(dirPath).eachFile (FileType.DIRECTORIES, closure) 
	}

	static forEachFileExtension(path, extension, closure) {
		new File(path).eachFile (FileType.FILES) { file ->
			if (isFileHasExtension(file, extension)) {
				closure.call(file)
			}
		} 
	}
}