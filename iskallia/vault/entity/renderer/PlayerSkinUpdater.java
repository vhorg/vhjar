package iskallia.vault.entity.renderer;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import iskallia.vault.entity.IPlayerSkinHolder;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.client.resources.SkinManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.SkullBlockEntity;

public class PlayerSkinUpdater {
   public ResourceLocation updatePlayerSkin(IPlayerSkinHolder entity, GameProfile gameProfile) {
      if (entity.isUpdatingSkin()) {
         return DefaultPlayerSkin.getDefaultSkin();
      } else {
         Minecraft minecraft = Minecraft.getInstance();
         if (!gameProfile.getProperties().containsKey("textures")) {
            entity.startUpdatingSkin();
            SkullBlockEntity.updateGameprofile(gameProfile, gp -> {
               entity.setGameProfile(gp);
               if (!gp.getProperties().containsKey("textures")) {
                  entity.setSkinLocation(DefaultPlayerSkin.getDefaultSkin());
               } else {
                  this.setSkinData(minecraft, gp, entity);
               }

               entity.stopUpdatingSkin();
            });
            return DefaultPlayerSkin.getDefaultSkin();
         } else {
            this.setSkinData(minecraft, gameProfile, entity);
            return entity.getSkinLocation().orElse(DefaultPlayerSkin.getDefaultSkin());
         }
      }
   }

   private void setSkinData(Minecraft minecraft, GameProfile gameProfile, IPlayerSkinHolder entity) {
      SkinManager skinManager = minecraft.getSkinManager();
      Map<Type, MinecraftProfileTexture> skinInfo = skinManager.getInsecureSkinInformation(gameProfile);
      if (skinInfo.containsKey(Type.SKIN)) {
         MinecraftProfileTexture profileTexture = skinInfo.get(Type.SKIN);
         entity.setSkinLocation(minecraft.getSkinManager().registerTexture(profileTexture, Type.SKIN));
         String metadata = profileTexture.getMetadata("model");
         entity.setSlimSkin(metadata != null && !metadata.equals("default"));
      } else {
         entity.setSkinLocation(DefaultPlayerSkin.getDefaultSkin());
         entity.setSlimSkin(false);
      }
   }
}
