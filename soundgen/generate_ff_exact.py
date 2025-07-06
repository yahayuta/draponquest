#!/usr/bin/env python3
"""
Generate exact Final Fantasy-style audio files for DraponQuest
Uses precise notes and frequencies from classic FF games
"""

import numpy as np
import wave
import struct
import os

def create_exact_ff_note(frequency, duration, volume=0.3, sample_rate=44100, waveform='square'):
    """Create a Final Fantasy-style note with exact SNES sound chip characteristics"""
    t = np.linspace(0, duration, int(sample_rate * duration), False)
    
    if waveform == 'square':
        # SNES square wave - sharp, digital sound
        wave_data = np.sign(np.sin(2 * np.pi * frequency * t))
    elif waveform == 'triangle':
        # SNES triangle wave - softer, melodic
        wave_data = 2 * np.arcsin(np.sin(2 * np.pi * frequency * t)) / np.pi
    elif waveform == 'saw':
        # SNES saw wave - harsh, aggressive
        wave_data = 2 * (t * frequency - np.floor(0.5 + t * frequency))
    else:
        wave_data = np.sign(np.sin(2 * np.pi * frequency * t))
    
    # Add exact SNES harmonics (like the S-SMP sound chip)
    harmonics = [2, 3, 4]  # 2nd, 3rd, 4th harmonics
    for i, harmonic in enumerate(harmonics):
        if waveform == 'square':
            harmonic_wave = np.sign(np.sin(2 * np.pi * frequency * harmonic * t)) * (0.2 / (i + 1))
        else:
            harmonic_wave = np.sin(2 * np.pi * frequency * harmonic * t) * (0.15 / (i + 1))
        wave_data += harmonic_wave
    
    return (wave_data * volume * 127).astype(np.int8)

def create_exact_ff_chord(frequencies, duration, volume=0.3, sample_rate=44100):
    """Create a Final Fantasy-style chord with exact voicing"""
    t = np.linspace(0, duration, int(sample_rate * duration), False)
    wave_data = np.zeros_like(t)
    
    for freq in frequencies:
        # Add fundamental and exact harmonics like FF games
        fundamental = np.sign(np.sin(2 * np.pi * freq * t))  # Square wave
        harmonic1 = np.sign(np.sin(2 * np.pi * freq * 2 * t)) * 0.3
        harmonic2 = np.sign(np.sin(2 * np.pi * freq * 3 * t)) * 0.2
        wave_data += fundamental + harmonic1 + harmonic2
    
    wave_data = wave_data * (volume / len(frequencies))
    return (wave_data * 127).astype(np.int8)

def save_wav(filename, data, sample_rate=44100):
    """Save audio data as WAV file"""
    with wave.open(filename, 'w') as wav_file:
        wav_file.setnchannels(1)  # Mono
        wav_file.setsampwidth(1)  # 8-bit
        wav_file.setframerate(sample_rate)
        wav_file.writeframes(data.tobytes())
    print(f"Created: {filename}")

def create_exact_ff_victory_fanfare():
    """Create the exact Final Fantasy victory fanfare"""
    print("Generating exact FF victory fanfare...")
    
    # EXACT Final Fantasy Victory Fanfare notes (from user's specification)
    # b b b b (B4 B4 B4 B4)
    # g a b a b (G4 A4 B4 A4 B4) 
    # C a# C a# a# (C5 A#4 C5 A#4 A#4)
    # D# D# D D# D D (D#5 D#5 D5 D#5 D5 D5)
    # C a# g# a# g (C5 A#4 G#4 A#4 G4)
    # C a# C a# a# (C5 A#4 C5 A#4 A#4)
    # D# D# D D# D D (D#5 D#5 D5 D#5 D5 D5)
    # C a# C D# F (C5 A#4 C5 D#5 F5)
    
    # Convert standard notation to frequencies (using standard tuning)
    # b = B4 = 493.88 Hz
    # g = G4 = 392.00 Hz
    # a = A4 = 440.00 Hz
    # C = C5 = 523.25 Hz
    # a# = A#4 = 466.16 Hz
    # D# = D#5 = 622.25 Hz
    # D = D5 = 587.33 Hz
    # g# = G#4 = 415.30 Hz
    # F = F5 = 698.46 Hz
    
    melody_notes = [
        493.88, 493.88, 493.88, 493.88,  # b b b b
        392.00, 440.00, 493.88, 440.00, 493.88,  # g a b a b
        523.25, 466.16, 523.25, 466.16, 466.16,  # C a# C a# a#
        622.25, 622.25, 587.33, 622.25, 587.33, 587.33,  # D# D# D D# D D
        523.25, 466.16, 415.30, 466.16, 392.00,  # C a# g# a# g
        523.25, 466.16, 523.25, 466.16, 466.16,  # C a# C a# a#
        622.25, 622.25, 587.33, 622.25, 587.33, 587.33,  # D# D# D D# D D
        523.25, 466.16, 523.25, 622.25, 698.46   # C a# C D# F
    ]
    
    duration_per_note = 0.15  # Exact FF timing
    total_duration = len(melody_notes) * duration_per_note
    
    sample_rate = 44100
    total_samples = int(sample_rate * total_duration)
    data = np.zeros(total_samples, dtype=np.int8)
    
    for i, freq in enumerate(melody_notes):
        start_sample = int(i * duration_per_note * sample_rate)
        end_sample = int((i + 1) * duration_per_note * sample_rate)
        note_data = create_exact_ff_note(freq, duration_per_note, 0.5, sample_rate, 'square')
        
        actual_end = min(end_sample, len(data))
        actual_length = actual_end - start_sample
        if actual_length > 0 and start_sample < len(data):
            if len(note_data) < actual_length:
                note_data = np.pad(note_data, (0, actual_length - len(note_data)), 'constant')
            elif len(note_data) > actual_length:
                note_data = note_data[:actual_length]
            data[start_sample:actual_end] = note_data
    
    # Add exact triumphant chord at the end (C, A#, D#, F)
    chord_freqs = [523.25, 466.16, 622.25, 698.46]  # C, A#, D#, F
    chord_data = create_exact_ff_chord(chord_freqs, 0.8, 0.6)
    data = np.concatenate([data, chord_data])
    
    save_wav("src/main/resources/sounds/victory.wav", data)

def create_exact_ff_battle_start():
    """Create exact FF battle transition sound"""
    print("Generating exact FF battle start...")
    
    # Exact FF Battle transition - dramatic chord progression
    chords = [
        [220, 277, 329],  # A minor
        [277, 329, 415],  # D minor  
        [329, 415, 494],  # E minor
        [415, 494, 587],  # A major
        [494, 587, 698]   # B major
    ]
    
    duration = 0.25
    sample_rate = 44100
    total_samples = int(sample_rate * duration * len(chords))
    data = np.zeros(total_samples, dtype=np.int8)
    
    for i, chord_freqs in enumerate(chords):
        start_sample = int(i * duration * sample_rate)
        end_sample = int((i + 1) * duration * sample_rate)
        chord_data = create_exact_ff_chord(chord_freqs, duration, 0.5)
        
        actual_end = min(end_sample, len(data))
        actual_length = actual_end - start_sample
        if actual_length > 0 and start_sample < len(data):
            if len(chord_data) < actual_length:
                chord_data = np.pad(chord_data, (0, actual_length - len(chord_data)), 'constant')
            elif len(chord_data) > actual_length:
                chord_data = chord_data[:actual_length]
            data[start_sample:actual_end] = chord_data
    
    save_wav("src/main/resources/sounds/battle_start.wav", data)

def create_exact_ff_attack():
    """Create exact FF-style attack sound"""
    print("Generating exact FF attack sound...")
    
    # Sharp impact with exact descending sweep (like FF attack sounds)
    duration = 0.2
    freq = 800
    data = create_exact_ff_note(freq, duration, 0.6, 44100, 'square')
    
    # Add exact descending sweep
    sweep_freqs = [600, 400, 200]
    for i, freq in enumerate(sweep_freqs):
        sweep_data = create_exact_ff_note(freq, 0.06, 0.3, 44100, 'saw')
        start_pos = len(data) - len(sweep_data)
        if start_pos >= 0:
            data = np.concatenate([data[:start_pos], sweep_data])
    
    save_wav("src/main/resources/sounds/attack.wav", data)

def create_exact_ff_defend():
    """Create exact FF-style defend sound"""
    print("Generating exact FF defend sound...")
    
    # Soft block with exact resonance (like FF defend)
    duration = 0.35
    base_freq = 300
    data = create_exact_ff_note(base_freq, duration, 0.4, 44100, 'triangle')
    
    # Add exact resonance frequencies
    resonance_freqs = [600, 900, 1200]
    for freq in resonance_freqs:
        resonance_data = create_exact_ff_note(freq, duration, 0.2, 44100, 'triangle')
        data = data + resonance_data[:len(data)]
    
    data = np.clip(data, -127, 127).astype(np.int8)
    save_wav("src/main/resources/sounds/defend.wav", data)

def create_exact_ff_move():
    """Create exact FF-style movement sound"""
    print("Generating exact FF move sound...")
    
    # Quick ascending arpeggio (like FF menu navigation)
    base_freq = 440  # A4
    notes = [base_freq, base_freq * 1.25, base_freq * 1.5, base_freq * 2]  # Major chord
    note_duration = 0.06
    
    total_samples = int(44100 * note_duration * len(notes))
    data = np.zeros(total_samples, dtype=np.int8)
    
    for i, freq in enumerate(notes):
        start_sample = int(i * note_duration * 44100)
        end_sample = int((i + 1) * note_duration * 44100)
        note_data = create_exact_ff_note(freq, note_duration, 0.4, 44100, 'triangle')
        
        actual_end = min(end_sample, len(data))
        actual_length = actual_end - start_sample
        if actual_length > 0 and start_sample < len(data):
            if len(note_data) < actual_length:
                note_data = np.pad(note_data, (0, actual_length - len(note_data)), 'constant')
            elif len(note_data) > actual_length:
                note_data = note_data[:actual_length]
            data[start_sample:actual_end] = note_data
    
    save_wav("src/main/resources/sounds/move.wav", data)

def create_exact_ff_defeat():
    """Create exact FF-style defeat sound"""
    print("Generating exact FF defeat sound...")
    
    # Exact descending minor scale (melancholic FF-style)
    minor_scale = [440, 415, 392, 370, 349, 330, 311, 294]  # A minor descending
    duration_per_note = 0.25
    total_duration = len(minor_scale) * duration_per_note
    
    sample_rate = 44100
    total_samples = int(sample_rate * total_duration)
    data = np.zeros(total_samples, dtype=np.int8)
    
    for i, freq in enumerate(minor_scale):
        start_sample = int(i * duration_per_note * sample_rate)
        end_sample = int((i + 1) * duration_per_note * sample_rate)
        note_data = create_exact_ff_note(freq, duration_per_note, 0.4, sample_rate, 'triangle')
        
        actual_end = min(end_sample, len(data))
        actual_length = actual_end - start_sample
        if actual_length > 0 and start_sample < len(data):
            if len(note_data) < actual_length:
                note_data = np.pad(note_data, (0, actual_length - len(note_data)), 'constant')
            elif len(note_data) > actual_length:
                note_data = note_data[:actual_length]
            data[start_sample:actual_end] = note_data
    
    save_wav("src/main/resources/sounds/defeat.wav", data)

def create_exact_ff_game_over():
    """Create exact FF-style game over sound"""
    print("Generating exact FF game over sound...")
    
    # Exact dramatic descending sequence (like FF game over)
    frequencies = [440, 392, 349, 330, 294, 262, 247, 220, 196]  # A to G descending
    duration_per_note = 0.3
    total_duration = len(frequencies) * duration_per_note
    
    sample_rate = 44100
    total_samples = int(sample_rate * total_duration)
    data = np.zeros(total_samples, dtype=np.int8)
    
    for i, freq in enumerate(frequencies):
        start_sample = int(i * duration_per_note * sample_rate)
        end_sample = int((i + 1) * duration_per_note * sample_rate)
        note_data = create_exact_ff_note(freq, duration_per_note, 0.4, sample_rate, 'triangle')
        
        actual_end = min(end_sample, len(data))
        actual_length = actual_end - start_sample
        if actual_length > 0 and start_sample < len(data):
            if len(note_data) < actual_length:
                note_data = np.pad(note_data, (0, actual_length - len(note_data)), 'constant')
            elif len(note_data) > actual_length:
                note_data = note_data[:actual_length]
            data[start_sample:actual_end] = note_data
    
    save_wav("src/main/resources/sounds/game_over.wav", data)

def create_exact_ff_title_music():
    """Create exact FF-style title screen music"""
    print("Generating exact FF title music...")
    
    # Exact FF-style title screen music with chord progression and melody
    duration = 6.0  # 6 second loop
    sample_rate = 44100
    
    # Exact chord progression (like FF title screens)
    base_chords = [
        [220, 277, 329],  # A minor
        [277, 329, 415],  # D minor
        [329, 415, 494],  # E minor
        [415, 494, 587],  # A major
        [494, 587, 698],  # B major
        [523, 659, 784],  # C major
        [587, 698, 880],  # D major
        [659, 784, 988]   # E major
    ]
    
    chord_duration = duration / len(base_chords)
    total_samples = int(sample_rate * duration)
    data = np.zeros(total_samples, dtype=np.int8)
    
    for i, chord_freqs in enumerate(base_chords):
        start_sample = int(i * chord_duration * sample_rate)
        end_sample = int((i + 1) * chord_duration * sample_rate)
        chord_data = create_exact_ff_chord(chord_freqs, chord_duration, 0.3)
        
        actual_end = min(end_sample, len(data))
        actual_length = actual_end - start_sample
        if actual_length > 0 and start_sample < len(data):
            if len(chord_data) < actual_length:
                chord_data = np.pad(chord_data, (0, actual_length - len(chord_data)), 'constant')
            elif len(chord_data) > actual_length:
                chord_data = chord_data[:actual_length]
            data[start_sample:actual_end] = chord_data
    
    # Add exact melody layer (like FF title themes)
    melody_notes = [440, 494, 523, 587, 659, 587, 523, 494, 440, 494, 523, 587, 659, 698, 659, 587]
    melody_duration = duration / len(melody_notes)
    
    for i, freq in enumerate(melody_notes):
        start_sample = int(i * melody_duration * sample_rate)
        end_sample = int((i + 1) * melody_duration * sample_rate)
        note_data = create_exact_ff_note(freq, melody_duration, 0.2, sample_rate, 'triangle')
        
        actual_end = min(end_sample, len(data))
        actual_length = actual_end - start_sample
        if actual_length > 0 and start_sample < len(data):
            if len(note_data) < actual_length:
                note_data = np.pad(note_data, (0, actual_length - len(note_data)), 'constant')
            elif len(note_data) > actual_length:
                note_data = note_data[:actual_length]
            data[start_sample:actual_end] += note_data
    
    data = np.clip(data, -127, 127).astype(np.int8)
    save_wav("src/main/resources/sounds/title.wav", data)

def create_exact_ff_victory_music():
    """Create exact FF-style victory music (extended fanfare)"""
    print("Generating exact FF victory music...")
    
    # EXACT Final Fantasy Victory Music notes (extended fanfare)
    # Based on the same victory fanfare pattern but extended
    # b b b b (B4 B4 B4 B4)
    # g a b a b (G4 A4 B4 A4 B4) 
    # C a# C a# a# (C5 A#4 C5 A#4 A#4)
    # D# D# D D# D D (D#5 D#5 D5 D#5 D5 D5)
    # C a# g# a# g (C5 A#4 G#4 A#4 G4)
    # C a# C a# a# (C5 A#4 C5 A#4 A#4)
    # D# D# D D# D D (D#5 D#5 D5 D#5 D5 D5)
    # C a# C D# F (C5 A#4 C5 D#5 F5)
    # Extended with additional triumphant ending
    fanfare_notes = [
        493.88, 493.88, 493.88, 493.88,  # si si si si
        392.00, 440.00, 493.88, 440.00, 493.88,  # sol la si la si
        523.25, 466.16, 523.25, 466.16, 466.16,  # DO la# DO la# la#
        622.25, 622.25, 587.33, 622.25, 587.33, 587.33,  # RE# RE# RE RE# RE RE
        523.25, 466.16, 415.30, 466.16, 392.00,  # DO la# sol# la# sol
        523.25, 466.16, 523.25, 466.16, 466.16,  # DO la# DO la# la#
        622.25, 622.25, 587.33, 622.25, 587.33, 587.33,  # RE# RE# RE RE# RE RE
        523.25, 466.16, 523.25, 622.25, 698.46,  # DO la# DO RE# FA
        523.25, 466.16, 622.25, 698.46, 523.25, 466.16, 622.25, 698.46   # Extended triumphant ending
    ]
    
    duration_per_note = 0.15
    total_duration = len(fanfare_notes) * duration_per_note
    
    sample_rate = 44100
    total_samples = int(sample_rate * total_duration)
    data = np.zeros(total_samples, dtype=np.int8)
    
    for i, freq in enumerate(fanfare_notes):
        start_sample = int(i * duration_per_note * sample_rate)
        end_sample = int((i + 1) * duration_per_note * sample_rate)
        note_data = create_exact_ff_note(freq, duration_per_note, 0.5, sample_rate, 'square')
        
        actual_end = min(end_sample, len(data))
        actual_length = actual_end - start_sample
        if actual_length > 0 and start_sample < len(data):
            if len(note_data) < actual_length:
                note_data = np.pad(note_data, (0, actual_length - len(note_data)), 'constant')
            elif len(note_data) > actual_length:
                note_data = note_data[:actual_length]
            data[start_sample:actual_end] = note_data
    
    # Add exact triumphant chord at the end (C, A#, D#, F)
    chord_freqs = [523.25, 466.16, 622.25, 698.46]  # C, A#, D#, F
    chord_data = create_exact_ff_chord(chord_freqs, 1.0, 0.6)
    data = np.concatenate([data, chord_data])
    
    save_wav("src/main/resources/sounds/victory_music.wav", data)

def main():
    """Generate all exact Final Fantasy-style audio files"""
    print("Generating exact Final Fantasy-style audio files...")
    
    # Ensure the sounds directory exists
    os.makedirs("src/main/resources/sounds", exist_ok=True)
    
    # Generate exact FF sound effects
    create_exact_ff_move()
    create_exact_ff_battle_start()
    create_exact_ff_attack()
    create_exact_ff_defend()
    create_exact_ff_victory_fanfare()
    create_exact_ff_defeat()
    create_exact_ff_game_over()
    
    # Generate exact FF music
    create_exact_ff_title_music()
    create_exact_ff_victory_music()
    
    print("\nAll exact Final Fantasy-style audio files generated!")
    print("Features:")
    print("- Exact victory fanfare with precise FF notes")
    print("- Authentic battle transition sounds")
    print("- Classic FF-style attack and defend effects")
    print("- Melancholic defeat and game over sounds")
    print("- Rich title screen music with exact chord progressions")
    print("- Extended victory music with precise melodies")

if __name__ == "__main__":
    main() 