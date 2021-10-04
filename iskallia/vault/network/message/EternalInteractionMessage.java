package iskallia.vault.network.message;

import iskallia.vault.block.entity.CryoChamberTileEntity;
import iskallia.vault.config.EternalAuraConfig;
import iskallia.vault.container.inventory.CryochamberContainer;
import iskallia.vault.entity.eternal.EternalData;
import iskallia.vault.entity.eternal.EternalDataAccess;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModItems;
import iskallia.vault.world.data.EternalsData;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class EternalInteractionMessage {
   private final EternalInteractionMessage.Action action;
   private CompoundNBT extraData = new CompoundNBT();

   private EternalInteractionMessage(EternalInteractionMessage.Action action) {
      this.action = action;
   }

   public static EternalInteractionMessage feedItem(ItemStack stack) {
      EternalInteractionMessage pkt = new EternalInteractionMessage(EternalInteractionMessage.Action.FEED_SELECTED);
      pkt.extraData.func_218657_a("stack", stack.serializeNBT());
      return pkt;
   }

   public static EternalInteractionMessage levelUp(String attribute) {
      EternalInteractionMessage pkt = new EternalInteractionMessage(EternalInteractionMessage.Action.LEVEL_UP);
      pkt.extraData.func_74778_a("attribute", attribute);
      return pkt;
   }

   public static EternalInteractionMessage selectEffect(String effectName) {
      EternalInteractionMessage pkt = new EternalInteractionMessage(EternalInteractionMessage.Action.SELECT_EFFECT);
      pkt.extraData.func_74778_a("effectName", effectName);
      return pkt;
   }

   public static void encode(EternalInteractionMessage pkt, PacketBuffer buffer) {
      buffer.func_179249_a(pkt.action);
      buffer.func_150786_a(pkt.extraData);
   }

   public static EternalInteractionMessage decode(PacketBuffer buffer) {
      EternalInteractionMessage pkt = new EternalInteractionMessage(
         (EternalInteractionMessage.Action)buffer.func_179257_a(EternalInteractionMessage.Action.class)
      );
      pkt.extraData = buffer.func_150793_b();
      return pkt;
   }

   public static void handle(EternalInteractionMessage pkt, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(
         () -> {
            ServerPlayerEntity player = contextSupplier.get().getSender();
            if (player.field_71070_bA instanceof CryochamberContainer) {
               CryoChamberTileEntity tile = ((CryochamberContainer)player.field_71070_bA).getCryoChamber(player.func_71121_q());
               if (tile != null) {
                  UUID eternalId = tile.getEternalId();
                  EternalsData data = EternalsData.get(player.func_71121_q());
                  EternalsData.EternalGroup eternals = data.getEternals(player);
                  EternalData eternal = eternals.get(eternalId);
                  if (eternal != null) {
                     switch (pkt.action) {
                        case FEED_SELECTED:
                           ItemStack activeStack = player.field_71071_by.func_70445_o();
                           if (activeStack.func_190926_b() || !canBeFed(eternal, activeStack)) {
                              return;
                           }

                           if (eternal.getLevel() < eternal.getMaxLevel()) {
                              ModConfigs.ETERNAL
                                 .getFoodExp(activeStack.func_77973_b())
                                 .ifPresent(
                                    foodExp -> {
                                       if (eternal.addExp(foodExp) && !player.func_184812_l_()) {
                                          activeStack.func_190918_g(1);
                                          player.field_71070_bA.func_75142_b();
                                          player.field_70170_p
                                             .func_184133_a(
                                                null,
                                                tile.func_174877_v(),
                                                SoundEvents.field_187537_bA,
                                                SoundCategory.PLAYERS,
                                                0.5F,
                                                player.field_70170_p.field_73012_v.nextFloat() * 0.1F + 0.9F
                                             );
                                          player.field_70170_p
                                             .func_184133_a(
                                                null,
                                                tile.func_174877_v(),
                                                SoundEvents.field_187739_dZ,
                                                SoundCategory.PLAYERS,
                                                0.5F,
                                                player.field_70170_p.field_73012_v.nextFloat() * 0.1F + 0.9F
                                             );
                                       }
                                    }
                                 );
                           }

                           if (!eternal.isAlive() && activeStack.func_77973_b().equals(ModItems.LIFE_SCROLL)) {
                              eternal.setAlive(true);
                              if (!player.func_184812_l_()) {
                                 activeStack.func_190918_g(1);
                                 player.field_71070_bA.func_75142_b();
                              }
                           }

                           if (activeStack.func_77973_b().equals(ModItems.AURA_SCROLL)) {
                              eternal.shuffleSeed();
                              if (eternal.getAura() != null) {
                                 eternal.setAura(null);
                              }

                              if (!player.func_184812_l_()) {
                                 activeStack.func_190918_g(1);
                                 player.field_71070_bA.func_75142_b();
                              }
                           }
                           break;
                        case LEVEL_UP:
                           if (eternal.getUsedLevels() >= eternal.getMaxLevel()) {
                              return;
                           }

                           String attribute = pkt.extraData.func_74779_i("attribute");
                           switch (attribute) {
                              case "health": {
                                 float added = ModConfigs.ETERNAL_ATTRIBUTES.getHealthRollRange().getRandom();
                                 eternal.addAttributeValue(Attributes.field_233818_a_, added);
                                 return;
                              }
                              case "damage": {
                                 float added = ModConfigs.ETERNAL_ATTRIBUTES.getDamageRollRange().getRandom();
                                 eternal.addAttributeValue(Attributes.field_233823_f_, added);
                                 return;
                              }
                              case "movespeed": {
                                 float added = ModConfigs.ETERNAL_ATTRIBUTES.getMoveSpeedRollRange().getRandom();
                                 eternal.addAttributeValue(Attributes.field_233821_d_, added);
                                 return;
                              }
                              default:
                                 return;
                           }
                        case SELECT_EFFECT:
                           if (eternal.getAura() != null) {
                              return;
                           }

                           List<String> options = ModConfigs.ETERNAL_AURAS
                              .getRandom(eternal.getSeededRand(), 3)
                              .stream()
                              .map(EternalAuraConfig.AuraConfig::getName)
                              .collect(Collectors.toList());
                           String selectedEffect = pkt.extraData.func_74779_i("effectName");
                           if (!options.contains(selectedEffect)) {
                              return;
                           }

                           eternal.setAura(selectedEffect);
                     }
                  }
               }
            }
         }
      );
      context.setPacketHandled(true);
   }

   public static boolean canBeFed(EternalDataAccess eternal, ItemStack stack) {
      if (stack.func_190926_b()) {
         return false;
      } else if (!eternal.isAlive() && stack.func_77973_b().equals(ModItems.LIFE_SCROLL)) {
         return true;
      } else {
         return stack.func_77973_b().equals(ModItems.AURA_SCROLL)
            ? true
            : eternal.getLevel() < eternal.getMaxLevel() && ModConfigs.ETERNAL.getFoodExp(stack.func_77973_b()).isPresent();
      }
   }

   public static enum Action {
      FEED_SELECTED,
      LEVEL_UP,
      SELECT_EFFECT;
   }
}
