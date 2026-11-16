package net.tropicraft.core.common.encyclopedia;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;
import org.jspecify.annotations.NonNull;

import javax.annotation.Nullable;

public enum ReadState implements StringRepresentable {

    HIDDEN(0),
    NEWLY_VISIBLE(1),
    READ(2);

    public static final Codec<ReadState> CODEC = StringRepresentable.fromEnum(ReadState::values);
    private final int state;

    ReadState(int state) {
        this.state = state;
    }
    public int getState() { return state;}
    public boolean isHidden() { return this == HIDDEN; }
    public boolean isNewlyVisible() { return this == NEWLY_VISIBLE; }
    public boolean isRead() { return this == READ; }
    public static @Nullable ReadState byStateNum(int stateNum) {
        for (ReadState state : values()) {
            if (state.state == stateNum) {
                return state;
            }
        }
        return null;
    }
    @Override
    public @NonNull String getSerializedName() {
        return this.name();
    }

}
