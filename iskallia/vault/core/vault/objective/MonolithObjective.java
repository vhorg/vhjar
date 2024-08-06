package iskallia.vault.core.vault.objective;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import iskallia.vault.VaultMod;
import iskallia.vault.block.MonolithBlock;
import iskallia.vault.block.PlaceholderBlock;
import iskallia.vault.block.entity.MonolithTileEntity;
import iskallia.vault.client.gui.helper.LightmapHelper;
import iskallia.vault.core.Version;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.data.key.FieldKey;
import iskallia.vault.core.data.key.LootTableKey;
import iskallia.vault.core.data.key.SupplierKey;
import iskallia.vault.core.data.key.registry.FieldRegistry;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.event.common.BlockSetEvent;
import iskallia.vault.core.event.common.BlockUseEvent;
import iskallia.vault.core.random.ChunkRandom;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.VaultLevel;
import iskallia.vault.core.vault.VaultRegistry;
import iskallia.vault.core.vault.modifier.registry.VaultModifierRegistry;
import iskallia.vault.core.vault.modifier.spi.VaultModifier;
import iskallia.vault.core.vault.player.Listener;
import iskallia.vault.core.vault.player.Runner;
import iskallia.vault.core.world.data.entity.PartialCompoundNbt;
import iskallia.vault.core.world.data.tile.PartialBlockState;
import iskallia.vault.core.world.data.tile.PartialTile;
import iskallia.vault.core.world.loot.generator.LootTableGenerator;
import iskallia.vault.core.world.storage.VirtualWorld;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.init.ModSounds;
import iskallia.vault.item.gear.DataInitializationItem;
import iskallia.vault.item.gear.DataTransferItem;
import iskallia.vault.item.gear.VaultLevelItem;
import iskallia.vault.network.message.MonolithIgniteMessage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.PacketDistributor;

public class MonolithObjective extends Objective {
   public static final ResourceLocation HUD = VaultMod.id("textures/gui/monolith/hud.png");
   public static final SupplierKey<Objective> KEY = SupplierKey.of("monolith", Objective.class).with(Version.v1_2, MonolithObjective::new);
   public static final FieldRegistry FIELDS = Objective.FIELDS.merge(new FieldRegistry());
   public static final FieldKey<Integer> COUNT = FieldKey.of("count", Integer.class)
      .with(Version.v1_0, Adapters.INT_SEGMENTED_3, DISK.all().or(CLIENT.all()))
      .register(FIELDS);
   public static final FieldKey<Integer> TARGET = FieldKey.of("target", Integer.class)
      .with(Version.v1_0, Adapters.INT_SEGMENTED_3, DISK.all().or(CLIENT.all()))
      .register(FIELDS);
   public static final FieldKey<Integer> BASE_TARGET = FieldKey.of("base_target", Integer.class)
      .with(Version.v1_25, Adapters.INT_SEGMENTED_3, DISK.all().or(CLIENT.all()))
      .register(FIELDS);
   public static final FieldKey<Float> OBJECTIVE_PROBABILITY = FieldKey.of("objective_probability", Float.class)
      .with(Version.v1_2, Adapters.FLOAT, DISK.all())
      .register(FIELDS);
   public static final FieldKey<ResourceLocation> STACK_MODIFIER_POOL = FieldKey.of("stack_modifier_pool", ResourceLocation.class)
      .with(Version.v1_22, Adapters.IDENTIFIER, DISK.all())
      .register(FIELDS);
   public static final FieldKey<ResourceLocation> OVER_STACK_MODIFIER_POOL = FieldKey.of("over_stack_modifier_pool", ResourceLocation.class)
      .with(Version.v1_22, Adapters.IDENTIFIER, DISK.all())
      .register(FIELDS);
   public static final FieldKey<ResourceLocation> OVER_STACK_LOOT_TABLE = FieldKey.of("loot_table", ResourceLocation.class)
      .with(Version.v1_22, Adapters.IDENTIFIER, DISK.all())
      .register(FIELDS);

   protected MonolithObjective() {
   }

   protected MonolithObjective(
      int target, float objectiveProbability, ResourceLocation stackModifierPool, ResourceLocation overStackModifierPool, ResourceLocation lootTable
   ) {
      this.set(COUNT, Integer.valueOf(0));
      this.set(TARGET, Integer.valueOf(target));
      this.set(BASE_TARGET, Integer.valueOf(target));
      this.set(OBJECTIVE_PROBABILITY, Float.valueOf(objectiveProbability));
      this.set(STACK_MODIFIER_POOL, stackModifierPool);
      this.set(OVER_STACK_MODIFIER_POOL, overStackModifierPool);
      this.set(OVER_STACK_LOOT_TABLE, lootTable);
   }

   public static MonolithObjective of(
      int target, float objectiveProbability, ResourceLocation stackModifierPool, ResourceLocation overStackModifierPool, ResourceLocation overStackLootTable
   ) {
      return new MonolithObjective(target, objectiveProbability, stackModifierPool, overStackModifierPool, overStackLootTable);
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
      CommonEvents.OBJECTIVE_PIECE_GENERATION
         .register(this, data -> this.ifPresent(OBJECTIVE_PROBABILITY, probability -> data.setProbability(probability.floatValue())));
      CommonEvents.BLOCK_USE
         .in(world)
         .at(BlockUseEvent.Phase.HEAD)
         .of(ModBlocks.MONOLITH)
         .register(
            this,
            data -> {
               if (data.getHand() != InteractionHand.MAIN_HAND) {
                  data.setResult(InteractionResult.SUCCESS);
               } else {
                  BlockPos pos = data.getPos();
                  if (data.getState().getValue(MonolithBlock.STATE) == MonolithBlock.State.EXTINGUISHED) {
                     if (vault.get(Vault.LISTENERS).getObjectivePriority(data.getPlayer().getUUID(), this) == 0) {
                        boolean overStacking = this.get(COUNT) >= this.get(TARGET);
                        if (overStacking) {
                           world.setBlock(pos, (BlockState)world.getBlockState(pos).setValue(MonolithBlock.STATE, MonolithBlock.State.DESTROYED), 3);
                        } else {
                           world.setBlock(pos, (BlockState)world.getBlockState(pos).setValue(MonolithBlock.STATE, MonolithBlock.State.LIT), 3);
                        }

                        this.playActivationEffects(world, pos, overStacking);
                        this.set(COUNT, Integer.valueOf(this.get(COUNT) + 1));
                        if (!overStacking) {
                           for (Objective objective : this.get(CHILDREN)) {
                              if (objective instanceof KillBossObjective killBoss) {
                                 killBoss.set(KillBossObjective.BOSS_POS, pos);
                              }
                           }
                        }

                        if (overStacking) {
                           LootTableKey table = VaultRegistry.LOOT_TABLE.getKey(this.get(OVER_STACK_LOOT_TABLE));
                           if (table == null) {
                              return;
                           }

                           LootTableGenerator generator = new LootTableGenerator(vault.get(Vault.VERSION), table, 0.0F);
                           ChunkRandom random = ChunkRandom.any();
                           random.setBlockSeed(vault.get(Vault.SEED), data.getPos(), 900397371L);
                           generator.generate(random);
                           List<ItemStack> loot = new ArrayList<>();
                           generator.getItems().forEachRemaining(loot::add);

                           for (int i = 0; i < loot.size(); i++) {
                              ItemStack stack = loot.get(i);
                              VaultLevelItem.doInitializeVaultLoot(stack, vault, null);
                              stack = DataTransferItem.doConvertStack(stack);
                              DataInitializationItem.doInitialize(stack);
                              loot.set(i, stack);
                           }

                           loot.removeIf(ItemStack::isEmpty);
                           loot.forEach(stackx -> Block.popResource(world, pos, stackx));
                           data.setResult(InteractionResult.SUCCESS);
                        }

                        if (data.getWorld().getBlockEntity(pos) instanceof MonolithTileEntity tile && !tile.getModifiers().isEmpty()) {
                           Iterator<Entry<ResourceLocation, Integer>> it = tile.getModifiers().entrySet().iterator();
                           TextComponent suffix = new TextComponent("");

                           while (it.hasNext()) {
                              Entry<ResourceLocation, Integer> entry = it.next();
                              VaultModifier<?> modifier = VaultModifierRegistry.get(entry.getKey());
                              suffix.append(modifier.getChatDisplayNameComponent(entry.getValue()));
                              if (it.hasNext()) {
                                 suffix.append(new TextComponent(", "));
                              }
                           }

                           TextComponent text = new TextComponent("");
                           if (!tile.getModifiers().isEmpty()) {
                              text.append(data.getPlayer().getDisplayName())
                                 .append(new TextComponent(" added ").withStyle(ChatFormatting.GRAY))
                                 .append(suffix)
                                 .append(new TextComponent(".").withStyle(ChatFormatting.GRAY));
                           }

                           ChunkRandom random = ChunkRandom.any();
                           random.setBlockSeed(vault.get(Vault.SEED), data.getPos(), 90039737L);
                           tile.getModifiers()
                              .forEach((modifier, count) -> vault.get(Vault.MODIFIERS).addModifier(VaultModifierRegistry.get(modifier), count, true, random));

                           for (Listener listener : vault.get(Vault.LISTENERS).getAll()) {
                              listener.getPlayer().ifPresent(other -> {
                                 world.playSound(null, other.getX(), other.getY(), other.getZ(), SoundEvents.NOTE_BLOCK_BELL, SoundSource.PLAYERS, 0.9F, 1.2F);
                                 other.displayClientMessage(text, false);
                              });
                           }
                        }

                        data.setResult(InteractionResult.SUCCESS);
                     }
                  }
               }
            }
         );
      CommonEvents.BLOCK_SET.at(BlockSetEvent.Type.RETURN).in(world).register(this, data -> {
         PartialTile target = PartialTile.of(PartialBlockState.of(ModBlocks.PLACEHOLDER), PartialCompoundNbt.empty());
         target.getState().set(PlaceholderBlock.TYPE, PlaceholderBlock.Type.OBJECTIVE);
         if (target.isSubsetOf(PartialTile.of(data.getState()))) {
            data.getWorld().setBlock(data.getPos(), ModBlocks.MONOLITH.defaultBlockState(), 3);
         }
      });
      CommonEvents.MONOLITH_UPDATE
         .register(
            this,
            data -> {
               if (data.getWorld() == world) {
                  if (!data.getEntity().isGenerated()
                     || data.getEntity().isOverStacking() != this.get(COUNT) >= this.get(TARGET)
                        && data.getState().getValue(MonolithBlock.STATE) == MonolithBlock.State.EXTINGUISHED) {
                     data.getEntity().setOverStacking(this.get(COUNT) >= this.get(TARGET));
                     if (data.getEntity().isOverStacking()) {
                        data.getEntity().removeModifiers();
                     }

                     ResourceLocation pool = data.getEntity().isOverStacking() ? this.get(OVER_STACK_MODIFIER_POOL) : this.get(STACK_MODIFIER_POOL);
                     if (pool != null) {
                        int level = vault.get(Vault.LEVEL).getOr(VaultLevel.VALUE, Integer.valueOf(0));
                        ChunkRandom random = ChunkRandom.any();
                        random.setBlockSeed(vault.get(Vault.SEED), data.getPos(), 90039737L);

                        for (VaultModifier<?> modifier : ModConfigs.VAULT_MODIFIER_POOLS.getRandom(pool, level, random)) {
                           data.getEntity().addModifier(modifier);
                        }
                     }

                     data.getEntity().setGenerated(true);
                  }
               }
            }
         );
      super.initServer(world, vault);
   }

   @Override
   public void tickServer(VirtualWorld world, Vault vault) {
      this.ifPresent(BASE_TARGET, value -> {
         double increase = CommonEvents.OBJECTIVE_TARGET.invoke(world, vault, 0.0).getIncrease();
         this.set(TARGET, Integer.valueOf((int)Math.round(this.get(BASE_TARGET).intValue() * (1.0 + increase))));
      });
      if (this.get(COUNT) >= this.get(TARGET)) {
         super.tickServer(world, vault);
      }
   }

   @Override
   public void tickListener(VirtualWorld world, Vault vault, Listener listener) {
      if (listener.getPriority(this) < 0) {
         listener.addObjective(vault, this);
      }

      if (listener instanceof Runner && this.get(COUNT) >= this.get(TARGET)) {
         super.tickListener(world, vault, listener);
      }
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public boolean render(Vault vault, PoseStack matrixStack, Window window, float partialTicks, Player player) {
      if (this.get(COUNT) >= this.get(TARGET)) {
         int midX = window.getGuiScaledWidth() / 2;
         Font font = Minecraft.getInstance().font;
         BufferSource buffer = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
         Component txt = new TextComponent("Pillage, or Exit to Complete").withStyle(ChatFormatting.WHITE);
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
         return true;
      } else {
         int current = this.get(COUNT);
         int total = this.get(TARGET);
         Component txt = new TextComponent(String.valueOf(current))
            .withStyle(ChatFormatting.WHITE)
            .append(new TextComponent(" / ").withStyle(ChatFormatting.WHITE))
            .append(new TextComponent(String.valueOf(total)).withStyle(ChatFormatting.WHITE));
         int midX = window.getGuiScaledWidth() / 2;
         matrixStack.pushPose();
         RenderSystem.setShader(GameRenderer::getPositionTexShader);
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         int previousTexture = RenderSystem.getShaderTexture(0);
         RenderSystem.setShaderTexture(0, HUD);
         float progress = (float)current / total;
         matrixStack.translate(midX - 80, 8.0, 0.0);
         GuiComponent.blit(matrixStack, 0, 0, 0.0F, 0.0F, 200, 26, 200, 100);
         GuiComponent.blit(matrixStack, 0, 8, 0.0F, 30.0F, 13 + (int)(130.0F * progress), 10, 200, 100);
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
         return true;
      }
   }

   @Override
   public boolean isActive(VirtualWorld world, Vault vault, Objective objective) {
      if (this.get(COUNT) < this.get(TARGET)) {
         return objective == this;
      } else {
         for (Objective child : this.get(CHILDREN)) {
            if (child.isActive(world, vault, objective)) {
               return true;
            }
         }

         return false;
      }
   }

   protected void playActivationEffects(VirtualWorld world, BlockPos pos, boolean overStacking) {
      if (overStacking) {
         BlockParticleOption particle = new BlockParticleOption(ParticleTypes.BLOCK, world.getBlockState(pos));
         Vec3 vec3 = new Vec3(pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F);
         world.sendParticles(particle, vec3.x, vec3.y, vec3.z, 400, 1.0, 1.0, 1.0, 0.5);
         world.sendParticles(ParticleTypes.SCRAPE, vec3.x, vec3.y, vec3.z, 50, 1.0, 1.0, 1.0, 0.5);
         world.playSound(null, vec3.x, vec3.y, vec3.z, ModSounds.DESTROY_MONOLITH, SoundSource.PLAYERS, 0.25F, 1.0F);
      } else {
         ModNetwork.CHANNEL.send(PacketDistributor.ALL.noArg(), new MonolithIgniteMessage(pos));
         world.playSound(null, pos, SoundEvents.FIRECHARGE_USE, SoundSource.BLOCKS, 1.0F, 1.0F);
      }
   }
}
