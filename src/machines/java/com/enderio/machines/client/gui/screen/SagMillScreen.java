package com.enderio.machines.client.gui.screen;

import com.enderio.EnderIO;
import com.enderio.api.grindingball.IGrindingBallData;
import com.enderio.base.common.lang.EIOLang;
import com.enderio.core.client.gui.screen.EIOScreen;
import com.enderio.core.client.gui.widgets.EnumIconWidget;
import com.enderio.core.common.util.TooltipUtil;
import com.enderio.core.common.util.Vector2i;
import com.enderio.machines.client.gui.widget.EnergyWidget;
import com.enderio.machines.client.gui.widget.ProgressWidget;
import com.enderio.machines.common.blockentity.SagMillBlockEntity;
import com.enderio.machines.common.lang.MachineLang;
import com.enderio.machines.common.menu.SagMillMenu;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import javax.annotation.Nullable;
import java.util.List;

public class SagMillScreen extends EIOScreen<SagMillMenu> {
    public static final ResourceLocation BG_TEXTURE = EnderIO.loc("textures/gui/sagmill.png");

    public SagMillScreen(SagMillMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @Override
    protected void init() {
        super.init();

        addRenderableOnly(new ProgressWidget.TopDown(this, () -> menu.getBlockEntity().getProgress(), getGuiLeft() + 81, getGuiTop() + 31, 15, 23, 202, 0));

        addRenderableOnly(new EnergyWidget(this, getMenu().getBlockEntity()::getEnergyStorage, 16 + leftPos, 14 + topPos, 9, 42));

        addRenderableOnly(new GrindingBallWidget(142 + leftPos, 23 + topPos));

        addRenderableWidget(new EnumIconWidget<>(this, leftPos + imageWidth - 8 - 12, topPos + 6, () -> menu.getBlockEntity().getRedstoneControl(),
            control -> menu.getBlockEntity().setRedstoneControl(control), EIOLang.REDSTONE_MODE));
    }

    @Override
    protected ResourceLocation getBackgroundImage() {
        return BG_TEXTURE;
    }

    @Override
    protected Vector2i getBackgroundImageSize() {
        return new Vector2i(176, 166);
    }

    private class GrindingBallWidget extends AbstractWidget {
        private static final int U = 186;
        private static final int V = 31;
        private static final int WIDTH = 4;
        private static final int HEIGHT = 16;

        public GrindingBallWidget(int x, int y) {
            super(x, y, WIDTH, HEIGHT, Component.empty());
        }

        // Stop the click noise

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            return false;
        }

        @Override
        public void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {}

        @Nullable
        private IGrindingBallData tooltipDataCache;
        private float tooltipDuraCache;

        @Override
        public void renderWidget(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, SagMillScreen.BG_TEXTURE);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

            SagMillBlockEntity be = SagMillScreen.this.getMenu().getBlockEntity();
            if (be == null)
                return;

            float durability = be.getGrindingBallDamage();
            IGrindingBallData data = be.getGrindingBallData();

            int yOffset = (int) Math.ceil(this.height * (1.0f - durability));
            int height = (int) Math.ceil(this.height * durability);

            poseStack.pushPose();
            blit(poseStack, getX(), getY() + yOffset, U, V + yOffset, width, height);

            if (this.isHoveredOrFocused() && (tooltipDataCache != data || tooltipDuraCache != durability)) {
                tooltipDataCache = data;
                tooltipDuraCache = durability;

                // Gather all parts of the tooltip
                List<Component> tooltipComponents = List.of(
                    TooltipUtil.styledWithArgs(MachineLang.SAG_MILL_GRINDINGBALL_REMAINING, (int) (durability * 100)),
                    MachineLang.SAG_MILL_GRINDINGBALL_TITLE,
                    TooltipUtil.styledWithArgs(EIOLang.GRINDINGBALL_MAIN_OUTPUT, (int) (data.getOutputMultiplier() * 100)),
                    TooltipUtil.styledWithArgs(EIOLang.GRINDINGBALL_BONUS_OUTPUT, (int) (data.getBonusMultiplier() * 100)),
                    TooltipUtil.styledWithArgs(EIOLang.GRINDINGBALL_POWER_USE, (int) (data.getPowerUse() * 100))
                );

                // Build single component
                MutableComponent tooltip = Component.empty().copy();
                for (int i = 0; i < tooltipComponents.size(); i++) {
                    tooltip.append(tooltipComponents.get(i));

                    if (i + 1 < tooltipComponents.size())
                        tooltip.append("\n");
                }

                // Set for display
                setTooltip(Tooltip.create(tooltip));
            }

            poseStack.popPose();
        }

        @Override
        protected ClientTooltipPositioner createTooltipPositioner() {
            return DefaultTooltipPositioner.INSTANCE;
        }
    }
}
