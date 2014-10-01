package webbcv

import grails.transaction.Transactional
import groovy.lang.Closure;

import java.awt.event.ItemEvent;
import java.util.regex.Pattern;




@Transactional
class JobService {
	
	def mailService
	

	
	def String absPath = "C:/Users/weidewind/workspace/grails/"
	def String configPath = "C:/Users/weidewind/Documents/CMD/website/bcvrun.prj.xml"
	
	def prepareDirectory(Job job, String sessionId, List fileList, List directoryList){
		
		def outputPath = ""
		
		def configFile = new File (configPath)
		
		def defaultConfig = new XmlParser().parse(configFile)
		

		
		//	Create directory structure

		def inputLine  = defaultConfig.BCV_Input
		def input = inputLine.text().replaceAll(Pattern.compile('[\\.\\/\\\\]'), '')
		
		def outputLine  = defaultConfig.BCV_Output
		def output = outputLine.text().replaceAll(Pattern.compile('[\\.\\/\\\\]'), '')
		
		def folderPath = "${absPath}${sessionId}"
		def inputPath = folderPath + "/" + input
			outputPath = folderPath + "/" + output
		
		new File (inputPath).mkdirs()
		new File (outputPath).mkdir()
		
		if (fileList != null){
			for (f in fileList){
				new File (outputPath + "/" + f.getOriginalFilename().replaceAll(Pattern.compile('\\..*'), '')).mkdir()
			}
		}
		
		if (job.sequences != null) {
			new File (outputPath + "/" + "").mkdir()
		}
		

		if (job.tasktype == "bcvstap"){
			uploadChromatograms(inputPath, fileList)
		}
		
		if (job.tasktype == "stap"){
			if (fileList != null){
				uploadSequenceFiles(outputPath, fileList)
			}
			if (job.sequences != null) {
				uploadSequences(outputPath, job.sequences)
			}
		}
		
		
		// Write custom configuration file
		
		
		if (job.tasktype == "bcvstap"){
			def vocabulary = defaultConfig.Vocabulary
			def vocabularyPath = vocabulary[0].text().substring(0, vocabulary[0].text().lastIndexOf("/")+1)
			def vocabularyName = job.vocabulary.replaceAll(/\s+/, "_")
			vocabulary[0].value = vocabularyPath + vocabularyName + ".seq.fas"

			def reads = defaultConfig.READS.find{'READS'}
			reads.children.clear()

			def counter = 0
			for (f in fileList){
				def stringDirection
				if (directionList.get(counter) == null){
					stringDirection = "reverse"
				}
				else stringDirection = "forward"
				reads.appendNode('Read', [name:f.getOriginalFilename()]).appendNode('Direction', stringDirection)
				counter++
			}
		}

		def database = defaultConfig.Database
		if (job.database == "full"){
			database[0].value = "all"
		}
		else if (job.database == "named isolates"){
			database[0].value = "named"
		}
		
		
		def distance = defaultConfig.DistanceThreshold
		distance[0].value = job.distance.toFloat()
		
		def writer = new StringWriter()
		def printer = new XmlNodePrinter(new PrintWriter(writer))
		printer.preserveWhitespace = false
		printer.print(defaultConfig)
		def result = writer.toString()
		new File(folderPath + "/bcvrun.prj.xml").write(result)
		
		return outputPath
		
	}
	

	def getPipeline(Job job){
		
		def Closure myRunnable = { sessionId, email ->
		
				sleep (7000)
				if (job.tasktype == "bcvstap"){
					jobService.runPipeline(sessionId)
				}
				else if (job.tasktype == "stap"){
					jobService.runSTAP(sessionId)
				}
				if (email != null) {
					jobService.sendResults(email, "5")
				}
		
			}
	}
	
	def runPipeline(String sessionId){
		def command = "cmd /c C:/Users/weidewind/workspace/test/email.pl"// Create the String
		def proc = command.execute()                 // Call *execute* on the string
		proc.waitFor()                               // Wait for the command to finish
	}
	
	def runSTAP(String sessionId){
		def command = "cmd /c C:/Users/weidewind/workspace/test/email.pl"// Create the String
		def proc = command.execute()                 // Call *execute* on the string
		proc.waitFor()                               // Wait for the command to finish
	}
	
	
	def uploadChromatograms(String uploadPath, List fileList){
		
		for (f in fileList){
			if(f instanceof org.springframework.web.multipart.commons.CommonsMultipartFile){
			
				def fileName = f.getOriginalFilename()
				new FileOutputStream(uploadPath + "/" + fileName).leftShift( f.getInputStream() )

			} else {
				log.error("wrong attachment type [${f.getClass()}]");
			}
		}
	}
	
	def uploadSequenceFiles(String uploadPath, List fileList){
		for (f in fileList){
			if(f instanceof org.springframework.web.multipart.commons.CommonsMultipartFile){
			
				def fileName = f.getOriginalFilename()
				new FileOutputStream(uploadPath + "/" + fileName.replaceAll(Pattern.compile('\\..*'), '') + "/" + fileName).leftShift( f.getInputStream() )

			} else {
				log.error("wrong attachment type [${f.getClass()}]");
			}
		}
	}
	
	
	

	
	def sendResults(String email, String sessionId) {
		
		def resultsFilePath = "${absPath}${sessionId}" + "/simple_results.html"
		mailService.sendMail {
		multipart true
		to email
		subject "BCV results"
		body "Have a nice day!"
		attachBytes resultsFilePath,'text/xml', new File(resultsFilePath).readBytes()
		
		}
		
	}
	
	def getResults (String sessionId){
		def resultsPath = "${absPath}${sessionId}" + "/simple_results.html"
		return resultsPath
		
	}
	
	def checkInput(Job job, List fileList){
		String errorMessage = ""
		
		if (!job.validate()) {

		errorMessage = "Some errors occured: "
		if (job.distance.toFloat() < 0 || job.distance.toFloat() > 0.1){
			errorMessage += "<p> Maximum distance must not be less than 0 or more than 0.1 </p>"
		}
		if (job.errors.hasFieldErrors("email")){
			errorMessage += "<p>Your e-mail does not seem valid </p>"
		}
		if (job.errors.hasFieldErrors("files")){
			errorMessage += "<p>Select at least one file </p>"
		}
		}
		for (f in fileList) {
			def name = f.getOriginalFilename()
			int dot= name.lastIndexOf(".");
			if (job.tasktype == "bcvstap" && name.substring(dot+1) != "ab1"){
				errorMessage += "<p>Unsupported extension: ${name} </p>"
			}
			else if (job.tasktype == "stap" && name.substring(dot+1) != "fasta"){
				errorMessage += "<p>Unsupported extension: ${name} </p>"
			}
		}
		

		return errorMessage
		
	}

}
