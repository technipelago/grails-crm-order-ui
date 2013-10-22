grails.project.work.dir = "target"

grails.project.repos.default = "crm"

grails.project.dependency.resolution = {
    inherits("global") {}
    log "warn"
    legacyResolve false
    repositories {
        grailsHome()
        mavenRepo "http://labs.technipelago.se/repo/plugins-releases-local/"
        mavenRepo "http://labs.technipelago.se/repo/crm-releases-local/"
        grailsCentral()
    }
    dependencies {
    }

    plugins {
        runtime ":hibernate:$grailsVersion"
        build(":tomcat:$grailsVersion",
              ":release:2.2.1",
              ":rest-client-builder:1.0.3") {
            export = false
        }
        test(":spock:0.7") { export = false }
        test(":codenarc:0.17") { export = false }

        compile "grails.crm:crm-core:latest.integration"
        runtime "grails.crm:crm-security:latest.integration"
        runtime "grails.crm:crm-ui-bootstrap:latest.integration"
        runtime "grails.crm:crm-tags:latest.integration"
        runtime "grails.crm:crm-order:latest.integration"

        runtime ":decorator:latest.integration"
        runtime ":user-tag:latest.integration"
        compile ":selection:latest.integration"
    }
}
