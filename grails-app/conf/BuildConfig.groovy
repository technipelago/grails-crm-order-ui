grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"

grails.project.dependency.resolver = "maven"
grails.project.dependency.resolution = {
    inherits("global") {}
    log "warn"
    legacyResolve false
    repositories {
        grailsCentral()
        mavenLocal()
        mavenCentral()
    }
    dependencies {
        // See https://jira.grails.org/browse/GPHIB-30
        test("javax.validation:validation-api:1.1.0.Final") { export = false }
        test("org.hibernate:hibernate-validator:5.0.3.Final") { export = false }
    }
    plugins {
        build(":release:3.0.1",
                ":rest-client-builder:1.0.3") {
            export = false
        }
        test(":hibernate4:4.3.6.1") {
            export = false
        }

        test(":codenarc:0.22") { export = false }
        test(":code-coverage:2.0.3-3") { export = false }

        compile ":decorator:1.1"
        compile ":user-tag:1.0"
        compile ":selection:0.9.8"

        compile ":crm-order:2.4.2"
        compile ":crm-security:2.4.2"
        compile ":crm-ui-bootstrap:2.4.1"
    }
}
