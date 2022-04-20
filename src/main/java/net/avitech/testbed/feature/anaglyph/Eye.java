package net.avitech.testbed.feature.anaglyph;

import java.util.Map;

import com.google.common.collect.Maps;

import org.lwjgl.openvr.VR;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public enum Eye {
    LEFT(VR.EVREye_Eye_Left),
    RIGHT(VR.EVREye_Eye_Right);

    private final int nativeValue;

    private Eye(int nativeValue) {
        this.nativeValue = nativeValue;
    }

    public int asNative() {
        return this.nativeValue;
    }

    static Map<Integer, Eye> BY_NATIVE_VALUE = Maps.newHashMap();
    static {
        for (var variant : Eye.values()) {
            BY_NATIVE_VALUE.put(variant.nativeValue, variant);
        }
    }

    public static Eye fromNative(int nativeValue) {
        return BY_NATIVE_VALUE.getOrDefault(nativeValue, null);
    }
}
