package com.microsoft.azure.toolkit.intellij.cognitiveservices;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.ProjectActivity;
import com.microsoft.azure.toolkit.ide.guidance.GuidanceViewManager;
import com.microsoft.azure.toolkit.intellij.common.settings.IntellijStore;
import com.microsoft.azure.toolkit.lib.Azure;
import com.microsoft.azure.toolkit.lib.cognitiveservices.AzureCognitiveServices;
import com.microsoft.azure.toolkit.lib.cognitiveservices.CognitiveDeployment;
import com.microsoft.azure.toolkit.lib.common.action.Action;
import com.microsoft.azure.toolkit.lib.common.action.AzureActionManager;
import com.microsoft.azure.toolkit.lib.common.bundle.AzureString;
import com.microsoft.azure.toolkit.lib.common.event.AzureEventBus;
import com.microsoft.azure.toolkit.lib.common.messager.AzureMessager;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import org.apache.commons.lang3.BooleanUtils;

import javax.annotation.Nonnull;

import static com.microsoft.azure.toolkit.ide.cognitiveservices.CognitiveServicesActionsContributor.OPEN_DEPLOYMENT_IN_PLAYGROUND;
import static com.microsoft.azure.toolkit.intellij.cognitiveservices.IntelliJCognitiveServicesActionsContributor.TRY_OPENAI;
import static com.microsoft.azure.toolkit.intellij.cognitiveservices.IntelliJCognitiveServicesActionsContributor.TRY_PLAYGROUND;

public class CognitiveServicesStartupListener implements ProjectActivity {
    @Override
    public Object execute(@Nonnull Project project, @Nonnull Continuation<? super Unit> continuation) {
        final String tryOpenAIId = TRY_OPENAI.getId();
        final String tryPlaygroundId = TRY_PLAYGROUND.getId();
        if (BooleanUtils.isNotTrue(IntellijStore.getInstance().getState().getSuppressedActions().get(tryOpenAIId))) {
            final Action<Project> tryOpenAI = new Action<>(TRY_OPENAI)
                .withLabel("Try Azure OpenAI")
                .withHandler((_d, e) -> {
                    GuidanceViewManager.getInstance().openCourseView(project, "hello-openai");
                    IntellijStore.getInstance().getState().getSuppressedActions().put(tryOpenAIId, true);
                })
                .withAuthRequired(true);
            ;
            final Action<Object> dismiss = new Action<>(Action.Id.of("user/common.never_show_again"))
                .withLabel("Don't show again")
                .withHandler((e) -> IntellijStore.getInstance().getState().getSuppressedActions().put(tryOpenAIId, true))
                .withAuthRequired(false);
            final AzureString msg = AzureString.format("You can use Azure OpenAI to build your own \"%s\" or other models. " +
                "<a href='https://go.microsoft.com/fwlink/?linkid=2202896'>Learn more</a> about Azure OpenAI.", "Copilot");
            AzureMessager.getMessager().info(msg, "Azure OpenAI is supported!", tryOpenAI, dismiss);
        }
        if (BooleanUtils.isNotTrue(IntellijStore.getInstance().getState().getSuppressedActions().get(tryPlaygroundId))) {
            AzureEventBus.once("account.subscription_changed.account", (_a, _b) -> Azure.az(AzureCognitiveServices.class).list().stream()
                .flatMap(m -> m.accounts().list().stream())
                .flatMap(a -> a.deployments().list().stream())
                .filter(d -> d.getModel().isGPTModel())
                .findFirst().ifPresent(d -> {
                    final Action<CognitiveDeployment> tryPlayGround =
                        new Action<>(TRY_PLAYGROUND)
                            .withIdParam(d.getName())
                            .withLabel("Open in AI Playground")
                            .withSource(d)
                            .withHandler((_d, e) -> {
                                AzureActionManager.getInstance().getAction(OPEN_DEPLOYMENT_IN_PLAYGROUND).handle(d, e);
                                IntellijStore.getInstance().getState().getSuppressedActions().put(tryPlaygroundId, true);
                            })
                            .withAuthRequired(true);
                    final Action<Object> dismiss = new Action<>(Action.Id.of("user/common.never_show_again"))
                        .withLabel("Don't show again")
                        .withHandler((e) -> IntellijStore.getInstance().getState().getSuppressedActions().put(tryPlaygroundId, true))
                        .withAuthRequired(false);
                    final AzureString msg = AzureString.format("GPT* model based deployment (%s) is detected in your Azure Cognitive Services account (%s). " +
                        "You can try your own \"%s\" in AI playground.", d.getName(), d.getParent().getName(), "Copilot");
                    AzureMessager.getMessager().info(msg, "GPT* model is detected!", tryPlayGround, dismiss);
                }));
        }
        return null;
    }
}