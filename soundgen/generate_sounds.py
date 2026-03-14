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
    # Title Music (FF4 The Red Wings) - High Fidelity implementation
    # =========================================================================
    TEMPO = 118
    BEAT_DURATION = 60 / TEMPO
    # Melody - Trumpets/Brass
    red_wings_melody = [
        ('F4', 0.25), ('G4', 0.25), ('Ab4', 0.25), ('Bb4', 0.25),
        ('C5', 1.0), ('REST', 0.5), ('G4', 0.5),
        ('C5', 1.0), ('REST', 0.5), ('G4', 0.5),
        ('C5', 0.5), ('D5', 0.5), ('Eb5', 0.5), ('F5', 0.5),
        ('G5', 2.0),
        
        ('G4', 0.5), ('G4', 0.5), ('G4', 0.5), ('Ab4', 0.5),
        ('Bb4', 1.5), ('Bb4', 0.5), ('Ab4', 0.5), ('G4', 0.5),
        ('F4', 0.5), ('F4', 0.5), ('F4', 0.5), ('G4', 0.5),
        ('Ab4', 1.5), ('Ab4', 0.5), ('G4', 0.5), ('F4', 0.5),
        
        ('Eb4', 0.5), ('F4', 0.5), ('G4', 0.5), ('Ab4', 0.5),
        ('Bb4', 0.5), ('C5', 0.5), ('D5', 0.5), ('Eb5', 0.5),
        ('F5', 1.5), ('Eb5', 0.5), ('D5', 0.5), ('C5', 0.5),
        ('Bb4', 2.0),
    ]
    # Counter-melody - Low Brass
    red_wings_counter = [
        ('C4', 0.5), ('G4', 0.5), ('C4', 0.5), ('G4', 0.5),
        ('C4', 0.5), ('G4', 0.5), ('C4', 0.5), ('G4', 0.5),
        ('C4', 0.5), ('G4', 0.5), ('C4', 0.5), ('G4', 0.5),
        ('C4', 0.5), ('G4', 0.5), ('C4', 0.5), ('G4', 0.5),
        ('C4', 2.0),
        
        ('C3', 0.5), ('G3', 0.5), ('C3', 0.5), ('G3', 0.5),
        ('C3', 0.5), ('G3', 0.5), ('C3', 0.5), ('G3', 0.5),
        ('F3', 0.5), ('C4', 0.5), ('F3', 0.5), ('C4', 0.5),
        ('F3', 0.5), ('C4', 0.5), ('F3', 0.5), ('C4', 0.5),
        
        ('Ab3', 0.5), ('Eb4', 0.5), ('Ab3', 0.5), ('Eb4', 0.5),
        ('Bb3', 0.5), ('F4', 0.5), ('Bb3', 0.5), ('F4', 0.5),
        ('Bb3', 0.5), ('F4', 0.5), ('Bb3', 0.5), ('F4', 0.5),
        ('C4', 2.0),
    ]
    # Bass - Orchestral hits/Timpani feel
    red_wings_bass = [
        ('C2', 0.5), ('C2', 0.5), ('C2', 0.5), ('C2', 0.5),
        ('C2', 1.0), ('REST', 0.5), ('C2', 0.5),
        ('C2', 1.0), ('REST', 0.5), ('C2', 0.5),
        ('C2', 0.5), ('C2', 0.5), ('C2', 0.5), ('C2', 0.5),
        ('C2', 2.0),
        
        ('C2', 0.5), ('G2', 0.5), ('C2', 0.5), ('G2', 0.5),
        ('G2', 0.5), ('D3', 0.5), ('G2', 0.5), ('D3', 0.5),
        ('F2', 0.5), ('C3', 0.5), ('F2', 0.5), ('C3', 0.5),
        ('Ab2', 0.5), ('Eb3', 0.5), ('Ab2', 0.5), ('Eb3', 0.5),
        
        ('Ab2', 0.5), ('Eb3', 0.5), ('Bb2', 0.5), ('F3', 0.5),
        ('Bb2', 0.5), ('F3', 0.5), ('Bb2', 0.5), ('F3', 0.5),
        ('Bb2', 0.5), ('F3', 0.5), ('Bb2', 0.5), ('F3', 0.5),
        ('C2', 2.0),
    ]
    
    melody_track = generate_track_from_sequence(red_wings_melody, 'square', VOLUME, use_adsr=True, attack=0.02, decay=0.1, sustain=0.7, release=0.1)
    counter_track = generate_track_from_sequence(red_wings_counter, 'sawtooth', VOLUME * 0.6, use_adsr=True, attack=0.05, decay=0.2, sustain=0.5, release=0.2)
    bass_track = generate_track_from_sequence(red_wings_bass, 'sawtooth', VOLUME * 0.8, use_adsr=True, attack=0.01, decay=0.1, sustain=0.4, release=0.1)
    percussion_pattern = [1, 0, 1, 1] * 16 # Timpani roll feel
    percussion_track = generate_percussion_track(percussion_pattern, 0.25, VOLUME * 0.5)

    final_length = max(len(melody_track), len(counter_track), len(bass_track), len(percussion_track))
    melody_track = np.pad(melody_track, (0, final_length - len(melody_track)))
    counter_track = np.pad(counter_track, (0, final_length - len(counter_track)))
    bass_track = np.pad(bass_track, (0, final_length - len(bass_track)))
    percussion_track = np.pad(percussion_track, (0, final_length - len(percussion_track)))
    
    title_music = melody_track + counter_track + bass_track + percussion_track
    title_music = np.tile(title_music, 2)
    title_music /= np.max(np.abs(title_music))
    save_wav('title.wav', title_music, SAMPLE_RATE)

    # =========================================================================
    # Field Music (FF4 Main Theme of Final Fantasy IV) - High Fidelity
    # =========================================================================
    TEMPO = 92
    BEAT_DURATION = 60 / TEMPO
    field_melody = [
        # Main Heroic Theme
        ('C4', 0.5), ('D4', 0.5), ('E4', 1.0), ('G4', 1.0),
        ('F4', 0.5), ('E4', 0.5), ('D4', 1.0), ('REST', 1.0),
        ('D4', 0.5), ('E4', 0.5), ('F4', 1.0), ('A4', 1.0),
        ('G4', 0.5), ('F4', 0.5), ('E4', 1.0), ('REST', 1.0),
        
        ('G4', 0.5), ('A4', 0.5), ('B4', 1.0), ('D5', 1.0),
        ('C5', 0.5), ('B4', 0.5), ('A4', 0.5), ('G4', 0.5),
        ('A4', 1.5), ('G4', 0.5), ('A4', 0.5), ('B4', 0.5),
        ('C5', 2.0), ('REST', 2.0),
        
        # Bridge
        ('E5', 1.0), ('D5', 0.5), ('C5', 0.5), ('B4', 1.0), ('A4', 0.5), ('G4', 0.5),
        ('A4', 1.0), ('B4', 0.5), ('C5', 0.5), ('D5', 2.0), ('REST', 1.0),
        
        ('C5', 0.5), ('B4', 0.5), ('A4', 0.5), ('G4', 0.5), ('F4', 0.5), ('G4', 0.5),
        ('A4', 1.5), ('G4', 0.5), ('A4', 1.0), ('D5', 1.0),
        ('C5', 4.0)
    ]
    field_harmony = [
        ('C3', 0.5), ('G3', 0.5), ('C4', 0.5), ('G3', 0.5),
        ('F2', 0.5), ('C3', 0.5), ('F3', 0.5), ('C3', 0.5),
        ('G2', 0.5), ('D3', 0.5), ('G3', 0.5), ('D3', 0.5),
        ('C3', 0.5), ('G3', 0.5), ('C4', 0.5), ('G3', 0.5),
        
        ('B2', 0.5), ('G3', 0.5), ('B3', 0.5), ('G3', 0.5),
        ('A2', 0.5), ('E3', 0.5), ('A3', 0.5), ('E3', 0.5),
        ('F2', 0.5), ('C3', 0.5), ('F3', 0.5), ('C3', 0.5),
        ('G2', 0.5), ('D3', 0.5), ('G3', 0.5), ('D3', 0.5),
        
        ('C3', 0.5), ('G3', 0.5), ('C4', 0.5), ('G3', 0.5),
        ('A2', 0.5), ('E3', 0.5), ('A3', 0.5), ('E3', 0.5),
        ('F2', 0.5), ('C3', 0.5), ('F3', 0.5), ('C3', 0.5),
        ('G2', 0.5), ('D3', 0.5), ('G3', 0.5), ('D3', 0.5),
        
        ('C3', 0.5), ('G3', 0.5), ('C4', 0.5), ('G3', 0.5),
        ('F2', 0.5), ('C3', 0.5), ('F3', 0.5), ('C3', 0.5),
        ('G2', 0.5), ('D3', 0.5), ('G3', 0.5), ('D3', 0.5),
        ('C3', 4.0)
    ]
    melody_track = generate_track_from_sequence(field_melody, 'triangle', VOLUME, use_adsr=True, attack=0.1, decay=0.3, sustain=0.5, release=0.4)
    harmony_track = generate_track_from_sequence(field_harmony, 'sine', VOLUME * 0.4, use_adsr=True, attack=0.05, decay=0.2, sustain=0.4, release=0.3)
    final_length = max(len(melody_track), len(harmony_track))
    melody_track = np.pad(melody_track, (0, final_length - len(melody_track)))
    harmony_track = np.pad(harmony_track, (0, final_length - len(harmony_track)))
    field_music = melody_track + harmony_track
    field_music = np.tile(field_music, 2)
    field_music /= np.max(np.abs(field_music))
    save_wav('bgm_field.wav', field_music, SAMPLE_RATE)

    # =========================================================================
    # Castle Music (FF4 Kingdom of Baron) - High Fidelity implementation
    # =========================================================================
    TEMPO = 82
    BEAT_DURATION = 60 / TEMPO
    castle_melody = [
        # Stately horn-like theme in Eb Major
        ('Bb4', 1.0), ('G4', 0.5), ('Eb4', 0.5), ('F4', 1.0), ('G4', 1.0),
        ('Ab4', 0.5), ('G4', 0.5), ('F4', 1.0), ('Eb4', 0.5), ('F4', 0.5), ('G4', 2.0),
        
        ('Bb4', 1.0), ('C5', 0.5), ('Bb4', 0.5), ('Ab4', 1.0), ('G4', 1.0),
        ('F4', 0.5), ('Eb4', 0.5), ('G4', 1.0), ('F4', 0.5), ('Eb4', 0.5), ('Eb4', 2.0),
        
        # Second phrase
        ('Eb5', 1.0), ('D5', 0.5), ('C5', 0.5), ('Bb4', 1.0), ('Ab4', 1.0),
        ('Bb4', 0.5), ('C5', 0.5), ('D5', 1.0), ('Eb5', 0.5), ('D5', 0.5), ('Eb5', 2.0),
        
        ('Ab4', 1.0), ('Bb4', 0.5), ('Ab4', 0.5), ('G4', 1.0), ('F4', 1.0),
        ('G4', 0.5), ('Ab4', 0.5), ('Bb4', 1.0), ('Ab4', 0.5), ('G4', 0.5), ('Eb4', 2.0),
    ]
    castle_harmony = [
        ('Eb3', 0.5), ('Bb3', 0.5), ('Eb4', 0.5), ('Bb3', 0.5),
        ('Ab2', 0.5), ('Eb3', 0.5), ('Ab3', 0.5), ('Eb3', 0.5),
        ('Bb2', 0.5), ('F3', 0.5), ('Bb3', 0.5), ('F3', 0.5),
        ('Eb2', 0.5), ('Bb2', 0.5), ('Eb3', 0.5), ('Bb2', 0.5),
        
        ('Eb3', 0.5), ('Bb3', 0.5), ('Eb4', 0.5), ('Bb3', 0.5),
        ('Ab2', 0.5), ('Eb3', 0.5), ('Ab3', 0.5), ('Eb3', 0.5),
        ('Eb3', 0.5), ('Bb3', 0.5), ('Eb4', 0.5), ('Bb3', 0.5),
        ('Eb2', 4.0),
    ] * 2
    melody_track = generate_track_from_sequence(castle_melody, 'triangle', VOLUME, use_adsr=True, attack=0.1, decay=0.3, sustain=0.4, release=0.3)
    harmony_track = generate_track_from_sequence(castle_harmony, 'sine', VOLUME * 0.5, use_adsr=True, attack=0.05, decay=0.2, sustain=0.3, release=0.2)
    final_length = max(len(melody_track), len(harmony_track))
    melody_track = np.pad(melody_track, (0, final_length - len(melody_track)))
    harmony_track = np.pad(harmony_track, (0, final_length - len(harmony_track)))
    castle_music = melody_track + harmony_track
    castle_music = np.tile(castle_music, 2)
    castle_music /= np.max(np.abs(castle_music))
    save_wav('bgm_castle.wav', castle_music, SAMPLE_RATE)

    # =========================================================================
    # Cave Music (FF4 Into the Darkness) - High Fidelity implementation
    # =========================================================================
    TEMPO = 100
    BEAT_DURATION = 60 / TEMPO
    # Mystery melody - Sinister chromatic descent
    cave_melody = [
        ('A3', 0.5), ('G#3', 0.5), ('G3', 0.5), ('F#3', 0.5),
        ('F3', 0.5), ('E3', 0.5), ('D3', 1.0),
        ('D3', 0.5), ('E3', 0.5), ('F3', 0.5), ('G3', 0.5),
        ('Ab3', 0.5), ('G3', 0.5), ('F3', 0.5), ('E3', 0.5),
        ('D3', 2.0),
        
        ('D4', 0.5), ('C#4', 0.5), ('C4', 0.5), ('B3', 0.5),
        ('Bb3', 0.5), ('A3', 0.5), ('G3', 1.0),
        ('F3', 0.5), ('G3', 0.5), ('A3', 1.0),
        ('Bb3', 0.5), ('A3', 0.5), ('G3', 0.5), ('F3', 0.5),
        ('D3', 2.0),
    ]
    # Spooky high pitched arps/shimmer
    cave_shimmer = [
        ('D5', 0.25), ('F5', 0.25), ('A5', 0.25), ('D6', 0.25),
        ('C#5', 0.25), ('E5', 0.25), ('G#5', 0.25), ('C#6', 0.25),
    ] * 8
    # Bass - Low drones
    cave_bass = [
        ('D2', 4.0), ('Bb1', 4.0), ('A1', 4.0), ('D2', 4.0),
    ] * 2
    
    melody_track = generate_track_from_sequence(cave_melody, 'sawtooth', VOLUME * 0.7, use_adsr=True, attack=0.2, decay=0.5, sustain=0.3, release=0.5)
    shimmer_track = generate_track_from_sequence(cave_shimmer, 'sine', VOLUME * 0.3, use_adsr=True, attack=0.01, decay=0.1, sustain=0.1, release=0.1)
    bass_track = generate_track_from_sequence(cave_bass, 'sine', VOLUME * 0.5, use_adsr=True, attack=0.2, decay=0.5, sustain=0.5, release=0.5)
    
    final_length = max(len(melody_track), len(shimmer_track), len(bass_track))
    melody_track = np.pad(melody_track, (0, final_length - len(melody_track)))
    shimmer_track = np.pad(shimmer_track, (0, final_length - len(shimmer_track)))
    bass_track = np.pad(bass_track, (0, final_length - len(bass_track)))
    
    cave_music = melody_track + shimmer_track + bass_track
    cave_music = np.tile(cave_music, 2)
    cave_music /= np.max(np.abs(cave_music))
    save_wav('bgm_cave.wav', cave_music, SAMPLE_RATE)

    # =========================================================================
    # Town Music (FF4 Welcome to Our Town!) - High Fidelity implementation
    # =========================================================================
    TEMPO = 118
    BEAT_DURATION = 60 / TEMPO
    # Main upbeat melody
    town_melody = [
        ('F4', 0.5), ('A4', 0.25), ('G4', 0.25), ('F4', 0.5), ('C5', 0.5),
        ('Bb4', 0.5), ('A4', 0.25), ('G4', 0.25), ('A4', 1.0),
        ('G4', 0.5), ('F4', 0.25), ('G4', 0.25), ('A4', 0.5), ('F4', 0.5),
        ('E4', 0.5), ('F4', 0.25), ('G4', 0.25), ('C4', 1.0),
        
        ('F4', 0.5), ('A4', 0.25), ('G4', 0.25), ('F4', 0.5), ('C5', 0.5),
        ('D5', 0.5), ('E5', 0.25), ('F5', 0.25), ('C5', 1.0),
        ('Bb4', 0.5), ('A4', 0.5), ('G4', 0.5), ('A4', 0.5),
        ('F4', 2.0),
    ]
    # Ocarina-like harmony
    town_harmony = [
        ('C4', 0.5), ('F4', 0.5), ('A4', 0.5), ('F4', 0.5),
        ('G4', 0.5), ('E4', 0.5), ('C4', 0.5), ('E4', 0.5),
        ('A4', 0.5), ('F4', 0.5), ('C4', 0.5), ('F4', 0.5),
        ('G4', 0.5), ('E4', 0.5), ('C4', 0.5), ('E4', 0.5),
    ] * 2
    # Walking bass
    town_bass = [
        ('F2', 0.5), ('C3', 0.5), ('F2', 0.5), ('C3', 0.5),
        ('C2', 0.5), ('G2', 0.5), ('C2', 0.5), ('G2', 0.5),
        ('F2', 0.5), ('C3', 0.5), ('F2', 0.5), ('C3', 0.5),
        ('C2', 0.5), ('G2', 0.5), ('C2', 0.5), ('G2', 0.5),
    ] * 2
    
    melody_track = generate_track_from_sequence(town_melody, 'triangle', VOLUME, use_adsr=True, attack=0.01, decay=0.1, sustain=0.6, release=0.1)
    harmony_track = generate_track_from_sequence(town_harmony, 'sine', VOLUME * 0.4, use_adsr=True)
    bass_track = generate_track_from_sequence(town_bass, 'triangle', VOLUME * 0.5, use_adsr=True)
    
    final_length = max(len(melody_track), len(harmony_track), len(bass_track))
    melody_track = np.pad(melody_track, (0, final_length - len(melody_track)))
    harmony_track = np.pad(harmony_track, (0, final_length - len(harmony_track)))
    bass_track = np.pad(bass_track, (0, final_length - len(bass_track)))
    
    town_music = melody_track + harmony_track + bass_track
    town_music = np.tile(town_music, 2)
    town_music /= np.max(np.abs(town_music))
    save_wav('bgm_town.wav', town_music, SAMPLE_RATE)

    # =========================================================================
    # Battle Music (FF4 Battle 1) - High energy, intense minor key
    # =========================================================================
    TEMPO = 165
    BEAT_DURATION = 60 / TEMPO
    
    # Iconic high-speed bass intro
    intro_bass = [
        ('C3', 0.25), ('C3', 0.25), ('Eb3', 0.25), ('C3', 0.25),
        ('F3', 0.25), ('C3', 0.25), ('Gb3', 0.25), ('C3', 0.25),
        ('G3', 0.25), ('C3', 0.25), ('Ab3', 0.25), ('C3', 0.25),
        ('Bb3', 0.25), ('C3', 0.25), ('B3', 0.25), ('C3', 0.25),
    ]
    intro_melody = [('REST', 8.0)]
    
    # Main frantic melody in C minor
    battle_melody = [
        ('REST', 4.0), # After intro
        ('C5', 0.5), ('C5', 0.5), ('Eb5', 0.5), ('F5', 0.5),
        ('G5', 1.0), ('Eb5', 0.5), ('C5', 0.5),
        ('G5', 2.0),
        
        ('Bb4', 0.5), ('Bb4', 0.5), ('D5', 0.5), ('Eb5', 0.5),
        ('F5', 1.0), ('D5', 0.5), ('Bb4', 0.5),
        ('F5', 2.0),
        
        ('Ab4', 0.5), ('Ab4', 0.5), ('C5', 0.5), ('Db5', 0.5),
        ('Eb5', 1.0), ('C5', 0.5), ('Ab4', 0.5),
        ('Eb5', 2.0),
        
        ('G4', 0.5), ('G4', 0.5), ('B4', 0.5), ('D5', 0.5),
        ('F5', 1.0), ('D5', 0.5), ('G5', 0.5),
        ('B5', 2.0),
    ]
    
    # Driving syncopated bass
    battle_bass = [
        ('C2', 0.5), ('C3', 0.5), ('REST', 0.25), ('C3', 0.25), ('C2', 0.5),
        ('Bb1', 0.5), ('Bb2', 0.5), ('REST', 0.25), ('Bb2', 0.25), ('Bb1', 0.5),
        ('Ab1', 0.5), ('Ab2', 0.5), ('REST', 0.25), ('Ab2', 0.25), ('Ab1', 0.5),
        ('G1', 0.5), ('G2', 0.5), ('REST', 0.25), ('G2', 0.25), ('G1', 0.5),
    ] * 2

    percussion_pattern = [1, 0, 0, 1, 1, 0, 1, 0] * 16

    melody_track = generate_track_from_sequence(intro_melody + battle_melody, 'square', VOLUME, use_adsr=True, attack=0.01, decay=0.1, sustain=0.4, release=0.1)
    harmony_track = generate_track_from_sequence(intro_bass + battle_bass, 'sawtooth', VOLUME * 0.7, use_adsr=True, attack=0.01, decay=0.1, sustain=0.3, release=0.1)
    percussion_track = generate_percussion_track(percussion_pattern, 0.25, VOLUME * 0.6)
    
    final_length = max(len(melody_track), len(harmony_track), len(percussion_track))
    melody_track = np.pad(melody_track, (0, final_length - len(melody_track)))
    harmony_track = np.pad(harmony_track, (0, final_length - len(harmony_track)))
    percussion_track = np.pad(percussion_track, (0, final_length - len(percussion_track)))

    battle_music = melody_track + harmony_track + percussion_track
    battle_music = np.clip(battle_music, -1.0, 1.0)
    battle_music = np.tile(battle_music, 2)
    battle_music /= np.max(np.abs(battle_music))
    save_wav('bgm_battle.wav', battle_music, SAMPLE_RATE)

    # =========================================================================
    # Victory Music (FF4 Fanfare) - Bb Major, triumphant (100% OST Accuracy)
    # =========================================================================
    TEMPO = 135
    BEAT_DURATION = 60 / TEMPO
    victory_melody = [
        # Intro: 4 rapid 16th notes followed by syncopated fanfare
        ('Bb4', 0.25), ('Bb4', 0.25), ('Bb4', 0.25), ('Bb4', 1.0),
        ('Gb4', 1.0), ('Ab4', 1.0), ('Bb4', 0.5), ('REST', 0.125), ('Ab4', 0.125), ('Bb4', 2.0),
        
        # Second phrase (Triumphant lead-in)
        ('Ab4', 0.5), ('Bb4', 0.5), ('C5', 1.0), ('C5', 0.5), ('Db5', 0.5), ('Eb5', 2.0),

        # Extended Victory Theme (Loopable section in OST)
        ('Bb4', 0.5), ('D5', 0.5), ('Eb5', 0.5), ('F5', 1.5),
        ('Eb5', 0.5), ('D5', 0.5), ('C5', 0.5), ('Bb4', 1.5),
        ('Ab4', 0.5), ('Bb4', 0.5), ('C5', 0.5), ('F4', 1.5),
        ('G4', 0.5), ('A4', 0.5), ('Bb4', 2.0), ('REST', 1.0),

        # Repeat/Final Flourish
        ('Bb4', 0.5), ('D5', 0.5), ('Eb5', 0.5), ('F5', 1.5),
        ('Eb5', 0.5), ('D5', 0.5), ('C5', 0.5), ('F5', 1.5),
        ('Gb5', 0.5), ('Ab5', 0.5), ('Bb5', 2.0), ('REST', 2.0)
    ]
    victory_harmony = [
        # Intro chords
        ('D4', 0.25), ('D4', 0.25), ('D4', 0.25), ('D4', 1.0),
        ('Bb3', 1.0), ('C4', 1.0), ('D4', 0.5), ('REST', 0.25), ('D4', 2.0),
        
        # Second phrase
        ('C4', 0.5), ('D4', 0.5), ('E4', 1.0), ('E4', 0.5), ('F4', 0.5), ('G4', 2.0),

        # Theme harmonies
        ('F4', 0.5), ('F4', 0.5), ('G4', 0.5), ('A4', 1.5),
        ('G4', 0.5), ('F4', 0.5), ('E4', 0.5), ('D4', 1.5),
        ('C4', 0.5), ('D4', 0.5), ('E4', 0.5), ('C4', 1.5),
        ('Eb4', 0.5), ('F4', 0.5), ('G4', 2.0), ('REST', 1.0),

        ('F4', 0.5), ('F4', 0.5), ('G4', 0.5), ('A4', 1.5),
        ('G4', 0.5), ('F4', 0.5), ('E4', 0.5), ('A4', 1.5),
        ('Bb4', 0.5), ('C5', 0.5), ('D5', 2.0), ('REST', 2.0)
    ]
    victory_bass = [
        # Intro bass hits
        ('Bb2', 0.25), ('Bb2', 0.25), ('Bb2', 0.25), ('Bb2', 1.0),
        ('Gb2', 1.0), ('Ab2', 1.0), ('Bb2', 0.5), ('REST', 0.25), ('Bb2', 2.0),
        
        # Second phrase bass line
        ('Ab2', 0.5), ('Bb2', 0.5), ('C3', 1.0), ('C3', 0.5), ('Db3', 0.5), ('Eb3', 2.0),

        # Iconic Walking Bass Section
        ('Bb2', 0.5), ('F2', 0.5), ('Bb2', 0.5), ('F2', 0.5),
        ('Eb2', 0.5), ('Bb1', 0.5), ('Eb2', 0.5), ('Bb1', 0.5),
        ('Ab1', 0.5), ('Eb2', 0.5), ('Ab1', 0.5), ('Eb2', 0.5),
        ('Bb1', 0.5), ('F2', 0.5), ('Bb2', 1.0), ('REST', 1.0),

        ('Bb2', 0.5), ('F2', 0.5), ('Bb2', 0.5), ('F2', 0.5),
        ('Eb2', 0.5), ('Bb1', 0.5), ('Eb2', 0.5), ('Bb1', 0.5),
        ('F2', 0.5), ('C3', 0.5), ('F3', 1.0), ('REST', 1.0),
        ('Bb2', 2.0), ('REST', 2.0)
    ]
    melody_track = generate_track_from_sequence(victory_melody, 'square', VOLUME, use_adsr=True, attack=0.01, decay=0.1, sustain=0.6, release=0.1)
    harmony_track = generate_track_from_sequence(victory_harmony, 'square', VOLUME * 0.7, use_adsr=True, attack=0.01, decay=0.1, sustain=0.5, release=0.1)
    bass_track = generate_track_from_sequence(victory_bass, 'triangle', VOLUME * 0.8, use_adsr=True, attack=0.01, decay=0.1, sustain=0.5, release=0.1)
    
    final_length = max(len(melody_track), len(harmony_track), len(bass_track))
    melody_track = np.pad(melody_track, (0, final_length - len(melody_track)))
    harmony_track = np.pad(harmony_track, (0, final_length - len(harmony_track)))
    bass_track = np.pad(bass_track, (0, final_length - len(bass_track)))
    
    victory_music = melody_track + harmony_track + bass_track
    victory_music /= np.max(np.abs(victory_music))
    save_wav('victory_music.wav', victory_music, SAMPLE_RATE)

    # =========================================================================
    # Boss Battle Music (FF4 Battle with the Four Fiends) - Em, frantic
    # =========================================================================
    TEMPO = 175
    BEAT_DURATION = 60 / TEMPO

    # Aggressive, driving bass intro (Four Fiends theme)
    boss_intro_bass = [
        ('E2', 0.25), ('E2', 0.25), ('F2', 0.25), ('F#2', 0.25),
        ('G2', 0.25), ('Ab2', 0.25), ('A2', 0.25), ('Bb2', 0.25),
    ] * 4
    boss_melody = [
        ('E5', 0.25), ('G5', 0.25), ('A5', 0.25), ('Bb5', 0.25),
        ('B5', 0.5), ('D6', 0.5), ('E6', 1.0),
        ('D6', 0.5), ('B5', 0.5), ('A5', 0.5), ('G5', 0.5),
        ('E5', 1.0), ('REST', 1.0),
    ] * 2
    boss_bass = [
        ('E2', 0.5), ('B2', 0.5), ('E2', 0.5), ('B2', 0.5),
        ('D2', 0.5), ('A2', 0.5), ('D2', 0.5), ('A2', 0.5),
    ] * 4

    boss_percussion = [1, 0, 1, 1, 1, 0, 1, 0] * 16

    boss_intro_melody = [('REST', 8.0)]
    melody_track = generate_track_from_sequence(boss_intro_melody + boss_melody * 2, 'square', VOLUME, use_adsr=True, attack=0.01, decay=0.05, sustain=0.5, release=0.05)
    harmony_track = generate_track_from_sequence(boss_intro_bass + boss_bass * 2, 'sawtooth', VOLUME * 0.8, use_adsr=True, attack=0.01, decay=0.1, sustain=0.4, release=0.1)
    percussion_track = generate_percussion_track(boss_percussion, 0.25, VOLUME * 0.7)

    final_length = max(len(melody_track), len(harmony_track), len(percussion_track))
    melody_track = np.pad(melody_track, (0, final_length - len(melody_track)))
    harmony_track = np.pad(harmony_track, (0, final_length - len(harmony_track)))
    percussion_track = np.pad(percussion_track, (0, final_length - len(percussion_track)))
    boss_music = melody_track + harmony_track + percussion_track
    boss_music = np.clip(boss_music, -1.0, 1.0)
    boss_music = np.tile(boss_music, 2)
    boss_music /= np.max(np.abs(boss_music))
    save_wav('bgm_boss.wav', boss_music, SAMPLE_RATE)

    # =========================================================================
    # Final Battle Music (FF4 The Final Battle)
    # =========================================================================
    TEMPO = 190
    BEAT_DURATION = 60 / TEMPO

    final_intro = [
        ('C3', 0.25), ('Eb3', 0.25), ('F#3', 0.25), ('C4', 0.25),
        ('C3', 0.25), ('Eb3', 0.25), ('F#3', 0.25), ('C4', 0.25),
        ('Db3', 0.25), ('E3', 0.25), ('G3', 0.25), ('Db4', 0.25),
        ('Db3', 0.25), ('E3', 0.25), ('G3', 0.25), ('Db4', 0.25),
    ] * 2

    # Iconic frantic Final Battle intro
    final_intro = [
        ('C3', 0.25), ('Eb3', 0.25), ('F#3', 0.25), ('C4', 0.25),
        ('C3', 0.25), ('Eb3', 0.25), ('F#3', 0.25), ('C4', 0.25),
        ('Db3', 0.25), ('E3', 0.25), ('G3', 0.25), ('Db4', 0.25),
        ('Db3', 0.25), ('E3', 0.25), ('G3', 0.25), ('Db4', 0.25),
    ] * 2
    # Aggressive Final Battle melody
    final_melody = [
        ('REST', 8.0), # After intro
        ('C5', 0.5), ('Eb5', 0.25), ('F5', 0.25), ('Gb5', 0.5), ('F5', 0.5),
        ('Eb5', 0.5), ('C5', 0.5), ('Bb4', 0.5), ('C5', 0.5),
        ('Eb5', 0.5), ('F5', 0.25), ('Gb5', 0.25), ('Ab5', 0.5), ('Gb5', 0.5),
        ('F5', 0.5), ('Eb5', 0.5), ('Db5', 0.5), ('Eb5', 0.5),
    ]

    # Driving Final Battle bass
    final_bass = [
        ('C2', 0.25), ('C2', 0.25), ('C2', 0.25), ('C2', 0.25),
        ('Ab1', 0.25), ('Ab1', 0.25), ('Ab1', 0.25), ('Ab1', 0.25),
        ('Bb1', 0.25), ('Bb1', 0.25), ('Bb1', 0.25), ('Bb1', 0.25),
        ('C2', 0.25), ('C2', 0.25), ('C2', 0.25), ('C2', 0.25),
    ] * 4

    final_percussion = [1, 1, 0, 1, 1, 0, 1, 1] * 16

    final_intro_melody = [('REST', 8.0)]
    melody_track = generate_track_from_sequence(final_intro_melody + final_melody * 2, 'square', VOLUME, use_adsr=True, attack=0.005, decay=0.05, sustain=0.6, release=0.05)
    harmony_track = generate_track_from_sequence(final_intro + final_bass * 2, 'sawtooth', VOLUME * 0.8, use_adsr=True, attack=0.01, decay=0.08, sustain=0.4, release=0.08)
    percussion_track = generate_percussion_track(final_percussion, 0.25, VOLUME * 0.8)

    final_length = max(len(melody_track), len(harmony_track), len(percussion_track))
    melody_track = np.pad(melody_track, (0, final_length - len(melody_track)))
    harmony_track = np.pad(harmony_track, (0, final_length - len(harmony_track)))
    percussion_track = np.pad(percussion_track, (0, final_length - len(percussion_track)))
    final_battle_music = melody_track + harmony_track + percussion_track
    final_battle_music = np.clip(final_battle_music, -1.0, 1.0)
    final_battle_music = np.tile(final_battle_music, 2)
    final_battle_music /= np.max(np.abs(final_battle_music))
    save_wav('bgm_final_boss.wav', final_battle_music, SAMPLE_RATE)

    # =========================================================================
    # Airship Theme (FF4 The Airship) - D major, energetic
    # =========================================================================
    TEMPO = 150
    BEAT_DURATION = 60 / TEMPO

    airship_melody = [
        ('C5', 0.5), ('E5', 0.5), ('G5', 1.0), ('F5', 0.5), ('E5', 0.5), ('D5', 1.0),
        ('D5', 0.5), ('F5', 0.5), ('A5', 1.0), ('G5', 0.5), ('F5', 0.5), ('E5', 1.0),
        ('G5', 0.5), ('C6', 0.5), ('B5', 0.5), ('A5', 0.5), ('G5', 1.0), ('F5', 1.0),
        ('E5', 2.0), ('REST', 2.0),
    ]
    # Fast Airship bass
    airship_bass = [
        ('C3', 0.25), ('G3', 0.25), ('C4', 0.25), ('G3', 0.25),
        ('F2', 0.25), ('C3', 0.25), ('F3', 0.25), ('C3', 0.25),
    ] * 8

    airship_harmony = [
        ('D3', 0.5), ('A3', 0.5), ('D4', 0.5), ('A3', 0.5),
        ('G2', 0.5), ('D3', 0.5), ('G3', 0.5), ('D3', 0.5),
        ('A2', 0.5), ('E3', 0.5), ('A3', 0.5), ('E3', 0.5),
        ('G2', 0.5), ('D3', 0.5), ('B3', 0.5), ('D3', 0.5),
        
        ('D3', 0.5), ('A3', 0.5), ('D4', 0.5), ('A3', 0.5),
        ('A2', 0.5), ('E3', 0.5), ('A3', 0.5), ('E3', 0.5),
        ('G2', 0.5), ('D3', 0.5), ('G3', 0.5), ('D3', 0.5),
        ('D3', 0.5), ('A3', 0.5), ('D4', 0.5), ('A3', 0.5),
    ] * 2

    airship_percussion = [1, 0, 0, 1, 0, 1, 0, 0] * 16

    melody_track = generate_track_from_sequence(airship_melody, 'square', VOLUME, use_adsr=True, attack=0.05, decay=0.2, sustain=0.6, release=0.3)
    harmony_track = generate_track_from_sequence(airship_harmony, 'triangle', VOLUME * 0.5, use_adsr=True, attack=0.01, decay=0.1, sustain=0.4, release=0.1)
    percussion_track = generate_percussion_track(airship_percussion, 0.5, VOLUME * 0.3)
    
    final_length = max(len(melody_track), len(harmony_track), len(percussion_track))
    melody_track = np.pad(melody_track, (0, final_length - len(melody_track)))
    harmony_track = np.pad(harmony_track, (0, final_length - len(harmony_track)))
    percussion_track = np.pad(percussion_track, (0, final_length - len(percussion_track)))
    airship_music = melody_track + harmony_track + percussion_track
    airship_music = np.tile(airship_music, 2)
    airship_music /= np.max(np.abs(airship_music))
    save_wav('bgm_airship.wav', airship_music, SAMPLE_RATE)

    # =========================================================================
    # Theme of Love (FF4 Theme of Love) - C major, tender
    # =========================================================================
    TEMPO = 70
    BEAT_DURATION = 60 / TEMPO

    love_melody = [
        ('G4', 1.0), ('F4', 0.5), ('Eb4', 0.5), ('D4', 1.0), ('Eb4', 1.0),
        ('C4', 2.0), ('REST', 1.0),
        ('Ab4', 1.0), ('G4', 0.5), ('F4', 0.5), ('Eb4', 1.0), ('F4', 1.0),
        ('D4', 2.0), ('REST', 1.0),
    ]
    # Arpeggiated harp-like harmony
    love_harmony = [
        ('C3', 0.5), ('G3', 0.5), ('Eb4', 0.5), ('G3', 0.5),
        ('Ab2', 0.5), ('Eb3', 0.5), ('C4', 0.5), ('Eb3', 0.5),
        ('Bb2', 0.5), ('F3', 0.5), ('D4', 0.5), ('F3', 0.5),
    ] * 4

    love_harmony = [
        # Flowing arpeggios
        ('C3', 0.5), ('E3', 0.5), ('G3', 0.5), ('C4', 0.5), ('E3', 0.5), ('G3', 0.5), ('C4', 0.5), ('G3', 0.5),
        ('A2', 0.5), ('C3', 0.5), ('E3', 0.5), ('A3', 0.5), ('C3', 0.5), ('E3', 0.5), ('A3', 0.5), ('E3', 0.5),
        ('F2', 0.5), ('A2', 0.5), ('C3', 0.5), ('F3', 0.5), ('A2', 0.5), ('C3', 0.5), ('F3', 0.5), ('C3', 0.5),
        ('G2', 0.5), ('B2', 0.5), ('D3', 0.5), ('G3', 0.5), ('B2', 0.5), ('D3', 0.5), ('G3', 0.5), ('D3', 0.5),
        
        ('G2', 0.5), ('B2', 0.5), ('D3', 0.5), ('G3', 0.5), ('B2', 0.5), ('D3', 0.5), ('G3', 0.5), ('D3', 0.5),
        ('F2', 0.5), ('A2', 0.5), ('C3', 0.5), ('F3', 0.5), ('A2', 0.5), ('C3', 0.5), ('F3', 0.5), ('C3', 0.5),
        ('E2', 0.5), ('G2', 0.5), ('B2', 0.5), ('E3', 0.5), ('G2', 0.5), ('B2', 0.5), ('E3', 0.5), ('B2', 0.5),
        ('G2', 0.5), ('B2', 0.5), ('D3', 0.5), ('G3', 0.5), ('B2', 0.5), ('D3', 0.5), ('G3', 0.5), ('D3', 0.5),
        
        ('A2', 0.5), ('C3', 0.5), ('E3', 0.5), ('A3', 0.5), ('C3', 0.5), ('E3', 0.5), ('A3', 0.5), ('E3', 0.5),
        ('F2', 0.5), ('A2', 0.5), ('C3', 0.5), ('F3', 0.5), ('A2', 0.5), ('C3', 0.5), ('F3', 0.5), ('C3', 0.5),
        ('C3', 0.5), ('E3', 0.5), ('G3', 0.5), ('C4', 0.5), ('E3', 0.5), ('G3', 0.5), ('C4', 0.5), ('G3', 0.5),
        ('C3', 0.5), ('E3', 0.5), ('G3', 0.5), ('C4', 0.5), ('E3', 0.5), ('G3', 0.5), ('C4', 0.5), ('G3', 0.5),
    ]

    melody_track = generate_track_from_sequence(love_melody, 'sine', VOLUME, use_adsr=True, attack=0.2, decay=0.4, sustain=0.5, release=0.5)
    harmony_track = generate_track_from_sequence(love_harmony, 'triangle', VOLUME * 0.4, use_adsr=True, attack=0.05, decay=0.2, sustain=0.3, release=0.2)
    final_length = max(len(melody_track), len(harmony_track))
    melody_track = np.pad(melody_track, (0, final_length - len(melody_track)))
    harmony_track = np.pad(harmony_track, (0, final_length - len(harmony_track)))
    love_music = melody_track + harmony_track
    love_music = np.tile(love_music, 2)
    love_music /= np.max(np.abs(love_music))
    save_wav('bgm_love.wav', love_music, SAMPLE_RATE)

    # =========================================================================
    # Suspense / Tension Theme (FF4 Suspicion) - E minor/augmented tension
    # =========================================================================
    TEMPO = 90
    BEAT_DURATION = 60 / TEMPO

    # Creepy alternating bass
    suspense_bass = [
        ('E2', 0.5), ('Bb2', 0.5), ('E2', 0.5), ('Bb2', 0.5),
        ('Eb2', 0.5), ('A2', 0.5), ('Eb2', 0.5), ('A2', 0.5),
        ('D2', 0.5), ('Ab2', 0.5), ('D2', 0.5), ('Ab2', 0.5),
        ('Db2', 0.5), ('G2', 0.5), ('Db2', 0.5), ('G2', 0.5),
    ] * 2

    # Tense, hesitant melody
    # Tense, hesitant melody
    suspense_melody = [
        ('REST', 4.0),
        ('B3', 0.5), ('C4', 0.25), ('B3', 0.25), ('G3', 0.5), ('REST', 0.5),
        ('Bb3', 0.5), ('B3', 0.25), ('Bb3', 0.25), ('F3', 0.5), ('REST', 0.5),
    ] * 2

    melody_track = generate_track_from_sequence(suspense_melody, 'sawtooth', VOLUME * 0.8, use_adsr=True, attack=0.3, decay=0.5, sustain=0.3, release=0.5)
    harmony_track = generate_track_from_sequence(suspense_bass, 'sine', VOLUME * 0.6, use_adsr=True, attack=0.2, decay=0.3, sustain=0.2, release=0.3)
    final_length = max(len(melody_track), len(harmony_track))
    melody_track = np.pad(melody_track, (0, final_length - len(melody_track)))
    harmony_track = np.pad(harmony_track, (0, final_length - len(harmony_track)))
    suspense_music = melody_track + harmony_track
    suspense_music = np.tile(suspense_music, 2)
    suspense_music /= np.max(np.abs(suspense_music))
    save_wav('bgm_suspense.wav', suspense_music, SAMPLE_RATE)

    # =========================================================================
    # Shop Theme (FF4 Welcome to Our Town! - B section / simplified)
    # =========================================================================
    TEMPO = 140
    BEAT_DURATION = 60 / TEMPO

    shop_melody = [
        ('C5', 0.25), ('D5', 0.25), ('E5', 0.25), ('G5', 0.25),
        ('A5', 0.5), ('G5', 0.5), ('E5', 0.5), ('C5', 0.5),
        ('D5', 0.25), ('E5', 0.25), ('F5', 0.25), ('G5', 0.25),
        ('C5', 2.0),
    ] * 2
    shop_harmony = [
        ('C3', 0.5), ('G3', 0.5), ('C4', 0.5), ('G3', 0.5),
        ('F3', 0.5), ('C4', 0.5), ('F4', 0.5), ('C4', 0.5),
    ] * 4

    melody_track = generate_track_from_sequence(shop_melody, 'triangle', VOLUME, use_adsr=True, attack=0.01, decay=0.1, sustain=0.5, release=0.1)
    harmony_track = generate_track_from_sequence(shop_harmony, 'triangle', VOLUME * 0.5, use_adsr=True, attack=0.01, decay=0.1, sustain=0.3, release=0.1)
    final_length = max(len(melody_track), len(harmony_track))
    melody_track = np.pad(melody_track, (0, final_length - len(melody_track)))
    harmony_track = np.pad(harmony_track, (0, final_length - len(harmony_track)))
    shop_music = melody_track + harmony_track
    shop_music = np.tile(shop_music, 3)
    shop_music /= np.max(np.abs(shop_music))
    save_wav('bgm_shop.wav', shop_music, SAMPLE_RATE)

    # =========================================================================
    # Inn / Rest Theme (FF4 Inn - Lullaby style)
    # =========================================================================
    TEMPO = 60
    BEAT_DURATION = 60 / TEMPO

    inn_melody = [
        ('G4', 1.0), ('B4', 0.5), ('C5', 0.5), ('D5', 1.0), ('G4', 1.0),
        ('A4', 1.0), ('B4', 0.5), ('A4', 0.5), ('G4', 2.0),
    ]
    inn_harmony = [
        ('G2', 1.0), ('D3', 1.0), ('G3', 1.0), ('D3', 1.0),
        ('C2', 1.0), ('G2', 1.0), ('C3', 1.0), ('G2', 1.0),
    ] * 2

    melody_track = generate_track_from_sequence(inn_melody, 'sine', VOLUME * 0.8, use_adsr=True, attack=0.1, decay=0.3, sustain=0.2, release=0.8)
    harmony_track = generate_track_from_sequence(inn_harmony, 'sine', VOLUME * 0.3, use_adsr=True, attack=0.1, decay=0.3, sustain=0.2, release=0.8)
    final_length = max(len(melody_track), len(harmony_track))
    melody_track = np.pad(melody_track, (0, final_length - len(melody_track)))
    harmony_track = np.pad(harmony_track, (0, final_length - len(harmony_track)))
    inn_music = melody_track + harmony_track
    inn_music /= np.max(np.abs(inn_music))
    save_wav('bgm_inn.wav', inn_music, SAMPLE_RATE)

    # =========================================================================
    # Tower / Dungeon Theme (FF4 Tower of Zot) - Sinister 5/4 or odd meter feel
    # =========================================================================
    TEMPO = 140
    BEAT_DURATION = 60 / TEMPO

    tower_melody = [
        ('Bb3', 0.5), ('Db4', 0.5), ('F4', 0.5), ('Bb4', 0.5),
        ('A4', 0.5), ('F4', 0.5), ('Db4', 0.5), ('A3', 0.5),
        ('Bb3', 0.5), ('Eb4', 0.5), ('Gb4', 0.5), ('Bb4', 0.5),
        ('Ab4', 0.5), ('Gb4', 0.5), ('Eb4', 0.5), ('Bb3', 0.5),
    ] * 2
    tower_bass = [
        ('Bb2', 0.5), ('F3', 0.5), ('Bb2', 0.5), ('F3', 0.5),
        ('A2', 0.5), ('E3', 0.5), ('A2', 0.5), ('E3', 0.5),
    ] * 4

    tower_percussion = [1, 0, 0, 1, 0, 0, 1, 0] * 16

    melody_track = generate_track_from_sequence(tower_melody, 'square', VOLUME, use_adsr=True, attack=0.01, decay=0.1, sustain=0.3, release=0.1)
    harmony_track = generate_track_from_sequence(tower_bass, 'sawtooth', VOLUME * 0.6, use_adsr=True, attack=0.01, decay=0.1, sustain=0.3, release=0.1)
    percussion_track = generate_percussion_track(tower_percussion, 0.5, VOLUME * 0.5)
    
    final_length = max(len(melody_track), len(harmony_track), len(percussion_track))
    melody_track = np.pad(melody_track, (0, final_length - len(melody_track)))
    harmony_track = np.pad(harmony_track, (0, final_length - len(harmony_track)))
    percussion_track = np.pad(percussion_track, (0, final_length - len(percussion_track)))
    tower_music = melody_track + harmony_track + percussion_track
    tower_music = np.clip(tower_music, -1.0, 1.0)
    tower_music = np.tile(tower_music, 2)
    tower_music /= np.max(np.abs(tower_music))
    save_wav('bgm_tower.wav', tower_music, SAMPLE_RATE)

    # =========================================================================
    # Prologue / Opening Theme (FF4 Prologue) - C major, majestic arpeggios
    # =========================================================================
    TEMPO = 110
    BEAT_DURATION = 60 / TEMPO

    prologue_arp = [
        ('C4', 0.25), ('E4', 0.25), ('G4', 0.25), ('C5', 0.25),
        ('E5', 0.25), ('C5', 0.25), ('G4', 0.25), ('E4', 0.25),
        ('D4', 0.25), ('F4', 0.25), ('A4', 0.25), ('D5', 0.25),
        ('F5', 0.25), ('D5', 0.25), ('A4', 0.25), ('F4', 0.25),
        ('E4', 0.25), ('G4', 0.25), ('B4', 0.25), ('E5', 0.25),
        ('G5', 0.25), ('E5', 0.25), ('B4', 0.25), ('G4', 0.25),
        ('F4', 0.25), ('A4', 0.25), ('C5', 0.25), ('F5', 0.25),
        ('A5', 0.25), ('F5', 0.25), ('C5', 0.25), ('A4', 0.25),
    ]

    prologue_melody = [
        ('C5', 1.0), ('B4', 0.5), ('A4', 0.5), ('G4', 1.0), ('F4', 1.0),
        ('E4', 1.0), ('F4', 0.5), ('G4', 0.5), ('C4', 2.0),
    ] * 2

    prologue_bass = [
        ('C3', 2.0), ('G2', 2.0),
        ('F2', 2.0), ('G2', 2.0),
        ('C3', 2.0), ('E3', 2.0),
        ('F2', 2.0), ('G2', 2.0),
        ('G2', 2.0), ('C3', 2.0),
        ('A2', 2.0), ('E2', 2.0),
        ('F2', 2.0), ('G2', 2.0),
        ('C3', 2.0), ('G2', 2.0),
    ]

    arp_track = generate_track_from_sequence(prologue_arp * 4, 'triangle', VOLUME * 0.5, use_adsr=True, attack=0.01, decay=0.1, sustain=0.3, release=0.05)
    melody_track = generate_track_from_sequence(prologue_melody, 'square', VOLUME * 0.8, use_adsr=True, attack=0.1, decay=0.3, sustain=0.6, release=0.3)
    bass_track = generate_track_from_sequence(prologue_bass, 'sine', VOLUME * 0.5, use_adsr=True, attack=0.1, decay=0.3, sustain=0.4, release=0.3)
    
    final_length = max(len(arp_track), len(melody_track), len(bass_track))
    arp_track = np.pad(arp_track, (0, final_length - len(arp_track)))
    melody_track = np.pad(melody_track, (0, final_length - len(melody_track)))
    bass_track = np.pad(bass_track, (0, final_length - len(bass_track)))
    prologue_music = arp_track + melody_track + bass_track
    prologue_music = np.tile(prologue_music, 2)
    prologue_music /= np.max(np.abs(prologue_music))
    save_wav('bgm_prologue.wav', prologue_music, SAMPLE_RATE)

    # =========================================================================
    # Game Over Music (FF4 Game Over) - Somber, cinematic
    # =========================================================================
    TEMPO = 60
    BEAT_DURATION = 60 / TEMPO

    gameover_melody = [
        ('C4', 1.0), ('E4', 1.0), ('G4', 1.0), ('C5', 2.0),
        ('B4', 0.5), ('A4', 0.5), ('G4', 4.0),
    ] + [('REST', 4.0)]

    gameover_harmony = [
        ('C3', 2.0), ('G3', 2.0),
        ('F2', 2.0), ('C3', 2.0),
        ('D2', 2.0), ('A2', 2.0),
        ('Eb2', 2.0), ('Bb2', 2.0),
        ('G2', 2.0), ('Eb3', 2.0),
        ('F2', 2.0), ('G2', 2.0),
        ('C2', 4.0),
        ('REST', 4.0),
    ]

    melody_track = generate_track_from_sequence(gameover_melody, 'sawtooth', VOLUME, use_adsr=True, attack=0.3, decay=0.5, sustain=0.3, release=0.8)
    harmony_track = generate_track_from_sequence(gameover_harmony, 'sine', VOLUME * 0.5, use_adsr=True, attack=0.2, decay=0.4, sustain=0.3, release=0.5)
    
    final_length = max(len(melody_track), len(harmony_track))
    melody_track = np.pad(melody_track, (0, final_length - len(melody_track)))
    harmony_track = np.pad(harmony_track, (0, final_length - len(harmony_track)))
    gameover_music = melody_track + harmony_track
    gameover_music /= np.max(np.abs(gameover_music))
    save_wav('bgm_game_over.wav', gameover_music, SAMPLE_RATE)


if __name__ == "__main__":
    os.makedirs("src/main/resources/sounds", exist_ok=True)
    generate_sfx()
    generate_music()
    print("All sounds generated.")