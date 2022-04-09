package me.allink.deviousmod.mixin.screen;

import me.allink.deviousmod.client.DeviousModClient;
import me.allink.deviousmod.util.EncryptionUtil;
import net.minecraft.client.gui.screen.ingame.BookScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(BookScreen.WrittenBookContents.class)
public class BookScreenMixin {
    @Inject(method = "getPages(Lnet/minecraft/item/ItemStack;)Ljava/util/List;", at = @At("RETURN"), cancellable = true)
    private static void getPages(ItemStack stack, CallbackInfoReturnable<List<String>> cir) {
        boolean err = false;
        NbtCompound nbtCompound = stack.getNbt();
        NbtList nbtList = nbtCompound.getList("pages", 8).copy();
        List<String> decryptedMessages = new ArrayList<>();
        for (NbtElement nbtElement : nbtList) {
            String text = nbtElement.asString();
            Text text1 = Text.Serializer.fromJson(text);
            if (text.contains(DeviousModClient.encryptionChar)) {
                String decrypted = EncryptionUtil.decryptMessage(text1);
                if (!decrypted.equals("Unable to decrypt")) {
                    decryptedMessages.add(decrypted);
                } else {
                    err = true;
                }
            }
        }

        if(!err && !(decryptedMessages.size() == 0)) {
            cir.setReturnValue(decryptedMessages);
        }
    }
}

