/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 */

package com.microsoft.intellij.runner;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.RunConfigurationExtension;
import com.intellij.execution.configuration.AbstractRunConfiguration;
import com.intellij.execution.configurations.JavaParameters;
import com.intellij.execution.configurations.RunConfigurationBase;
import com.intellij.execution.configurations.RunnerSettings;
import com.intellij.spring.boot.run.SpringBootApplicationRunConfiguration;
import org.apache.commons.collections4.MapUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class LinkAzureServiceRunConfigurationExtension extends RunConfigurationExtension {

    @Override
    public <T extends RunConfigurationBase> void updateJavaParameters(@NotNull T t, @NotNull JavaParameters javaParameters,
                                                                      RunnerSettings runnerSettings) throws ExecutionException {
        Boolean linkAzureServiceFlag = t.getUserData(LinkAzureServiceBeforeRunProvider.LINK_AZURE_SERVICE);
        Map<String, String> envMap = t.getUserData(LinkAzureServiceBeforeRunProvider.LINK_AZURE_SERVICE_ENVS);
        if (Boolean.TRUE.equals(linkAzureServiceFlag) && MapUtils.isNotEmpty(envMap)) {
            for (Map.Entry<String, String> entry : envMap.entrySet()) {
                javaParameters.addEnv(entry.getKey(), entry.getValue());
            }
        }
    }

    @Override
    public boolean isApplicableFor(@NotNull RunConfigurationBase<?> runConfigurationBase) {
        if (runConfigurationBase instanceof AbstractRunConfiguration || runConfigurationBase instanceof SpringBootApplicationRunConfiguration) {
            Boolean linkAzureService = runConfigurationBase.getUserData(LinkAzureServiceBeforeRunProvider.LINK_AZURE_SERVICE);
            if (Boolean.TRUE.equals(linkAzureService)) {
                return true;
            }
        }
        return false;
    }
}
