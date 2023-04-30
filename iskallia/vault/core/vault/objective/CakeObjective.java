package iskallia.vault.core.vault.objective;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import iskallia.vault.VaultMod;
import iskallia.vault.client.gui.helper.LightmapHelper;
import iskallia.vault.client.gui.helper.ScreenDrawHelper;
import iskallia.vault.core.Version;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.data.adapter.vault.CompoundAdapter;
import iskallia.vault.core.data.adapter.vault.LegacyBlockPosAdapter;
import iskallia.vault.core.data.compound.IntList;
import iskallia.vault.core.data.key.FieldKey;
import iskallia.vault.core.data.key.SupplierKey;
import iskallia.vault.core.data.key.registry.FieldRegistry;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.event.common.BlockUseEvent;
import iskallia.vault.core.event.common.TemplateGenerationEvent;
import iskallia.vault.core.random.ChunkRandom;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.util.RegionPos;
import iskallia.vault.core.vault.ClassicPortalLogic;
import iskallia.vault.core.vault.PortalLogic;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.WorldManager;
import iskallia.vault.core.vault.modifier.spi.VaultModifier;
import iskallia.vault.core.vault.player.Listener;
import iskallia.vault.core.vault.player.Runner;
import iskallia.vault.core.vault.stat.StatCollector;
import iskallia.vault.core.world.generator.GridGenerator;
import iskallia.vault.core.world.generator.VaultGenerator;
import iskallia.vault.core.world.generator.layout.ClassicInfiniteLayout;
import iskallia.vault.core.world.generator.layout.VaultLayout;
import iskallia.vault.core.world.storage.VirtualWorld;
import iskallia.vault.core.world.template.configured.ConfiguredTemplate;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModDynamicModels;
import iskallia.vault.world.data.DiscoveredModelsData;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.TickTask;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CakeObjective extends Objective {
   public static final ResourceLocation CAKE_HUD = VaultMod.id("textures/gui/cake_event_bar.png");
   protected static final ResourceLocation VIGNETTE = VaultMod.id("textures/gui/cake_vignette.png");
   public static final SupplierKey<Objective> KEY = SupplierKey.of("cake", Objective.class).with(Version.v1_0, CakeObjective::new);
   public static final FieldRegistry FIELDS = Objective.FIELDS.merge(new FieldRegistry());
   public static final FieldKey<BlockPos> CAKE_POS = FieldKey.of("cake_pos", BlockPos.class)
      .with(Version.v1_0, LegacyBlockPosAdapter.create().asNullable(), DISK.all().or(CLIENT.all()))
      .register(FIELDS);
   public static final FieldKey<IntList> REGIONS_X = FieldKey.of("regions_x", IntList.class)
      .with(Version.v1_0, CompoundAdapter.of(() -> IntList.createSegmented(3)), DISK.all())
      .register(FIELDS);
   public static final FieldKey<IntList> REGIONS_Z = FieldKey.of("regions_z", IntList.class)
      .with(Version.v1_0, CompoundAdapter.of(() -> IntList.createSegmented(3)), DISK.all())
      .register(FIELDS);
   public static final FieldKey<Integer> COUNT = FieldKey.of("count", Integer.class)
      .with(Version.v1_0, Adapters.INT_SEGMENTED_3, DISK.all().or(CLIENT.all()))
      .register(FIELDS);
   public static final FieldKey<Integer> TARGET = FieldKey.of("target", Integer.class)
      .with(Version.v1_0, Adapters.INT_SEGMENTED_3, DISK.all().or(CLIENT.all()))
      .register(FIELDS);
   public static final FieldKey<ResourceLocation> MODIFIER_POOL = FieldKey.of("modifier_pool", ResourceLocation.class)
      .with(Version.v1_0, Adapters.IDENTIFIER, DISK.all().or(CLIENT.all()))
      .register(FIELDS);

   protected CakeObjective() {
   }

   protected CakeObjective(ResourceLocation modifierPool) {
      this.set(COUNT, Integer.valueOf(0));
      this.set(CAKE_POS, null);
      this.set(REGIONS_X, IntList.createSegmented(3));
      this.set(REGIONS_Z, IntList.createSegmented(3));
      this.set(MODIFIER_POOL, modifierPool);
   }

   public static CakeObjective of(ResourceLocation modifierPool) {
      return new CakeObjective(modifierPool);
   }

   public static CakeObjective of(int target, ResourceLocation modifierPool) {
      return (CakeObjective)new CakeObjective(modifierPool).set(TARGET, Integer.valueOf(target));
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
      CommonEvents.BLOCK_USE.in(world).at(BlockUseEvent.Phase.HEAD).of(ModBlocks.CAKE).register(this, data -> {
         MinecraftServer server = data.getWorld().getServer();
         if (server != null) {
            server.tell(new TickTask(server.getTickCount() + 1, () -> {
               if (!this.has(TARGET) || this.get(COUNT) < this.get(TARGET)) {
                  BlockPos pos = data.getPos();
                  if (pos.equals(this.get(CAKE_POS))) {
                     if (vault.get(Vault.LISTENERS).getObjectivePriority(data.getPlayer().getUUID(), this) == 0) {
                        world.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                        this.doEatingEffects(world, pos);
                        this.onCakeEaten(world, vault, vault.get(Vault.WORLD).get(WorldManager.GENERATOR), data.getPlayer());
                        if (this.has(TARGET) && this.get(COUNT) >= this.get(TARGET)) {
                           this.onCompleted(world, vault);
                        }
                     }
                  }
               }
            }));
            data.setResult(InteractionResult.SUCCESS);
         }
      });
      CommonEvents.TEMPLATE_GENERATION.at(TemplateGenerationEvent.Phase.PRE).in(world).register(this, data -> {
         if (data.getRegion().getX() != 0 || data.getRegion().getZ() != 0) {
            data.setTemplate(ConfiguredTemplate.EMPTY);
         }
      });
      CommonEvents.BLOCK_USE.in(world).at(BlockUseEvent.Phase.HEAD).of(Blocks.CAKE).register(this, data -> {
         Listener listener = vault.get(Vault.LISTENERS).get(data.getPlayer().getUUID());
         if (listener != null) {
            DiscoveredModelsData.get(world).discoverRandomArmorPieceAndBroadcast(data.getPlayer(), ModDynamicModels.Armor.JARDOON_CHEESE, new Random());
         }
      });
      CommonEvents.LISTENER_LEAVE
         .register(
            this,
            data -> {
               if (data.getVault() == vault) {
                  vault.getOptional(Vault.STATS)
                     .map(stats -> stats.get(data.getListener()))
                     .ifPresent(stats -> stats.modify(StatCollector.OBJECTIVE_EXP_MULTIPLIER, exp -> exp * this.get(COUNT).intValue()));
               }
            }
         );
      super.initServer(world, vault);
   }

   @Override
   public void tickServer(VirtualWorld world, Vault vault) {
      if (this.get(CAKE_POS) == null) {
         PortalLogic logic = vault.get(Vault.WORLD).get(WorldManager.PORTAL_LOGIC);
         if (logic instanceof ClassicPortalLogic classic) {
            classic.getStart().ifPresent(pos -> {
               pos = pos.relative(vault.get(Vault.WORLD).get(WorldManager.FACING), 9);
               pos = pos.relative(Direction.DOWN, 1);
               world.setBlock(pos, ModBlocks.CAKE.defaultBlockState(), 3);
               this.set(CAKE_POS, pos);
            });
         }
      } else if (world.getBlockState(this.get(CAKE_POS)).getBlock() != ModBlocks.CAKE) {
         RegionPos region = RegionPos.ofBlockPos(
            this.get(CAKE_POS),
            vault.get(Vault.WORLD).get(WorldManager.GENERATOR).get(GridGenerator.CELL_X),
            vault.get(Vault.WORLD).get(WorldManager.GENERATOR).get(GridGenerator.CELL_Z)
         );
         ChunkRandom random = ChunkRandom.any();
         random.setRegionSeed(vault.get(Vault.SEED), region.getX(), region.getZ(), 912345678);
         this.generateCake(world, region, random);
      } else {
         world.getChunkSource().blockChanged(this.get(CAKE_POS));
      }

      if (this.has(TARGET) && this.get(COUNT) >= this.get(TARGET)) {
         super.tickServer(world, vault);
      }
   }

   @Override
   public void tickListener(VirtualWorld world, Vault vault, Listener listener) {
      if (listener.getPriority(this) < 0) {
         listener.addObjective(vault, this);
      }

      if (listener instanceof Runner && this.has(TARGET) && this.get(COUNT) >= this.get(TARGET)) {
         super.tickListener(world, vault, listener);
      }
   }

   private void onCakeEaten(VirtualWorld world, Vault vault, VaultGenerator generator, Player player) {
      if (generator instanceof GridGenerator gen) {
         if (generator.get(GridGenerator.LAYOUT) instanceof ClassicInfiniteLayout layout) {
            this.modify(COUNT, count -> count + 1);
            RegionPos var11 = RegionPos.ofBlockPos(this.get(CAKE_POS), gen.get(GridGenerator.CELL_X), gen.get(GridGenerator.CELL_Z));
            List neighbors = this.getValidNeighbors(vault, layout, var11);
            if (this.has(TARGET) && neighbors.isEmpty()) {
               this.set(TARGET, this.get(COUNT));
            } else {
               ChunkRandom random = ChunkRandom.any();
               random.setRegionSeed(vault.get(Vault.SEED), var11.getX(), var11.getZ(), 987654321);
               RegionPos neighbor = (RegionPos)neighbors.get(random.nextInt(neighbors.size()));
               this.generateRoom(vault, world, gen, var11, neighbor);
               this.generateCake(world, neighbor, random);
               this.get(REGIONS_X).add(Integer.valueOf(neighbor.getX()));
               this.get(REGIONS_Z).add(Integer.valueOf(neighbor.getZ()));
               this.addModifier(world, vault, player, random);
            }
         } else {
            throw new UnsupportedOperationException("Cake objective requires a vault layout");
         }
      } else {
         throw new UnsupportedOperationException("Cake objective requires a grid generator");
      }
   }

   public void addModifier(VirtualWorld world, Vault vault, Player player, RandomSource random) {
      List<VaultModifier<?>> modifiers = new ArrayList<>(
         ModConfigs.VAULT_MODIFIER_POOLS.getRandom(this.get(MODIFIER_POOL), vault.get(Vault.LEVEL).get(), random)
      );
      Object2IntMap<VaultModifier<?>> groups = new Object2IntOpenHashMap();
      modifiers.forEach(modifier -> groups.put(modifier, groups.getOrDefault(modifier, 0) + 1));
      ObjectIterator<Entry<VaultModifier<?>>> it = groups.object2IntEntrySet().iterator();
      TextComponent suffix = new TextComponent("");

      while (it.hasNext()) {
         Entry<VaultModifier<?>> entry = (Entry<VaultModifier<?>>)it.next();
         suffix.append(((VaultModifier)entry.getKey()).getChatDisplayNameComponent(entry.getIntValue()));
         if (it.hasNext()) {
            suffix.append(new TextComponent(", "));
         }
      }

      TextComponent text = new TextComponent("");
      if (modifiers.isEmpty()) {
         text.append(player.getDisplayName())
            .append(new TextComponent(" found a ").withStyle(ChatFormatting.GRAY))
            .append(new TextComponent("cake").withStyle(ChatFormatting.GREEN))
            .append(new TextComponent(".").withStyle(ChatFormatting.GRAY));
      } else {
         text.append(player.getDisplayName())
            .append(new TextComponent(" found a ").withStyle(ChatFormatting.GRAY))
            .append(new TextComponent("cake").withStyle(ChatFormatting.GREEN))
            .append(new TextComponent(" and added ").withStyle(ChatFormatting.GRAY))
            .append(suffix)
            .append(new TextComponent(".").withStyle(ChatFormatting.GRAY));
      }

      groups.forEach((modifier, count) -> vault.get(Vault.MODIFIERS).addModifier(modifier, count, true, random));

      for (Listener listener : vault.get(Vault.LISTENERS).getAll()) {
         listener.getPlayer().ifPresent(other -> {
            world.playSound(null, other.getX(), other.getY(), other.getZ(), SoundEvents.NOTE_BLOCK_BELL, SoundSource.PLAYERS, 0.9F, 1.2F);
            other.displayClientMessage(text, false);
         });
      }
   }

   private void generateRoom(Vault vault, VirtualWorld world, GridGenerator generator, RegionPos region, RegionPos neighbor) {
      int minX = Math.min(region.getX(), neighbor.getX());
      int minZ = Math.min(region.getZ(), neighbor.getZ());
      int maxX = Math.max(region.getX(), neighbor.getX());
      int maxZ = Math.max(region.getZ(), neighbor.getZ());

      for (int x = minX; x <= maxX; x++) {
         for (int z = minZ; z <= maxZ; z++) {
            if (x != region.getX() || z != region.getZ()) {
               generator.generate(vault, world, region.with(x, z));
            }
         }
      }
   }

   private void generateCake(VirtualWorld world, RegionPos region, RandomSource random) {
      int minX = region.getX() * region.getSizeX();
      int minZ = region.getZ() * region.getSizeZ();
      MutableBlockPos pos = new MutableBlockPos();

      for (int i = 0; i < 5000; i++) {
         int x = minX + random.nextInt(region.getSizeX());
         int z = minZ + random.nextInt(region.getSizeZ());
         int y = random.nextInt(64);
         pos.set(x, y, z);
         if (world.getBlockState(pos).isAir()
            && world.getBlockState(pos.above()).isAir()
            && world.getBlockState(pos.below()).isFaceSturdy(world, pos, Direction.UP)) {
            world.setBlock(pos, ModBlocks.CAKE.defaultBlockState(), 3);
            this.set(CAKE_POS, pos.immutable());
            break;
         }
      }
   }

   private List<RegionPos> getValidNeighbors(Vault vault, ClassicInfiniteLayout layout, RegionPos region) {
      List<RegionPos> neighbors = new ArrayList<>(
         Arrays.asList(
            region.add(layout.get(ClassicInfiniteLayout.TUNNEL_SPAN) + 1, 0),
            region.add(-layout.get(ClassicInfiniteLayout.TUNNEL_SPAN) - 1, 0),
            region.add(0, layout.get(ClassicInfiniteLayout.TUNNEL_SPAN) + 1),
            region.add(0, -layout.get(ClassicInfiniteLayout.TUNNEL_SPAN) - 1)
         )
      );
      neighbors.removeIf(r -> {
         for (int i = 0; i < this.get(REGIONS_X).size(); i++) {
            if (this.get(REGIONS_X).get(i) == r.getX() && this.get(REGIONS_Z).get(i) == r.getZ()) {
               return true;
            }
         }

         return false;
      });
      neighbors.removeIf(other -> {
         int minX = Math.min(region.getX(), other.getX());
         int minZ = Math.min(region.getZ(), other.getZ());
         int maxX = Math.max(region.getX(), other.getX());
         int maxZ = Math.max(region.getZ(), other.getZ());

         for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
               if (x != region.getX() || z != region.getZ()) {
                  VaultLayout.PieceType type = layout.getType(vault, region.with(x, z));
                  if (!type.isTunnel() && type != VaultLayout.PieceType.ROOM) {
                     return true;
                  }
               }
            }
         }

         return false;
      });
      return neighbors;
   }

   private void onCompleted(VirtualWorld world, Vault vault) {
      for (Listener listener : vault.get(Vault.LISTENERS).getAll()) {
         listener.getPlayer()
            .ifPresent(player -> DiscoveredModelsData.get(world).discoverRandomArmorPieceAndBroadcast(player, ModDynamicModels.Armor.CAKE, new Random()));
      }
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public boolean render(Vault vault, PoseStack matrixStack, Window window, float partialTicks, Player player) {
      if (this.has(TARGET) && this.get(COUNT) >= this.get(TARGET)) {
         boolean rendered = false;

         for (Objective objective : this.get(CHILDREN)) {
            rendered |= objective.render(vault, matrixStack, window, partialTicks, player);
         }

         if (rendered) {
            return true;
         }
      }

      int width = window.getGuiScaledWidth();
      int height = window.getGuiScaledHeight();
      Minecraft mc = Minecraft.getInstance();
      BufferSource buffer = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
      Font fr = mc.font;
      float part = this.has(TARGET) ? (float)this.get(COUNT).intValue() / this.get(TARGET).intValue() : 1.0F;
      int midX = width / 2;
      Component txt = new TextComponent(this.has(TARGET) ? this.get(COUNT) + " / ?" : String.valueOf(this.get(COUNT))).withStyle(ChatFormatting.LIGHT_PURPLE);
      fr.drawInBatch(
         txt.getVisualOrderText(),
         midX - mc.font.width(txt) / 2.0F,
         31.0F,
         -1,
         true,
         matrixStack.last().pose(),
         buffer,
         false,
         0,
         LightmapHelper.getPackedFullbrightCoords()
      );
      buffer.endBatch();
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.setShader(GameRenderer::getPositionTexShader);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.setShaderTexture(0, CAKE_HUD);
      ScreenDrawHelper.drawTexturedQuads(buf -> {
         ScreenDrawHelper.rect(buf, matrixStack).at(midX - 14.400001F, 3.6F).dim(28.800001F, 25.6F).texVanilla(0.0F, 2.0F, 36.0F, 32.0F).draw();
         ScreenDrawHelper.rect(buf, matrixStack).at(midX - 12.8F, 5.6F).dim(32.0F * part * 0.8F, 22.4F).texVanilla(2.0F, 40.0F, 32.0F * part, 28.0F).draw();
      });
      if (this.get(CAKE_POS) != null && mc.player != null) {
         double distance = Math.sqrt(this.get(CAKE_POS).distSqr(mc.player.blockPosition()));
         float alpha = (float)Mth.clamp(distance / 22.0, 0.0, 1.0);
         alpha = (float)Mth.clamp(Math.exp(alpha - 0.3) - 1.0, 0.0, 1.0);
         this.renderVignette(TextColor.fromRgb(16742330), 0.65F - alpha * 0.72F, width, height);
      }

      RenderSystem.setShader(GameRenderer::getPositionTexShader);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.setShaderTexture(0, GuiComponent.GUI_ICONS_LOCATION);
      return true;
   }

   @OnlyIn(Dist.CLIENT)
   protected void renderVignette(TextColor color, float alpha, int width, int height) {
      RenderSystem.disableDepthTest();
      RenderSystem.depthMask(false);
      RenderSystem.defaultBlendFunc();
      int colorValue = color.getValue();
      float b = (colorValue & 0xFF) / 255.0F;
      float g = (colorValue >> 8 & 0xFF) / 255.0F;
      float r = (colorValue >> 16 & 0xFF) / 255.0F;
      RenderSystem.setShaderColor(r, g, b, alpha);
      RenderSystem.setShader(GameRenderer::getPositionTexShader);
      RenderSystem.setShaderTexture(0, VIGNETTE);
      Tesselator tesselator = Tesselator.getInstance();
      BufferBuilder bufferbuilder = tesselator.getBuilder();
      bufferbuilder.begin(Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
      bufferbuilder.vertex(0.0, height, -90.0).uv(0.0F, 1.0F).endVertex();
      bufferbuilder.vertex(width, height, -90.0).uv(1.0F, 1.0F).endVertex();
      bufferbuilder.vertex(width, 0.0, -90.0).uv(1.0F, 0.0F).endVertex();
      bufferbuilder.vertex(0.0, 0.0, -90.0).uv(0.0F, 0.0F).endVertex();
      tesselator.end();
      RenderSystem.depthMask(true);
      RenderSystem.enableDepthTest();
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.defaultBlendFunc();
   }

   @Override
   public boolean isActive(Vault vault, Objective objective) {
      if (objective != this || this.has(TARGET) && this.get(COUNT) >= this.get(TARGET)) {
         for (Objective child : this.get(CHILDREN)) {
            if (child.isActive(vault, objective)) {
               return true;
            }
         }

         return false;
      } else {
         return true;
      }
   }

   protected void doEatingEffects(VirtualWorld world, BlockPos pos) {
      for (int i = 0; i < 20; i++) {
         double d0 = world.random.nextGaussian() * 0.02;
         double d1 = world.random.nextGaussian() * 0.02;
         double d2 = world.random.nextGaussian() * 0.02;
         BlockParticleOption particle = new BlockParticleOption(ParticleTypes.BLOCK, ModBlocks.CAKE.defaultBlockState());
         world.sendParticles(
            particle,
            pos.getX() + world.random.nextDouble() - d0,
            pos.getY() + world.random.nextDouble() - d1,
            pos.getZ() + world.random.nextDouble() - d2,
            10,
            d0,
            d1,
            d2,
            1.0
         );
      }

      world.playSound(null, pos, SoundEvents.BEEHIVE_EXIT, SoundSource.BLOCKS, 1.0F, 0.5F);
   }
}
