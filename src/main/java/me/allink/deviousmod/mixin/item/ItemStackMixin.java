package me.allink.deviousmod.mixin.item;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import me.allink.deviousmod.managers.ModuleManager;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

	Map<String, String> alreadyHashed = new HashMap<>();
	@Shadow
	@Nullable
	private NbtCompound nbt;

	@Shadow
	public abstract boolean hasNbt();

	@Inject(at = @At("RETURN"), method = "getTooltip", cancellable = true)
	public void getTooltip(PlayerEntity player, TooltipContext context,
		CallbackInfoReturnable<List<Text>> cir) {
		boolean advanced = ModuleManager.isEnabled("SuperAdvancedTooltips");
		List<Text> tooltip = cir.getReturnValue();

		if (advanced || ModuleManager.isEnabled("NoRender")) {
			int totalTooltipSize = 0;

			for (int i = 0; i < tooltip.size(); i++) {
				LiteralText newTooltip = new LiteralText(tooltip.get(i).getString());
				tooltip.set(i, newTooltip);

				totalTooltipSize += newTooltip.getString().length();
			}

			if (tooltip.size() > 10 || totalTooltipSize > 200) {
				if (tooltip.get(0).getString().length() >= 255) {
					tooltip = new ArrayList<>(
						List.of(new LiteralText(tooltip.get(0).getString().substring(0, 255))));
				} else {
					tooltip = new ArrayList<>(List.of(new LiteralText(tooltip.get(0).getString())));
				}
				tooltip.add(new LiteralText("Tooltip shortened.").formatted(Formatting.RED));
			}
		}

		if (this.hasNbt() && advanced) {
			try {
				String strNbt = this.nbt.toString();
				String finalHash;
				if (!alreadyHashed.containsKey(strNbt)) {
					MessageDigest md = MessageDigest.getInstance("SHA-256");
					byte[] hash = md.digest(strNbt.getBytes());
					BigInteger big_int = new BigInteger(1,
						Arrays.copyOfRange(hash, 0, hash.length));
					String strHash = big_int.toString(16);
					alreadyHashed.put(strNbt, strHash);
					finalHash = strHash;
				} else {
					finalHash = alreadyHashed.get(strNbt);
				}

				tooltip.add(new LiteralText(String.format("Hash: %s", finalHash)).formatted(
					Formatting.DARK_GRAY));
				tooltip.add(new LiteralText(
					String.format("Size: %sKB", strNbt.getBytes().length / 1024)).formatted(
					Formatting.DARK_GRAY));
			} catch (Exception e) {
				tooltip.add(new LiteralText("Lol! TF R u doing bro?").formatted(Formatting.RED));
			}
		}

		cir.setReturnValue(tooltip);
	}
}
