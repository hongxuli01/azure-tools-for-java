/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 */

package com.microsoft.azure.toolkit.ide.storage;

import com.microsoft.azure.toolkit.ide.common.IActionsContributor;
import com.microsoft.azure.toolkit.ide.common.action.Action;
import com.microsoft.azure.toolkit.ide.common.action.ActionGroup;
import com.microsoft.azure.toolkit.ide.common.action.ActionView;
import com.microsoft.azure.toolkit.ide.common.action.AzureActionManager;
import com.microsoft.azure.toolkit.ide.common.action.ResourceCommonActionsContributor;
import com.microsoft.azure.toolkit.ide.storage.action.OpenStorageBrowserAction;
import com.microsoft.azure.toolkit.lib.storage.service.StorageAccount;

import java.util.Optional;
import java.util.function.Consumer;

import static com.microsoft.azure.toolkit.lib.common.operation.AzureOperationBundle.title;

public class StorageActionsContributor implements IActionsContributor {
    public static final String SERVICE_ACTIONS = "actions.storage.service";
    public static final String ACCOUNT_ACTIONS = "actions.storage.account";

    public static final Action.Id<StorageAccount> OPEN_STORAGE_BROWSER = Action.Id.of("action.storage.open_storage_explorer");

    @Override
    public void registerActions(AzureActionManager am) {
        final Consumer<StorageAccount> open = OpenStorageBrowserAction::openStorageBrowser;
        final ActionView.Builder openView = new ActionView.Builder("Open Storage Explorer", "/icons/action/portal.svg")
                .title(s -> Optional.ofNullable(s).map(r -> title("storage|account.open_storage_explorer", ((StorageAccount) r).name())).orElse(null))
                .enabled(s -> s instanceof StorageAccount);
        am.registerAction(OPEN_STORAGE_BROWSER, new Action<>(open, openView));
    }

    @Override
    public void registerGroups(AzureActionManager am) {
        final ActionGroup serviceActionGroup = new ActionGroup(
                ResourceCommonActionsContributor.SERVICE_REFRESH,
                ResourceCommonActionsContributor.CREATE
        );
        am.registerGroup(SERVICE_ACTIONS, serviceActionGroup);

        final ActionGroup accountActionGroup = new ActionGroup(
                OPEN_STORAGE_BROWSER,
                ResourceCommonActionsContributor.DELETE
        );
        am.registerGroup(ACCOUNT_ACTIONS, accountActionGroup);
    }
}
