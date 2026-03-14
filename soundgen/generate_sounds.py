import numpy as np
import wave
import struct
import os

SAMPLE_RATE = 44100
VOLUME = 0.25

NOTE_FREQS = {
    'C1': 32.70, 'C#1': 34.65, 'D1': 36.71, 'Eb1': 38.89, 'E1': 41.20, 'F1': 43.65, 'F#1': 46.25, 'G1': 49.00, 'G#1': 51.91, 'Ab1': 51.91, 'A1': 55.00, 'Bb1': 58.27, 'B1': 61.74,
    'C2': 65.41, 'C#2': 69.30, 'D2': 73.42, 'Eb2': 77.78, 'E2': 82.41, 'F2': 87.31, 'F#2': 92.50, 'G2': 98.00, 'G#2': 103.83, 'Ab2': 103.83, 'A2': 110.00, 'Bb2': 116.54, 'B2': 123.47,
    'C3': 130.81, 'C#3': 138.59, 'Db3': 138.59, 'D3': 146.83, 'Eb3': 155.56, 'E3': 164.81, 'F3': 174.61, 'F#3': 185.00, 'Gb3': 185.00, 'G3': 196.00, 'G#3': 207.65, 'Ab3': 207.65, 'A3': 220.00, 'Bb3': 233.08, 'B3': 246.94,
    'C4': 261.63, 'C#4': 277.18, 'Db4': 277.18, 'D4': 293.66, 'Eb4': 311.13, 'E4': 329.63, 'F4': 349.23, 'F#4': 369.99, 'G4': 392.00, 'G#4': 415.30, 'Ab4': 415.30, 'A4': 440.00, 'Bb4': 466.16, 'B4': 493.88, 'Cb5': 493.88,
    'C5': 523.25, 'C#5': 554.37, 'Db5': 554.37, 'D5': 587.33, 'Eb5': 622.25, 'E5': 659.25, 'F5': 698.46, 'F#5': 739.99, 'G5': 783.99, 'G#5': 830.61, 'Ab5': 830.61, 'A5': 880.00, 'Bb5': 932.33, 'B5': 987.77,
    'C6': 1046.50, 'C#6': 1108.73, 'D6': 1174.66, 'Eb6': 1244.51, 'E6': 1318.51, 'F6': 1396.91, 'F#6': 1479.98, 'G6': 1567.98, 'G#6': 1661.22, 'A6': 1760.00, 'Bb6': 1864.66, 'B6': 1975.53,
    'REST': 0
}

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

def generate_wave(frequency, duration, waveform='square', vol=1.0, use_adsr=False, attack=0.01, decay=0.1, sustain=0.7, release=0.2):
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

    if waveform == 'square':
        wave = 0.5 * np.sign(np.sin(2 * np.pi * frequency * t))
    elif waveform == 'triangle':
        wave = 1.0 * np.arcsin(np.sin(2 * np.pi * frequency * t)) / (np.pi / 2)
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
    audio_int16 = np.int16(audio * 32767)
    with wave.open(filename, 'w') as wf:
        wf.setnchannels(1)
        wf.setsampwidth(2)
        wf.setframerate(sample_rate)
        wf.writeframes(audio_int16.tobytes())
    print(f'Generated {filename}')

def generate_sfx():
    print("Generating sound effects...")
    global BEAT_DURATION
    BEAT_DURATION = 60 / 120 # A standard tempo for sfx

    # Heal
    heal_notes = [('C5', 0.1), ('E5', 0.1), ('G5', 0.1), ('C6', 0.2)]
    heal_sound = generate_track_from_sequence(heal_notes, 'sine', VOLUME, use_adsr=True, attack=0.01, decay=0.1, sustain=0.1, release=0.1)
    save_wav('heal.wav', heal_sound, SAMPLE_RATE)

    # Move
    move_sound = np.array([], dtype=np.float32)
    for f in [440, 550, 660, 880]:
        move_sound = np.concatenate((move_sound, generate_wave(f, 0.05, 'triangle', use_adsr=True, attack=0.01, decay=0.01, sustain=0.5, release=0.02)))
    save_wav('move.wav', move_sound, SAMPLE_RATE)

    # Battle Start
    duration = 0.5
    frequencies = [220, 277, 329] # A minor triad
    battle_start_sound = np.zeros(int(duration * SAMPLE_RATE), dtype=np.float32)
    for f in frequencies:
        battle_start_sound += generate_wave(f, duration, 'sawtooth', vol=0.3, use_adsr=True, attack=0.01, decay=0.3, sustain=0.1, release=0.1)
    save_wav('battle_start.wav', battle_start_sound, SAMPLE_RATE)

    # Attack
    attack_sound = generate_wave(800, 0.2, 'square', use_adsr=True, attack=0.001, decay=0.1, sustain=0, release=0.099)
    save_wav('attack.wav', attack_sound, SAMPLE_RATE)
    
    # Victory (short fanfare)
    victory_notes = [('Bb4', 0.1), ('D5', 0.1), ('F5', 0.1), ('Bb5', 0.2)]
    victory_sound = generate_track_from_sequence(victory_notes, 'square', VOLUME, use_adsr=True)
    save_wav('victory.wav', victory_sound, SAMPLE_RATE)
    
    # Defeat
    defeat_notes = [('Ab4', 0.2), ('G4', 0.2), ('Gb4', 0.2), ('F4', 0.4)]
    defeat_sound = generate_track_from_sequence(defeat_notes, 'sawtooth', VOLUME, use_adsr=True)
    save_wav('defeat.wav', defeat_sound, SAMPLE_RATE)

    # Game Over
    game_over_notes = [('C3', 0.3), ('G2', 0.3), ('Eb2', 0.3), ('C2', 0.6)]
    game_over_sound = generate_track_from_sequence(game_over_notes, 'sawtooth', VOLUME, use_adsr=True)
    save_wav('game_over.wav', game_over_sound, SAMPLE_RATE)

    # Defend
    defend_sound = generate_wave(300, 0.1, 'triangle', use_adsr=True, attack=0.01, decay=0.05, sustain=0.5, release=0.05)
    save_wav('defend.wav', defend_sound, SAMPLE_RATE)

    # Escape
    escape_sound = np.array([], dtype=np.float32)
    for f in [660, 880, 1100, 1320]:
        escape_sound = np.concatenate((escape_sound, generate_wave(f, 0.05, 'sine', use_adsr=True, attack=0.005, decay=0.01, sustain=0.2, release=0.01)))
    save_wav('escape.wav', escape_sound, SAMPLE_RATE)

    # Menu Select / Open
    menu_select_sound = generate_wave(1200, 0.05, 'square', use_adsr=True, attack=0.001, decay=0.01, sustain=0.1, release=0.01)
    save_wav('menu_select.wav', menu_select_sound, SAMPLE_RATE)
    save_wav('menu_open.wav', menu_select_sound, SAMPLE_RATE)

    # Save / Load
    save_notes = [('C5', 0.1), ('G5', 0.1), ('C6', 0.1)]
    save_sound = generate_track_from_sequence(save_notes, 'sine', VOLUME, use_adsr=True)
    save_wav('save.wav', save_sound, SAMPLE_RATE)
    save_wav('load.wav', save_sound, SAMPLE_RATE)

    # Cursor
    cursor_sound = generate_wave(1000, 0.02, 'square', use_adsr=True, attack=0.001, decay=0.01, sustain=0, release=0.01)
    save_wav('cursor.wav', cursor_sound, SAMPLE_RATE)

def generate_music():
    print("Generating music...")
    global BEAT_DURATION

    # =========================================================================
    # Title Music (FF1 Opening Theme) - The Bridge Theme
    # =========================================================================
    TEMPO = 112
    BEAT_DURATION = 60 / TEMPO
    opening_melody = [
        ('C5', 0.5), ('G4', 0.5), ('E4', 0.5), ('C4', 0.5),
        ('D4', 0.5), ('E4', 0.5), ('F4', 0.5), ('G4', 0.5),
        ('A4', 0.5), ('B4', 0.5), ('C5', 0.5), ('D5', 0.5),
        ('E5', 1.0), ('REST', 1.0),
        
        ('C5', 0.5), ('D5', 0.25), ('E5', 0.25), ('F5', 0.5), ('G5', 0.5),
        ('A5', 0.5), ('G5', 0.5), ('F5', 0.5), ('E5', 0.5),
        ('D5', 0.5), ('E5', 0.25), ('F5', 0.25), ('G5', 0.5), ('A5', 0.5),
        ('B5', 0.5), ('A5', 0.5), ('G5', 0.5), ('F5', 0.5),
        
        ('E5', 1.0), ('D5', 1.0), ('C5', 2.0)
    ]
    opening_harmony = [
        ('C4', 0.5), ('E4', 0.5), ('G4', 0.5), ('C5', 0.5),
        ('G3', 0.5), ('B3', 0.5), ('D4', 0.5), ('G4', 0.5),
        ('A3', 0.5), ('C4', 0.5), ('E4', 0.5), ('A4', 0.5),
        ('F3', 0.5), ('A3', 0.5), ('C4', 0.5), ('F4', 0.5),
    ] * 2
    
    melody_track = generate_track_from_sequence(opening_melody, 'square', VOLUME, use_adsr=True, attack=0.01, decay=0.1, sustain=0.6, release=0.1)
    harmony_track = generate_track_from_sequence(opening_harmony, 'square', VOLUME * 0.6, use_adsr=True, attack=0.01, decay=0.1, sustain=0.5, release=0.1)
    
    final_length = max(len(melody_track), len(harmony_track))
    melody_track = np.pad(melody_track, (0, final_length - len(melody_track)))
    harmony_track = np.pad(harmony_track, (0, final_length - len(harmony_track)))
    
    title_music = melody_track + harmony_track
    title_music = np.tile(title_music, 2)
    title_music /= np.max(np.abs(title_music))
    save_wav('title.wav', title_music, SAMPLE_RATE)

    # =========================================================================
    # Field Music (FF1 Main Theme)
    # =========================================================================
    TEMPO = 144
    BEAT_DURATION = 60 / TEMPO
    field_melody = [
        ('C5', 0.5), ('C5', 0.5), ('C5', 0.5), ('C5', 0.5),
        ('Bb4', 0.5), ('Ab4', 0.5), ('Bb4', 0.5), ('C5', 0.5),
        ('C5', 0.5), ('C5', 0.5), ('C5', 0.5), ('C5', 0.5),
        ('Bb4', 0.5), ('Ab4', 0.5), ('Bb4', 0.5), ('G4', 0.5),
    ]
    field_harmony = [
        ('Ab3', 0.5), ('Eb3', 0.5), ('Ab3', 0.5), ('Eb3', 0.5),
    ] * 8
    
    melody_track = generate_track_from_sequence(field_melody, 'square', VOLUME, use_adsr=True, attack=0.01, decay=0.1, sustain=0.5, release=0.1)
    harmony_track = generate_track_from_sequence(field_harmony, 'triangle', VOLUME * 0.5, use_adsr=True, attack=0.01, decay=0.1, sustain=0.3, release=0.1)
    
    final_length = max(len(melody_track), len(harmony_track))
    melody_track = np.pad(melody_track, (0, final_length - len(melody_track)))
    harmony_track = np.pad(harmony_track, (0, final_length - len(harmony_track)))
    
    field_music = melody_track + harmony_track
    field_music = np.tile(field_music, 8)
    if np.max(np.abs(field_music)) > 0:
        field_music /= np.max(np.abs(field_music))
    save_wav('bgm_field.wav', field_music, SAMPLE_RATE)

    # =========================================================================
    # Castle Music (FF1 Cornelia Castle)
    # =========================================================================
    TEMPO = 96
    BEAT_DURATION = 60 / TEMPO
    castle_melody = [
        ('C5', 0.5), ('G4', 0.5), ('C5', 0.5), ('D5', 0.5),
        ('E5', 0.5), ('D5', 0.5), ('C5', 0.5), ('G4', 0.5),
        ('F4', 0.5), ('A4', 0.5), ('C5', 0.5), ('F5', 0.5),
        ('E5', 1.0), ('D5', 1.0),
    ]
    castle_harmony = [
        ('C4', 0.5), ('G3', 0.5), ('C4', 0.5), ('G3', 0.5),
        ('F3', 0.5), ('C3', 0.5), ('F3', 0.5), ('C3', 0.5),
    ] * 2
    
    melody_track = generate_track_from_sequence(castle_melody, 'triangle', VOLUME, use_adsr=True, attack=0.1, decay=0.3, sustain=0.4, release=0.3)
    harmony_track = generate_track_from_sequence(castle_harmony, 'sine', VOLUME * 0.5, use_adsr=True, attack=0.05, decay=0.2, sustain=0.3, release=0.2)
    
    final_length = max(len(melody_track), len(harmony_track))
    melody_track = np.pad(melody_track, (0, final_length - len(melody_track)))
    harmony_track = np.pad(harmony_track, (0, final_length - len(harmony_track)))
    
    castle_music = melody_track + harmony_track
    castle_music = np.tile(castle_music, 8)
    if np.max(np.abs(castle_music)) > 0:
        castle_music /= np.max(np.abs(castle_music))
    save_wav('bgm_castle.wav', castle_music, SAMPLE_RATE)

    # =========================================================================
    # Cave Music (FF1 Dungeon)
    # =========================================================================
    TEMPO = 120
    BEAT_DURATION = 60 / TEMPO
    cave_melody = [
        ('A3', 0.5), ('G3', 0.5), ('F3', 0.5), ('E3', 0.5),
        ('D3', 0.5), ('E3', 0.5), ('F3', 0.5), ('G3', 0.5),
    ] * 4
    cave_bass = [
        ('D2', 1.0), ('A2', 1.0),
    ] * 8
    
    melody_track = generate_track_from_sequence(cave_melody, 'sawtooth', VOLUME * 0.7, use_adsr=True, attack=0.2, decay=0.5, sustain=0.3, release=0.5)
    bass_track = generate_track_from_sequence(cave_bass, 'sine', VOLUME * 0.5, use_adsr=True, attack=0.2, decay=0.5, sustain=0.5, release=0.5)
    
    final_length = max(len(melody_track), len(bass_track))
    melody_track = np.pad(melody_track, (0, final_length - len(melody_track)))
    bass_track = np.pad(bass_track, (0, final_length - len(bass_track)))
    
    cave_music = melody_track + bass_track
    cave_music = np.tile(cave_music, 8)
    if np.max(np.abs(cave_music)) > 0:
        cave_music /= np.max(np.abs(cave_music))
    save_wav('bgm_cave.wav', cave_music, SAMPLE_RATE)

    # =========================================================================
    # Town Music (FF1 Town)
    # =========================================================================
    TEMPO = 112
    BEAT_DURATION = 60 / TEMPO
    town_melody = [
        ('G4', 0.5), ('E4', 0.5), ('F4', 0.5), ('G4', 0.5),
        ('A4', 0.5), ('G4', 0.5), ('F4', 0.5), ('E4', 0.5),
        ('D4', 0.5), ('E4', 0.5), ('F4', 0.5), ('G4', 0.5),
        ('C4', 1.0), ('REST', 1.0),
    ]
    town_harmony = [
        ('C4', 0.5), ('G3', 0.5), ('C4', 0.5), ('G3', 0.5),
        ('F3', 0.5), ('C3', 0.5), ('F3', 0.5), ('C3', 0.5),
    ] * 2
    
    melody_track = generate_track_from_sequence(town_melody, 'triangle', VOLUME, use_adsr=True, attack=0.01, decay=0.1, sustain=0.6, release=0.1)
    harmony_track = generate_track_from_sequence(town_harmony, 'sine', VOLUME * 0.4, use_adsr=True)
    
    final_length = max(len(melody_track), len(harmony_track))
    melody_track = np.pad(melody_track, (0, final_length - len(melody_track)))
    harmony_track = np.pad(harmony_track, (0, final_length - len(harmony_track)))
    
    town_music = melody_track + harmony_track
    town_music = np.tile(town_music, 8)
    if np.max(np.abs(town_music)) > 0:
        town_music /= np.max(np.abs(town_music))
    save_wav('bgm_town.wav', town_music, SAMPLE_RATE)

    # =========================================================================
    # Battle Music (FF1 Battle)
    # =========================================================================
    TEMPO = 160
    BEAT_DURATION = 60 / TEMPO
    battle_bass = [
        ('C3', 0.25), ('C3', 0.25), ('C3', 0.25), ('C3', 0.25),
        ('Bb2', 0.25), ('Bb2', 0.25), ('Bb2', 0.25), ('Bb2', 0.25),
        ('Ab2', 0.25), ('Ab2', 0.25), ('Ab2', 0.25), ('Ab2', 0.25),
        ('G2', 0.25), ('G2', 0.25), ('G2', 0.25), ('G2', 0.25),
    ] * 8
    battle_melody = [
        ('C5', 0.5), ('C5', 0.5), ('Eb5', 0.5), ('F5', 0.5),
        ('G5', 1.0), ('F5', 0.5), ('Eb5', 0.5),
        ('G5', 2.0),
    ]
    percussion_pattern = [1, 0, 0, 1, 1, 0, 1, 0] * 16
    melody_track = generate_track_from_sequence(battle_melody, 'square', VOLUME, use_adsr=True, attack=0.01, decay=0.1, sustain=0.4, release=0.1)
    harmony_track = generate_track_from_sequence(battle_bass, 'sawtooth', VOLUME * 0.7, use_adsr=True, attack=0.01, decay=0.1, sustain=0.3, release=0.1)
    percussion_track = generate_percussion_track(percussion_pattern, 0.25, VOLUME * 0.6)
    final_length = max(len(melody_track), len(harmony_track), len(percussion_track))
    melody_track = np.pad(melody_track, (0, final_length - len(melody_track)))
    harmony_track = np.pad(harmony_track, (0, final_length - len(harmony_track)))
    percussion_track = np.pad(percussion_track, (0, final_length - len(percussion_track)))
    battle_music = melody_track + harmony_track + percussion_track
    battle_music = np.clip(battle_music, -1.0, 1.0)
    battle_music = np.tile(battle_music, 8)
    if np.max(np.abs(battle_music)) > 0:
        battle_music /= np.max(np.abs(battle_music))
    save_wav('bgm_battle.wav', battle_music, SAMPLE_RATE)

    # =========================================================================
    # Victory Music (FF1 Fanfare - Full Theme)
    # =========================================================================
    TEMPO = 135
    BEAT_DURATION = 60 / TEMPO
    
    # Part 1: Initial Fanfare
    fanfare_melody = [
        ('C5', 0.25), ('C5', 0.25), ('C5', 0.25), ('C5', 1.0),
        ('Ab4', 1.0), ('Bb4', 1.0), ('C5', 2.0),
    ]
    fanfare_track = generate_track_from_sequence(fanfare_melody, 'square', VOLUME, use_adsr=True, attack=0.01, decay=0.1, sustain=0.6, release=0.1)
    
    # Part 2: Walking Bass Section (The "Waiting" music)
    walking_bass_seq = [
        ('C3', 0.5), ('E3', 0.5), ('G3', 0.5), ('A3', 0.5),
        ('Bb3', 0.5), ('A3', 0.5), ('G3', 0.5), ('E3', 0.5),
    ] * 4
    loop_melody_seq = [
        ('C5', 0.5), ('E5', 0.5), ('G5', 0.5), ('E5', 0.5),
        ('F5', 0.5), ('A5', 0.5), ('C6', 0.5), ('A5', 0.5),
    ] * 2
    
    bass_track = generate_track_from_sequence(walking_bass_seq, 'triangle', VOLUME * 0.6, use_adsr=True)
    loop_melody_track = generate_track_from_sequence(loop_melody_seq, 'square', VOLUME * 0.5, use_adsr=True)
    
    # Pad loop melody to match bass
    loop_melody_track = np.pad(loop_melody_track, (0, len(bass_track) - len(loop_melody_track)))
    loop_section = bass_track + loop_melody_track
    
    # Combine fanfare and loop (tiled)
    victory_full = np.concatenate((fanfare_track, np.tile(loop_section, 2)))
    if np.max(np.abs(victory_full)) > 0:
        victory_full /= np.max(np.abs(victory_full))
    save_wav('victory_music.wav', victory_full, SAMPLE_RATE)

    # =========================================================================
    # Shop Music (FF1 Shop)
    # =========================================================================
    TEMPO = 144
    BEAT_DURATION = 60 / TEMPO
    shop_melody = [
        ('C5', 0.5), ('E5', 0.5), ('G5', 0.5), ('E5', 0.5),
        ('F5', 0.5), ('A5', 0.5), ('C6', 0.5), ('A5', 0.5),
        ('G5', 0.5), ('E5', 0.5), ('C5', 0.5), ('G4', 0.5),
        ('F4', 0.5), ('G4', 0.5), ('A4', 0.5), ('B4', 0.5),
    ]
    melody_track = generate_track_from_sequence(shop_melody, 'triangle', VOLUME, use_adsr=True)
    shop_music = np.tile(melody_track, 8) # Increased tiling
    if np.max(np.abs(shop_music)) > 0:
        shop_music /= np.max(np.abs(shop_music))
    save_wav('bgm_shop.wav', shop_music, SAMPLE_RATE)

    # =========================================================================
    # Inn Music (FF1 Inn)
    # =========================================================================
    TEMPO = 100
    BEAT_DURATION = 60 / TEMPO
    inn_melody = [
        ('C5', 0.5), ('G4', 0.5), ('E4', 0.5), ('C4', 1.0),
    ]
    melody_track = generate_track_from_sequence(inn_melody, 'sine', VOLUME, use_adsr=True)
    save_wav('bgm_inn.wav', melody_track, SAMPLE_RATE)

    # =========================================================================
    # Airship Music (FF1 Airship)
    # =========================================================================
    TEMPO = 160
    BEAT_DURATION = 60 / TEMPO
    airship_melody = [
        ('C5', 0.25), ('D5', 0.25), ('E5', 0.25), ('F5', 0.25),
        ('G5', 0.5), ('A5', 0.5), ('B5', 0.5), ('C6', 0.5),
    ] * 4
    melody_track = generate_track_from_sequence(airship_melody, 'square', VOLUME, use_adsr=True)
    airship_music = np.tile(melody_track, 8) # Increased tiling
    if np.max(np.abs(airship_music)) > 0:
        airship_music /= np.max(np.abs(airship_music))
    save_wav('bgm_airship.wav', airship_music, SAMPLE_RATE)

    # =========================================================================
    # Boss Music (FF1 Chaos/Boss)
    # =========================================================================
    TEMPO = 150
    BEAT_DURATION = 60 / TEMPO
    boss_melody = [
        ('C4', 0.25), ('C3', 0.25), ('Eb4', 0.25), ('C3', 0.25),
        ('F4', 0.25), ('C3', 0.25), ('Gb4', 0.25), ('C3', 0.25),
    ] * 8
    melody_track = generate_track_from_sequence(boss_melody, 'sawtooth', VOLUME, use_adsr=True)
    boss_music = np.tile(melody_track, 4) # Increased tiling
    save_wav('bgm_boss.wav', boss_music, SAMPLE_RATE)

    # =========================================================================
    # Final Boss Music (FF1 Final Battle)
    # =========================================================================
    TEMPO = 180
    BEAT_DURATION = 60 / TEMPO
    final_boss_melody = [
        ('C3', 0.125), ('C3', 0.125), ('C3', 0.25),
    ] * 32
    melody_track = generate_track_from_sequence(final_boss_melody, 'sawtooth', VOLUME, use_adsr=True)
    final_boss_music = np.tile(melody_track, 4) # Increased tiling
    save_wav('bgm_final_boss.wav', final_boss_music, SAMPLE_RATE)

    # =========================================================================
    # Game Over Music (FF1 Game Over)
    # =========================================================================
    TEMPO = 72
    BEAT_DURATION = 60 / TEMPO
    game_over_melody = [
        ('C4', 1.0), ('G3', 1.0), ('Eb3', 1.0), ('C3', 2.0),
    ]
    melody_track = generate_track_from_sequence(game_over_melody, 'sine', VOLUME, use_adsr=True)
    save_wav('bgm_game_over.wav', melody_track, SAMPLE_RATE)

    # =========================================================================
    # Suspense Music (FF1 Chaos Shrine)
    # =========================================================================
    TEMPO = 90
    BEAT_DURATION = 60 / TEMPO
    suspense_melody = [
        ('C3', 0.5), ('Db3', 0.5), ('D3', 0.5), ('Eb3', 0.5),
    ] * 8
    melody_track = generate_track_from_sequence(suspense_melody, 'sine', VOLUME, use_adsr=True)
    suspense_music = np.tile(melody_track, 4) # Increased tiling
    save_wav('bgm_suspense.wav', suspense_music, SAMPLE_RATE)

if __name__ == "__main__":
    os.makedirs("src/main/resources/sounds", exist_ok=True)
    generate_sfx()
    generate_music()
    print("All sounds generated.")