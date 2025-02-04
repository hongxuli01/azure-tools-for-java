pluginManagement {
    repositories {
        maven {
            url = java.net.URI("https://oss.sonatype.org/content/repositories/snapshots/")
        }
        gradlePluginPortal()
    }
}

rootProject.name = "azure-toolkit-for-intellij"
include("azure-intellij-plugin-lib")
include("azure-intellij-plugin-lib-java")
include("azure-intellij-resource-connector-lib")
include("azure-intellij-resource-connector-lib-java")
include("azure-intellij-plugin-service-explorer")
include("azure-intellij-plugin-guidance")
include("azure-intellij-plugin-guidance-java")
include("azure-sdk-reference-book")
include("azure-intellij-plugin-springcloud")
include("azure-intellij-plugin-storage")
include("azure-intellij-plugin-storage-java")
include("azure-intellij-plugin-appservice")
include("azure-intellij-plugin-appservice-java")
include("azure-intellij-plugin-arm")
include("azure-intellij-plugin-applicationinsights")
include("azure-intellij-plugin-cosmos")
include("azure-intellij-plugin-cognitiveservices")
include("azure-intellij-plugin-monitor")
include("azure-intellij-plugin-containerregistry")
include("azure-intellij-plugin-containerservice")
include("azure-intellij-plugin-containerapps")
include("azure-intellij-plugin-database")
include("azure-intellij-plugin-database-java")
include("azure-intellij-plugin-vm")
include("azure-intellij-plugin-redis")
include("azure-intellij-plugin-redis-java")
include("azure-intellij-plugin-samples")
include("azure-intellij-plugin-bicep")
include("azure-intellij-plugin-eventhubs")
include("azure-intellij-plugin-servicebus")
include("azure-intellij-resource-connector-aad")
include("azure-intellij-plugin-hdinsight-lib")
include("azure-intellij-plugin-hdinsight")
include("azure-intellij-plugin-hdinsight-base")
include("azure-intellij-plugin-synapse")
include("azure-intellij-plugin-sparkoncosmos")
include("azure-intellij-plugin-sqlserverbigdata")
include("azure-intellij-plugin-keyvault")
include("azure-intellij-plugin-keyvault-java")
include("azure-intellij-plugin-integration-services")
include("azure-intellij-plugin-java-sdk")
include("azure-intellij-plugin-cloud-shell")
