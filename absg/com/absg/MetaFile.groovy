package com.absg

class MetaFile {
	def title
	def titleWithExt
	def dirPath
	def fullPath

	def inputDirPath
	def buildDirPath

	MetaFile(metaDirName, metaFileName, metaFilePath, buildDirName, inputDirName) {
		this.title = metaFileName
		this.dirPath = "${metaFilePath}/${metaDirName}"
		this.fullPath = "${this.dirPath}/${this.title}.properties"

		this.inputDirPath = "${this.dirPath}/${inputDirName}"
		this.buildDirPath = "${this.dirPath}/${buildDirName}"
	}

	def getFile() {
		new File(this.fullPath)
	}
}