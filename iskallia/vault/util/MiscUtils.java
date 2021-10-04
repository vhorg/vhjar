package iskallia.vault.util;

import com.google.common.collect.Iterables;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.Point2D.Float;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.RepairContainer;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.Util;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.LogicalSidedProvider;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

public class MiscUtils {
   private static final Random rand = new Random();

   public static <T> T eitherOf(Random r, T... selection) {
      return selection.length == 0 ? null : selection[r.nextInt(selection.length)];
   }

   public static <T> List<T> concat(List<T> list1, T... elements) {
      return Stream.concat(list1.stream(), Arrays.stream(elements)).collect(Collectors.toList());
   }

   public static <T> List<T> concat(List<T> list1, List<T> list2) {
      return Stream.concat(list1.stream(), list2.stream()).collect(Collectors.toList());
   }

   public static Float getMidpoint(Rectangle r) {
      return new Float(r.x + r.width / 2.0F, r.y + r.height / 2.0F);
   }

   public static boolean hasEmptySlot(IInventory inventory) {
      return getRandomEmptySlot(inventory) != -1;
   }

   public static boolean hasEmptySlot(IItemHandler inventory) {
      return getRandomEmptySlot(inventory) != -1;
   }

   public static int getRandomEmptySlot(IInventory inventory) {
      return getRandomEmptySlot(new InvWrapper(inventory));
   }

   public static int getRandomEmptySlot(IItemHandler handler) {
      List<Integer> slots = new ArrayList<>();

      for (int slot = 0; slot < handler.getSlots(); slot++) {
         if (handler.getStackInSlot(slot).func_190926_b()) {
            slots.add(slot);
         }
      }

      return slots.isEmpty() ? -1 : getRandomEntry(slots, rand);
   }

   public static int getRandomSlot(IItemHandler handler) {
      List<Integer> slots = new ArrayList<>();

      for (int slot = 0; slot < handler.getSlots(); slot++) {
         slots.add(slot);
      }

      return slots.isEmpty() ? -1 : getRandomEntry(slots, rand);
   }

   public static boolean inventoryContains(IInventory inventory, Predicate<ItemStack> filter) {
      for (int slot = 0; slot < inventory.func_70302_i_(); slot++) {
         if (filter.test(inventory.func_70301_a(slot))) {
            return true;
         }
      }

      return false;
   }

   public static boolean inventoryContains(IItemHandler handler, Predicate<ItemStack> filter) {
      for (int slot = 0; slot < handler.getSlots(); slot++) {
         if (filter.test(handler.getStackInSlot(slot))) {
            return true;
         }
      }

      return false;
   }

   public static <T extends Enum<T>> T getEnumEntry(Class<T> enumClass, int index) {
      T[] constants = (T[])enumClass.getEnumConstants();
      return constants[MathHelper.func_76125_a(index, 0, constants.length - 1)];
   }

   public static BlockPos getRandomPos(AxisAlignedBB box, Random r) {
      int sizeX = Math.max(1, MathHelper.func_76128_c(box.func_216364_b()));
      int sizeY = Math.max(1, MathHelper.func_76128_c(box.func_216360_c()));
      int sizeZ = Math.max(1, MathHelper.func_76128_c(box.func_216362_d()));
      return new BlockPos(box.field_72340_a + r.nextInt(sizeX), box.field_72338_b + r.nextInt(sizeY), box.field_72339_c + r.nextInt(sizeZ));
   }

   public static Vector3d getRandomOffset(AxisAlignedBB box, Random r) {
      return new Vector3d(
         box.field_72340_a + r.nextFloat() * (box.field_72336_d - box.field_72340_a),
         box.field_72338_b + r.nextFloat() * (box.field_72337_e - box.field_72338_b),
         box.field_72339_c + r.nextFloat() * (box.field_72334_f - box.field_72339_c)
      );
   }

   public static Vector3d getRandomOffset(BlockPos pos, Random r) {
      return new Vector3d(pos.func_177958_n() + r.nextFloat(), pos.func_177956_o() + r.nextFloat(), pos.func_177952_p() + r.nextFloat());
   }

   public static Vector3d getRandomOffset(BlockPos pos, Random r, float scale) {
      float x = pos.func_177958_n() + 0.5F - scale / 2.0F + r.nextFloat() * scale;
      float y = pos.func_177956_o() + 0.5F - scale / 2.0F + r.nextFloat() * scale;
      float z = pos.func_177952_p() + 0.5F - scale / 2.0F + r.nextFloat() * scale;
      return new Vector3d(x, y, z);
   }

   @Nullable
   public static <T> T getRandomEntry(Collection<T> collection, Random rand) {
      if (collection.isEmpty()) {
         return null;
      } else {
         int randomPick = rand.nextInt(collection.size());
         return (T)Iterables.get(collection, randomPick, null);
      }
   }

   public static void broadcast(ITextComponent message) {
      MinecraftServer srv = (MinecraftServer)LogicalSidedProvider.INSTANCE.get(LogicalSide.SERVER);
      if (srv != null) {
         srv.func_184103_al().func_232641_a_(message, ChatType.CHAT, Util.field_240973_b_);
      }
   }

   public static Color blendColors(Color color1, Color color2, float color1Ratio) {
      return new Color(blendColors(color1.getRGB(), color2.getRGB(), color1Ratio), true);
   }

   public static int blendColors(int color1, int color2, float color1Ratio) {
      float ratio1 = MathHelper.func_76131_a(color1Ratio, 0.0F, 1.0F);
      float ratio2 = 1.0F - ratio1;
      int a1 = (color1 & 0xFF000000) >> 24;
      int r1 = (color1 & 0xFF0000) >> 16;
      int g1 = (color1 & 0xFF00) >> 8;
      int b1 = color1 & 0xFF;
      int a2 = (color2 & 0xFF000000) >> 24;
      int r2 = (color2 & 0xFF0000) >> 16;
      int g2 = (color2 & 0xFF00) >> 8;
      int b2 = color2 & 0xFF;
      int a = MathHelper.func_76125_a(Math.round(a1 * ratio1 + a2 * ratio2), 0, 255);
      int r = MathHelper.func_76125_a(Math.round(r1 * ratio1 + r2 * ratio2), 0, 255);
      int g = MathHelper.func_76125_a(Math.round(g1 * ratio1 + g2 * ratio2), 0, 255);
      int b = MathHelper.func_76125_a(Math.round(b1 * ratio1 + b2 * ratio2), 0, 255);
      return a << 24 | r << 16 | g << 8 | b;
   }

   public static Color overlayColor(Color base, Color overlay) {
      return new Color(overlayColor(base.getRGB(), overlay.getRGB()), true);
   }

   public static int overlayColor(int base, int overlay) {
      int alpha = (base & 0xFF000000) >> 24;
      int baseR = (base & 0xFF0000) >> 16;
      int baseG = (base & 0xFF00) >> 8;
      int baseB = base & 0xFF;
      int overlayR = (overlay & 0xFF0000) >> 16;
      int overlayG = (overlay & 0xFF00) >> 8;
      int overlayB = overlay & 0xFF;
      int r = Math.round(baseR * (overlayR / 255.0F)) & 0xFF;
      int g = Math.round(baseG * (overlayG / 255.0F)) & 0xFF;
      int b = Math.round(baseB * (overlayB / 255.0F)) & 0xFF;
      return alpha << 24 | r << 16 | g << 8 | b;
   }

   @OnlyIn(Dist.CLIENT)
   public static int getOverlayColor(ItemStack stack) {
      if (stack.func_190926_b()) {
         return -1;
      } else if (stack.func_77973_b() instanceof BlockItem) {
         Block b = Block.func_149634_a(stack.func_77973_b());
         if (b == Blocks.field_150350_a) {
            return -1;
         } else {
            BlockState state = b.func_176223_P();
            return Minecraft.func_71410_x().func_184125_al().func_228054_a_(state, null, null, 0);
         }
      } else {
         return Minecraft.func_71410_x().getItemColors().func_186728_a(stack, 0);
      }
   }

   @Nullable
   public static PlayerEntity findPlayerUsingAnvil(ItemStack left, ItemStack right) {
      for (PlayerEntity player : SidedHelper.getSidedPlayers()) {
         if (player.field_71070_bA instanceof RepairContainer) {
            NonNullList<ItemStack> contents = player.field_71070_bA.func_75138_a();
            if (contents.get(0) == left && contents.get(1) == right) {
               return player;
            }
         }
      }

      return null;
   }

   public static void fillContainer(Container ct, NonNullList<ItemStack> items) {
      for (int slot = 0; slot < items.size(); slot++) {
         ct.func_75141_a(slot, (ItemStack)items.get(slot));
      }
   }

   public static void giveItem(ServerPlayerEntity player, ItemStack stack) {
      stack = stack.func_77946_l();
      if (player.field_71071_by.func_70441_a(stack) && stack.func_190926_b()) {
         stack.func_190920_e(1);
         ItemEntity dropped = player.func_71019_a(stack, false);
         if (dropped != null) {
            dropped.func_174870_v();
         }

         player.field_70170_p
            .func_184148_a(
               null,
               player.func_226277_ct_(),
               player.func_226278_cu_(),
               player.func_226281_cx_(),
               SoundEvents.field_187638_cR,
               SoundCategory.PLAYERS,
               0.2F,
               ((player.func_70681_au().nextFloat() - player.func_70681_au().nextFloat()) * 0.7F + 1.0F) * 2.0F
            );
         player.field_71069_bz.func_75142_b();
      } else {
         ItemEntity dropped = player.func_71019_a(stack, false);
         if (dropped != null) {
            dropped.func_174868_q();
            dropped.func_200217_b(player.func_110124_au());
         }
      }
   }

   public static boolean isPlayerFakeMP(ServerPlayerEntity player) {
      if (player instanceof FakePlayer) {
         return true;
      } else if (player.field_71135_a == null) {
         return true;
      } else {
         try {
            player.func_71114_r().length();
            player.field_71135_a.field_147371_a.func_74430_c().toString();
            return !player.field_71135_a.field_147371_a.channel().isOpen();
         } catch (Exception var2) {
            return true;
         }
      }
   }
}
