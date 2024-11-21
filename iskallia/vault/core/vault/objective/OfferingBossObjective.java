package iskallia.vault.core.vault.objective;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import iskallia.vault.VaultMod;
import iskallia.vault.block.entity.OfferingPillarTileEntity;
import iskallia.vault.client.gui.helper.LightmapHelper;
import iskallia.vault.core.Version;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.data.adapter.vault.DirectAdapter;
import iskallia.vault.core.data.key.FieldKey;
import iskallia.vault.core.data.key.PaletteKey;
import iskallia.vault.core.data.key.SupplierKey;
import iskallia.vault.core.data.key.TemplatePoolKey;
import iskallia.vault.core.data.key.registry.FieldRegistry;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.random.JavaRandom;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.util.RegionPos;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.VaultRegistry;
import iskallia.vault.core.vault.WorldManager;
import iskallia.vault.core.vault.objective.offering.OfferingBossFight;
import iskallia.vault.core.vault.objective.offering.OfferingBossFights;
import iskallia.vault.core.vault.objective.scavenger.ScavengeTask;
import iskallia.vault.core.vault.player.Listener;
import iskallia.vault.core.vault.player.Runner;
import iskallia.vault.core.world.generator.layout.ClassicInfiniteLayout;
import iskallia.vault.core.world.generator.layout.VaultLayout;
import iskallia.vault.core.world.storage.VirtualWorld;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModItems;
import iskallia.vault.item.OfferingItem;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class OfferingBossObjective extends Objective {
   public static final SupplierKey<Objective> KEY = SupplierKey.of("offering_boss", Objective.class).with(Version.v1_27, OfferingBossObjective::new);
   public static final FieldRegistry FIELDS = Objective.FIELDS.merge(new FieldRegistry());
   public static final FieldKey<Float> OBJECTIVE_PROBABILITY = FieldKey.of("objective_probability", Float.class)
      .with(Version.v1_27, Adapters.FLOAT, DISK.all())
      .register(FIELDS);
   public static final FieldKey<OfferingBossFights> FIGHTS = FieldKey.of("fights", OfferingBossFights.class)
      .with(Version.v1_31, new DirectAdapter<>((value, buffer, context) -> value.writeBits(buffer, context), (buffer, context) -> {
         OfferingBossFights fights = new OfferingBossFights();
         fights.readBits(buffer, context);
         return Optional.of(fights);
      }), DISK.all().or(CLIENT.all()))
      .register(FIELDS);

   protected OfferingBossObjective() {
      this.set(FIGHTS, new OfferingBossFights());
   }

   protected OfferingBossObjective(float objectiveProbability) {
      this.set(OBJECTIVE_PROBABILITY, Float.valueOf(objectiveProbability));
      this.set(FIGHTS, new OfferingBossFights());
   }

   public static OfferingBossObjective of(float objectiveProbability) {
      return new OfferingBossObjective(objectiveProbability);
   }

   @Override
   public SupplierKey<Objective> getKey() {
      return KEY;
   }

   @Override
   public FieldRegistry getFields() {
      return FIELDS;
   }

   @Override
   public void initServer(VirtualWorld world, Vault vault) {
      CommonEvents.OBJECTIVE_PIECE_GENERATION.register(this, data -> {
         if (data.getVault() == vault) {
            data.setProbability(0.0);
         }
      });
      CommonEvents.SCAVENGER_ALTAR_CONSUME.register(this, data -> {
         if (data.getTile() instanceof OfferingPillarTileEntity pillar) {
            if (data.getLevel() != world || pillar.getItemPlacedBy() == null) {
               return;
            }

            Listener listener = vault.get(Vault.LISTENERS).get(pillar.getItemPlacedBy());
            if (!(listener instanceof Runner)) {
               return;
            }

            boolean creative = listener.getPlayer().<Boolean>map(ServerPlayer::isCreative).orElse(false);
            CompoundTag nbt = pillar.getHeldItem().getTag();
            if (!creative && (nbt == null || !nbt.getString("VaultId").equals(vault.get(Vault.ID).toString()))) {
               return;
            }

            if (pillar.getOfferingCount() >= pillar.getOfferingTarget()) {
               return;
            }

            while (pillar.getOfferingCount() < pillar.getOfferingTarget() && !pillar.getHeldItem().isEmpty()) {
               if (pillar.getHeldItem().getItem() instanceof OfferingItem) {
                  String modifierName = OfferingItem.getModifier(pillar.getHeldItem());
                  pillar.getModifiers().put(modifierName, pillar.getModifiers().getOrDefault(modifierName, 0) + 1);
                  pillar.getLoot().addAll(OfferingItem.getItems(pillar.getHeldItem()));
                  pillar.getHeldItem().shrink(1);
                  pillar.setOfferingCount(pillar.getOfferingCount() + 1);
               }
            }

            if (pillar.getOfferingCount() >= pillar.getOfferingTarget()) {
               this.get(FIGHTS).add(pillar.createFight());
            }
         }
      });
      CommonEvents.ITEM_SCAVENGE_TASK.register(this, data -> {
         if (data.getWorld() == world) {
            RandomSource random = JavaRandom.ofNanoTime();
            data.getItems().forEach(stack -> {
               if (stack.getItem() == ModItems.OFFERING) {
                  OfferingItem.setModifier(stack, ModConfigs.VAULT_BOSS.getRandomModifier());
                  OfferingItem.setItems(stack, ModConfigs.VAULT_BOSS.getRandomLootItems(data.getVault().get(Vault.LEVEL).get(), random));
               }
            });
         }
      });

      for (ScavengeTask task : ModConfigs.OFFERING_BOSS.getTasks()) {
         task.initServer(world, vault, this);
      }

      CommonEvents.LAYOUT_TEMPLATE_GENERATION
         .register(
            this,
            data -> {
               if (data.getVault() == vault && data.getPieceType() == VaultLayout.PieceType.ROOM) {
                  Direction facing = data.getVault().get(Vault.WORLD).get(WorldManager.FACING);
                  RegionPos back = data.getRegion().add(facing, -(data.getLayout().get(ClassicInfiniteLayout.TUNNEL_SPAN) + 1));
                  if (back.getX() == 0 && back.getZ() == 0 || data.getRandom().nextFloat() < this.getOr(OBJECTIVE_PROBABILITY, Float.valueOf(0.0F))) {
                     TemplatePoolKey key = VaultRegistry.TEMPLATE_POOL.getKey(VaultMod.id("vault/rooms/special/boss"));
                     if (key == null) {
                        return;
                     }

                     data.setTemplate(
                        data.getLayout()
                           .getRoom(key.get(vault.get(Vault.VERSION)), vault.get(Vault.VERSION), data.getRegion(), data.getRandom(), data.getSettings())
                     );
                     ResourceLocation theme = vault.get(Vault.WORLD).get(WorldManager.THEME);
                     ResourceLocation id = new ResourceLocation(theme.toString().replace("classic_vault_", "universal_"));
                     PaletteKey palette = VaultRegistry.PALETTE.getKey(id);
                     if (palette != null) {
                        data.getSettings().addProcessor(palette.get(Version.latest()));
                     }
                  }
               }
            }
         );
      this.get(FIGHTS).onAttach(world, vault);
      super.initServer(world, vault);
   }

   @Override
   public void tickServer(VirtualWorld world, Vault vault) {
      this.get(FIGHTS).onTick(world, vault);
      super.tickServer(world, vault);
   }

   @Override
   public void releaseServer() {
      this.get(FIGHTS).onDetach();
      super.releaseServer();
   }

   @Override
   public void tickListener(VirtualWorld world, Vault vault, Listener listener) {
      if (listener.getPriority(this) < 0) {
         listener.addObjective(vault, this);
      }

      if (listener instanceof Runner) {
         OfferingBossFight fight = this.get(FIGHTS).getFight(listener.getId());
         if (fight != null && fight.isCompleted()) {
            super.tickListener(world, vault, listener);
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public boolean render(Vault vault, PoseStack matrixStack, Window window, float partialTicks, Player player) {
      OfferingBossFight fight = this.get(FIGHTS).getFight(player.getUUID());
      if (fight != null && fight.isCompleted()) {
         boolean rendered = false;

         for (Objective objective : this.get(CHILDREN)) {
            rendered |= objective.render(vault, matrixStack, window, partialTicks, player);
         }

         if (rendered) {
            return true;
         }
      }

      if (fight == null) {
         int midX = window.getGuiScaledWidth() / 2;
         Font font = Minecraft.getInstance().font;
         BufferSource buffer = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
         Component txt = new TextComponent("Collect Offerings!").withStyle(ChatFormatting.AQUA);
         font.drawInBatch(
            txt.getVisualOrderText(),
            midX - font.width(txt) / 2.0F,
            9.0F,
            -1,
            true,
            matrixStack.last().pose(),
            buffer,
            false,
            0,
            LightmapHelper.getPackedFullbrightCoords()
         );
         buffer.endBatch();
      } else {
         fight.render(matrixStack, window, partialTicks);
      }

      return true;
   }

   @Override
   public boolean isActive(VirtualWorld world, Vault vault, Objective objective) {
      return true;
   }
}
