package iskallia.vault.entity.renderer.dungeon;

import iskallia.vault.VaultMod;
import javax.annotation.Nonnull;
import net.minecraft.client.renderer.entity.WitchRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Witch;

public class DungeonWitchRenderer extends WitchRenderer {
   public static final ResourceLocation TEXTURE = VaultMod.id("textures/entity/dungeon/witch.png");

   public DungeonWitchRenderer(Context context) {
      super(context);
   }

   @Nonnull
   public ResourceLocation getTextureLocation(@Nonnull Witch entity) {
      return TEXTURE;
   }
}
