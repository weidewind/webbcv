import org.apache.commons.logging.LogFactory

class GparsGrailsPlugin {
    def version = "0.3"
    def grailsVersion = "1.2.3 > *"
    def author = "Vaclav Pech"
    def authorEmail = "vaclav@vaclavpech.eu"
    def title = "GPars Plugin"
    def description = '''\\
Adds GPars jar files to the projects. More on GPars at http://gpars.codehaus.org/
'''

    def documentation = "http://grails.org/plugin/gpars"

    String license = 'APACHE'
    def developers = [ [name: 'Vaclav Pech', email: 'vaclav@vaclavpech.eu'] ]
    def issueManagement = [system: 'JIRA', url: 'http://jira.grails.org/browse/GPGPARS']
    def scm = [url: 'https://svn.codehaus.org/grails-plugins/grails-gpars/']

    def doWithSpring = { detectPoolSize(application) }

    def onConfigChange = { event -> detectPoolSize(application) }

    private void detectPoolSize(application) {
        final String poolSize = application.config.gpars.poolsize ?: ''
        final log = LogFactory.getLog(getClass())
        if (poolSize) {
            log.info "[GPars] Setting gpars pool size to ${poolSize} as specified in the configuration"
            System.setProperty 'gpars.poolsize', poolSize
        } else {
            if (System.properties['gpars.poolsize']) log.info "[GPars] Using the gpars pool size of ${System.properties['gpars.poolsize']} as specified in the system properties"
            else {
                log.info "[GPars] Using the default gpars pool size since no explicit configuration has been found"
            }
        }
    }
}
