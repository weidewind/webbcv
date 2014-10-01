package webbcv

class Job {
	
String tasktype
String sequences
List files
String database
String vocabulary
float distance
String email
List checkbox


    static constraints = {
		email (blank:true, nullable:true, email:true)
		distance (min: 0f, max: 0.1f)
		vocabulary (nullable: true)
		files (nullable: true)
		checkbox (nullable: true)
		sequences (nullable: true)

    }
	
}
