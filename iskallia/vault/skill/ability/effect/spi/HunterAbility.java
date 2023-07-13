package iskallia.vault.skill.ability.effect.spi;

import com.google.gson.JsonObject;
import iskallia.vault.block.entity.base.HunterHiddenTileEntity;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.data.adapter.array.ArrayAdapter;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.core.world.data.tile.PartialTile;
import iskallia.vault.core.world.data.tile.TilePredicate;
import iskallia.vault.gear.attribute.ability.special.HunterRangeModification;
import iskallia.vault.gear.attribute.ability.special.base.ConfiguredModification;
import iskallia.vault.gear.attribute.ability.special.base.SpecialAbilityModification;
import iskallia.vault.gear.attribute.ability.special.base.template.FloatValueConfig;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.init.ModSounds;
import iskallia.vault.network.message.ClientboundHunterParticlesMessage;
import iskallia.vault.skill.ability.effect.spi.core.Ability;
import iskallia.vault.skill.ability.effect.spi.core.InstantManaAbility;
import iskallia.vault.skill.base.SkillContext;
import iskallia.vault.util.MiscUtils;
import iskallia.vault.util.ServerScheduler;
import iskallia.vault.util.calc.AreaOfEffectHelper;
import iskallia.vault.world.data.ServerVaults;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkDirection;

public class HunterAbility extends InstantManaAbility {
   private double searchRadius;
   private int color;
   private int durationTicks;
   private List<TilePredicate> filters;
   private static ArrayAdapter<TilePredicate> KEYS = Adapters.ofArray(TilePredicate[]::new, Adapters.TILE_PREDICATE);

   public HunterAbility(
      int unlockLevel,
      int learnPointCost,
      int regretPointCost,
      int cooldownTicks,
      float manaCost,
      double searchRadius,
      int color,
      int durationTicks,
      List<TilePredicate> filters
   ) {
      super(unlockLevel, learnPointCost, regretPointCost, cooldownTicks, manaCost);
      this.searchRadius = searchRadius;
      this.color = color;
      this.durationTicks = durationTicks;
      this.filters = filters;
   }

   public HunterAbility() {
   }

   public double getUnmodifiedSearchRadius() {
      return this.searchRadius;
   }

   public double getRadius(Entity attacker) {
      double realRadius = this.getUnmodifiedSearchRadius();
      if (attacker instanceof Player player) {
         for (ConfiguredModification<FloatValueConfig, HunterRangeModification> mod : SpecialAbilityModification.getModifications(
            player, HunterRangeModification.class
         )) {
            realRadius = mod.modification().adjustRange(mod.config(), realRadius);
         }
      }

      if (attacker instanceof LivingEntity livingEntity) {
         realRadius = AreaOfEffectHelper.adjustAreaOfEffect(livingEntity, (float)realRadius);
      }

      return realRadius;
   }

   public int getColor() {
      return this.color;
   }

   public int getDurationTicks() {
      return this.durationTicks;
   }

   public List<TilePredicate> getFilters() {
      return this.filters;
   }

   public boolean shouldHighlightTile(PartialTile tile) {
      return this.filters.stream().anyMatch(filter -> filter.test(tile));
   }

   @Override
   public String getAbilityGroupName() {
      return "Hunter";
   }

   @Override
   protected boolean canDoAction(SkillContext context) {
      return context.getSource().as(ServerPlayer.class).map(player -> super.canDoAction(context) && ServerVaults.get(player.level).isPresent()).orElse(false);
   }

   @Override
   protected Ability.ActionResult doAction(SkillContext context) {
      return context.getSource()
         .as(ServerPlayer.class)
         .map(
            player -> {
               if (!(player.getCommandSenderWorld() instanceof ServerLevel serverWorld)) {
                  return Ability.ActionResult.fail();
               } else {
                  for (int delay = 0; delay < this.getDurationTicks() / 5; delay++) {
                     ServerScheduler.INSTANCE
                        .schedule(
                           delay * 5,
                           () -> this.selectPositions(serverWorld, player)
                              .forEach(
                                 highlightPosition -> {
                                    Color color = highlightPosition.color;

                                    for (int i = 0; i < 8; i++) {
                                       Vec3 v = MiscUtils.getRandomOffset(highlightPosition.blockPos, serverWorld.getRandom());
                                       ModNetwork.CHANNEL
                                          .sendTo(
                                             new ClientboundHunterParticlesMessage(
                                                v.x, v.y, v.z, color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F
                                             ),
                                             player.connection.getConnection(),
                                             NetworkDirection.PLAY_TO_CLIENT
                                          );
                                    }
                                 }
                              )
                        );
                  }

                  return Ability.ActionResult.successCooldownImmediate();
               }
            }
         )
         .orElse(Ability.ActionResult.fail());
   }

   @Override
   protected void doParticles(SkillContext context) {
   }

   @Override
   protected void doSound(SkillContext context) {
      context.getSource()
         .as(ServerPlayer.class)
         .ifPresent(
            player -> player.level
               .playSound(null, player.position().x, player.position().y, player.position().z, ModSounds.HUNTER_SFX, SoundSource.PLAYERS, 1.0F, 1.0F)
         );
   }

   protected List<HunterAbility.HighlightPosition> selectPositions(ServerLevel world, ServerPlayer player) {
      List<HunterAbility.HighlightPosition> result = new ArrayList<>();
      Color c = new Color(this.getColor(), false);
      this.forEachTile(world, player, tile -> {
         if (this.shouldHighlightTile(tile)) {
            if (tile instanceof HunterHiddenTileEntity hiddenTile && hiddenTile.isHidden()) {
               return;
            }

            result.add(new HunterAbility.HighlightPosition(tile.getPos(), c));
         }
      });
      return result;
   }

   protected void forEachTile(Level world, Player player, Consumer<PartialTile> consumer) {
      BlockPos playerOffset = player.blockPosition();
      double radius = this.getRadius(player);
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
                  if (tile != null && pos.distSqr(playerOffset) <= radiusSq) {
                     consumer.accept(PartialTile.at(world, pos));
                  }
               });
            }
         }
      }
   }

   @Override
   public void writeBits(BitBuffer buffer) {
      super.writeBits(buffer);
      Adapters.DOUBLE.writeBits(Double.valueOf(this.searchRadius), buffer);
      Adapters.INT.writeBits(Integer.valueOf(this.color), buffer);
      Adapters.INT_SEGMENTED_7.writeBits(Integer.valueOf(this.durationTicks), buffer);
      KEYS.writeBits(this.filters.toArray(TilePredicate[]::new), buffer);
   }

   @Override
   public void readBits(BitBuffer buffer) {
      super.readBits(buffer);
      this.searchRadius = Adapters.DOUBLE.readBits(buffer).orElseThrow();
      this.color = Adapters.INT.readBits(buffer).orElseThrow();
      this.durationTicks = Adapters.INT_SEGMENTED_7.readBits(buffer).orElseThrow();
      this.filters = Arrays.stream(KEYS.readBits(buffer).orElseThrow()).toList();
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return super.writeNbt().map(nbt -> {
         Adapters.DOUBLE.writeNbt(Double.valueOf(this.searchRadius)).ifPresent(tag -> nbt.put("searchRadius", tag));
         Adapters.INT.writeNbt(Integer.valueOf(this.color)).ifPresent(tag -> nbt.put("color", tag));
         Adapters.INT.writeNbt(Integer.valueOf(this.durationTicks)).ifPresent(tag -> nbt.put("durationTicks", tag));
         KEYS.writeNbt(this.filters.toArray(TilePredicate[]::new)).ifPresent(tag -> nbt.put("filters", tag));
         return (CompoundTag)nbt;
      });
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
      this.searchRadius = Adapters.DOUBLE.readNbt(nbt.get("searchRadius")).orElse(0.0);
      this.color = Adapters.INT.readNbt(nbt.get("color")).orElse(0);
      this.durationTicks = Adapters.INT.readNbt(nbt.get("durationTicks")).orElse(0);
      this.filters = Arrays.stream(KEYS.readNbt(nbt.get("filters")).orElse(new TilePredicate[0])).toList();
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return super.writeJson().map(json -> {
         Adapters.DOUBLE.writeJson(Double.valueOf(this.searchRadius)).ifPresent(element -> json.add("searchRadius", element));
         Adapters.INT.writeJson(Integer.valueOf(this.color)).ifPresent(element -> json.add("color", element));
         Adapters.INT.writeJson(Integer.valueOf(this.durationTicks)).ifPresent(element -> json.add("durationTicks", element));
         KEYS.writeJson(this.filters.toArray(TilePredicate[]::new)).ifPresent(element -> json.add("filters", element));
         return (JsonObject)json;
      });
   }

   @Override
   public void readJson(JsonObject json) {
      super.readJson(json);
      this.searchRadius = Adapters.DOUBLE.readJson(json.get("searchRadius")).orElse(0.0);
      this.color = Adapters.INT.readJson(json.get("color")).orElse(0);
      this.durationTicks = Adapters.INT.readJson(json.get("durationTicks")).orElse(0);
      this.filters = Arrays.stream(KEYS.readJson(json.get("filters")).orElse(new TilePredicate[0])).toList();
   }

   public record HighlightPosition(BlockPos blockPos, Color color) {
   }
}
