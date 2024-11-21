package iskallia.vault.core.vault.objective.offering;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import iskallia.vault.VaultMod;
import iskallia.vault.block.entity.challenge.raid.RaidChallengeManager;
import iskallia.vault.client.gui.helper.LightmapHelper;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.data.adapter.basic.EnumAdapter;
import iskallia.vault.core.data.sync.context.SyncContext;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.world.data.entity.PartialEntity;
import iskallia.vault.core.world.storage.BlockCuboid;
import iskallia.vault.core.world.storage.IZonedWorld;
import iskallia.vault.core.world.storage.VirtualWorld;
import iskallia.vault.entity.boss.VaultBossEntity;
import iskallia.vault.world.data.WorldZonesData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.function.IntFunction;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.registries.ForgeRegistries;

public class OfferingBossFight {
   private BlockPos origin;
   private BlockCuboid zone;
   private int zoneId;
   private OfferingBossFight.RoomStyle roomStyle;
   private final Set<UUID> players;
   private PartialEntity boss;
   private Map<String, Integer> modifiers;
   private List<ItemStack> loot;
   private UUID bossId;
   private double currentHealth;
   private double totalHealth;
   private final OfferingBossAnimation animation;
   private boolean completed;

   public OfferingBossFight() {
      this.players = new HashSet<>();
      this.animation = new OfferingBossAnimation();
      this.modifiers = new HashMap<>();
      this.loot = new ArrayList<>();
   }

   public OfferingBossFight(
      BlockPos origin,
      BlockCuboid zone,
      int zoneId,
      OfferingBossFight.RoomStyle roomStyle,
      PartialEntity boss,
      Map<String, Integer> modifiers,
      List<ItemStack> loot
   ) {
      this.origin = origin;
      this.zone = zone;
      this.zoneId = zoneId;
      this.roomStyle = roomStyle;
      this.players = new HashSet<>();
      this.boss = boss;
      this.animation = new OfferingBossAnimation();
      this.modifiers = modifiers;
      this.loot = loot;
   }

   public Set<UUID> getPlayers() {
      return this.players;
   }

   public boolean isCompleted() {
      return this.completed;
   }

   public void onAttach(VirtualWorld world, Vault vault) {
      CommonEvents.SERVER_TICK.at(Phase.END).register(this, event -> this.onTick(world, vault));
   }

   public void onDetach() {
      CommonEvents.SERVER_TICK.release(this);
   }

   public void onTick(VirtualWorld world, Vault vault) {
      if (!this.completed) {
         this.refreshPlayers(world);
         if (this.animation.getState() == OfferingBossAnimation.State.IDLE) {
            this.animation.onStart(OfferingBossAnimation.State.CLOSE_ROOM);
         }

         if (this.animation.getState() == OfferingBossAnimation.State.CLOSE_ROOM && this.animation.isCompleted()) {
            IZonedWorld.runWithBypass(world, true, () -> world.setBlock(this.origin, Blocks.AIR.defaultBlockState(), 3));
            this.summonBoss(world);
            this.animation.onStart(OfferingBossAnimation.State.FIGHT);
         }

         if (this.animation.getState() == OfferingBossAnimation.State.FIGHT && this.bossId == null) {
            this.animation.onStart(OfferingBossAnimation.State.OPEN_ROOM);
         }

         if (this.animation.getState() == OfferingBossAnimation.State.OPEN_ROOM && this.animation.isCompleted()) {
            WorldZonesData.get(world.getServer()).getOrCreate(world.dimension()).remove(this.zoneId);
            this.completed = true;
         }

         this.updateHealth(world);
         this.animation.onTick(world, this.origin, this.roomStyle, vault);
      }
   }

   public void summonBoss(ServerLevel world) {
      EntityType<?> type = (EntityType<?>)ForgeRegistries.ENTITIES.getValue(this.boss.getId());
      Entity entity = type.spawn(world, null, null, null, this.origin, MobSpawnType.SPAWNER, false, false);
      this.boss.getNbt().asWhole().ifPresent(nbt -> entity.deserializeNBT(nbt));
      entity.setPos(this.origin.getX() + 0.5, this.origin.getY(), this.origin.getZ() + 0.5);
      if (entity instanceof VaultBossEntity boss) {
         boss.setLoot(this.loot);
         boss.addTraits(this.modifiers);
         boss.setPersistenceRequired();
      }

      world.addFreshEntity(entity);
      this.bossId = entity.getUUID();
   }

   private void updateHealth(VirtualWorld world) {
      if (this.bossId != null) {
         Entity entity = world.getEntity(this.bossId);
         if (entity == null) {
            this.bossId = null;
         } else {
            double total = 0.0;
            this.currentHealth = 0.0;
            if (entity instanceof LivingEntity living) {
               this.currentHealth = this.currentHealth + living.getHealth();
               total += living.getMaxHealth();
            }

            if (total > this.totalHealth) {
               this.totalHealth = total;
            }
         }
      }
   }

   public void refreshPlayers(ServerLevel world) {
      Set<ServerPlayer> toRemove = new HashSet<>();

      for (UUID uuid : this.players) {
         ServerPlayer player = world.getServer().getPlayerList().getPlayer(uuid);
         if (player != null && (player.level != world || !this.zone.offset(this.origin).grow(-1, -1, -1).contains(player.blockPosition()))) {
            toRemove.add(player);
         }
      }

      for (ServerPlayer player : toRemove) {
         this.players.remove(player.getUUID());
      }

      for (ServerPlayer player : world.players()) {
         if (this.zone.offset(this.origin).grow(-1, -1, -1).contains(player.blockPosition())) {
            this.players.add(player.getUUID());
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   public void render(PoseStack matrixStack, Window window, float partialTicks) {
      Component txt = new TextComponent("").withStyle(ChatFormatting.WHITE).append(new TextComponent("Defeat the Boss").withStyle(ChatFormatting.WHITE));
      int midX = window.getGuiScaledWidth() / 2;
      matrixStack.pushPose();
      RenderSystem.setShader(GameRenderer::getPositionTexShader);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      int previousTexture = RenderSystem.getShaderTexture(0);
      RenderSystem.setShaderTexture(0, RaidChallengeManager.HUD);
      double progress = this.currentHealth / this.totalHealth;
      matrixStack.translate(midX - 80, 8.0, 0.0);
      GuiComponent.blit(matrixStack, 0, 0, 0.0F, 0.0F, 200, 26, 200, 50);
      GuiComponent.blit(matrixStack, 0, 8, 0.0F, 28.0F, 15 + (int)(130.0 * progress), 10, 200, 50);
      RenderSystem.setShader(GameRenderer::getPositionTexShader);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.setShaderTexture(0, previousTexture);
      matrixStack.popPose();
      Font font = Minecraft.getInstance().font;
      BufferSource buffer = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
      matrixStack.pushPose();
      matrixStack.scale(0.6F, 0.6F, 0.6F);
      font.drawInBatch(
         txt.getVisualOrderText(),
         midX / 0.6F - font.width(txt) / 2.0F,
         9 + 22,
         -1,
         true,
         matrixStack.last().pose(),
         buffer,
         false,
         0,
         LightmapHelper.getPackedFullbrightCoords()
      );
      buffer.endBatch();
      matrixStack.popPose();
   }

   public void writeBits(BitBuffer buffer, SyncContext context) {
      Adapters.BLOCK_POS.writeBits(this.origin, buffer);
      Adapters.BLOCK_CUBOID.writeBits(this.zone, buffer);
      Adapters.INT_SEGMENTED_7.writeBits(Integer.valueOf(this.zoneId), buffer);
      Adapters.ofEnum(OfferingBossFight.RoomStyle.class, EnumAdapter.Mode.NAME).writeBits(this.roomStyle, buffer);
      Adapters.INT_SEGMENTED_3.writeBits(Integer.valueOf(this.players.size()), buffer);

      for (UUID player : this.players) {
         Adapters.UUID.writeBits(player, buffer);
      }

      Adapters.PARTIAL_ENTITY.writeBits(this.boss, buffer);
      Adapters.DOUBLE.writeBits(Double.valueOf(this.currentHealth), buffer);
      Adapters.DOUBLE.writeBits(Double.valueOf(this.totalHealth), buffer);
      this.animation.writeBits(buffer, context);
      Adapters.BOOLEAN.writeBits(this.completed, buffer);
      Adapters.INT_SEGMENTED_3.writeBits(Integer.valueOf(this.modifiers.size()), buffer);

      for (Entry<String, Integer> entry : this.modifiers.entrySet()) {
         Adapters.UTF_8.writeBits(entry.getKey(), buffer);
         Adapters.INT_SEGMENTED_3.writeBits(entry.getValue(), buffer);
      }

      Adapters.INT_SEGMENTED_3.writeBits(Integer.valueOf(this.loot.size()), buffer);

      for (ItemStack stack : this.loot) {
         Adapters.ITEM_STACK.writeBits(stack, buffer);
      }
   }

   public void readBits(BitBuffer buffer, SyncContext context) {
      this.origin = Adapters.BLOCK_POS.readBits(buffer).orElseThrow();
      this.zone = Adapters.BLOCK_CUBOID.readBits(buffer).orElse(null);
      this.zoneId = Adapters.INT_SEGMENTED_7.readBits(buffer).orElseThrow();
      this.roomStyle = Adapters.ofEnum(OfferingBossFight.RoomStyle.class, EnumAdapter.Mode.NAME).readBits(buffer).orElseThrow();
      int size = Adapters.INT_SEGMENTED_3.readBits(buffer).orElseThrow();
      this.players.clear();

      for (int i = 0; i < size; i++) {
         Adapters.UUID.readBits(buffer).ifPresent(this.players::add);
      }

      this.boss = Adapters.PARTIAL_ENTITY.readBits(buffer).orElse(null);
      this.currentHealth = Adapters.DOUBLE.readBits(buffer).orElseThrow();
      this.totalHealth = Adapters.DOUBLE.readBits(buffer).orElseThrow();
      this.animation.readBits(buffer, context);
      this.completed = Adapters.BOOLEAN.readBits(buffer).orElseThrow();
      size = Adapters.INT_SEGMENTED_3.readBits(buffer).orElseThrow();
      this.modifiers.clear();

      for (int i = 0; i < size; i++) {
         String key = Adapters.UTF_8.readBits(buffer).orElseThrow();
         int value = Adapters.INT_SEGMENTED_3.readBits(buffer).orElseThrow();
         this.modifiers.put(key, value);
      }

      size = Adapters.INT_SEGMENTED_3.readBits(buffer).orElseThrow();
      this.loot.clear();

      for (int i = 0; i < size; i++) {
         Adapters.ITEM_STACK.readBits(buffer).ifPresent(this.loot::add);
      }
   }

   public static enum RoomStyle {
      BOSS_1(frame -> VaultMod.id("vault/animations/boss1/gate" + frame)),
      BOSS_2(frame -> VaultMod.id("vault/animations/boss2/gate" + frame));

      private final IntFunction<ResourceLocation> gate;

      private RoomStyle(IntFunction<ResourceLocation> gate) {
         this.gate = gate;
      }

      public ResourceLocation getGateFrame(int frame) {
         return this.gate.apply(frame);
      }
   }
}
