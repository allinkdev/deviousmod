package me.allink.deviousmod.gui.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.minecraft.util.math.ColorHelper;

@Data
@AllArgsConstructor
public class RGB {
	int alpha;
	int red;
	int green;
	int blue;

	public int getIntRGB() {
		return ColorHelper.Argb.getArgb(alpha, red, green, blue);
	}

	public RGB add(int alpha, int red, int green, int blue) {
		return new RGB(this.alpha + alpha, this.red + red, this.green + green, this.blue + blue);
	}

	public RGB subtract(int alpha, int red, int green, int blue) {
		return new RGB(this.alpha - alpha, this.red - red, this.green - green, this.blue - blue);
	}
}
