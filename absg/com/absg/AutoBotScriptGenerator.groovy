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
		def lstDirPath = "${META_FILE_PATH}/${metaDirName}/${INPUT_DAT_DIR}"
		def backupExt = ".bak"

		Utils.forEachSubfolder(lstDirPath) { directory ->
			Utils.forEachFileExtension(directory.path, backupExt) { file ->
				def dest = new String(file.path.substring(0, file.path.length() - backupExt.length()))
 				Files.copy(Paths.get(file.path), Paths.get(dest), StandardCopyOption.REPLACE_EXISTING)
			}
		}
	}

	static void main(String[] args) {
		def metaDirName
		if (args.size() > 0) {
			metaDirName = args[0]
		}
		// TODO: comment this line on production
		prepareDATFile(metaDirName)

		start(metaDirName)
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

	static start(metaDirName) {
		def metaFile = new MetaFile(metaDirName, META_FILE_NAME, META_FILE_PATH, BUILD_DIR_NAME, INPUT_DAT_DIR)

		def metaProperties = Utils.loadMetaProperties(metaFile, extraConfigProperties)
		metaProperties.lineSweepToolsDir = Utils.convertToWindowsPath(LST_PATH)

		// Clean up output dir
		purgeDirectory(new File(metaFile.buildDirPath))

		// Create build folder, it may not exist
		new File(metaFile.buildDirPath).mkdir()
		// Perform scan Sub folder file
		Utils.forEachSubfolder(metaFile.inputDirPath) { directory ->
			new File("${metaFile.buildDirPath}/${directory.name}").mkdir()
			new File("${metaFile.buildDirPath}/${directory.name}/ahk").mkdir()
			// Read specific (sub) meta properties for each sub folder
			def subMetaProperties = Utils.loadSubMetaProperties(metaFile, directory)
			// then merge with host meta properties
			def mergeProperties = [:]
			mergeProperties.putAll(metaProperties)
			mergeProperties.putAll(subMetaProperties)
			// read config from operator which may be the Host operator or may not (if sub meta properties has defined)
			def measurementTypeConfig = loadConfig(MT_CONFIG_PATH, mergeProperties.operator)
			println ""
			println "processing folder: ${directory.name}, operator: ${mergeProperties.operator}"
			// Scan for DAT file
			Utils.forEachFileExtension(directory.path, ".dat") { datFile ->
				def instanceConfigProperties = 
					prepareInstanceConfig(datFile, mergeProperties, measurementTypeConfig)

				try {
					generateScript(instanceConfigProperties.outputDir, instanceConfigProperties)
				} catch(MissingPropertyException mpe) {
					println "\tMissing config property in measurement type: " + 
						"${instanceConfigProperties.measurementType.toUpperCase()}"
					println "\t\t${mpe.message}"
				}
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
		Utils.forEachSubfolder(metaFile.inputDirPath) { directory ->
			Utils.forEachFileExtension(directory.path, ".dat") { file ->
				inputFileList << file
			}
		}

		inputFileList
	}

	

	static generateScript(buildDirPath, instanceConfigProperties) {
		def autobotTemplate = new File(BOT_SCRIPT_TEMPLATE_PATH)
		def engine = new GStringTemplateEngine()
		def template = engine.createTemplate(autobotTemplate).make(instanceConfigProperties)
		def autobotScriptOutputDir = "${buildDirPath}/ahk/${instanceConfigProperties.inputName}.ahk"
		def autobotScriptFile = new File(autobotScriptOutputDir)
		autobotScriptFile.text = template.toString()

		def windowsAutobotScriptOutputDir = Utils.convertToWindowsPath(autobotScriptOutputDir)
		def windowsAutohotkeyDir = Utils.convertToWindowsPath(AUTOHOTKEY_APP_PATH)

		def startTime = new Date()
		println "\tprocess file ${instanceConfigProperties.inputName}"
		def prog = "\"${windowsAutohotkeyDir}\" ${windowsAutobotScriptOutputDir}".execute()
		prog.waitForOrKill(WAIT_BOT_SCRIPT_TIME)

		def endTime = new Date()
		println "\tprocess finished in : ${(endTime.getTime() - startTime.getTime()) / 1000} seconds"
		println ""
	}

	static getHitDownForDisplayModeAmount(displayMode) {
		switch(displayMode) {
			case "Return Loss (Negative)":
				return 1
			case "Cable Loss":
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