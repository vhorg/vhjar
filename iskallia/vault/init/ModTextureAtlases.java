package iskallia.vault.init;

import iskallia.vault.VaultMod;
import iskallia.vault.client.atlas.ITextureAtlas;
import iskallia.vault.client.atlas.ResourceTextureAtlasHolder;
import iskallia.vault.client.gui.overlay.AbilitiesOverlay;
import iskallia.vault.client.gui.screen.player.legacy.widget.AbilityNodeTextures;
import iskallia.vault.client.gui.screen.player.legacy.widget.ArchetypeNodeTextures;
import iskallia.vault.config.AbilitiesGUIConfig;
import iskallia.vault.config.ArchetypeGUIConfig;
import iskallia.vault.config.entry.ResearchGroupStyle;
import iskallia.vault.core.vault.modifier.registry.VaultModifierRegistry;
import iskallia.vault.core.vault.modifier.spi.VaultModifier;
import iskallia.vault.quest.base.Quest;
import iskallia.vault.util.function.Memo;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(
   value = {Dist.CLIENT},
   bus = Bus.MOD
)
public class ModTextureAtlases {
   private static final Map<ResourceLocation, Supplier<ITextureAtlas>> REGISTRY = new HashMap<>();
   public static final Supplier<ITextureAtlas> MODIFIERS = register(
      VaultMod.id("textures/atlas/modifiers.png"),
      VaultMod.id("textures/gui/modifiers"),
      () -> VaultModifierRegistry.getAll().map(VaultModifier::getIcon).flatMap(Optional::stream).toList()
   );
   public static final Supplier<ITextureAtlas> ABILITIES = register(
      VaultMod.id("textures/atlas/abilities.png"),
      VaultMod.id("textures/gui/abilities"),
      () -> Stream.concat(
            ModConfigs.ABILITIES_GUI.getStyles().values().stream().map(AbilitiesGUIConfig.AbilityStyle::getIcons).flatMap(Collection::stream),
            AbilityNodeTextures.stream()
         )
         .collect(Collectors.toCollection(() -> new ArrayList<>(AbilitiesOverlay.GUI_ELEMENTS)))
   );
   public static final Supplier<ITextureAtlas> ARCHETYPES = register(
      VaultMod.id("textures/atlas/archetypes.png"),
      VaultMod.id("textures/gui/archetypes"),
      () -> Stream.concat(ModConfigs.ARCHETYPES_GUI.getStyles().values().stream().map(ArchetypeGUIConfig.IconStyle::getIcon), ArchetypeNodeTextures.stream())
         .collect(Collectors.toList())
   );
   public static final Supplier<ITextureAtlas> RESEARCHES = register(
      VaultMod.id("textures/atlas/researches.png"),
      VaultMod.id("textures/gui/researches"),
      () -> ModConfigs.RESEARCHES_GUI.getStyles().values().stream().map(skillStyle -> skillStyle.icon).toList()
   );
   public static final Supplier<ITextureAtlas> RESEARCH_GROUPS = register(
      VaultMod.id("textures/atlas/research_groups.png"),
      VaultMod.id("textures/gui/research_groups"),
      () -> ModConfigs.RESEARCH_GROUP_STYLES.getStyles().values().stream().map(ResearchGroupStyle::getIcon).toList()
   );
   public static final Supplier<ITextureAtlas> SKILLS = register(
      VaultMod.id("textures/atlas/skills.png"),
      VaultMod.id("textures/gui/skills"),
      () -> ModConfigs.TALENTS_GUI.getStyles().values().stream().map(skillStyle -> skillStyle.icon).toList()
   );
   public static final Supplier<ITextureAtlas> SCREEN = register(VaultMod.id("textures/atlas/screen.png"), VaultMod.id("textures/gui/screen"), null);
   public static final Supplier<ITextureAtlas> SLOT = register(VaultMod.id("textures/atlas/slot.png"), VaultMod.id("textures/gui/slot"), null);
   public static final Supplier<ITextureAtlas> SCAVENGER = register(VaultMod.id("textures/atlas/scavenger.png"), VaultMod.id("textures/gui/scavenger"), null);
   public static final Supplier<ITextureAtlas> MOB_HEADS = register(VaultMod.id("textures/atlas/mob_heads.png"), VaultMod.id("textures/gui/mob_heads"), null);
   public static final Supplier<ITextureAtlas> QUESTS = register(VaultMod.id("textures/atlas/quests.png"), VaultMod.id("textures/gui/quests"), () -> {
      List<ResourceLocation> icons = new ArrayList<>(ModConfigs.QUESTS.getQuests().stream().map(Quest::getIcon).toList());
      ModConfigs.SKY_QUESTS.getQuests().forEach(quest -> {
         if (!icons.contains(quest.getIcon())) {
            icons.add(quest.getIcon());
         }
      });
      icons.add(VaultMod.id("gui/quests/check"));
      return icons;
   });
   public static final Supplier<ITextureAtlas> ACHIEVEMENTS = register(
      VaultMod.id("textures/atlas/achievements.png"), VaultMod.id("textures/gui/achievements"), null
   );

   @SubscribeEvent
   public static void on(RegisterClientReloadListenersEvent event) {
      REGISTRY.values().stream().map(Supplier::get).forEach(event::registerReloadListener);
   }

   private static Supplier<ITextureAtlas> register(
      ResourceLocation id, ResourceLocation resourceLocation, @Nullable Supplier<List<ResourceLocation>> validationSupplier
   ) {
      if (REGISTRY.containsKey(id)) {
         throw new IllegalStateException("Duplicate atlas resource location registered: " + id);
      } else {
         Supplier<ITextureAtlas> supplier = Memo.of(() -> {
            TextureManager textureManager = Minecraft.getInstance().textureManager;
            ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
            return new ResourceTextureAtlasHolder(textureManager, resourceManager, id, resourceLocation, validationSupplier);
         });
         REGISTRY.put(id, supplier);
         return supplier;
      }
   }
}
