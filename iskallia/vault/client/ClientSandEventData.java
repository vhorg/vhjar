package iskallia.vault.client;

import iskallia.vault.network.message.SandEventUpdateMessage;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedList;
import net.minecraft.network.chat.Component;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.Phase;

public class ClientSandEventData {
   private static final ClientSandEventData INSTANCE = new ClientSandEventData();
   private final Deque<ClientSandEventData.ContributorDisplay> contributors = new LinkedList<>();
   private float filledPercentage = 0.0F;
   private int collectedSand = 0;
   private int totalSand = 0;
   private int timeout = 0;

   private ClientSandEventData() {
   }

   public static ClientSandEventData getInstance() {
      return INSTANCE;
   }

   public float getFilledPercentage() {
      return this.filledPercentage;
   }

   public int getCollectedSand() {
      return this.collectedSand;
   }

   public int getTotalSand() {
      return this.totalSand;
   }

   public boolean isValid() {
      return this.timeout > 0;
   }

   public Collection<ClientSandEventData.ContributorDisplay> getContributors() {
      synchronized (this.contributors) {
         return Collections.unmodifiableCollection(this.contributors);
      }
   }

   public void init() {
      MinecraftForge.EVENT_BUS.addListener(this::onTick);
   }

   private void onTick(ClientTickEvent event) {
      if (event.phase == Phase.START) {
         if (this.isValid()) {
            this.timeout--;
         }

         synchronized (this.contributors) {
            this.contributors.removeIf(contributor -> {
               contributor.timeout--;
               return contributor.timeout <= 0;
            });
         }
      }
   }

   public void receive(SandEventUpdateMessage pkt) {
      this.filledPercentage = pkt.getPercentFilled();
      this.collectedSand = pkt.getSandCollected();
      this.totalSand = pkt.getSandSpawned();
      this.timeout = 60;
   }

   public void addContributor(Component contributorDisplay) {
      synchronized (this.contributors) {
         this.contributors.addFirst(new ClientSandEventData.ContributorDisplay(contributorDisplay));
         if (this.contributors.size() > 6) {
            this.contributors.removeLast();
         }
      }
   }

   public static class ContributorDisplay {
      public static final int TICK_TOTAL_DISPLAY = 30;
      private final Component contributorDisplay;
      private int timeout;

      public ContributorDisplay(Component contributorDisplay) {
         this.contributorDisplay = contributorDisplay;
         this.timeout = 40;
      }

      public Component getContributorDisplay() {
         return this.contributorDisplay;
      }

      public float getRenderOpacity() {
         float half = 15.0F;
         return this.timeout > half ? 1.0F : this.timeout / half;
      }
   }
}
