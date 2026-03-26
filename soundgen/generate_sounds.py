import numpy as np
import wave
import struct
import os
from music_data import *

SAMPLE_RATE = 44100
VOLUME = 0.25

def adsr_envelope(duration_samples, attack_time, decay_time, sustain_level, release_time):
    attack_samples = int(attack_time * SAMPLE_RATE)
    decay_samples = int(decay_time * SAMPLE_RATE)
    release_samples = int(release_time * SAMPLE_RATE)
    
    sustain_samples = duration_samples - attack_samples - decay_samples - release_samples
    if sustain_samples < 0:
        sustain_samples = 0

    envelope = np.zeros(duration_samples)
    
    pos = 0
    if attack_samples > 0:
        end = min(pos + attack_samples, duration_samples)
        if end > pos:
            envelope[pos:end] = np.linspace(0, 1, end - pos)
        pos = end
    
    if decay_samples > 0:
        end = min(pos + decay_samples, duration_samples)
        if end > pos:
            envelope[pos:end] = np.linspace(1, sustain_level, end - pos)
        pos = end

    if sustain_samples > 0:
        end = min(pos + sustain_samples, duration_samples)
        envelope[pos:end] = sustain_level
        pos = end

    if release_samples > 0:
        start = duration_samples - release_samples
        if start < pos:
            start = pos
        if duration_samples - start > 0:
            envelope[start:] = np.linspace(envelope[start-1] if start > 0 else sustain_level, 0, duration_samples - start)
        
    return envelope

def generate_wave(frequency, duration, waveform='pulse_50', vol=1.0, use_adsr=False, attack=0.01, decay=0.1, sustain=0.7, release=0.2):
    t = np.linspace(0, duration, int(SAMPLE_RATE * duration), False)
    
    if frequency == 0:
        return np.zeros_like(t)

    adsr_attack, adsr_decay, adsr_release = attack, decay, release
    if use_adsr:
        total_time = adsr_attack + adsr_decay + adsr_release
        if total_time > duration:
            if total_time > 0:
                factor = duration / total_time
                adsr_attack *= factor
                adsr_decay *= factor
                adsr_release *= factor

    if waveform in ['square', 'pulse_50']:
        wave = 0.5 * np.sign(np.sin(2 * np.pi * frequency * t))
    elif waveform == 'pulse_12_5':
        phase = (t * frequency) % 1.0
        wave = np.where(phase < 0.125, 0.5, -0.5)
    elif waveform == 'pulse_25':
        phase = (t * frequency) % 1.0
        wave = np.where(phase < 0.25, 0.5, -0.5)
    elif waveform == 'triangle':
        # Digital-style triangle
        wave = 2.0 * np.abs(2.0 * ((t * frequency) % 1.0) - 1.0) - 1.0
    elif waveform == 'sawtooth':
        wave = 2 * (t * frequency - np.floor(0.5 + t * frequency))
    else: # sine
        wave = np.sin(2 * np.pi * frequency * t)

    if use_adsr:
        envelope = adsr_envelope(len(t), adsr_attack, adsr_decay, sustain, adsr_release)
        wave *= envelope

    return (vol * wave).astype(np.float32)

def generate_percussion(duration, vol=1.0):
    noise = np.random.uniform(-1, 1, int(SAMPLE_RATE * duration))
    envelope = adsr_envelope(len(noise), 0.001, 0.1, 0, 0.1)
    percussion = noise * envelope
    return (vol * percussion).astype(np.float32)

def generate_track_from_sequence(sequence, waveform, vol, use_adsr=True, **kwargs):
    global BEAT_DURATION
    track = np.array([], dtype=np.float32)
    for note, dur in sequence:
        duration = dur * BEAT_DURATION
        freq = NOTE_FREQS.get(note, 0)
        wave = generate_wave(freq, duration, waveform, vol, use_adsr, **kwargs)
        track = np.concatenate((track, wave))
    return track

def generate_percussion_track(pattern, duration_mult, vol):
    global BEAT_DURATION
    track = np.array([], dtype=np.float32)
    duration = BEAT_DURATION * duration_mult
    for hit in pattern:
        if hit == 1:
            track = np.concatenate((track, generate_percussion(duration, vol)))
        else:
            track = np.concatenate((track, np.zeros(int(duration * SAMPLE_RATE))))
    return track

def save_wav(filename, audio, sample_rate):
    filepath = os.path.join("src", "main", "resources", "sounds", filename)
    audio_int16 = np.int16(audio * 32767)
    with wave.open(filepath, 'w') as wf:
        wf.setnchannels(1)
        wf.setsampwidth(2)
        wf.setframerate(sample_rate)
        wf.writeframes(audio_int16.tobytes())
    print(f'Generated {filepath}')

def generate_sfx():
    print("Generating sound effects...")
    global BEAT_DURATION
    BEAT_DURATION = 60 / 120 # A standard tempo for sfx

    # Heal
    heal_notes = [('C5', 0.1), ('E5', 0.1), ('G5', 0.1), ('C6', 0.2)]
    heal_sound = generate_track_from_sequence(heal_notes, 'pulse_50', VOLUME, use_adsr=True, attack=0.01, decay=0.1, sustain=0.1, release=0.1)
    save_wav('heal.wav', heal_sound, SAMPLE_RATE)

    # Move / Cursor / Select / etc...
    move_sound = np.array([], dtype=np.float32)
    for f in [440, 550, 660, 880]:
        move_sound = np.concatenate((move_sound, generate_wave(f, 0.05, 'pulse_25', use_adsr=True, attack=0.01, decay=0.01, sustain=0.5, release=0.02)))
    save_wav('move.wav', move_sound, SAMPLE_RATE)

    # Battle Start
    duration = 0.5
    frequencies = [220, 277, 329] # A minor triad
    battle_start_sound = np.zeros(int(duration * SAMPLE_RATE), dtype=np.float32)
    for f in frequencies:
        battle_start_sound += generate_wave(f, duration, 'pulse_12_5', vol=0.3, use_adsr=True, attack=0.01, decay=0.3, sustain=0.1, release=0.1)
    save_wav('battle_start.wav', battle_start_sound, SAMPLE_RATE)

    # Attack
    attack_sound = generate_wave(800, 0.2, 'pulse_50', use_adsr=True, attack=0.001, decay=0.1, sustain=0, release=0.099)
    save_wav('attack.wav', attack_sound, SAMPLE_RATE)
    
    # Victory (short fanfare)
    victory_notes = [('Bb4', 0.1), ('D5', 0.1), ('F5', 0.1), ('Bb5', 0.2)]
    victory_sound = generate_track_from_sequence(victory_notes, 'pulse_50', VOLUME, use_adsr=True)
    save_wav('victory.wav', victory_sound, SAMPLE_RATE)
    
    # Defeat
    defeat_notes = [('Ab4', 0.2), ('G4', 0.2), ('Gb4', 0.2), ('F4', 0.4)]
    defeat_sound = generate_track_from_sequence(defeat_notes, 'pulse_25', VOLUME, use_adsr=True)
    save_wav('defeat.wav', defeat_sound, SAMPLE_RATE)

    # Game Over (short sfx)
    game_over_notes = [('C3', 0.3), ('G2', 0.3), ('Eb2', 0.3), ('C2', 0.6)]
    game_over_sound = generate_track_from_sequence(game_over_notes, 'triangle', VOLUME, use_adsr=True)
    save_wav('game_over.wav', game_over_sound, SAMPLE_RATE)

    # Defend
    defend_sound = generate_wave(300, 0.1, 'triangle', use_adsr=True, attack=0.01, decay=0.05, sustain=0.5, release=0.05)
    save_wav('defend.wav', defend_sound, SAMPLE_RATE)

    # Escape
    escape_sound = np.array([], dtype=np.float32)
    for f in [660, 880, 1100, 1320]:
        escape_sound = np.concatenate((escape_sound, generate_wave(f, 0.05, 'pulse_25', use_adsr=True, attack=0.005, decay=0.01, sustain=0.2, release=0.01)))
    save_wav('escape.wav', escape_sound, SAMPLE_RATE)

    # Menu Select / Open
    menu_select_sound = generate_wave(1200, 0.05, 'pulse_50', use_adsr=True, attack=0.001, decay=0.01, sustain=0.1, release=0.01)
    save_wav('menu_select.wav', menu_select_sound, SAMPLE_RATE)
    save_wav('menu_open.wav', menu_select_sound, SAMPLE_RATE)

    # Save / Load
    save_notes = [('C5', 0.1), ('G5', 0.1), ('C6', 0.1)]
    save_sound = generate_track_from_sequence(save_notes, 'pulse_25', VOLUME, use_adsr=True)
    save_wav('save.wav', save_sound, SAMPLE_RATE)
    save_wav('load.wav', save_sound, SAMPLE_RATE)

    # Cursor
    cursor_sound = generate_wave(1000, 0.02, 'pulse_50', use_adsr=True, attack=0.001, decay=0.01, sustain=0, release=0.01)
    save_wav('cursor.wav', cursor_sound, SAMPLE_RATE)

def sync_tracks(tracks):
    """Pad tracks to be exactly the same length and mix them."""
    max_len = max([len(t) for t in tracks])
    mixed = np.zeros(max_len, dtype=np.float32)
    for t in tracks:
        padded = np.pad(t, (0, max_len - len(t)))
        mixed += padded
    return mixed

def wrap_and_save(filename, mixed_audio, tile_count=1):
    mixed_audio = np.tile(mixed_audio, tile_count)
    if np.max(np.abs(mixed_audio)) > 0:
        mixed_audio /= np.max(np.abs(mixed_audio))
    save_wav(filename, mixed_audio, SAMPLE_RATE)

def generate_music():
    print("Generating music...")
    global BEAT_DURATION

    # 1. Title Theme
    BEAT_DURATION = 60 / TITLE_TEMPO
    melody = generate_track_from_sequence(TITLE_MELODY, 'pulse_25', VOLUME, use_adsr=True, attack=0.01, decay=0.1, sustain=0.6, release=0.1)
    harmony = generate_track_from_sequence(TITLE_HARMONY, 'pulse_12_5', VOLUME * 0.6, use_adsr=True, attack=0.01, decay=0.1, sustain=0.5, release=0.1)
    wrap_and_save('title.wav', sync_tracks([melody, harmony]), 2)

    # 2. Field Music
    BEAT_DURATION = 60 / FIELD_TEMPO
    melody = generate_track_from_sequence(FIELD_MELODY, 'pulse_50', VOLUME, use_adsr=True, attack=0.01, decay=0.1, sustain=0.5, release=0.1)
    harmony = generate_track_from_sequence(FIELD_HARMONY, 'triangle', VOLUME * 0.5, use_adsr=True, attack=0.01, decay=0.1, sustain=0.3, release=0.1)
    wrap_and_save('bgm_field.wav', sync_tracks([melody, harmony]), 4)

    # 3. Castle Music
    BEAT_DURATION = 60 / CASTLE_TEMPO
    melody = generate_track_from_sequence(CASTLE_MELODY, 'pulse_25', VOLUME, use_adsr=True, attack=0.05, decay=0.2, sustain=0.4, release=0.2)
    harmony = generate_track_from_sequence(CASTLE_HARMONY, 'pulse_12_5', VOLUME * 0.5, use_adsr=True, attack=0.05, decay=0.2, sustain=0.3, release=0.2)
    wrap_and_save('bgm_castle.wav', sync_tracks([melody, harmony]), 4)

    # 4. Cave (Dungeon)
    BEAT_DURATION = 60 / CAVE_TEMPO
    melody = generate_track_from_sequence(CAVE_MELODY, 'pulse_12_5', VOLUME * 0.7, use_adsr=True, attack=0.05, decay=0.3, sustain=0.3, release=0.3)
    bass = generate_track_from_sequence(CAVE_BASS, 'triangle', VOLUME * 0.5, use_adsr=True, attack=0.05, decay=0.3, sustain=0.5, release=0.3)
    wrap_and_save('bgm_cave.wav', sync_tracks([melody, bass]), 4)

    # 5. Town Theme
    BEAT_DURATION = 60 / TOWN_TEMPO
    melody = generate_track_from_sequence(TOWN_MELODY, 'pulse_50', VOLUME, use_adsr=True, attack=0.01, decay=0.1, sustain=0.6, release=0.1)
    harmony = generate_track_from_sequence(TOWN_HARMONY, 'pulse_25', VOLUME * 0.4, use_adsr=True)
    wrap_and_save('bgm_town.wav', sync_tracks([melody, harmony]), 4)

    # 6. Battle Music
    BEAT_DURATION = 60 / BATTLE_TEMPO
    intro_mel = generate_track_from_sequence(BATTLE_INTRO_MELODY, 'pulse_50', VOLUME, use_adsr=True, attack=0.01, decay=0.05, sustain=0.4, release=0.05)
    intro_har = generate_track_from_sequence(BATTLE_INTRO_HARMONY, 'pulse_25', VOLUME * 0.7, use_adsr=True)
    intro_bas = generate_track_from_sequence(BATTLE_INTRO_BASS, 'triangle', VOLUME * 0.8, use_adsr=True)
    intro_sec = sync_tracks([intro_mel, intro_har, intro_bas])

    loop_mel = generate_track_from_sequence(BATTLE_LOOP_MELODY, 'pulse_50', VOLUME, use_adsr=True, attack=0.01, decay=0.05, sustain=0.4, release=0.05)
    loop_har = generate_track_from_sequence(BATTLE_LOOP_HARMONY, 'pulse_25', VOLUME * 0.7, use_adsr=True)
    loop_bas = generate_track_from_sequence(BATTLE_LOOP_BASS, 'triangle', VOLUME * 0.8, use_adsr=True, attack=0.01, decay=0.05, sustain=0.3, release=0.05)
    
    # Generate percussion for the loop only (20 bars * 4 beats * 4 sixteenths = 320 sixteenths = 40 times pattern of 8)
    percussion_pattern = [1, 0, 0, 1, 1, 0, 1, 0] * 40
    loop_perc = generate_percussion_track(percussion_pattern, 0.25, VOLUME * 0.6)
    loop_sec = sync_tracks([loop_mel, loop_har, loop_bas, loop_perc])
    
    battle_full = np.concatenate((intro_sec, np.tile(loop_sec, 2)))
    wrap_and_save('bgm_battle.wav', battle_full, 1)

    # 7. Victory Fanfare
    BEAT_DURATION = 60 / VICTORY_TEMPO
    fanfare_mel = generate_track_from_sequence(VICTORY_FANFARE_MELODY, 'pulse_50', VOLUME, use_adsr=True, attack=0.01, decay=0.1, sustain=0.6, release=0.1)
    fanfare_har = generate_track_from_sequence(VICTORY_FANFARE_HARMONY, 'pulse_25', VOLUME * 0.5, use_adsr=True, attack=0.01, decay=0.1, sustain=0.6, release=0.1)
    fanfare_bas = generate_track_from_sequence(VICTORY_FANFARE_BASS, 'triangle', VOLUME * 0.6, use_adsr=True, attack=0.01, decay=0.1, sustain=0.6, release=0.1)
    fanfare = sync_tracks([fanfare_mel, fanfare_har, fanfare_bas])

    loop_mel = generate_track_from_sequence(VICTORY_LOOP_MELODY, 'pulse_50', VOLUME, use_adsr=True)
    loop_har = generate_track_from_sequence(VICTORY_LOOP_HARMONY, 'pulse_25', VOLUME * 0.5, use_adsr=True)
    loop_bas = generate_track_from_sequence(VICTORY_LOOP_BASS, 'triangle', VOLUME * 0.6, use_adsr=True)
    loop_sec = sync_tracks([loop_mel, loop_har, loop_bas])
    
    victory_full = np.concatenate((fanfare, np.tile(loop_sec, 2)))
    wrap_and_save('victory_music.wav', victory_full, 1)

    # 8. Shop
    BEAT_DURATION = 60 / SHOP_TEMPO
    melody = generate_track_from_sequence(SHOP_MELODY, 'pulse_25', VOLUME, use_adsr=True)
    wrap_and_save('bgm_shop.wav', melody, 8)

    # 9. Inn
    BEAT_DURATION = 60 / INN_TEMPO
    melody = generate_track_from_sequence(INN_MELODY, 'pulse_50', VOLUME, use_adsr=True)
    wrap_and_save('bgm_inn.wav', melody, 1)

    # 10. Airship
    BEAT_DURATION = 60 / AIRSHIP_TEMPO
    melody = generate_track_from_sequence(AIRSHIP_MELODY, 'pulse_25', VOLUME, use_adsr=True)
    wrap_and_save('bgm_airship.wav', melody, 8)

    # 11. Boss
    BEAT_DURATION = 60 / BOSS_TEMPO
    melody = generate_track_from_sequence(BOSS_MELODY, 'pulse_12_5', VOLUME, use_adsr=True)
    wrap_and_save('bgm_boss.wav', melody, 8)

    # 12. Final Boss
    BEAT_DURATION = 60 / FINAL_BOSS_TEMPO
    melody = generate_track_from_sequence(FINAL_BOSS_MELODY, 'pulse_12_5', VOLUME, use_adsr=True)
    wrap_and_save('bgm_final_boss.wav', melody, 8)

    # 13. Game Over
    BEAT_DURATION = 60 / GAME_OVER_TEMPO
    melody = generate_track_from_sequence(GAME_OVER_MELODY, 'triangle', VOLUME, use_adsr=True)
    wrap_and_save('bgm_game_over.wav', melody, 1)

    # 14. Suspense
    BEAT_DURATION = 60 / SUSPENSE_TEMPO
    melody = generate_track_from_sequence(SUSPENSE_MELODY, 'triangle', VOLUME, use_adsr=True)
    wrap_and_save('bgm_suspense.wav', melody, 4)

if __name__ == "__main__":
    os.makedirs("src/main/resources/sounds", exist_ok=True)
    generate_sfx()
    generate_music()
    print("All sounds generated.")