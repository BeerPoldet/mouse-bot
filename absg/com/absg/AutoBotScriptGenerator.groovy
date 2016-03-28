package com.absg

import groovy.text.*
import groovy.io.FileType
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

import static com.absg.Utils.*

class AutoBotScriptGenerator {

	// only for dev env purpose
	static prepareDATFile(metaDirName) {
		def metaFileDir = "${META_FILE_PATH}/${metaDirName}"
		def backupExt = ".bak"

		new File(metaFileDir).eachFile (FileType.FILES) { file ->
			if (Utils.getFileTitle(file, true).endsWith(backupExt)) {
				def dest = new String(file.path.substring(0, file.path.length() - backupExt.length()))
				Files.copy(Paths.get(file.path), Paths.get(dest), StandardCopyOption.REPLACE_EXISTING)
			}
		}
	}

	static void main(String[] args) {
		// sample setup
		def metaFileName = "meta.properties"
		def operator = "dtac"
		def metaDirName = "f1s1"

		if (args.size() > 0) {
			metaFileName = args[0]
			metaDirName = args[1]
		}
		// TODO: comment this line on production
		prepareDATFile(metaDirName)

		start(metaDirName, metaFileName)
	}

	// Configuration
	static MT_CONFIG_PATH = "C:/Users/Poldet/dev/workspaces/mobicat/mouse-bot/absg/com/absg/Config.groovy"
	static BOT_SCRIPT_TEMPLATE_PATH = "C:/Users/Poldet/dev/workspaces/mobicat/mouse-bot/absg/com/absg/autobot.ahk.template"
	static AUTOHOTKEY_APP_PATH = "C:/Program Files/AutoHotkey/AutoHotkey.exe"
	static LST_PATH = "C:/Program Files (x86)/Anritsu/Line Sweep Tools"

	static WAIT_BOT_SCRIPT_TIME = 30 * 1000 // wait script to run until 30 sec to terminate bot

	// Script Config
	static extraConfigProperties = [
		offsetX: 3,
		offsetY: 26
	]

	static start(metaDirName, metaFileName) {
		def metaFile = new MetaFile(metaDirName, metaFileName, META_FILE_PATH, BUILD_DIR_NAME)

		def metaProperties = Utils.loadMetaProperties(metaFile, extraConfigProperties)
		metaProperties.lineSweepToolsDir = Utils.convertToWindowsPath(LST_PATH)
		
		def measurementTypeConfig = loadConfig(MT_CONFIG_PATH, metaProperties.operator)

		// Perform scan DAT file
		def inputFileList = listDATFile(metaFile)

		// Clean up output dir
		purgeDirectory(new File(metaProperties.outputDir))

		// Create build folder, it may not exist
		new File(metaFile.buildDirPath).mkdir()

		inputFileList.eachWithIndex { datFile, i -> 
			def instanceConfigProperties = 
				prepareInstanceConfig(datFile, metaProperties, measurementTypeConfig)

			try {
				generateScript(metaFile.buildDirPath, instanceConfigProperties)
			} catch(MissingPropertyException mpe) {
				println "Missing config property in measurement type: " + 
					"${instanceConfigProperties.measurementType.toUpperCase()}"
				println "\t${mpe.message}"
			}
		}
	}

	static prepareInstanceConfig(datFile, metaProperties, measurementTypeConfig) {
		def instanceConfigProperties = [:]
		// put meta properties
		instanceConfigProperties.putAll(metaProperties) 
		// put config properties
		instanceConfigProperties.putAll(getMeasurementConfig(measurementTypeConfig, datFile))
		// add instance config properties
		instanceConfigProperties.subtitle = Utils.subtitle(Utils.getFileTitle(datFile))
		instanceConfigProperties.measurementType = Utils.getMeasurementType(Utils.getFileTitle(datFile))
		instanceConfigProperties.inputName = Utils.getFileTitle(datFile)
		instanceConfigProperties.hitDownForDisplayModeAmount = 
			getHitDownForDisplayModeAmount(instanceConfigProperties.displayMode)

		instanceConfigProperties
	}

	static getMeasurementConfig(measurementTypeConfig, file) {
		measurementTypeConfig[getMeasurementType(Utils.getFileTitle(file))]
	}

	static loadConfig(scriptConfigDir, operator) {
		new ConfigSlurper(operator.toLowerCase())
			.parse(new File(scriptConfigDir).toURI().toURL())
	}

	static listDATFile(metaFile) {
		def inputFileList = []
		new File(metaFile.dirPath).eachFile (FileType.FILES) { file ->
			if (Utils.isFileHasExtension(file, ".dat"))
				inputFileList << file
		}

		inputFileList
	}

	

	static generateScript(buildDirPath, instanceConfigProperties) {
		def autobotTemplate = new File(BOT_SCRIPT_TEMPLATE_PATH)
		def engine = new GStringTemplateEngine()
		def template = engine.createTemplate(autobotTemplate).make(instanceConfigProperties)
		def autobotScriptOutputDir = "${buildDirPath}/${instanceConfigProperties.inputName}.ahk"
		def autobotScriptFile = new File(autobotScriptOutputDir)
		autobotScriptFile.text = template.toString()

		def windowsAutobotScriptOutputDir = Utils.convertToWindowsPath(autobotScriptOutputDir)
		def windowsAutohotkeyDir = Utils.convertToWindowsPath(AUTOHOTKEY_APP_PATH)

		def startTime = new Date()
		println "start process script for ${instanceConfigProperties.inputName}"
		def prog = "\"${windowsAutohotkeyDir}\" ${windowsAutobotScriptOutputDir}".execute()
		prog.waitForOrKill(WAIT_BOT_SCRIPT_TIME)

		def endTime = new Date()
		println "process finished in : ${(endTime.getTime() - startTime.getTime()) / 1000} seconds"
		println "----------"
	}

	static getHitDownForDisplayModeAmount(displayMode) {
		switch(displayMode) {
			case "Return Loss (Negative)":
				return 2
			case "DTF-VSWR":
				return 3
		}
		return 0
	}

	static purgeDirectory(fileDirectory) {
	    fileDirectory.listFiles().each { file ->
	        if (file.isDirectory()) 
	        	purgeDirectory(file);
	        if (isFileOutput(Utils.getFileTitle(file, true)))
	        	file.delete()
	    }
	}

	static isFileOutput(fileNameWithExtension) {
		fileNameWithExtension.contains(".ahk") || 
		fileNameWithExtension.contains(".csv") || 
		fileNameWithExtension.contains(".jpg")
	}
}