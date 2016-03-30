package com.absg

import static com.absg.Utils.*

class VerifyWorker {
	static void main(String[] args) {
		verify(args[0])
	}

	static verify(metaDirName) {
		def metaFile = new MetaFile(metaDirName, META_FILE_NAME, META_FILE_PATH, BUILD_DIR_NAME, INPUT_DAT_DIR)

		Utils.forEachSubfolder(metaFile.inputDirPath) { directory ->
			def datAmount = 0
			def csvAmount = 0
			Utils.forEachFileExtension(directory.path, ".dat") { datFile ->
				datAmount++
			}

			Utils.forEachFileExtension("${metaFile.buildDirPath}/${directory.name}", ".csv") { datFile ->
				csvAmount++
			}

			println ""
			println "${directory.name} : ${datAmount} DAT => ${csvAmount} CSV"
			println "\tverify result\t" + (datAmount == csvAmount ? "OK" : "FAILED")
		}
	}
}