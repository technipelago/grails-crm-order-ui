grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"

grails.project.dependency.resolution = {
    inherits("global") {}
    log "warn"
    legacyResolve false
    repositories {
        grailsCentral()
        mavenCentral()
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
        test(":codenarc:0.21") { export = false }

        compile ":crm-core:2.0.2"
        compile ":crm-security:2.0.0"
        compile ":crm-ui-bootstrap:2.0.0"
        compile ":crm-tags:2.0.0"
        compile ":crm-order:2.0.0"

        compile ":decorator:1.1"
        compile ":user-tag:0.6"
        compile ":selection:0.9.7"
    }
}
