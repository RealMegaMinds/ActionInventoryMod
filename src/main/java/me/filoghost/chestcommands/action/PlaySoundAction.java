/*
 * Copyright (C) filoghost and contributors
 *
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package me.filoghost.chestcommands.action;

import me.filoghost.chestcommands.logging.Errors;
import me.filoghost.chestcommands.parsing.NumberParser;
import me.filoghost.chestcommands.parsing.ParseException;
import me.filoghost.fcommons.Strings;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class PlaySoundAction implements Action {
    
    private final SoundEvent sound;
    private final float pitch;
    private final float volume;

    public PlaySoundAction(String serializedAction) throws ParseException {
        String[] split = Strings.splitAndTrim(serializedAction, ",", 3);
        
        sound = Registry.SOUND_EVENT.get(new Identifier(split[0]));
        if (sound == null) {
            throw new ParseException(Errors.Parsing.unknownSound(split[0]));
        }

        if (split.length > 1) {
            try {
                pitch = NumberParser.getFloat(split[1]);
            } catch (ParseException e) {
                throw new ParseException(Errors.Parsing.invalidSoundPitch(split[1]), e);
            }
        } else {
            pitch = 1.0f;
        }

        if (split.length > 2) {
            try {
                volume = NumberParser.getFloat(split[2]);
            } catch (ParseException e) {
                throw new ParseException(Errors.Parsing.invalidSoundVolume(split[2]), e);
            }
        } else {
            volume = 1.0f;
        }
    }

    @Override
    public void execute(ServerPlayerEntity player) {
        player.playSound(sound, volume, pitch);
    }
}