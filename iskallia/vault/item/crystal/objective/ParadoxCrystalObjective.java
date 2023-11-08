package iskallia.vault.item.crystal.objective;

import com.google.gson.JsonObject;
import iskallia.vault.block.VaultCrateBlock;
import iskallia.vault.client.gui.helper.UIHelper;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.data.adapter.basic.EnumAdapter;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.vault.ClassicMobLogic;
import iskallia.vault.core.vault.ClassicPortalLogic;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.VaultLevel;
import iskallia.vault.core.vault.WorldManager;
import iskallia.vault.core.vault.influence.VaultGod;
import iskallia.vault.core.vault.objective.AwardCrateObjective;
import iskallia.vault.core.vault.objective.BailObjective;
import iskallia.vault.core.vault.objective.DeathObjective;
import iskallia.vault.core.vault.objective.Objectives;
import iskallia.vault.core.vault.objective.ParadoxObjective;
import iskallia.vault.core.vault.objective.ScavengerObjective;
import iskallia.vault.core.vault.objective.VictoryObjective;
import iskallia.vault.core.vault.player.ClassicListenersLogic;
import iskallia.vault.core.vault.player.Listeners;
import iskallia.vault.item.crystal.CrystalData;
import iskallia.vault.item.tool.ColorBlender;
import iskallia.vault.world.data.ParadoxCrystalData;
import iskallia.vault.world.data.PlayerVaultStatsData;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.server.ServerLifecycleHooks;

public class ParadoxCrystalObjective extends CrystalObjective {
   protected ParadoxObjective.Type type;
   protected String playerName;
   protected UUID playerUuid;
   protected long expiry;

   public ParadoxCrystalObjective() {
   }

   public ParadoxCrystalObjective(ParadoxObjective.Type type) {
      this.type = type;
   }

   public ParadoxObjective.Type getType() {
      return this.type;
   }

   public void setPlayerUuid(UUID playerUuid) {
      this.playerUuid = playerUuid;
   }

   @Override
   public void configure(Vault vault, RandomSource random) {
      int level = PlayerVaultStatsData.get(ServerLifecycleHooks.getCurrentServer()).getVaultStats(this.playerUuid).getVaultLevel();
      vault.get(Vault.LEVEL).set(VaultLevel.VALUE, Integer.valueOf(level));
      if (vault.get(Vault.LISTENERS).get(Listeners.LOGIC) instanceof ClassicListenersLogic classic) {
         if (this.type == ParadoxObjective.Type.RUN) {
            classic.set(ClassicListenersLogic.MAX_PLAYERS, Integer.valueOf(1));
         }

         if (this.type == ParadoxObjective.Type.BUILD) {
            classic.set(ClassicListenersLogic.GAME_MODE, GameType.ADVENTURE);
         }
      }

      if (this.type == ParadoxObjective.Type.BUILD && vault.get(Vault.WORLD).get(WorldManager.MOB_LOGIC) instanceof ClassicMobLogic classic) {
         classic.set(ClassicMobLogic.BLOCK_SPAWNS);
      }

      vault.ifPresent(
         Vault.OBJECTIVES,
         objectives -> {
            if (this.type == ParadoxObjective.Type.RUN) {
               objectives.add(
                  ScavengerObjective.of(0.0F, ScavengerObjective.Config.DIVINE_PARADOX)
                     .add(AwardCrateObjective.ofConfig(VaultCrateBlock.Type.PARADOX, "paradox", level, true))
                     .add(VictoryObjective.of(300))
               );
            }

            objectives.add(ParadoxObjective.of(this.type, this.playerUuid, this.getSeed()));
            objectives.add(BailObjective.create(ClassicPortalLogic.EXIT));
            objectives.add(DeathObjective.create(true));
            objectives.set(Objectives.KEY, CrystalData.OBJECTIVE.getType(this));
         }
      );
   }

   @Override
   public void addText(List<Component> tooltip, TooltipFlag flag, float time) {
      tooltip.add(new TextComponent("Objective: ").append(this.styleLetters("Divine Paradox", time, 2.0F)));
      tooltip.add(
         new TextComponent("")
            .append(new TextComponent(" • ").withStyle(ChatFormatting.GRAY))
            .append(new TextComponent("Type: "))
            .append(this.styleLetters(this.type.getName(), time, 2.0F))
      );
      if (this.playerUuid == null) {
         if (this.type == ParadoxObjective.Type.RUN) {
            tooltip.add(
               new TextComponent("")
                  .append(new TextComponent(" • ").withStyle(ChatFormatting.GRAY))
                  .append(new TextComponent("Cooldown: ???").withStyle(ChatFormatting.GRAY))
            );
         }

         tooltip.add(
            new TextComponent("")
               .append(new TextComponent(" • ").withStyle(ChatFormatting.GRAY))
               .append(new TextComponent("Player: ???").withStyle(ChatFormatting.GRAY))
         );
      } else {
         long ticksLeft = this.getCooldown() / 50L;
         if (this.type == ParadoxObjective.Type.RUN) {
            tooltip.add(
               new TextComponent("")
                  .append(new TextComponent(" • ").withStyle(ChatFormatting.GRAY))
                  .append(new TextComponent("Cooldown: "))
                  .append(new TextComponent(ticksLeft < 0L ? "Ready" : UIHelper.formatTimeString((int)ticksLeft)).withStyle(ChatFormatting.GRAY))
            );
         }

         tooltip.add(
            new TextComponent("")
               .append(new TextComponent(" • ").withStyle(ChatFormatting.GRAY))
               .append(new TextComponent("Player: "))
               .append(new TextComponent(this.playerName == null ? "Unknown" : this.playerName).withStyle(ChatFormatting.YELLOW))
         );
      }
   }

   @Override
   public void onWorldTick(Level world, BlockPos pos, BlockState state) {
      if (this.isExpired()) {
         world.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
      }

      super.onWorldTick(world, pos, state);
   }

   @Override
   public boolean onPlaced(UseOnContext context) {
      if (this.playerUuid != null && (this.type != ParadoxObjective.Type.RUN || this.getCooldown() <= 0L)) {
         if (this.type == ParadoxObjective.Type.RUN) {
            long unlockTime = ZonedDateTime.now().plusHours(20L).withZoneSameInstant(ZoneId.of("UTC")).toInstant().toEpochMilli();
            ParadoxCrystalData.Entry entry = ParadoxCrystalData.getEntry(this.playerUuid);
            entry.unlockTime = unlockTime;
            entry.changed = true;
            this.expiry = unlockTime;
         }

         return super.onPlaced(context);
      } else {
         super.onPlaced(context);
         return false;
      }
   }

   public long getCooldown() {
      long time = ZonedDateTime.now().withZoneSameInstant(ZoneId.of("UTC")).toInstant().toEpochMilli();
      return ParadoxCrystalData.getEntry(this.playerUuid).unlockTime - time;
   }

   public boolean isExpired() {
      if (this.expiry <= 0L) {
         return false;
      } else {
         long time = ZonedDateTime.now().withZoneSameInstant(ZoneId.of("UTC")).toInstant().toEpochMilli();
         return time > this.expiry;
      }
   }

   private long getSeed() {
      return ParadoxCrystalData.getEntry(this.playerUuid).seed;
   }

   @Override
   public void onInventoryTick(Level world, Entity entity, int slot, boolean selected) {
      if (entity instanceof ServerPlayer player) {
         this.playerName = player.getGameProfile().getName();
         this.playerUuid = player.getGameProfile().getId();
      }
   }

   private TextComponent styleLetters(String string, float time, float offset) {
      TextComponent text = new TextComponent("");
      int count = 0;

      for (int i = 0; i < string.length(); i++) {
         char c = string.charAt(i);
         text.append(new TextComponent(String.valueOf(c)).withStyle(Style.EMPTY.withColor(this.getColor(time + count * offset).orElseThrow())));
         if (c != ' ') {
            count++;
         }
      }

      return text;
   }

   @Override
   public Optional<Integer> getColor(float time) {
      ColorBlender blender = new ColorBlender(1.0F)
         .add(VaultGod.VELARA.getColor(), 60.0F)
         .add(VaultGod.WENDARR.getColor(), 60.0F)
         .add(VaultGod.TENOS.getColor(), 60.0F)
         .add(VaultGod.IDONA.getColor(), 60.0F);
      return Optional.of(blender.getColor(time));
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      CompoundTag nbt = new CompoundTag();
      Adapters.ofEnum(ParadoxObjective.Type.class, EnumAdapter.Mode.NAME).writeNbt(this.type).ifPresent(tag -> nbt.put("goal", tag));
      Adapters.UTF_8.writeNbt(this.playerName).ifPresent(tag -> nbt.put("player_name", tag));
      Adapters.UUID.writeNbt(this.playerUuid).ifPresent(tag -> nbt.put("player_uuid", tag));
      if (this.expiry >= 0L) {
         Adapters.LONG.writeNbt(Long.valueOf(this.expiry)).ifPresent(tag -> nbt.put("expiry", tag));
      }

      return Optional.of(nbt);
   }

   public void readNbt(CompoundTag nbt) {
      this.type = Adapters.ofEnum(ParadoxObjective.Type.class, EnumAdapter.Mode.NAME).readNbt(nbt.get("goal")).orElse(ParadoxObjective.Type.BUILD);
      this.playerName = Adapters.UTF_8.readNbt(nbt.get("player_name")).orElse(null);
      this.playerUuid = Adapters.UUID.readNbt(nbt.get("player_uuid")).orElse(null);
      this.expiry = Adapters.LONG.readNbt(nbt.get("expiry")).orElse(0L);
   }

   @Override
   public Optional<JsonObject> writeJson() {
      JsonObject json = new JsonObject();
      Adapters.ofEnum(ParadoxObjective.Type.class, EnumAdapter.Mode.NAME).writeJson(this.type).ifPresent(tag -> json.add("goal", tag));
      Adapters.UTF_8.writeJson(this.playerName).ifPresent(tag -> json.add("player_name", tag));
      Adapters.UUID.writeJson(this.playerUuid).ifPresent(tag -> json.add("player_uuid", tag));
      if (this.expiry >= 0L) {
         Adapters.LONG.writeJson(Long.valueOf(this.expiry)).ifPresent(tag -> json.add("expiry", tag));
      }

      return Optional.of(json);
   }

   public void readJson(JsonObject json) {
      this.type = Adapters.ofEnum(ParadoxObjective.Type.class, EnumAdapter.Mode.NAME).readJson(json.get("goal")).orElse(ParadoxObjective.Type.BUILD);
      this.playerName = Adapters.UTF_8.readJson(json.get("player_name")).orElse(null);
      this.playerUuid = Adapters.UUID.readJson(json.get("player_uuid")).orElse(null);
      this.expiry = Adapters.LONG.readJson(json.get("expiry")).orElse(0L);
   }
}
