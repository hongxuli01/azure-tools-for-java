
/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 */

package com.microsoft.azure.toolkit.intellij.containerapps.guidance;

import com.microsoft.azure.toolkit.ide.common.action.ResourceCommonActionsContributor;
import com.microsoft.azure.toolkit.ide.guidance.ComponentContext;
import com.microsoft.azure.toolkit.ide.guidance.Phase;
import com.microsoft.azure.toolkit.ide.guidance.Task;
import com.microsoft.azure.toolkit.lib.common.action.AzureActionManager;
import com.microsoft.azure.toolkit.lib.common.operation.AzureOperation;
import com.microsoft.azure.toolkit.lib.containerapps.containerapp.ContainerApp;

import javax.annotation.Nonnull;
import java.util.Objects;

public class OpenInBrowserTask implements Task {
    public static final String CONTAINER_APP = "containerApp";

    private final ComponentContext context;

    public OpenInBrowserTask(@Nonnull final ComponentContext context) {
        this.context = context;
    }

    @Override
    @AzureOperation(name = "internal/guidance.open_container_app_in_browser")
    public void execute() {
        final ContainerApp app = (ContainerApp) Objects.requireNonNull(context.getParameter(CONTAINER_APP));
        AzureActionManager.getInstance().getAction(ResourceCommonActionsContributor.OPEN_URL).handle("https://" + app.getIngressFqdn());
    }

    @Nonnull
    @Override
    public String getName() {
        return "task.containerapp.open_in_browser";
    }
}