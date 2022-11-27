package iskallia.vault.core.vault.objective;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.block.PlaceholderBlock;
import iskallia.vault.block.VaultCrateBlock;
import iskallia.vault.client.gui.helper.ScreenDrawHelper;
import iskallia.vault.client.gui.helper.UIHelper;
import iskallia.vault.core.Version;
import iskallia.vault.core.data.DataMap;
import iskallia.vault.core.data.adapter.Adapter;
import iskallia.vault.core.data.key.FieldKey;
import iskallia.vault.core.data.key.SupplierKey;
import iskallia.vault.core.data.key.registry.FieldRegistry;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.event.common.BlockSetEvent;
import iskallia.vault.core.random.JavaRandom;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.objective.scavenger.ScavengeTask;
import iskallia.vault.core.vault.objective.scavenger.ScavengerGoal;
import iskallia.vault.core.vault.player.Listener;
import iskallia.vault.core.vault.player.Runner;
import iskallia.vault.core.world.storage.VirtualWorld;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.item.BasicScavengerItem;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.RenderProperties;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

public class ScavengerObjective extends Objective {
   public static final SupplierKey<Objective> KEY = SupplierKey.of("scavenger", Objective.class).with(Version.v1_0, ScavengerObjective::new);
   public static final FieldRegistry FIELDS = Objective.FIELDS.merge(new FieldRegistry());
   public static final FieldKey<ScavengerObjective.GoalMap> GOALS = FieldKey.of("goals", ScavengerObjective.GoalMap.class)
      .with(Version.v1_0, Adapter.ofCompound(ScavengerObjective.GoalMap::new), DISK.all().or(CLIENT.all()))
      .register(FIELDS);
   public static final FieldKey<Float> OBJECTIVE_PROBABILITY = FieldKey.of("objective_probability", Float.class)
      .with(Version.v1_2, Adapter.ofFloat(), DISK.all())
      .register(FIELDS);

   public ScavengerObjective() {
      this.set(GOALS, new ScavengerObjective.GoalMap());
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
      BlockState targetState = (BlockState)ModBlocks.PLACEHOLDER.defaultBlockState().setValue(PlaceholderBlock.TYPE, PlaceholderBlock.Type.OBJECTIVE);
      CommonEvents.BLOCK_SET.at(BlockSetEvent.Type.RETURN).of(targetState).in(world).register(this, data -> {
         BlockState state = ModBlocks.SCAVENGER_ALTAR.defaultBlockState();
         data.getWorld().setBlock(data.getPos(), state, 3);
      });
      CommonEvents.SCAVENGER_ALTAR_CONSUME.register(this, data -> {
         if (data.getLevel() == world && data.getTile().getItemPlacedBy() != null) {
            Listener listener = vault.get(Vault.LISTENERS).get(data.getTile().getItemPlacedBy());
            if (listener instanceof Runner) {
               boolean creative = listener.getPlayer().<Boolean>map(ServerPlayer::isCreative).orElse(false);
               CompoundTag nbt = data.getTile().getHeldItem().getTag();
               if (creative || nbt != null && nbt.getString("VaultId").equals(vault.get(Vault.ID).toString())) {
                  for (ScavengerGoal goal : this.get(GOALS).get(listener.get(Listener.ID))) {
                     goal.consume(data.getTile().getHeldItem());
                  }
               }
            }
         }
      });
      CommonEvents.LISTENER_LEAVE.register(this, data -> {
         if (data.getVault() == vault) {
            data.getListener().getPlayer().ifPresent(player -> {
               Inventory inventory = player.getInventory();

               for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
                  ItemStack stack = inventory.getItem(slot);
                  if (stack.getItem() instanceof BasicScavengerItem) {
                     inventory.setItem(slot, ItemStack.EMPTY);
                  }

                  LazyOptional<IItemHandler> itemHandler = stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);
                  itemHandler.ifPresent(handler -> {
                     if (handler instanceof IItemHandlerModifiable invHandler) {
                        for (int nestedSlot = 0; nestedSlot < invHandler.getSlots(); nestedSlot++) {
                           ItemStack nestedStack = invHandler.getStackInSlot(nestedSlot);
                           if (nestedStack.getItem() instanceof BasicScavengerItem) {
                              invHandler.setStackInSlot(nestedSlot, ItemStack.EMPTY);
                           }
                        }
                     }
                  });
                  if (stack.getItem() instanceof BlockItem && ((BlockItem)stack.getItem()).getBlock() instanceof VaultCrateBlock) {
                     CompoundTag tag = stack.getTagElement("BlockEntityTag");
                     if (tag != null) {
                        NonNullList<ItemStack> stacks = NonNullList.withSize(54, ItemStack.EMPTY);
                        ContainerHelper.loadAllItems(tag, stacks);

                        for (int i = 0; i < stacks.size(); i++) {
                           if (((ItemStack)stacks.get(i)).getItem() instanceof BasicScavengerItem) {
                              stacks.set(i, ItemStack.EMPTY);
                           }
                        }

                        ContainerHelper.saveAllItems(tag, stacks);
                     }
                  }
               }
            });
         }
      });

      for (ScavengeTask task : ModConfigs.SCAVENGER.getTasks()) {
         task.initServer(world, vault, this);
      }

      super.initServer(world, vault);
   }

   @Override
   public void tickServer(VirtualWorld world, Vault vault) {
      if (this.get(GOALS).areAllCompleted()) {
         super.tickServer(world, vault);
      }
   }

   @Override
   public void tickListener(VirtualWorld world, Vault vault, Listener listener) {
      if (listener instanceof Runner && listener.getPriority(this) < 0) {
         listener.addObjective(vault, this);
         this.generateGoal(vault, listener);
      }

      ScavengerGoal.ObjList goal = this.get(GOALS).get(listener.get(Listener.ID));
      if (goal != null && goal.areAllCompleted()) {
         super.tickListener(world, vault, listener);
      }
   }

   private void generateGoal(Vault vault, Listener listener) {
      ScavengerGoal.ObjList list = new ScavengerGoal.ObjList();
      this.get(GOALS).put(listener.get(Listener.ID), list);
      JavaRandom random = JavaRandom.ofInternal(vault.get(Vault.SEED) ^ listener.get(Listener.ID).getMostSignificantBits());
      list.addAll(ModConfigs.SCAVENGER.generateGoals(vault.get(Vault.LEVEL).get(), random));
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public boolean render(PoseStack matrixStack, Window window, float partialTicks, Player player) {
      ScavengerGoal.ObjList goals = this.get(GOALS).get(player.getUUID());
      if (goals == null || goals.areAllCompleted()) {
         boolean rendered = false;

         for (Objective objective : this.get(CHILDREN)) {
            rendered |= objective.render(matrixStack, window, partialTicks, player);
         }

         if (rendered) {
            return true;
         }
      }

      if (goals == null) {
         return true;
      } else {
         int totalX = 0;
         int totalY = 0;
         int midX = window.getGuiScaledWidth() / 2;
         int gapWidth = 7;
         int itemBoxWidth = 32;
         List<ScavengerGoal> filteredGoals = new ArrayList<>(goals);
         filteredGoals.removeIf(ScavengerGoal::isCompleted);
         int totalWidth = filteredGoals.size() * itemBoxWidth + (filteredGoals.size() - 1) * gapWidth;
         int shiftX = -totalWidth / 2 + itemBoxWidth / 2;
         RenderSystem.setShader(GameRenderer::getPositionTexShader);
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
         matrixStack.pushPose();
         int yOffset = 0;
         matrixStack.pushPose();
         matrixStack.translate(midX + shiftX, itemBoxWidth * 0.75F, 0.0);
         totalX += midX + shiftX;
         totalY = (int)(totalY + itemBoxWidth * 0.75);

         for (ScavengerGoal goal : filteredGoals) {
            int reqYOffset = renderItemRequirement(matrixStack, goal, itemBoxWidth, totalX, totalY);
            if (reqYOffset > yOffset) {
               yOffset = reqYOffset;
            }

            matrixStack.translate(itemBoxWidth + gapWidth, 0.0, 0.0);
            totalX += itemBoxWidth + gapWidth;
         }

         matrixStack.popPose();
         matrixStack.popPose();
         return true;
      }
   }

   @OnlyIn(Dist.CLIENT)
   private static int renderItemRequirement(PoseStack matrixStack, ScavengerGoal goal, int itemBoxWidth, int totalX, int totalY) {
      ItemStack requiredStack = new ItemStack((ItemLike)goal.get(ScavengerGoal.ITEM));
      ResourceLocation iconPath = goal.get(ScavengerGoal.ICON);
      matrixStack.pushPose();
      matrixStack.translate(0.0, -itemBoxWidth / 2.0F, 0.0);
      totalY = (int)(totalY + -itemBoxWidth / 2.0F);
      renderItemStack(matrixStack, requiredStack, totalX, totalY);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.setShaderTexture(0, iconPath);
      matrixStack.pushPose();
      matrixStack.translate(-16.0, -2.4, 0.0);
      matrixStack.scale(0.4F, 0.4F, 1.0F);
      ScreenDrawHelper.drawTexturedQuads(buf -> ScreenDrawHelper.rect(buf, matrixStack).dim(16.0F, 16.0F).draw());
      matrixStack.popPose();
      matrixStack.translate(0.0, 10.0, 0.0);
      String requiredText = goal.get(ScavengerGoal.CURRENT) + "/" + goal.get(ScavengerGoal.TOTAL);
      MutableComponent cmp = new TextComponent(requiredText).withStyle(ChatFormatting.GREEN);
      UIHelper.renderCenteredWrappedText(matrixStack, cmp, 30, 0);
      matrixStack.translate(0.0, 10.0, 0.0);
      matrixStack.pushPose();
      matrixStack.scale(0.5F, 0.5F, 1.0F);
      Component name = requiredStack.getHoverName();
      MutableComponent display = name.copy().withStyle(Style.EMPTY.withColor(goal.get(ScavengerGoal.COLOR)));
      int lines = UIHelper.renderCenteredWrappedText(matrixStack, display, 60, 0);
      matrixStack.popPose();
      matrixStack.popPose();
      return 25 + lines * 5;
   }

   @OnlyIn(Dist.CLIENT)
   private static void renderItemStack(PoseStack renderStack, ItemStack item, int totalX, int totalY) {
      Minecraft mc = Minecraft.getInstance();
      ItemRenderer ir = mc.getItemRenderer();
      Font fr = RenderProperties.get(item).getFont(item);
      if (fr == null) {
         fr = mc.font;
      }

      ir.blitOffset = 200.0F;
      ir.renderAndDecorateItem(item, totalX - 8, totalY - 8);
      ir.renderGuiItemDecorations(fr, item, totalX - 8, totalY - 8, null);
      ir.blitOffset = 0.0F;
   }

   @Override
   public boolean isActive(Objective objective) {
      if (!this.get(GOALS).areAllCompleted()) {
         return objective == this;
      } else {
         for (Objective child : this.get(CHILDREN)) {
            if (child.isActive(objective)) {
               return true;
            }
         }

         return false;
      }
   }

   public static class GoalMap extends DataMap<ScavengerObjective.GoalMap, UUID, ScavengerGoal.ObjList> {
      public GoalMap() {
         super(new HashMap<>(), Adapter.ofUUID(), Adapter.ofCompound(ScavengerGoal.ObjList::new));
      }

      public boolean areAllCompleted() {
         return this.values().stream().allMatch(ScavengerGoal.ObjList::areAllCompleted);
      }
   }
}
