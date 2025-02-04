/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 */

package com.microsoft.azure.hdinsight.projects;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.microsoft.azure.hdinsight.common.StreamUtil;
import com.microsoft.azure.hdinsight.projects.util.ProjectSampleUtil;
import com.microsoft.azuretools.azurecommons.helpers.NotNull;
import com.microsoft.tooling.msservices.components.DefaultLoader;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.jetbrains.concurrency.Promise;
import org.jetbrains.concurrency.Promises;
import org.jetbrains.idea.maven.project.MavenProject;
import org.jetbrains.idea.maven.project.MavenProjectsManager;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;

public class MavenProjectGenerator {
    private Module module;
    private HDInsightTemplatesType templatesType;
    private SparkVersion sparkVersion;
    private static final String ARTIFACT_ID = "artifactId";
    private static final String GROUP_ID = "groupId";
    private static final String UTF8_ENCODING = "UTF-8";

    public MavenProjectGenerator(@NotNull Module module,
                                 @NotNull HDInsightTemplatesType templatesType,
                                 @NotNull SparkVersion sparkVersion) {
        this.module = module;
        this.templatesType = templatesType;
        this.sparkVersion = sparkVersion;
    }

    public Promise<MavenProject> generate() {
        String root = ProjectSampleUtil.getRootOrSourceFolder(this.module, false);

        try {
            createDirectories(root);
            createPom(root);
            copySamples(root);
            return importMavenProject();
        } catch (Exception e) {
            DefaultLoader.getUIHelper().showError("Failed to create project: " + e.getMessage(), "Create Sample Project");

            return Promises.rejectedPromise(e);
        }
    }

    @SuppressWarnings("checkstyle:FallThrough")
    private void createDirectories(String root) throws IOException {
        switch (this.templatesType) {
            case ScalaFailureTaskDebugSample:
                VfsUtil.createDirectories(root + "/lib");
            case Java:
                VfsUtil.createDirectories(root + "/src/main/java/sample");
                VfsUtil.createDirectories(root + "/src/main/resources");
                VfsUtil.createDirectories(root + "/src/test/java");
                break;
            case Scala:
            case ScalaClusterSample:
                VfsUtil.createDirectories(root + "/src/main/scala/sample");
                VfsUtil.createDirectories(root + "/src/main/resources");
                VfsUtil.createDirectories(root + "/src/test/scala");
                break;
        }
    }

    private void createPom(String root) throws Exception {
        File file = null;
        switch (this.sparkVersion) {
            case SPARK_1_5_2:
                file = StreamUtil.getResourceFile("/hdinsight/templates/pom/spark_1_5_2_pom.xml");
                break;
            case SPARK_1_6_2:
                file = StreamUtil.getResourceFile("/hdinsight/templates/pom/spark_1_6_2_pom.xml");
                break;
            case SPARK_1_6_3:
                file = StreamUtil.getResourceFile("/hdinsight/templates/pom/spark_1_6_3_pom.xml");
                break;
            case SPARK_2_0_2:
                file = StreamUtil.getResourceFile("/hdinsight/templates/pom/spark_2_0_2_pom.xml");
                break;
            case SPARK_2_1_0:
                file = this.templatesType != HDInsightTemplatesType.ScalaFailureTaskDebugSample ?
                        StreamUtil.getResourceFile("/hdinsight/templates/pom/spark_2_1_0_pom.xml") :
                        StreamUtil.getResourceFile("/hdinsight/templates/pom/spark_2_1_0_failure_task_debug_pom.xml");
                break;
            case SPARK_2_2_0:
                file = StreamUtil.getResourceFile("/hdinsight/templates/pom/spark_2_2_0_pom.xml");
                break;
            case SPARK_2_3_0:
                file = this.templatesType != HDInsightTemplatesType.ScalaFailureTaskDebugSample ?
                        StreamUtil.getResourceFile("/hdinsight/templates/pom/spark_2_3_0_pom.xml") :
                        StreamUtil.getResourceFile("/hdinsight/templates/pom/spark_2_3_0_failure_task_debug_pom.xml");
                break;
            case SPARK_2_3_2:
                file = this.templatesType != HDInsightTemplatesType.ScalaFailureTaskDebugSample ?
                        StreamUtil.getResourceFile("/hdinsight/templates/pom/spark_2_3_2_pom.xml") :
                        StreamUtil.getResourceFile("/hdinsight/templates/pom/spark_2_3_2_failure_task_debug_pom.xml");
                break;
            case SPARK_2_4_0:
                file = StreamUtil.getResourceFile("/hdinsight/templates/pom/spark_2_4_0_pom.xml");
                break;
            case SPARK_3_0_1:
                file = StreamUtil.getResourceFile("/hdinsight/templates/pom/spark_3_0_1_pom.xml");
                break;
        }

        if (null == file) {
            DefaultLoader.getUIHelper().showError("Failed to get the sample resource folder for project", "Create Sample Project");
        } else {
            FileUtil.copy(file, new File(root + "/pom.xml"));
        }
    }

    private void copySamples(String root) throws Exception {
        switch (this.templatesType) {
            case ScalaClusterSample:
                ProjectSampleUtil.copyFileToPath(new String[]{
                        "/hdinsight/templates/java/JavaSparkPi.java"
                }, root + "/src/main/java/sample");

                ProjectSampleUtil.copyFileToPath(new String[]{
                        "/hdinsight/templates/scala/scala_cluster_run/SparkCore_WasbIOTest.scala",
                        "/hdinsight/templates/scala/scala_cluster_run/SparkStreaming_HdfsWordCount.scala",
                        "/hdinsight/templates/scala/scala_cluster_run/SparkSQL_RDDRelation.scala"
                }, root + "/src/main/scala/sample");

                ProjectSampleUtil.copyFileToPath(new String[]{
                        "/hdinsight/templates/scala/scala_local_run/LogQuery.scala",
                        "/hdinsight/templates/scala/scala_local_run/SparkML_RankingMetricsExample.scala"
                }, root + "/src/main/scala/sample");

                ProjectSampleUtil.copyFileToPath(new String[]{
                        "/hdinsight/templates/scala/scala_local_run/data/data/sample_movielens_data.txt"
                }, root + "/data/__default__/data/");

                ProjectSampleUtil.copyFileToPath(new String[]{
                        "/hdinsight/templates/scala/scala_local_run/data/HdiSamples/HdiSamples/FoodInspectionData/README"
                }, root + "/data/__default__/HdiSamples/HdiSamples/FoodInspectionData/");

                ProjectSampleUtil.copyFileToPath(new String[]{
                        "/hdinsight/templates/scala/scala_local_run/data/HdiSamples/HdiSamples/SensorSampleData/hvac/HVAC.csv"
                }, root + "/data/__default__/HdiSamples/HdiSamples/SensorSampleData/hvac/");

                if (SparkVersion.sparkVersionComparator.compare(this.sparkVersion, SparkVersion.SPARK_2_1_0) >= 0) {
                    // sample code
                    ProjectSampleUtil.copyFileToPath(new String[]{
                            "/hdinsight/templates/scala/sparksql/SparkSQLExample.scala"
                    }, root + "/src/main/scala/sample");

                    // sample data
                    ProjectSampleUtil.copyFileToPath(new String[]{
                            "/hdinsight/templates/scala/scala_local_run/data/example/data/people.json"
                    }, root + "/data/__default__/example/data/");
                }

                // Falling through
            case Scala:
            case Java:
                new File(root, "data/__default__/user/current/").mkdirs();

                ProjectSampleUtil.copyFileToPath(new String[]{
                        "/hdinsight/templates/log4j.properties"
                }, root + "/src/main/resources");
                break;
            case ScalaFailureTaskDebugSample:
                ProjectSampleUtil.copyFileToPath(new String[]{
                        "/hdinsight/templates/scala/sample/AgeMean_Div0.scala"
                }, root + "/src/main/scala/sample");

                new File(root, "data/__default__/user/current/").mkdirs();

                ProjectSampleUtil.copyFileToPath(new String[]{
                        "/hdinsight/templates/log4j.properties"
                }, root + "/src/main/resources");

                ProjectSampleUtil.copyFileToPath(new String[]{
                        "/spark/" + SparkToolsLib.INSTANCE.getJarFileName(this.sparkVersion)
                }, root + "/lib");

                break;
        }
    }

    private Promise<MavenProject> importMavenProject() {
        Project project = this.module.getProject();
        String baseDirPath = project.getBasePath();
        MavenProjectsManager manager = MavenProjectsManager.getInstance(project);

        File pomFile = new File(baseDirPath + File.separator + "pom.xml");
        VirtualFile pom = VfsUtil.findFileByIoFile(pomFile, true);

        if (pom == null) {
            return Promises.rejectedPromise("Can't find Maven pom.xml file to import into IDEA");
        }

        if(ObjectUtils.isEmpty(pomFile) || !changeArtifactName(pomFile.getPath())){
            return Promises.rejectedPromise("The artifactId in the Maven pom.xml file has not been modified");
        }

        manager.addManagedFiles(Collections.singletonList(pom));

        manager.forceUpdateAllProjectsOrFindAllAvailablePomFiles();

        return Promises.resolvedPromise()
                .then(o -> {
                    MavenProject mavenProject = manager.findProject(this.module);
                    return mavenProject;
                });
    }

    /**
     *
     * Solve the problem that idea mistakenly takes the artifactId in the pom.xml file as the Modules name during the template addition process, which causes build failure
     *
     * @param pomFile Path to the pom.xml file
     * @return
     *        false: indicates that the modification fails
     *        true:  Indicates that the corresponding artifactId in the pom.xml file is consistent with the project name
     *
     */
    public boolean changeArtifactName(String pomFile)
    {
        if(StringUtils.isBlank(pomFile)){
            return false;
        }
        boolean result = false;
        Document document = null;
        XMLWriter writer = null;
        try
        {
            SAXReader reader = new SAXReader();
            document = reader.read(pomFile);
            Element rootElement = document.getRootElement();
            rootElement.element(ARTIFACT_ID).setText(this.module.getName());
            rootElement.element(GROUP_ID).setText(this.module.getName());
            OutputFormat format = new OutputFormat();
            format.setEncoding(UTF8_ENCODING);
            writer = new XMLWriter(new FileWriter(pomFile), format);
            writer.write(document);
            result = true;
        }
        catch (DocumentException | IOException de)
        {
            Promises.rejectedPromise(de);
        } finally {
            try {
                assert writer != null;
                writer.close();
                document.clearContent();
            } catch (IOException | AssertionError e) {
                Promises.rejectedPromise(e);
                result = false;
            }
        }
        return result;
    }
}
