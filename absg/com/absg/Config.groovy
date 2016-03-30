da {
	title = "Distance-to-fault"
	displayMode = "DTF-VSWR" // "DTF-VSWR" or "Return Loss (Negative)"
	distanceUnits = "Meters" // Feet or Meters
	limit = "Single Value"
	m1 = "Peak" // "Peak" or "Valley" or $value
	m2 = 0	    // "Peak" or "Valley" or $value
	miscLine = "Thin Line" // "Thin Line" or "Medium" or "Thick Line"
}

dl {
	title = "Distance-to-fault"
	displayMode = "DTF-VSWR" // "DTF-VSWR" or "Return Loss (Negative)"
	distanceUnits = "Meters" // Feet or Meters
	limit = "Single Value"
	m1 = "Peak" // "Peak" or "Valley" or $value
	m2 = 0	    // "Peak" or "Valley" or $value
	miscLine = "Thin Line" // "Thin Line" or "Medium" or "Thick Line"
}

ra {
	title = "Distance-to-fault"
	displayMode = "Return Loss (Negative)" // "DTF-VSWR" or "Return Loss (Negative)"
	distanceUnits = "Meters" // Feet or Meters
	limit = "Single Value"
	m1 = "Peak" // "Peak" or "Valley" or $value
	m2 = "Valley"	    // "Peak" or "Valley" or $value
	miscLine = "Thin Line" // "Thin Line" or "Medium" or "Thick Line"
}

rl {
	title = "Return Loss"
	displayMode = "Return Loss (Negative)" // "DTF-VSWR" or "Return Loss (Negative)"
	distanceUnits = "Meters" // Feet or Meters
	limit = "Single Value"
	m1 = "Peak" // "Peak" or "Valley" or $value
	m2 = "Valley"	    // "Peak" or "Valley" or $value
	miscLine = "Thin Line" // "Thin Line" or "Medium" or "Thick Line"
}

va {
	title = "VSWR"
	displayMode = "DTF-VSWR" // "DTF-VSWR" or "Return Loss (Negative)"
	distanceUnits = "Meters" // Feet or Meters
	limit = "Single Value"
	m1 = "Peak" // "Peak" or "Valley" or $value
	m2 = "0"	    // "Peak" or "Valley" or $value
	miscLine = "Thin Line" // "Thin Line" or "Medium" or "Thick Line"
}

vl {
	title = "VSWR"
	displayMode = "DTF-VSWR" // "DTF-VSWR" or "Return Loss (Negative)"
	distanceUnits = "Meters" // Feet or Meters
	limit = "Single Value"
	m1 = "Peak" // "Peak" or "Valley" or $value
	m2 = "0"	    // "Peak" or "Valley" or $value
	miscLine = "Thin Line" // "Thin Line" or "Medium" or "Thick Line"
}

cl {

}

environments {
	ais {
		da {
			subtitleSuffix = " (VA)"
			top = 1.5
			bottom = 1
			singleLimitLineValue = 1.35  // "off" or $value
		}

		dl {
			subtitleSuffix = " (VL)"
			top = 1.5
			bottom = 1
			singleLimitLineValue = 1.35  // "off" or $value
		}

		ra {
			subtitleSuffix = " (RA)"
			top = -10
			bottom = -60
			singleLimitLineValue = -15.6  // "off" or $value
		}

		rl {
			subtitleSuffix = " (RL)"
			top = -10
			bottom = -60
			singleLimitLineValue = -15.6  // "off" or $value
		}
	}

	dtac {
		da {
			subtitleSuffix = " (ANT)"
			top = 1.35
			bottom = 0.95
			singleLimitLineValue = 1.3  // "off" or $value
		}

		dl {
			subtitleSuffix = " (Dummy Load)"
			top = 1.35
			bottom = 0.95
			singleLimitLineValue = 1.3  // "off" or $value
		}

		ra {
			subtitleSuffix = " (ANT)"
			top = -10
			bottom = -54
			singleLimitLineValue = -18  // "off" or $value
		}

		rl {
			subtitleSuffix = " (Dummy Load)"
			top = -10
			bottom = -54
			singleLimitLineValue = -18  // "off" or $value
		}

		va {
			subtitleSuffix = " (ANT)"
			top = 1.35
			bottom = 0.95
			singleLimitLineValue = 1.3  // "off" or $value
		}

		vl {
			subtitleSuffix = " (Dummy Load)"
			top = 1.35
			bottom = 0.95
			singleLimitLineValue = 1.3  // "off" or $value
		}

		cl {
			title = "Cable Loss"
			subtitleSuffix = " (Short Test)"
			displayMode = "Cable Loss" // "DTF-VSWR" or "Return Loss (Negative)"
			top = 0
			bottom = -5
			distanceUnits = "Meters" // Feet or Meters
			limit = "Single Value"
			singleLimitLineValue = "off" // "off" or $value 
			m1 = "Peak" // "Peak" or "Valley" or $value
			m2 = 0	    // "Peak" or "Valley" or $value
			miscLine = "Thin Line" // "Thin Line" or "Medium" or "Thick Line"
		}
	}

	"true" {
		da {
			subtitleSuffix = " (ANT)"
			top = 1.35
			bottom = 0.95
			singleLimitLineValue = 1.3  // "off" or $value
		}

		dl {
			subtitleSuffix = " (Dummy Load)"
			top = 1.35
			bottom = 0.95
			singleLimitLineValue = 1.3  // "off" or $value
		}

		ra {
			subtitleSuffix = " (ANT)"
			top = -10
			bottom = -54
			singleLimitLineValue = -17  // "off" or $value
		}

		rl {
			subtitleSuffix = " (Dummy Load)"
			top = -10
			bottom = -54
			singleLimitLineValue = -17  // "off" or $value
		}

		va {
			subtitleSuffix = " (ANT)"
			top = 1.35
			bottom = 0.95
			singleLimitLineValue = 1.3  // "off" or $value
		}

		vl {
			subtitleSuffix = " (Dummy Load)"
			top = 1.35
			bottom = 0.95
			singleLimitLineValue = 1.3  // "off" or $value
		}
	}
}