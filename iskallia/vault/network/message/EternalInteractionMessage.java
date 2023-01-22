package iskallia.vault.network.message;

import iskallia.vault.block.entity.EternalPedestalTileEntity;
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
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent.Context;

public class EternalInteractionMessage {
   private final EternalInteractionMessage.Action action;
   private CompoundTag extraData = new CompoundTag();

   private EternalInteractionMessage(EternalInteractionMessage.Action action) {
      this.action = action;
   }

   public static EternalInteractionMessage feedItem(ItemStack stack, boolean shiftDown) {
      EternalInteractionMessage pkt = new EternalInteractionMessage(EternalInteractionMessage.Action.FEED_SELECTED);
      pkt.extraData.put("stack", stack.serializeNBT());
      pkt.extraData.putBoolean("shiftDown", shiftDown);
      return pkt;
   }

   public static EternalInteractionMessage levelUp(String attribute) {
      EternalInteractionMessage pkt = new EternalInteractionMessage(EternalInteractionMessage.Action.LEVEL_UP);
      pkt.extraData.putString("attribute", attribute);
      return pkt;
   }

   public static EternalInteractionMessage selectEffect(String effectName) {
      EternalInteractionMessage pkt = new EternalInteractionMessage(EternalInteractionMessage.Action.SELECT_EFFECT);
      pkt.extraData.putString("effectName", effectName);
      return pkt;
   }

   public static void encode(EternalInteractionMessage pkt, FriendlyByteBuf buffer) {
      buffer.writeEnum(pkt.action);
      buffer.writeNbt(pkt.extraData);
   }

   public static EternalInteractionMessage decode(FriendlyByteBuf buffer) {
      EternalInteractionMessage pkt = new EternalInteractionMessage((EternalInteractionMessage.Action)buffer.readEnum(EternalInteractionMessage.Action.class));
      pkt.extraData = buffer.readNbt();
      return pkt;
   }

   public static void handle(EternalInteractionMessage pkt, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(
         () -> {
            ServerPlayer player = contextSupplier.get().getSender();
            if (player.containerMenu instanceof CryochamberContainer) {
               EternalPedestalTileEntity tile = ((CryochamberContainer)player.containerMenu).getPedestal(player.getLevel());
               if (tile != null) {
                  UUID eternalId = tile.getEternalId();
                  EternalsData data = EternalsData.get(player.getLevel());
                  EternalsData.EternalGroup eternals = data.getEternals(player);
                  EternalData eternal = eternals.get(eternalId);
                  if (eternal != null) {
                     switch (pkt.action) {
                        case FEED_SELECTED:
                           boolean shiftDown = pkt.extraData.getBoolean("shiftDown");
                           ItemStack activeStack = player.containerMenu.getCarried();
                           if (activeStack.isEmpty() || !canBeFed(eternal, activeStack)) {
                              return;
                           }

                           if (eternal.getLevel() < eternal.getMaxLevel()) {
                              int num = 1;
                              int foodExp = 0;
                              if (shiftDown) {
                                 num = activeStack.getCount();
                              }

                              for (int i = 0; i < num; i++) {
                                 foodExp += ModConfigs.ETERNAL.getFoodExp(activeStack.getItem()).orElse(0);
                              }

                              if (foodExp > 0 && eternal.addExp(foodExp)) {
                                 if (!player.isCreative()) {
                                    activeStack.shrink(num);
                                    player.containerMenu.broadcastChanges();
                                 }

                                 player.level
                                    .playSound(
                                       null,
                                       tile.getBlockPos(),
                                       SoundEvents.GENERIC_EAT,
                                       SoundSource.PLAYERS,
                                       0.5F,
                                       player.level.random.nextFloat() * 0.1F + 0.9F
                                    );
                                 player.level
                                    .playSound(
                                       null,
                                       tile.getBlockPos(),
                                       SoundEvents.PLAYER_BURP,
                                       SoundSource.PLAYERS,
                                       0.5F,
                                       player.level.random.nextFloat() * 0.1F + 0.9F
                                    );
                              }
                           }

                           if (!eternal.isAlive() && activeStack.getItem().equals(ModItems.LIFE_SCROLL)) {
                              eternal.setAlive(true);
                              if (!player.isCreative()) {
                                 activeStack.shrink(1);
                                 player.containerMenu.broadcastChanges();
                              }
                           }

                           if (activeStack.getItem().equals(ModItems.AURA_SCROLL)) {
                              eternal.shuffleSeed();
                              if (eternal.getAura() != null) {
                                 eternal.setAura(null);
                              }

                              if (!player.isCreative()) {
                                 activeStack.shrink(1);
                                 player.containerMenu.broadcastChanges();
                              }
                           }
                           break;
                        case LEVEL_UP:
                           if (eternal.getUsedLevels() >= eternal.getMaxLevel()) {
                              return;
                           }

                           String attribute = pkt.extraData.getString("attribute");
                           switch (attribute) {
                              case "health": {
                                 float added = ModConfigs.ETERNAL_ATTRIBUTES.getHealthRollRange().getRandom();
                                 eternal.addAttributeValue(Attributes.MAX_HEALTH, added);
                                 return;
                              }
                              case "damage": {
                                 float added = ModConfigs.ETERNAL_ATTRIBUTES.getDamageRollRange().getRandom();
                                 eternal.addAttributeValue(Attributes.ATTACK_DAMAGE, added);
                                 return;
                              }
                              case "movespeed": {
                                 float added = ModConfigs.ETERNAL_ATTRIBUTES.getMoveSpeedRollRange().getRandom();
                                 eternal.addAttributeValue(Attributes.MOVEMENT_SPEED, added);
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
                           String selectedEffect = pkt.extraData.getString("effectName");
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
      if (stack.isEmpty()) {
         return false;
      } else if (!eternal.isAlive() && stack.getItem().equals(ModItems.LIFE_SCROLL)) {
         return true;
      } else {
         return stack.getItem().equals(ModItems.AURA_SCROLL)
            ? true
            : eternal.getLevel() < eternal.getMaxLevel() && ModConfigs.ETERNAL.getFoodExp(stack.getItem()).isPresent();
      }
   }

   public static enum Action {
      FEED_SELECTED,
      LEVEL_UP,
      SELECT_EFFECT;
   }
}
