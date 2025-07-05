#!/usr/bin/env python3
"""
Generate Final Fantasy-style audio files for DraponQuest
Creates enhanced sound effects and music with FF characteristics
"""

import numpy as np
import wave
import struct
import os
import random

def create_ff_sound(frequency, duration, volume=0.3, sample_rate=44100, waveform='sine'):
    """Create a Final Fantasy-style sound wave with harmonics"""
    t = np.linspace(0, duration, int(sample_rate * duration), False)
    
    if waveform == 'sine':
        wave_data = np.sin(2 * np.pi * frequency * t)
    elif waveform == 'square':
        wave_data = np.sign(np.sin(2 * np.pi * frequency * t))
    elif waveform == 'saw':
        wave_data = 2 * (t * frequency - np.floor(0.5 + t * frequency))
    else:
        wave_data = np.sin(2 * np.pi * frequency * t)
    
    # Add harmonics for FF-style richness
    harmonics = [2, 3, 4]  # 2nd, 3rd, 4th harmonics
    for i, harmonic in enumerate(harmonics):
        if waveform == 'sine':
            harmonic_wave = np.sin(2 * np.pi * frequency * harmonic * t) * (0.3 / (i + 1))
        else:
            harmonic_wave = np.sin(2 * np.pi * frequency * harmonic * t) * (0.2 / (i + 1))
        wave_data += harmonic_wave
    
    return (wave_data * volume * 127).astype(np.int8)

def create_ff_chord(frequencies, duration, volume=0.3, sample_rate=44100):
    """Create a Final Fantasy-style chord with rich harmonics"""
    t = np.linspace(0, duration, int(sample_rate * duration), False)
    wave_data = np.zeros_like(t)
    
    for freq in frequencies:
        # Add fundamental and harmonics
        fundamental = np.sin(2 * np.pi * freq * t)
        harmonic1 = np.sin(2 * np.pi * freq * 2 * t) * 0.3
        harmonic2 = np.sin(2 * np.pi * freq * 3 * t) * 0.2
        wave_data += fundamental + harmonic1 + harmonic2
    
    wave_data = wave_data * (volume / len(frequencies))
    return (wave_data * 127).astype(np.int8)

def create_ff_arpeggio(base_freq, duration, volume=0.4, sample_rate=44100):
    """Create a Final Fantasy-style arpeggio"""
    notes = [base_freq, base_freq * 1.25, base_freq * 1.5, base_freq * 2]  # Major chord
    note_duration = duration / len(notes)
    
    total_samples = int(sample_rate * duration)
    data = np.zeros(total_samples, dtype=np.int8)
    
    for i, freq in enumerate(notes):
        start_sample = int(i * note_duration * sample_rate)
        end_sample = int((i + 1) * note_duration * sample_rate)
        note_data = create_ff_sound(freq, note_duration, volume, sample_rate, 'sine')
        
        actual_end = min(end_sample, len(data))
        actual_length = actual_end - start_sample
        if actual_length > 0 and start_sample < len(data):
            if len(note_data) < actual_length:
                note_data = np.pad(note_data, (0, actual_length - len(note_data)), 'constant')
            elif len(note_data) > actual_length:
                note_data = note_data[:actual_length]
            data[start_sample:actual_end] = note_data
    
    return data

def save_wav(filename, data, sample_rate=44100):
    """Save audio data as WAV file"""
    with wave.open(filename, 'w') as wav_file:
        wav_file.setnchannels(1)  # Mono
        wav_file.setsampwidth(1)  # 8-bit
        wav_file.setframerate(sample_rate)
        wav_file.writeframes(data.tobytes())
    print(f"Created: {filename}")

def create_ff_move_sound():
    """Create FF-style movement sound - quick ascending arpeggio"""
    print("Generating FF-style move.wav...")
    
    # Quick ascending arpeggio
    base_freq = 440  # A4
    data = create_ff_arpeggio(base_freq, 0.3, 0.4)
    save_wav("src/main/resources/sounds/move.wav", data)

def create_ff_battle_start():
    """Create FF-style battle start - dramatic chord progression"""
    print("Generating FF-style battle_start.wav...")
    
    # Dramatic chord progression
    chords = [
        [220, 277, 329],  # A minor
        [277, 329, 415],  # D minor
        [329, 415, 494],  # E minor
        [415, 494, 587]   # A major
    ]
    
    duration = 0.4
    sample_rate = 44100
    total_samples = int(sample_rate * duration * len(chords))
    data = np.zeros(total_samples, dtype=np.int8)
    
    for i, chord_freqs in enumerate(chords):
        start_sample = int(i * duration * sample_rate)
        end_sample = int((i + 1) * duration * sample_rate)
        chord_data = create_ff_chord(chord_freqs, duration, 0.5)
        
        actual_end = min(end_sample, len(data))
        actual_length = actual_end - start_sample
        if actual_length > 0 and start_sample < len(data):
            if len(chord_data) < actual_length:
                chord_data = np.pad(chord_data, (0, actual_length - len(chord_data)), 'constant')
            elif len(chord_data) > actual_length:
                chord_data = chord_data[:actual_length]
            data[start_sample:actual_end] = chord_data
    
    save_wav("src/main/resources/sounds/battle_start.wav", data)

def create_ff_attack():
    """Create FF-style attack sound - sharp impact with harmonics"""
    print("Generating FF-style attack.wav...")
    
    # Sharp impact sound
    duration = 0.2
    freq = 800
    data = create_ff_sound(freq, duration, 0.6, 44100, 'square')
    
    # Add a quick descending sweep
    sweep_freqs = [600, 400, 200]
    for i, freq in enumerate(sweep_freqs):
        sweep_data = create_ff_sound(freq, 0.05, 0.3, 44100, 'saw')
        start_pos = len(data) - len(sweep_data)
        if start_pos >= 0:
            data = np.concatenate([data[:start_pos], sweep_data])
    
    save_wav("src/main/resources/sounds/attack.wav", data)

def create_ff_defend():
    """Create FF-style defend sound - soft block with resonance"""
    print("Generating FF-style defend.wav...")
    
    # Soft block sound with resonance
    duration = 0.4
    base_freq = 300
    data = create_ff_sound(base_freq, duration, 0.4, 44100, 'sine')
    
    # Add resonance frequencies
    resonance_freqs = [600, 900]
    for freq in resonance_freqs:
        resonance_data = create_ff_sound(freq, duration, 0.2, 44100, 'sine')
        data = data + resonance_data[:len(data)]
    
    data = np.clip(data, -127, 127).astype(np.int8)
    save_wav("src/main/resources/sounds/defend.wav", data)

def create_ff_victory():
    """Create FF-style victory fanfare - triumphant ascending melody"""
    print("Generating FF-style victory.wav...")
    
    # Triumphant ascending melody
    melody_notes = [262, 330, 392, 440, 523, 659, 784, 1047]  # C major scale
    duration_per_note = 0.2
    total_duration = len(melody_notes) * duration_per_note
    
    sample_rate = 44100
    total_samples = int(sample_rate * total_duration)
    data = np.zeros(total_samples, dtype=np.int8)
    
    for i, freq in enumerate(melody_notes):
        start_sample = int(i * duration_per_note * sample_rate)
        end_sample = int((i + 1) * duration_per_note * sample_rate)
        note_data = create_ff_sound(freq, duration_per_note, 0.5, sample_rate, 'sine')
        
        actual_end = min(end_sample, len(data))
        actual_length = actual_end - start_sample
        if actual_length > 0 and start_sample < len(data):
            if len(note_data) < actual_length:
                note_data = np.pad(note_data, (0, actual_length - len(note_data)), 'constant')
            elif len(note_data) > actual_length:
                note_data = note_data[:actual_length]
            data[start_sample:actual_end] = note_data
    
    # Add triumphant chord at the end
    chord_freqs = [523, 659, 784, 1047]  # C major chord
    chord_data = create_ff_chord(chord_freqs, 0.5, 0.6)
    data = np.concatenate([data, chord_data])
    
    save_wav("src/main/resources/sounds/victory.wav", data)

def create_ff_defeat():
    """Create FF-style defeat sound - descending minor scale"""
    print("Generating FF-style defeat.wav...")
    
    # Descending minor scale
    minor_scale = [440, 415, 392, 370, 349, 330, 311, 294]  # A minor descending
    duration_per_note = 0.25
    total_duration = len(minor_scale) * duration_per_note
    
    sample_rate = 44100
    total_samples = int(sample_rate * total_duration)
    data = np.zeros(total_samples, dtype=np.int8)
    
    for i, freq in enumerate(minor_scale):
        start_sample = int(i * duration_per_note * sample_rate)
        end_sample = int((i + 1) * duration_per_note * sample_rate)
        note_data = create_ff_sound(freq, duration_per_note, 0.4, sample_rate, 'sine')
        
        actual_end = min(end_sample, len(data))
        actual_length = actual_end - start_sample
        if actual_length > 0 and start_sample < len(data):
            if len(note_data) < actual_length:
                note_data = np.pad(note_data, (0, actual_length - len(note_data)), 'constant')
            elif len(note_data) > actual_length:
                note_data = note_data[:actual_length]
            data[start_sample:actual_end] = note_data
    
    save_wav("src/main/resources/sounds/defeat.wav", data)

def create_ff_game_over():
    """Create FF-style game over sound - dramatic descending sequence"""
    print("Generating FF-style game_over.wav...")
    
    # Dramatic descending sequence
    frequencies = [440, 392, 349, 330, 294, 262, 247, 220]  # A to A descending
    duration_per_note = 0.3
    total_duration = len(frequencies) * duration_per_note
    
    sample_rate = 44100
    total_samples = int(sample_rate * total_duration)
    data = np.zeros(total_samples, dtype=np.int8)
    
    for i, freq in enumerate(frequencies):
        start_sample = int(i * duration_per_note * sample_rate)
        end_sample = int((i + 1) * duration_per_note * sample_rate)
        note_data = create_ff_sound(freq, duration_per_note, 0.4, sample_rate, 'sine')
        
        actual_end = min(end_sample, len(data))
        actual_length = actual_end - start_sample
        if actual_length > 0 and start_sample < len(data):
            if len(note_data) < actual_length:
                note_data = np.pad(note_data, (0, actual_length - len(note_data)), 'constant')
            elif len(note_data) > actual_length:
                note_data = note_data[:actual_length]
            data[start_sample:actual_end] = note_data
    
    save_wav("src/main/resources/sounds/game_over.wav", data)

def create_ff_title_music():
    """Create FF-style title music - ambient loop with melody"""
    print("Generating FF-style title.wav...")
    
    # Create ambient loop with melody
    duration = 6.0  # 6 second loop
    sample_rate = 44100
    
    # Base ambient chord progression
    base_chords = [
        [220, 277, 329],  # A minor
        [277, 329, 415],  # D minor
        [329, 415, 494],  # E minor
        [415, 494, 587]   # A major
    ]
    
    chord_duration = duration / len(base_chords)
    total_samples = int(sample_rate * duration)
    data = np.zeros(total_samples, dtype=np.int8)
    
    for i, chord_freqs in enumerate(base_chords):
        start_sample = int(i * chord_duration * sample_rate)
        end_sample = int((i + 1) * chord_duration * sample_rate)
        chord_data = create_ff_chord(chord_freqs, chord_duration, 0.3)
        
        actual_end = min(end_sample, len(data))
        actual_length = actual_end - start_sample
        if actual_length > 0 and start_sample < len(data):
            if len(chord_data) < actual_length:
                chord_data = np.pad(chord_data, (0, actual_length - len(chord_data)), 'constant')
            elif len(chord_data) > actual_length:
                chord_data = chord_data[:actual_length]
            data[start_sample:actual_end] = chord_data
    
    # Add melody layer
    melody_notes = [440, 494, 523, 587, 659, 587, 523, 494]  # A major scale
    melody_duration = duration / len(melody_notes)
    
    for i, freq in enumerate(melody_notes):
        start_sample = int(i * melody_duration * sample_rate)
        end_sample = int((i + 1) * melody_duration * sample_rate)
        note_data = create_ff_sound(freq, melody_duration, 0.2, sample_rate, 'sine')
        
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

def create_ff_victory_music():
    """Create FF-style victory music - triumphant fanfare"""
    print("Generating FF-style victory_music.wav...")
    
    # Triumphant fanfare
    fanfare_notes = [523, 659, 784, 1047, 1319, 1047, 784, 659, 523]  # C major arpeggio
    duration_per_note = 0.3
    total_duration = len(fanfare_notes) * duration_per_note
    
    sample_rate = 44100
    total_samples = int(sample_rate * total_duration)
    data = np.zeros(total_samples, dtype=np.int8)
    
    for i, freq in enumerate(fanfare_notes):
        start_sample = int(i * duration_per_note * sample_rate)
        end_sample = int((i + 1) * duration_per_note * sample_rate)
        note_data = create_ff_sound(freq, duration_per_note, 0.5, sample_rate, 'sine')
        
        actual_end = min(end_sample, len(data))
        actual_length = actual_end - start_sample
        if actual_length > 0 and start_sample < len(data):
            if len(note_data) < actual_length:
                note_data = np.pad(note_data, (0, actual_length - len(note_data)), 'constant')
            elif len(note_data) > actual_length:
                note_data = note_data[:actual_length]
            data[start_sample:actual_end] = note_data
    
    # Add triumphant chord at the end
    chord_freqs = [523, 659, 784, 1047, 1319]  # C major chord with high C
    chord_data = create_ff_chord(chord_freqs, 1.0, 0.6)
    data = np.concatenate([data, chord_data])
    
    save_wav("src/main/resources/sounds/victory_music.wav", data)

def main():
    """Generate all Final Fantasy-style audio files"""
    print("Generating Final Fantasy-style audio files for DraponQuest...")
    
    # Ensure the sounds directory exists
    os.makedirs("src/main/resources/sounds", exist_ok=True)
    
    # Generate FF-style sound effects
    create_ff_move_sound()
    create_ff_battle_start()
    create_ff_attack()
    create_ff_defend()
    create_ff_victory()
    create_ff_defeat()
    create_ff_game_over()
    
    # Generate FF-style music
    create_ff_title_music()
    create_ff_victory_music()
    
    print("\nAll Final Fantasy-style audio files generated successfully!")
    print("Files created with FF characteristics:")
    print("- Rich harmonics and layered sounds")
    print("- Arpeggios and chord progressions")
    print("- Memorable melodies and fanfares")
    print("- Dramatic sound effects")

if __name__ == "__main__":
    main() 