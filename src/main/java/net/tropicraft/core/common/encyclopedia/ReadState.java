package net.tropicraft.core.common.encyclopedia;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;


import java.util.function.Function;

public enum ReadState implements StringRepresentable {
    //true in this context represents a page that has been read
    //READ("read"),
    //HIDDEN("hidden"),
    //NEWLY_VISIBLE("newly_visible");
    HIDDEN(0),
    NEWLY_VISIBLE(1),
    READ(2);
    //public static final Codec<ReadState> CODEC = Codec.either(Codec.BOOL, StringRepresentable.fromEnum(ReadState::values)).xmap((either) -> (ReadState)either.map(ReadState::from, Function.identity()), (readState) -> {
/*
        Either either;
        switch (readState.ordinal()) {
            case 0 -> either = Either.left(true);
            case 1 -> either = Either.left(false);
            case 2 -> either = Either.right(readState);
            default -> throw new MatchException( (String) null, (Throwable) null);
        }
        return either;
    });


    public boolean toBoolean(boolean defaultValue) {
        boolean ret;
        switch(this.ordinal()) {
            case 0 -> ret = true;
            case 1 -> ret = false;
            default -> ret = defaultValue;
        }
        return ret;
    }
    public static ReadState from(boolean value) { return value ? HIDDEN : READ;}
    private final String name;
    ReadState(String name) {
        this.name = name;
    }

    @Override
    public String getSerializedName() {
        return name;
    }
    public boolean isHidden() { return this == HIDDEN; }
    public boolean isRead() { return this == READ; }
    public boolean isNewlyVisible() { return this == NEWLY_VISIBLE; }
*/
    /*public static final Codec<ReadState> CODEC = RecordCodecBuilder.create(i -> i.group(
           Codec.INT.fieldOf("readState").forGetter(ReadState::getState)
).apply(i, ReadState::new));*.
     */
    public static final Codec<ReadState> CODEC = StringRepresentable.fromEnum(ReadState::values);
    //public static final StreamCodec<ByteBuf, String> STREAM_CODEC = StreamCodec.
    private final int state;
    ReadState(int state) {
        this.state = state;
    }
    public int getState() { return state;}
    public boolean isHidden() { return this == HIDDEN; }
    public boolean isNewlyVisible() { return this == NEWLY_VISIBLE; }
    public boolean isRead() { return this == READ; }

    @Override
    public String getSerializedName() {
        return this.name();
    }
}
