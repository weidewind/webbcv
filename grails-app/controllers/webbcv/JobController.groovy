package webbcv

import groovyx.gpars.GParsPool
import jsr166y.ForkJoinPool
import java.util.concurrent.Future


//import groovyx.gpars.*

class JobController {
	
	def scaffold = true
	def jobService
	def Random random = new Random()
	

	def index = {
	//	redirect(action: form)
		}
	
	def form =  {
	}
	
	def stapform =  {
	}
	
	def submit() {
		
		def job = new Job(params)
		
		// Get files and directions
		
		def fileList = request.getFiles('files')
		def  directionList = []
		for (int i = 0; i < fileList.size(); i++){
			directionList.add(params.('checkbox' + i))
		}
		
		def errorMessage = jobService.checkInput(job, fileList)
		
		if (errorMessage) {
			render "${errorMessage}"
			return
		}
		
		
		//def sessionId = createSessionId()
		def sessionId = "5"
		
		
		def uploadPath = jobService.prepareDirectory(job, sessionId, fileList, directionList)
		Closure pipeline = jobService.getPipeline(job)
		
		if (job.email != null){
			
			def GParsPool = new GParsPool()
			def pool = new ForkJoinPool(1)
			GParsPool.withExistingPool (pool, {
			myTestRun.callAsync(sessionId, job.email) //why async?
				})
			
			render "Success! We will send your results at ${job.email} in about 30 minutes"
			
		}
		else {
			//render "Wait here. Your sessionId is ${sessionId}"
			def GParsPool = new GParsPool()
			def pool = new ForkJoinPool(1)
			GParsPool.withExistingPool (pool, {
				myTestRun.callAsync(sessionId, null) 
				pool.shutdown()
				})
			def start = new Date(System.currentTimeMillis())
			render "<p>Please, don't close this page. Your task was submitted at ${start}.</p>"
			while (!pool.isTerminated()){
				def randomString = talkToUser()
				render "<p>${randomString}</p>"
				sleep(5000)
			}

			def url = createLink(controller: 'job', action: 'renderResults', params: [sessionId: sessionId])
			render(contentType: 'text/html', text: "<script>window.location.href='$url'</script>")

		}
		
	}
	
	def createSessionId() {
		def randomNum = new Random().nextInt(100000)
		def currentTime = System.currentTimeMillis()
		return "${randomNum}_${currentTime}"
	}
	
	def renderResults (String sessionId){
		def htmlContent = new File(jobService.getResults(sessionId)).text
		render (text: htmlContent, contentType:"text/html", encoding:"UTF-8")
	}
	
	
	
	def sendResults(String email, String resultsFilePath) {
		mailService.sendMail {
		multipart true
		to email
		subject "BCV results"
		body "Have a nice day!"
		attachBytes resultsFilePath,'text/xml', new File(resultsFilePath).readBytes()
		
		}
		
	}
	
	def compute (String a){
		Thread.sleep (3000)
		return a
	}
	
	def talkToUser(){
		def lines = [
			"Spinning up the hamster...",
			"Shovelling coal into the server...",
			"Programming the flux capacitor",
			"Checking the gravitational constant in your locale",
			"Please wait. The server is powered by a lemon and two electrodes"
		]
		return lines[random.nextInt(lines.size())]
		
	}
	
	Closure myExpensiveCalculation = {
		sleep (5000)
		mailService.sendMail {
			multipart true
			to "weidewind@gmail.com"
			subject "BCV results"
			body "Have a nice day!"
			attachBytes "C:/Users/weidewind/Documents/CMD/website/bcvrun.prj.xml",'text/xml', new File("C:/Users/weidewind/Documents/CMD/website/bcvrun.prj.xml").readBytes()
			
			}
	}
	
	Closure myTestRun = { sessionId, email ->

		sleep (7000)
		jobService.runPipeline(sessionId)
		if (email != null) {
			jobService.sendResults(email, "5")
		}

	}
	
	Closure checkLogs = { sessionId ->
		
		
		
	}
}
	
	

