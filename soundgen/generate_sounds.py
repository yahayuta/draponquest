import numpy as np
import wave
import struct
import os

SAMPLE_RATE = 44100
VOLUME = 0.25

NOTE_FREQS = {
    'C2': 65.41, 'G2': 98.00,
    'C3': 130.81, 'D3': 146.83, 'Eb3': 155.56, 'E3': 164.81, 'F3': 174.61, 'G3': 196.00, 'A3': 220.00, 'Bb3': 233.08, 'B3': 246.94, 'Ab3': 207.65,
    'C4': 261.63, 'D4': 293.66, 'Eb4': 311.13, 'E4': 329.63, 'F4': 349.23, 'G4': 392.00, 'G#4': 415.30, 'A4': 440.00, 'Bb4': 466.16, 'B4': 493.88,
    'C5': 523.25, 'D5': 587.33, 'Eb5': 622.25, 'E5': 659.25, 'F5': 698.46, 'F#5': 739.99, 'G5': 783.99, 'A5': 880.00, 'Bb5': 932.33, 'B5': 987.77,
    'C6': 1046.50,
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
    victory_notes = [('B4', 0.1), ('B4', 0.1), ('B4', 0.1), ('B4', 0.1)]
    victory_sound = generate_track_from_sequence(victory_notes, 'square', VOLUME, use_adsr=True)
    save_wav('victory.wav', victory_sound, SAMPLE_RATE)
    
    # Defeat
    defeat_notes = [('A4', 0.25), ('G#4', 0.25), ('G4', 0.25), ('F#4', 0.5)]
    defeat_sound = generate_track_from_sequence(defeat_notes, 'sawtooth', VOLUME, use_adsr=True)
    save_wav('defeat.wav', defeat_sound, SAMPLE_RATE)

    # Game Over
    game_over_notes = [('C4', 0.4), ('G3', 0.4), ('C3', 0.8)]
    game_over_sound = generate_track_from_sequence(game_over_notes, 'sawtooth', VOLUME, use_adsr=True)
    save_wav('game_over.wav', game_over_sound, SAMPLE_RATE)

def generate_music():
    print("Generating music...")
    global BEAT_DURATION

    # Title Music (FF Prelude inspired)
    TEMPO = 100
    BEAT_DURATION = 60 / TEMPO
    prelude_arpeggio = [
        ('C4', 0.25), ('E4', 0.25), ('G4', 0.25), ('C5', 0.25), 
        ('E5', 0.25), ('G5', 0.25), ('C6', 0.25), ('G5', 0.25),
        ('E5', 0.25), ('C5', 0.25), ('G4', 0.25), ('E4', 0.25),
    ] * 4
    title_music = generate_track_from_sequence(prelude_arpeggio, 'triangle', VOLUME, use_adsr=True)
    title_music = np.tile(title_music, 2)
    title_music /= np.max(np.abs(title_music))
    save_wav('title.wav', title_music, SAMPLE_RATE)

    # Field Music
    TEMPO = 140
    BEAT_DURATION = 60 / TEMPO
    field_melody = [
        ('G4', 0.5), ('A4', 0.5), ('B4', 1.0),
        ('G4', 0.5), ('A4', 0.5), ('B4', 1.0),
        ('C5', 0.5), ('B4', 0.5), ('A4', 1.0),
        ('G4', 0.5), ('A4', 0.5), ('G4', 1.0),
    ] * 2
    field_harmony = [
        ('C4', 2.0), ('G4', 2.0),
        ('D4', 2.0), ('G4', 2.0),
    ] * 2
    melody_track = generate_track_from_sequence(field_melody, 'square', VOLUME, use_adsr=True)
    harmony_track = generate_track_from_sequence(field_harmony, 'sawtooth', VOLUME * 0.6, use_adsr=True)
    final_length = max(len(melody_track), len(harmony_track))
    melody_track = np.pad(melody_track, (0, final_length - len(melody_track)))
    harmony_track = np.pad(harmony_track, (0, final_length - len(harmony_track)))
    field_music = melody_track + harmony_track
    field_music = np.tile(field_music, 2)
    field_music /= np.max(np.abs(field_music))
    save_wav('bgm_field.wav', field_music, SAMPLE_RATE)

    # Castle Music
    TEMPO = 110
    BEAT_DURATION = 60 / TEMPO
    castle_melody = [
        ('C4', 1.0), ('G4', 1.0), ('E4', 1.0), ('G4', 1.0),
        ('F4', 0.5), ('E4', 0.5), ('D4', 1.0), ('C4', 1.0),
    ] * 2
    castle_harmony = [
        ('C3', 2.0), ('G3', 2.0),
        ('F3', 2.0), ('C3', 2.0),
    ] * 2
    melody_track = generate_track_from_sequence(castle_melody, 'square', VOLUME, use_adsr=True, attack=0.01, decay=0.3, sustain=0.5, release=0.2)
    harmony_track = generate_track_from_sequence(castle_harmony, 'triangle', VOLUME * 0.7, use_adsr=True)
    final_length = max(len(melody_track), len(harmony_track))
    melody_track = np.pad(melody_track, (0, final_length - len(melody_track)))
    harmony_track = np.pad(harmony_track, (0, final_length - len(harmony_track)))
    castle_music = melody_track + harmony_track
    castle_music /= np.max(np.abs(castle_music))
    save_wav('bgm_castle.wav', castle_music, SAMPLE_RATE)

    # Cave Music
    TEMPO = 80
    BEAT_DURATION = 60 / TEMPO
    cave_melody = [
        ('C3', 2.0), ('D3', 1.0), ('Eb3', 1.0),
        ('C3', 2.0), ('REST', 2.0)
    ] * 4
    cave_harmony = [
        ('C2', 4.0), ('G2', 4.0)
    ] * 2
    melody_track = generate_track_from_sequence(cave_melody, 'sawtooth', VOLUME, use_adsr=True, attack=0.1, decay=0.5, sustain=0.2, release=0.5)
    harmony_track = generate_track_from_sequence(cave_harmony, 'sawtooth', VOLUME * 0.6, use_adsr=True, attack=0.2, decay=0.8, sustain=0.1, release=0.5)
    final_length = max(len(melody_track), len(harmony_track))
    melody_track = np.pad(melody_track, (0, final_length - len(melody_track)))
    harmony_track = np.pad(harmony_track, (0, final_length - len(harmony_track)))
    cave_music = melody_track + harmony_track
    cave_music /= np.max(np.abs(cave_music))
    save_wav('bgm_cave.wav', cave_music, SAMPLE_RATE)
    
    # Battle Music
    TEMPO = 150
    BEAT_DURATION = 60 / TEMPO
    
    melody = [
        ('G4', 0.25), ('A4', 0.25), ('Bb4', 0.25), ('C5', 0.25), ('D5', 0.25), ('Eb5', 0.25), ('F5', 0.25), ('G5', 0.25),
        ('F5', 0.5), ('Eb5', 0.5), ('D5', 0.5), ('C5', 0.5)
    ] * 2
    harmony = [
        ('G3', 0.5), ('D4', 0.5), ('G4', 0.5), ('B4', 0.5),
        ('E4', 0.5), ('B4', 0.5), ('E5', 0.5), ('G5', 0.5)
    ] * 2
    percussion_pattern = [1, 0, 1, 0] * 8
    
    melody_track = generate_track_from_sequence(melody, 'square', VOLUME, use_adsr=True)
    harmony_track = generate_track_from_sequence(harmony, 'sawtooth', VOLUME * 0.7, use_adsr=True)
    percussion_track = generate_percussion_track(percussion_pattern, 0.25, VOLUME * 0.5)
    
    final_length = max(len(melody_track), len(harmony_track), len(percussion_track))
    melody_track = np.pad(melody_track, (0, final_length - len(melody_track)))
    harmony_track = np.pad(harmony_track, (0, final_length - len(harmony_track)))
    percussion_track = np.pad(percussion_track, (0, final_length - len(percussion_track)))

    battle_music = melody_track + harmony_track + percussion_track
    battle_music = np.clip(battle_music, -1.0, 1.0)
    battle_music = np.tile(battle_music, 2)
    battle_music /= np.max(np.abs(battle_music))
    save_wav('bgm_battle.wav', battle_music, SAMPLE_RATE)

    # Town Music (FF4 Inspired)
    TEMPO = 120
    BEAT_DURATION = 60 / TEMPO
    
    melody = [
        ('A4', 0.5), ('C5', 0.5), ('E5', 0.5),
        ('G5', 0.5), ('F#5', 0.5), ('D5', 0.5),
        ('E5', 1.0), ('REST', 0.5),
        ('D5', 0.5), ('C5', 0.5), ('A4', 1.0)
    ] * 2
    arpeggio = [
        ('A3', 0.25), ('E4', 0.25), ('A4', 0.25), 
        ('G3', 0.25), ('D4', 0.25), ('G4', 0.25), 
        ('D3', 0.25), ('A3', 0.25), ('D4', 0.25), 
        ('E3', 0.25), ('G3', 0.25), ('E4', 0.25), 
    ] * 3
    melody_track = generate_track_from_sequence(melody, 'triangle', VOLUME, use_adsr=True)
    harmony_track = generate_track_from_sequence(arpeggio, 'sine', VOLUME * 0.7, use_adsr=True)
    final_length = max(len(melody_track), len(harmony_track))
    melody_track = np.pad(melody_track, (0, final_length - len(melody_track)))
    harmony_track = np.pad(harmony_track, (0, final_length - len(harmony_track)))
    town_music = melody_track + harmony_track
    town_music = np.clip(town_music, -1.0, 1.0)
    town_music = np.tile(town_music, 2)
    town_music /= np.max(np.abs(town_music))
    save_wav('bgm_town.wav', town_music, SAMPLE_RATE)

    # Victory Music
    victory_notes_long = [
        ('B4', 0.5), ('B4', 0.5), ('B4', 0.5), ('B4', 0.5),
        ('G4', 0.5), ('A4', 0.5), ('B4', 0.5), ('A4', 0.5), ('B4', 1.0),
        ('C5', 0.5), ('Bb4', 0.5), ('C5', 0.5), ('Bb4', 0.5), ('Bb4', 1.0)
    ]
    victory_music = generate_track_from_sequence(victory_notes_long, 'square', VOLUME)
    victory_music /= np.max(np.abs(victory_music))
    save_wav('victory_music.wav', victory_music, SAMPLE_RATE)


if __name__ == "__main__":
    os.makedirs("src/main/resources/sounds", exist_ok=True)
    generate_sfx()
    generate_music()
    print("All sounds generated.")