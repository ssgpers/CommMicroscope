#from _winreg import *
from ij import IJ
from ij.plugin import Converter
from trainableSegmentation import WekaSegmentation
from fiji.threshold import Auto_Local_Threshold
import WindowsRegistry
import time

test_filename = "D:/data/tischi/3metaphase.tif"
test_filename = "D:/data/tischi/ffff/dsfgsdfg.mdb/_R1.lsm"
test_filename = "D:/data/tischi/_R1.tif"
test_filename = "D:/data/tischi/_R1.lsm"
test_filename = "Z:/software/micropilot/Tischi-FIJI/testimages/3metaphase.tif"
test_filename = "Y:/Tischi/projects/AndreaBoni_Ellenberg/data/_L1_R1.lsm"
test_filename = "Z:/Tischi/projects/JuliaRoberti_Ellenberg/aaa/_L1_R1.lsm"

#test_filename = "D:/data/iliana/training_06_liveh2bcherry_tubgfp/plate/well/_L9_R1.lsm"
#test_filename = "D:/tischi/_L2_R6.lsm"

#test_filename = "Z:/Tischi/projects/Iliana_greeceMargieVisitor_cellDivision/metaphase_orientation.mdb/3metaphase.tif"

LSM510_CODE_ALLWAIT = "0"
LSM510_CODE_READIMAGE = "1"
LSM510_CODE_NO_OBJECT_FOUND = "2"
LSM510_CODE_OBJECT_FOUND = "3"


LSM780_CODE_NO_OBJECT_FOUND = "2"
LSM780_CODE_OBJECT_FOUND_STORE = "4"
LSM780_CODE_OBJECT_FOUND_AND_START_IMAGING = "5"

LSM780_CODE_VALID_IMAGE = "valid_image"
LSM780_CODE_INVALID_IMAGE = "invalid_image"

LSM510_CODE_READIMAGE_HIGHRES = "10"


LSM510 = 0
LSM780 = 1



#  LSM510
# "Code 1: MicroPilot should do something"
# "Code 2: No classified objects."
# "Code 3: Classified object found."

def txt_out(text):
	IJ.log(""+text)
	#IJ.log("\\Update"+line_number":"+text)
	

def get_imageFile_from_microscope(winReg_location, winReg_separator, winReg_waitIntervall):
	txt_out("waiting for microscope...")

	wr = WindowsRegistry()
	#reg = wr.writeRegistry(winReg_location,"Code","0")
	#reg = wr.writeRegistry(winReg_location,"filepath",test_filename)
	#txt_out("write: "+reg)

	while True:
		# check microscope status 
		time.sleep(winReg_waitIntervall)
		
		output = wr.readRegistry(winReg_location,"Code")
		#txt_out("microscope status: " + str(output))
		#winReg_address, winReg_name, winReg_type, winReg_value =  output.split(winReg_separator)
		winReg_rest, winReg_value = output.split(winReg_separator)
		code = winReg_value.strip()
		if (code==LSM510_CODE_READIMAGE) or (code==LSM510_CODE_READIMAGE_HIGHRES) :
			txt_out("microscope requesting action! obtaining image file name...")
			output = wr.readRegistry(winReg_location,"filepath")
			#winReg_address, winReg_name, winReg_type, winReg_value =  output.split(winReg_separator)
			winReg_rest, winReg_value = output.split(winReg_separator)
			image_file = winReg_value.strip()  # get rid of the line feed and stuff like this
			txt_out("image file name: " + image_file)
			txt_out("code: " + code)
			# tell all programs to wait
			reg = wr.writeRegistry(winReg_location,"Code",LSM510_CODE_ALLWAIT)
			break
			
	return code, image_file

	
def run():

	# project = "andrea_boni_ellenberg"
	project = "julia_roberti_ellenberg"
	
	# running modalities
	max_images_to_analyse = 10000 #10000
	communicateWithMicroscope = True
	#winReg_separator = "    "
	winReg_separator = "REG_SZ"
	winReg_waitIntervall = 1
	microscope = LSM780 # LSM510
	#winReg_location = "HKLM\SOFTWARE\Test"
	winReg_location = "HKCU\SOFTWARE\VB and VBA Program Settings\OnlineImageAnalysis\macro"

	if project == "andrea_boni_ellenberg":
		particles_area_min = 3750    # divide by 4 for 512x512
		particles_area_max = 12500    # divide by 4 for 512x512
		particles_minCirc = 0.5
		particles_maxCirc = 1.0
		particles_threshold_method = "globalOtsuThreshold"
		particle_selectionMode = "nuclear_rim_intensity"
		iSlice_particles = 2
		iSlice_measurement = 1
		meanIntensThreshold = 30
		
		nPixShrink = 2
		nPixDilate = 10
		minCellsPerImage = 1 # required cells per image
		minCellsPerWell = 4 # required cells per well 
		drawRings = False # True or False !!needs to be False for the real measurement !!
		
	elif project == "julia_roberti_ellenberg":#  !!!!!!!!!!!CHANGE PARAMETERS HERE!!!!!!!!!!!!!!!!!
		particles_area_min = 1001  #  depends whether you use 512x512 or something else
		particles_area_max = 5000
		particles_minCirc = 0.0 # reject non-round particles
		particles_maxCirc = 1.0 
		particles_threshold_method = "globalOtsuThreshold"
		particle_selectionMode = "nuclear_intensity"
		iSlice_particles = 2 # channel containing the particle marker
		iSlice_measurement = 1  # channel to be measured 
		intensityThreshold = 300   #  if depends on microscope settings
		criterium = "mean_intensity" # !!! intensityThreshold is the mean
		# criterium = "total_intensity" # !!! intensityThreshold is the total
						
		
	else:
		particles_area_min = 15000
		particles_area_max = 50000 
		particles_area_min = 50
		particles_minCirc = 0.0
		particles_maxCirc = 1.0
		particles_threshold_method = "autoLocalThreshold"
		particle_selectionMode = "classification"

		
	if particle_selectionMode == "classification":
		# select classifier file
		classifier_file = "/Users/tischi/Documents/40x_widefield_cherry_bg_interphase_mitosis.model.model"
		classifier_file = "Z:/software/micropilot/Tischi-FIJI/testimages/3metaphase.model"
		
		#classifier_file = "D:/data/iliana/training_06_liveh2bcherry_tubgfp/classifier.model"
		ws = WekaSegmentation()  # create an instance
		ws.loadClassifier(classifier_file) # load classifier

	
	for iImageAnalyzed in range(max_images_to_analyse):

		# txt_out(str(iImageAnalyzed))
		
		if communicateWithMicroscope:
			code, image_file = get_imageFile_from_microscope(winReg_location, winReg_separator, winReg_waitIntervall)
		else:
			code  = LSM510_CODE_READIMAGE			
			image_file = test_filename

		# prepare output
		sX = "0" 
		sY = "0" 
		sOrientation = "0"

		
		if project == "andrea_boni_ellenberg":
			pass  # parameters have been set above
		elif code == LSM510_CODE_READIMAGE:
			particles_radius = 50 # ask the user
			particles_threshold = 2 # ask the user (pixels > mean + threshold*sd)
		elif code == LSM510_CODE_READIMAGE_HIGHRES:
			particles_radius = 4*50 # ask the user
			particles_threshold = 2 # ask the user (pixels > mean + threshold*sd)

		
		# clean up for new stuff **********************
		
		# close all open windows
		if WindowManager.getIDList() != None:
			#print "WindowManager.getIDList :"
			#print WindowManager.getIDList()
			for iImage in WindowManager.getIDList():
				imp = WindowManager.getImage(iImage)
				imp.changes = False
				#imp.close()
				imp.getWindow().close()
		
		
		# clean up old ROIs
		if RoiManager.getInstance() != None:
			RoiManager.getInstance().runCommand("reset")
		 	
		# end of clean up *****************************
		
		# run("Bio-Formats Importer", "  open=D:\\data\\tischi\\_R1.lsm color_mode=Default view=Hyperstack stack_order=XYCZT");
		#imp_image_tmp = ImagePlus(image_file) 
		#imp_image_tmp.show()
		
		# OPEN THE IMAGE
		ImagePlus(image_file).show() # todo: check for errors while opening!!
		imp_image = IJ.getImage()
		IJ.run(imp_image, "Properties...", "unit=pix pixel_width=1 pixel_height=1 voxel_depth=1 origin=0,0")

	

		# find particles
		txt_out("finding particles...")

		# get/generate/extract the image containing the particles
		if project == "andrea_boni_ellenberg":
			imp_image.setSlice(iSlice_particles)
			#imp_particles = imp_image.duplicate()
			# todo; how to only duplicate the current slice??
			IJ.run(imp_image, "Duplicate...", "title=im_particles");
			imp_particles = IJ.getImage()
		elif project == "julia_roberti_ellenberg":
			IJ.run(imp_image, "Z Project...", "projection=[Max Intensity]");
			imp_zMax = IJ.getImage()
			imp_zMax.setSlice(iSlice_particles)
			IJ.run(imp_zMax, "Duplicate...", "title=im_particles");
			imp_particles = IJ.getImage()
		else: 
			imp_particles = None
		
		 
		# smooth
		IJ.run(imp_particles, "Median...", "radius=5 slice");

		# threshold
		txt_out("thresholding...")
		imp_mask = imp_particles.duplicate()
		IJ.run(imp_mask, "8-bit", "")

		if particles_threshold_method == "globalFixedThreshold":
			ip = imp_mask.getProcessor()
			ip.setThreshold(particles_threshold, 255, ImageProcessor.NO_LUT_UPDATE)
			IJ.run(imp_mask, "Convert to Mask", "")
		if particles_threshold_method == "globalOtsuThreshold":
			IJ.run(imp_mask, "Auto Threshold", "method=Otsu white")
		elif particles_threshold_method == "autoLocalThreshold":
			alt = Auto_Local_Threshold()
			alt.exec(imp_mask,"Niblack",particles_radius,particles_threshold,0.0,True)


		# post-processing of the binary image (e.g. watershed touching objects)

	
		imp_mask.show()

		
		# Create a ParticleAnalyzer, with arguments:
		# 1. options (could be SHOW_ROI_MASKS, SHOW_OUTLINES, SHOW_MASKS, SHOW_NONE, ADD_TO_MANAGER, and others; combined with bitwise-or)
		# 2. measurement options (see [http://rsb.info.nih.gov/ij/developer/api/ij/measure/Measurements.html Measurements])
		# 3. a ResultsTable to store the measurements
		# 4. The minimum size of a particle to consider for measurement
		# 5. The maximum size (idem)
		# 6. The minimum circularity of a particle
		# 7. The maximum circularity
		table = ResultsTable()
		pa = ParticleAnalyzer(ParticleAnalyzer.ADD_TO_MANAGER | ParticleAnalyzer.EXCLUDE_EDGE_PARTICLES, Measurements.CENTROID | Measurements.CENTER_OF_MASS | Measurements.ELLIPSE | Measurements.AREA, table, particles_area_min, Double.POSITIVE_INFINITY, particles_minCirc, particles_maxCirc)
		pa.setHideOutputImage(False)

			
		if pa.analyze(imp_mask):
			pass 
		else:
			print "There was a problem in analyzing the particles"
			return()


		# initialise 
		nGoodParticles = 0
		iBestParticle = 0
						
		if (RoiManager.getInstance() != None): # found a particle
			
			if (len(RoiManager.getInstance().getRoisAsArray()) > 0):  # found a particle
		
				# todo: show the outlines on the original grayscale image
				#imp = pa.getOutputImage()
				
				#table.show("")
				
				# find "the best" particle
				if particle_selectionMode == "classification":
					ws.setTrainingImage(imp_image)
					#ws.loadClassifier(classifier_file)
					
					myClassNumber = 0 # ask the user	
					
					#/**     * Apply current classifier to a given image.     *     
					#* @param imp image (2D single image or stack)     
					#* @param numThreads The number of threads to use. Set to zero for     
					#* auto-detection.     
					#* @param probabilityMaps create probability maps for each class instead of     
					#* a classification     
					#* @return result image     
					#*/    public ImagePlus applyClassifier(final ImagePlus imp, int numThreads, final boolean probabilityMaps)
					IJ.log("Searching for class "+str(ws.getClassLabel(myClassNumber)))
					imp_probabilities = ws.applyClassifier(imp_image,0,True)
					
					# show the classification results and rename the images in the stack to show the class names
					stack = imp_probabilities.getStack()
					for iClass in range(0,imp_probabilities.getImageStackSize()):
						stack.setSliceLabel(ws.getClassLabel(iClass), iClass+1) 
					imp_probabilities.setStack(stack)
					imp_probabilities.setSlice(myClassNumber+1) 
					imp_probabilities.show()
					
					
					# Create a new list to store the mean intensity values of each class:
					means = [] 
					classification_max = 0
					imp_myClass = ImagePlus("myClass",imp_probabilities.getProcessor().duplicate())
					for i, roi in enumerate(RoiManager.getInstance().getRoisAsArray()):
						imp_myClass.setRoi(roi)
						stats = imp_myClass.getStatistics(Measurements.MEAN)
						if stats.mean > classification_max:
							classification_max = stats.mean
							iBestParticle = i
							
						table.setValue("classification", i, stats.mean) 
		
		
				elif particle_selectionMode == "nuclear_rim_intensity":
					
					if project == "julia_roberti_ellenberg":
						pass
					else:
						imp= imp_image

					imp.setSlice(iSlice_measurement)
					IJ.run(imp, "Duplicate...", "title=signal");
					imp_measure = IJ.getImage()
						
					
					# loop through the particles
					for i, roi in enumerate(RoiManager.getInstance().getRoisAsArray()):
						
						imp_measure.setRoi(roi)
						
						IJ.run(imp_measure, "Enlarge...", "enlarge=-"+str(nPixShrink));
						if drawRings:
							IJ.run(imp_measure, "Draw", "");
						stats = imp_measure.getStatistics(Measurements.MEAN + Measurements.AREA)
						areaSmall = stats.area
						meanIntensSmall = stats.mean
						
						IJ.run(imp_measure, "Enlarge...", "enlarge="+str(nPixDilate+nPixShrink));
						if drawRings:
							IJ.run(imp_measure, "Draw", "");
						stats = imp_measure.getStatistics(Measurements.MEAN + Measurements.AREA)
						areaLarge = stats.area
						meanIntensLarge = stats.mean
		
						meanIntensRing = (meanIntensLarge*areaLarge - meanIntensSmall*areaSmall ) / (areaLarge - areaSmall)
						
						txt_out("mean intens ring: " + str(int(meanIntensRing)) + "/" +str(meanIntensThreshold) )
	
						# only count valid cells
						if (meanIntensRing > meanIntensThreshold) & (areaSmall < particles_area_max):
							nGoodParticles = nGoodParticles + 1

					
				elif particle_selectionMode == "nuclear_intensity":
					
					if project == "julia_roberti_ellenberg":
						imp = imp_zMax
					else:
						imp = imp_image

					imp.setSlice(iSlice_measurement)
					IJ.run(imp, "Duplicate...", "title=signal");
					imp_measure = IJ.getImage()
					
					# loop through the cells
					intens_current_max = -1
					for i, roi in enumerate(RoiManager.getInstance().getRoisAsArray()):
						
						imp_measure.setRoi(roi)
						stats = imp_measure.getStatistics(Measurements.MEAN + Measurements.AREA)
						txt_out("particle " + str(i) + " mean: " + str(int(stats.mean)) + " area: " + str(int(stats.area)))

						# only count valid cells
						if (criterium == "mean_intensity"):
							intensity_measure = stats.mean
						elif (criterium == "total_intensity"):
							intensity_measure = stats.mean*stats.area
							
						if (intensity_measure > intensityThreshold) & (stats.area < particles_area_max):
							nGoodParticles = nGoodParticles + 1  # count valid 
							if (intensity_measure > intens_current_max):
								iBestParticle = i
								intens_current_max = intensity_measure
						
				# get location and orientation of the best particle
				x = table.getColumn(table.getColumnIndex("X"))
				y = table.getColumn(table.getColumnIndex("Y"))
				angle = table.getColumn(table.getColumnIndex("Angle"))
				
				dx = int(x[iBestParticle]-imp_image.getWidth()/2)
				dy = int(y[iBestParticle]-imp_image.getWidth()/2)
				angle = int(angle[iBestParticle])
				

		txt_out("number of good particles: " + str(nGoodParticles))

		if project == "andrea_boni_ellenberg":				
			code = "validCells_"+str(int(nGoodParticles))+"_"+str(int(minCellsPerImage))+"_"+str(int(minCellsPerWell))
			txt_out(code)
		
		elif project == "julia_roberti_ellenberg":				
			if nGoodParticles >= 1:
				sX = str(-dx) #.replace(".",",")
				sY = str(-dy) #.replace(".",",")
				sOrientation = str(angle) #.replace(".",",")
				code = LSM780_CODE_OBJECT_FOUND_AND_START_IMAGING
				txt_out("index of best good particle: " + str(iBestParticle))
		
			else:
				sX = "0"
				sY = "0"
				sOrientation = "0"
				code = LSM780_CODE_NO_OBJECT_FOUND
				
		elif microscope == LSM510:
			sX = str(dx) #.replace(".",",")
			sY = str(dy) #.replace(".",",")
			sOrientation = str(angle) #.replace(".",",")
			code = LSM510_CODE_OBJECT_FOUND
		
		elif microscope == LSM780:
			sX = str(-dx) #.replace(".",",")
			sY = str(-dy) #.replace(".",",")
			sOrientation = str(angle) #.replace(".",",")
			code = LSM780_CODE_OBJECT_FOUND_AND_START_IMAGING

		
		#table.show("")
	
		# write results to windows registry
		txt_out("sX "+ sX)
		txt_out("sY "+ sY)
		txt_out("sOrientation "+ sOrientation)
		
		wr = WindowsRegistry()
		reg = wr.writeRegistry(winReg_location,"offsetx",sX)
		reg = wr.writeRegistry(winReg_location,"offsety",sY)
		reg = wr.writeRegistry(winReg_location,"orientation",sOrientation)
		reg = wr.writeRegistry(winReg_location,"Code",code)
		
		# clean up to save memory
		wr = None
		pa = None
		ws = None

			
	return()
	
	
run()
	
