package com.absg

class MetaFile {
	def title
	def dirPath
	def fullPath
	def buildDirPath

	MetaFile(metaDirName, metaFileName, metaFilePath, buildDirName) {
		this.title = metaFileName
		this.dirPath = "${metaFilePath}/${metaDirName}"
		this.fullPath = "${this.dirPath}/${this.title}"

		this.buildDirPath = "${this.dirPath}/${buildDirName}"
	}

	def getFile() {
		new File(this.fullPath)
	}
}