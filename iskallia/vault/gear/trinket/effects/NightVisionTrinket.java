package iskallia.vault.gear.trinket.effects;

import com.google.common.collect.Lists;
import com.google.gson.annotations.Expose;
import iskallia.vault.block.entity.base.HunterHiddenTileEntity;
import iskallia.vault.core.world.data.tile.PartialTile;
import iskallia.vault.core.world.data.tile.TilePredicate;
import iskallia.vault.gear.attribute.VaultGearAttributeInstance;
import iskallia.vault.gear.attribute.custom.EffectGearAttribute;
import iskallia.vault.gear.trinket.GearAttributeTrinket;
import iskallia.vault.gear.trinket.TrinketEffect;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.item.gear.TrinketItem;
import iskallia.vault.network.message.ClientboundNightVisionGogglesParticlesMessage;
import iskallia.vault.skill.ability.effect.spi.HunterAbility;
import iskallia.vault.skill.tree.AbilityTree;
import iskallia.vault.util.calc.AreaOfEffectHelper;
import iskallia.vault.world.data.PlayerAbilitiesData;
import iskallia.vault.world.data.ServerVaults;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
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
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.registries.ForgeRegistries;

public class NightVisionTrinket extends TrinketEffect<NightVisionTrinket.Config> implements GearAttributeTrinket {
   private final MobEffect effect;
   private final int addedAmplifier;
   private final float radius;
   private static final List<TilePredicate> filters = List.of(
      TilePredicate.of("the_vault:wooden_chest{Hidden:0b}", true).orElseThrow(),
      TilePredicate.of("the_vault:gilded_strongbox{Hidden:0b}", true).orElseThrow(),
      TilePredicate.of("the_vault:gilded_chest{Hidden:0b}", true).orElseThrow(),
      TilePredicate.of("the_vault:ornate_strongbox{Hidden:0b}", true).orElseThrow(),
      TilePredicate.of("the_vault:ornate_chest{Hidden:0b}", true).orElseThrow(),
      TilePredicate.of("the_vault:coin_pile{Hidden:0b}", true).orElseThrow(),
      TilePredicate.of("the_vault:living_strongbox{Hidden:0b}", true).orElseThrow(),
      TilePredicate.of("the_vault:living_chest{Hidden:0b}", true).orElseThrow()
   );

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
      return (List<VaultGearAttributeInstance<?>>)(cfg.getEffect() == null
         ? List.of()
         : Lists.newArrayList(
            new VaultGearAttributeInstance[]{
               new VaultGearAttributeInstance<>(ModGearAttributes.EFFECT, new EffectGearAttribute(cfg.getEffect(), cfg.getAddedAmplifier()))
            }
         ));
   }

   protected void forEachTile(Level world, Player player, double radius, Consumer<PartialTile> consumer) {
      double radiusSq = radius * radius;
      int iRadius = Mth.ceil(radius);
      Vec3i radVec = new Vec3i(iRadius, iRadius, iRadius);
      ChunkPos posMin = new ChunkPos(player.blockPosition().subtract(radVec));
      ChunkPos posMax = new ChunkPos(player.blockPosition().offset(radVec));

      for (int xx = posMin.x; xx <= posMax.x; xx++) {
         for (int zz = posMin.z; zz <= posMax.z; zz++) {
            LevelChunk ch = world.getChunkSource().getChunkNow(xx, zz);
            if (ch != null) {
               ch.getBlockEntities().forEach((pos, tile) -> {
                  if (tile != null && pos.distSqr(player.blockPosition()) <= radiusSq) {
                     consumer.accept(PartialTile.at(world, pos));
                  }
               });
            }
         }
      }
   }

   public boolean shouldHighlightTile(PartialTile tile) {
      return filters.stream().anyMatch(filter -> filter.test(tile));
   }

   protected List<NightVisionTrinket.HighlightPosition> selectPositions(ServerLevel world, ServerPlayer player, double radius) {
      List<NightVisionTrinket.HighlightPosition> result = new ArrayList<>();
      this.forEachTile(
         world,
         player,
         radius,
         tile -> {
            AbilityTree abilities = PlayerAbilitiesData.get(player.getLevel()).getAbilities(player);

            for (HunterAbility ability : abilities.getAll(HunterAbility.class, serverPlayer -> true)) {
               if (!ability.getParent().getId().equals("Hunter_Base")
                  && ability.getFilters().stream().anyMatch(filter -> filter.test(tile))
                  && this.shouldHighlightTile(tile)) {
                  if (tile instanceof HunterHiddenTileEntity hiddenTile && hiddenTile.isHidden()) {
                     return;
                  }

                  result.add(new NightVisionTrinket.HighlightPosition(tile.getPos(), ability.getParent().getId()));
                  break;
               }
            }
         }
      );
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
            if (!trinketEffect.get().isUsable(stack, player)) {
               return;
            }

            if (player.tickCount % 10 != 0) {
               return;
            }

            NightVisionTrinket.Config cfg = this.getConfig();
            float radius = AreaOfEffectHelper.adjustAreaOfEffect(player, cfg.radius);
            this.selectPositions((ServerLevel)player.level, player, radius)
               .forEach(
                  highlightPosition -> ModNetwork.CHANNEL
                     .sendTo(
                        new ClientboundNightVisionGogglesParticlesMessage(
                           highlightPosition.blockPos().getX(),
                           highlightPosition.blockPos().getY(),
                           highlightPosition.blockPos().getZ(),
                           30.0,
                           0.0,
                           0.0,
                           highlightPosition.type
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

   public record HighlightPosition(BlockPos blockPos, String type) {
   }
}
