package webbcv

import java.util.List;

class User {

	String login
	String password
	String email
	List vocabularies
	List jobs
	
    static constraints = {
		login (blank:false, unique:true)
		password (size: 6..20)
		email (nullable: true, email: true)
		
    }
}
