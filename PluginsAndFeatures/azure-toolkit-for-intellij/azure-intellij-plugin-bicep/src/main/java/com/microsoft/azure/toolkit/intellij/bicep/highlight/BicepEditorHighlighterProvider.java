/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 */

package com.microsoft.azure.toolkit.intellij.bicep.highlight;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.editor.ex.util.EmptyEditorHighlighter;
import com.intellij.openapi.editor.highlighter.EditorHighlighter;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.progress.ProcessCanceledException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.microsoft.azure.toolkit.lib.common.exception.SystemException;
import com.microsoft.azure.toolkit.lib.common.operation.AzureOperation;
import com.microsoft.intellij.CommonConst;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.jetbrains.plugins.textmate.TextMateService;
import org.jetbrains.plugins.textmate.configuration.BundleConfigBean;
import org.jetbrains.plugins.textmate.configuration.TextMateSettings;
import org.jetbrains.plugins.textmate.configuration.TextMateSettings.TextMateSettingsState;
import org.jetbrains.plugins.textmate.language.TextMateLanguageDescriptor;
import org.jetbrains.plugins.textmate.language.syntax.highlighting.TextMateEditorHighlighterProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.locks.Lock;

public class BicepEditorHighlighterProvider extends TextMateEditorHighlighterProvider {
    protected static final Logger LOG = Logger.getInstance(BicepEditorHighlighterProvider.class);

    @Override
    @AzureOperation("platform/bicep.get_editor_highlighter")
    public EditorHighlighter getEditorHighlighter(@Nullable Project project, @Nonnull FileType fileType, @Nullable VirtualFile virtualFile, @Nonnull EditorColorsScheme colors) {
        final TextMateLanguageDescriptor descriptor = TextMateService.getInstance().getLanguageDescriptorByExtension("bicep");
        if (Objects.isNull(descriptor)) { // register textmate if not registered
            if (registerBicepTextMateBundle()) {
                TextMateService.getInstance().reloadEnabledBundles();
            }
        }
        try {
            return super.getEditorHighlighter(project, fileType, virtualFile, colors);
        } catch (final ProcessCanceledException e) {
            LOG.warn(e);
            return new EmptyEditorHighlighter();
        }
    }

    @AzureOperation("boundary/bicep.register_textmate_bundles")
    public static synchronized boolean registerBicepTextMateBundle() {
        final TextMateSettingsState state = TextMateSettings.getInstance().getState();
        try {
            if (Objects.nonNull(state)) {
                final Lock registrationLock = (Lock) FieldUtils.readField(TextMateService.getInstance(), "myRegistrationLock", true);
                try {
                    registrationLock.lock();
                    final Path bicepTextmatePath = Path.of(CommonConst.PLUGIN_PATH, "bicep", "textmate", "bicep");
                    final Path bicepParamTextmatePath = Path.of(CommonConst.PLUGIN_PATH, "bicep", "textmate", "bicepparam");
                    final Collection<BundleConfigBean> bundles = state.getBundles();
                    if (bicepTextmatePath.toFile().exists() && bundles.stream().noneMatch(b -> "bicep".equals(b.getName()) && b.isEnabled() && Path.of(b.getPath()).equals(bicepTextmatePath))) {
                        final ArrayList<BundleConfigBean> newBundles = new ArrayList<>(bundles);
                        newBundles.removeIf(bundle -> StringUtils.equalsAnyIgnoreCase(bundle.getName(), "bicep", "bicepparam"));
                        newBundles.add(new BundleConfigBean("bicep", bicepTextmatePath.toString(), true));
                        newBundles.add(new BundleConfigBean("bicepparam", bicepParamTextmatePath.toString(), true));
                        state.setBundles(newBundles);
                        return true;
                    }
                } finally {
                    registrationLock.unlock();
                }
            }
        } catch (final IllegalAccessException e) {
            throw new SystemException("can not acquire lock of 'TextMateService'.", e);
        }
        return false;
    }
}
