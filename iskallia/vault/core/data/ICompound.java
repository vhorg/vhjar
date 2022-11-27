package iskallia.vault.core.data;

import iskallia.vault.core.data.sync.context.SyncContext;
import iskallia.vault.core.data.sync.handler.ClientSyncHandler;
import iskallia.vault.core.data.sync.handler.DiskSyncHandler;
import iskallia.vault.core.net.BitBuffer;

public interface ICompound<D> {
   DiskSyncHandler.Factory DISK = new DiskSyncHandler.Factory();
   ClientSyncHandler.Factory CLIENT = new ClientSyncHandler.Factory();

   D write(BitBuffer var1, SyncContext var2);

   D read(BitBuffer var1, SyncContext var2);

   boolean isDirty(SyncContext var1);

   D writeDiff(BitBuffer var1, SyncContext var2);

   D readDiff(BitBuffer var1, SyncContext var2);

   D resetDiff();

   boolean isDirtyTree(SyncContext var1);

   D writeDiffTree(BitBuffer var1, SyncContext var2);

   D readDiffTree(BitBuffer var1, SyncContext var2);

   D resetDiffTree();
}
