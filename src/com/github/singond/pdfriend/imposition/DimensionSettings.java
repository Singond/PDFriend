package com.github.singond.pdfriend.imposition;

import java.util.EnumMap;
import java.util.Map;

import com.github.singond.pdfriend.SpecVal;
import com.github.singond.pdfriend.geometry.Dimensions;
import com.github.singond.pdfriend.imposition.DimensionSettings.Special;

/**
 * An extension of {@link Dimensions} which allows for special values.
 *
 * @author Singon
 */
public final class DimensionSettings extends SpecVal<Special, Dimensions> {
	
	private static final Map<Special, DimensionSettings> instanceMap = new EnumMap<>(Special.class);
	static {
		for (Special type : Special.values()) {
			instanceMap.put(type, new DimensionSettings(type));
		}
	}
	
	public static final DimensionSettings AUTO = DimensionSettings.of(Special.AUTO);

	private DimensionSettings(Special type) {
		super(type);
	}
	
	private DimensionSettings(Dimensions value) {
		super(value);
	}
	
	public static DimensionSettings of(Special type) {
		return instanceMap.get(type);
	}
	
	public static DimensionSettings of(Dimensions value) {
		return new DimensionSettings(value);
	}

	@Override
	protected Special getValueConstant() {
		return Special.VALUE;
	}

	public enum Special {
		/** Auto dimensions */
		AUTO,
		/** Regular value */
		VALUE;
	}
}
