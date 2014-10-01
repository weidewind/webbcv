grails.project.class.dir = 'target/classes'
grails.project.test.class.dir = 'target/test-classes'
grails.project.test.reports.dir = 'target/test-reports'

grails.release.scm.enabled = false // manually commit changes to SVN

grails.project.dependency.resolution = {

	inherits 'global'

	log 'warn'

	repositories {
		grailsPlugins()
		grailsHome()
		grailsCentral()

		mavenCentral()
	}

	dependencies {

		compile('org.codehaus.gpars:gpars:0.12') {
			transitive = false
		}

		runtime('org.codehaus.jsr166-mirror:jsr166y:1.7.0') {
			transitive = false
		}

		runtime('org.codehaus.jsr166-mirror:extra166y:1.7.0') {
			transitive = false
		}
	}
}
