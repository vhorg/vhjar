package iskallia.vault.client.gui.framework.render.spi;

import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.client.atlas.TextureAtlasRegion;
import iskallia.vault.client.gui.framework.render.NineSlice;
import iskallia.vault.client.gui.framework.spatial.spi.IPosition;
import iskallia.vault.client.gui.framework.spatial.spi.ISize;
import iskallia.vault.client.gui.framework.spatial.spi.ISpatial;
import net.minecraft.client.gui.Font;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.Item;

public interface IElementRenderer {
   void render(Item var1, PoseStack var2, IPosition var3);

   void render(TextureAtlasRegion var1, PoseStack var2, IPosition var3);

   void render(TextureAtlasRegion var1, PoseStack var2, IPosition var3, ISize var4);

   void render(TextureAtlasRegion var1, PoseStack var2, int var3, int var4, int var5);

   void render(TextureAtlasRegion var1, PoseStack var2, int var3, int var4, int var5, int var6, int var7);

   void render(TextureAtlasRegion var1, PoseStack var2, int var3, int var4, int var5, int var6, int var7, float var8, float var9, float var10, float var11);

   void render(NineSlice.TextureRegion var1, PoseStack var2, ISpatial var3);

   void render(NineSlice.TextureRegion var1, PoseStack var2, int var3, int var4, int var5, int var6, int var7);

   default void renderColoredQuad(PoseStack poseStack, int color, ISpatial spatial) {
      this.renderColoredQuad(poseStack, color, spatial.x(), spatial.y(), spatial.z(), spatial.width(), spatial.height());
   }

   void renderColoredQuad(PoseStack var1, int var2, int var3, int var4, int var5, int var6, int var7);

   default void renderColoredHollowRect(PoseStack poseStack, int color, ISpatial spatial) {
      this.renderColoredHollowRect(poseStack, color, spatial.x(), spatial.y(), spatial.z(), spatial.width(), spatial.height(), 1);
   }

   void renderColoredHollowRect(PoseStack var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8);

   void renderPlain(FormattedCharSequence var1, Font var2, PoseStack var3, int var4, int var5, int var6, int var7);

   void renderShadow(FormattedCharSequence var1, FormattedCharSequence var2, Font var3, PoseStack var4, int var5, int var6, int var7, int var8, int var9);

   void renderBorder4(FormattedCharSequence var1, FormattedCharSequence var2, Font var3, PoseStack var4, int var5, int var6, int var7, int var8, int var9);

   void renderBorder8(FormattedCharSequence var1, FormattedCharSequence var2, Font var3, PoseStack var4, int var5, int var6, int var7, int var8, int var9);

   default void beginFrame() {
   }

   default void endFrame() {
   }

   default void begin() {
   }

   default void end() {
   }

   void beginClipRegion(ISpatial var1);

   void endClipRegion();
}
