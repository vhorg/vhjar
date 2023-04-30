package iskallia.vault.gear.trinket.effects;

import com.google.common.collect.Lists;
import com.google.gson.annotations.Expose;
import iskallia.vault.block.entity.base.HunterHiddenTileEntity;
import iskallia.vault.gear.attribute.VaultGearAttributeInstance;
import iskallia.vault.gear.attribute.custom.EffectGearAttribute;
import iskallia.vault.gear.trinket.GearAttributeTrinket;
import iskallia.vault.gear.trinket.TrinketEffect;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.item.gear.TrinketItem;
import iskallia.vault.network.message.ClientboundNightVisionGogglesParticlesMessage;
import iskallia.vault.skill.ability.effect.spi.HunterAbility;
import iskallia.vault.world.data.ServerVaults;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.registries.ForgeRegistries;

public class NightVisionTrinket extends TrinketEffect<NightVisionTrinket.Config> implements GearAttributeTrinket {
   private final MobEffect effect;
   private final int addedAmplifier;
   private final float radius;

   public NightVisionTrinket(ResourceLocation name, MobEffect effect, int addedAmplifier, float radius) {
      super(name);
      this.effect = effect;
      this.addedAmplifier = addedAmplifier;
      this.radius = radius;
   }

   @Override
   public Class<NightVisionTrinket.Config> getConfigClass() {
      return NightVisionTrinket.Config.class;
   }

   public NightVisionTrinket.Config getDefaultConfig() {
      return new NightVisionTrinket.Config(this.effect.getRegistryName(), this.addedAmplifier, this.radius);
   }

   @Override
   public List<VaultGearAttributeInstance<?>> getAttributes() {
      NightVisionTrinket.Config cfg = this.getConfig();
      return Lists.newArrayList(
         new VaultGearAttributeInstance[]{
            new VaultGearAttributeInstance<>(ModGearAttributes.EFFECT, new EffectGearAttribute(cfg.getEffect(), cfg.getAddedAmplifier()))
         }
      );
   }

   protected void forEachTileEntity(Level world, Player player, double radius, BiConsumer<BlockPos, BlockEntity> consumer) {
      BlockPos playerOffset = player.blockPosition();
      double radiusSq = radius * radius;
      int iRadius = Mth.ceil(radius);
      Vec3i radVec = new Vec3i(iRadius, iRadius, iRadius);
      ChunkPos posMin = new ChunkPos(playerOffset.subtract(radVec));
      ChunkPos posMax = new ChunkPos(playerOffset.offset(radVec));

      for (int xx = posMin.x; xx <= posMax.x; xx++) {
         for (int zz = posMin.z; zz <= posMax.z; zz++) {
            LevelChunk ch = world.getChunkSource().getChunkNow(xx, zz);
            if (ch != null) {
               ch.getBlockEntities().forEach((pos, tile) -> {
                  if (tile != null && pos.distSqr(playerOffset) <= radiusSq) {
                     consumer.accept(pos, tile);
                  }
               });
            }
         }
      }
   }

   public boolean shouldHighlightTileEntity(BlockEntity tile) {
      if (tile.getType().getRegistryName() == null) {
         return false;
      } else {
         String var2 = tile.getType().getRegistryName().toString();
         switch (var2) {
            case "minecraft:chest":
            case "minecraft:trapped_chest":
            case "the_vault:coin_pile":
            case "the_vault:vault_chest_tile_entity":
               return true;
            default:
               return false;
         }
      }
   }

   protected List<HunterAbility.HighlightPosition> selectPositions(ServerLevel world, ServerPlayer player, double radius) {
      List<HunterAbility.HighlightPosition> result = new ArrayList<>();
      Color c = Color.cyan;
      this.forEachTileEntity(world, player, radius, (pos, tile) -> {
         if (this.shouldHighlightTileEntity(tile)) {
            if (tile instanceof HunterHiddenTileEntity hiddenTile && hiddenTile.isHidden()) {
               return;
            }

            result.add(new HunterAbility.HighlightPosition(pos, c));
         }
      });
      return result;
   }

   @Override
   public void onWornTick(LivingEntity entity, ItemStack stack) {
      super.onWornTick(entity, stack);
      if (entity instanceof ServerPlayer player) {
         if (ServerVaults.get(player.level).isEmpty()) {
            return;
         }

         Optional<TrinketEffect<?>> trinketEffect = TrinketItem.getTrinket(stack);
         if (trinketEffect.isPresent()) {
            if (player.tickCount % 10 != 0) {
               return;
            }

            NightVisionTrinket.Config cfg = this.getConfig();
            this.selectPositions((ServerLevel)player.level, player, cfg.radius)
               .forEach(
                  highlightPosition -> ModNetwork.CHANNEL
                     .sendTo(
                        new ClientboundNightVisionGogglesParticlesMessage(
                           highlightPosition.blockPos().getX(), highlightPosition.blockPos().getY(), highlightPosition.blockPos().getZ(), 30.0, 0.0, 0.0
                        ),
                        player.connection.getConnection(),
                        NetworkDirection.PLAY_TO_CLIENT
                     )
               );
         }
      }
   }

   public static class Config extends TrinketEffect.Config {
      @Expose
      private ResourceLocation effect;
      @Expose
      private int addedAmplifier;
      @Expose
      private float radius;

      public Config(ResourceLocation effect, int addedAmplifier, float radius) {
         this.effect = effect;
         this.addedAmplifier = addedAmplifier;
         this.radius = radius;
      }

      public MobEffect getEffect() {
         return (MobEffect)ForgeRegistries.MOB_EFFECTS.getValue(this.effect);
      }

      public int getAddedAmplifier() {
         return this.addedAmplifier;
      }

      public float getRadius() {
         return this.radius;
      }
   }
}
