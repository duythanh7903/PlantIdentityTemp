package com.plantcare.ai.identifier.plantid.ui.component.remind

import android.content.Context
import com.plantcare.ai.identifier.plantid.R
import com.plantcare.ai.identifier.plantid.app.AppConstants.SOUND_DEFAULT
import com.plantcare.ai.identifier.plantid.app.AppConstants.SOUND_SILENT
import com.plantcare.ai.identifier.plantid.domains.SoundDomain

object SoundUtils {

    fun getAllSound(context: Context) = mutableListOf(
        SoundDomain(0L, context.getString(R.string.txt_default), SOUND_DEFAULT),
        SoundDomain(1L, context.getString(R.string.silent), SOUND_SILENT),
        SoundDomain(2L, context.getString(R.string.voice_sound_1), R.raw.voice_air_horn),
        SoundDomain(3L, context.getString(R.string.voice_sound_2), R.raw.voice_alarm_clock),
        SoundDomain(4L, context.getString(R.string.voice_sound_3), R.raw.voice_cal_ringtone_1),
        SoundDomain(5L, context.getString(R.string.voice_sound_4), R.raw.voice_cell_ringtone_2),
        SoundDomain(6L, context.getString(R.string.voice_sound_5), R.raw.voice_classic_alarm),
        SoundDomain(7L, context.getString(R.string.voice_sound_6), R.raw.voice_discord_call),
        SoundDomain(8L, context.getString(R.string.voice_sound_7), R.raw.voice_gentle_guitar),
        SoundDomain(9L, context.getString(R.string.voice_sound_8), R.raw.voice_kalimba),
        SoundDomain(10L, context.getString(R.string.voice_sound_9), R.raw.voice_marimba_ringtone_5),
        SoundDomain(11L, context.getString(R.string.voice_sound_10), R.raw.voice_marimba_ringtone_9),
        SoundDomain(12L, context.getString(R.string.voice_sound_11), R.raw.voice_original_phone_ringtone),
        SoundDomain(13L, context.getString(R.string.voice_sound_12), R.raw.voice_ringtone),
        SoundDomain(14L, context.getString(R.string.voice_sound_13), R.raw.voice_ringtone_2),
        SoundDomain(15L, context.getString(R.string.voice_sound_14), R.raw.voice_ringtone_3),
        SoundDomain(16L, context.getString(R.string.voice_sound_15), R.raw.voice_ringtone_6),
        SoundDomain(17L, context.getString(R.string.voice_sound_16), R.raw.voice_ringtone_bubbly_bubbles),
        SoundDomain(18L, context.getString(R.string.voice_sound_17), R.raw.voice_ringtone_for_mobile_phone_with_cheerful_mood),
        SoundDomain(19L, context.getString(R.string.voice_sound_18), R.raw.voice_ringtone_jungle),
        SoundDomain(20L, context.getString(R.string.voice_sound_19), R.raw.voice_rooster),
        SoundDomain(21L, context.getString(R.string.voice_sound_20), R.raw.voice_sound_1),
    )

}