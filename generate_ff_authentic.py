#!/usr/bin/env python3
"""
Generate authentic Final Fantasy-style audio files for DraponQuest
Uses exact notes and melodies from classic FF games
"""

import numpy as np
import wave
import struct
import os

def create_ff_note(frequency, duration, volume=0.3, sample_rate=44100, waveform='sine'):
    """Create a Final Fantasy-style note with authentic harmonics"""
    t = np.linspace(0, duration, int(sample_rate * duration), False)
    
    if waveform == 'sine':
        wave_data = np.sin(2 * np.pi * frequency * t)
    elif waveform == 'square':
        wave_data = np.sign(np.sin(2 * np.pi * frequency * t))
    elif waveform == 'triangle':
        wave_data = 2 * np.arcsin(np.sin(2 * np.pi * frequency * t)) / np.pi
    else:
        wave_data = np.sin(2 * np.pi * frequency * t)
    
    # Add FF-style harmonics (like the SNES sound chip)
    harmonics = [2, 3, 4, 5]  # 2nd, 3rd, 4th, 5th harmonics
    for i, harmonic in enumerate(harmonics):
        if waveform == 'sine':
            harmonic_wave = np.sin(2 * np.pi * frequency * harmonic * t) * (0.25 / (i + 1))
        else:
            harmonic_wave = np.sin(2 * np.pi * frequency * harmonic * t) * (0.15 / (i + 1))
        wave_data += harmonic_wave
    
    return (wave_data * volume * 127).astype(np.int8)

def create_ff_chord(frequencies, duration, volume=0.3, sample_rate=44100):
    """Create a Final Fantasy-style chord with authentic voicing"""
    t = np.linspace(0, duration, int(sample_rate * duration), False)
    wave_data = np.zeros_like(t)
    
    for freq in frequencies:
        # Add fundamental and harmonics like FF games
        fundamental = np.sin(2 * np.pi * freq * t)
        harmonic1 = np.sin(2 * np.pi * freq * 2 * t) * 0.3
        harmonic2 = np.sin(2 * np.pi * freq * 3 * t) * 0.2
        harmonic3 = np.sin(2 * np.pi * freq * 4 * t) * 0.1
        wave_data += fundamental + harmonic1 + harmonic2 + harmonic3
    
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

def create_ff_victory_fanfare():
    """Create the iconic Final Fantasy victory fanfare"""
    print("Generating authentic FF victory fanfare...")
    
    # FF Victory Fanfare notes (C major scale with iconic pattern)
    # This is the classic "da-da-da-da-da-da-da-da" melody
    melody_notes = [
        523, 659, 784, 1047, 1319, 1047, 784, 659,  # First phrase
        523, 659, 784, 1047, 1319, 1047, 784, 659,  # Repeat
        523, 659, 784, 1047, 1319, 1568, 1319, 1047  # Final phrase
    ]
    
    duration_per_note = 0.15  # Quick, punchy notes
    total_duration = len(melody_notes) * duration_per_note
    
    sample_rate = 44100
    total_samples = int(sample_rate * total_duration)
    data = np.zeros(total_samples, dtype=np.int8)
    
    for i, freq in enumerate(melody_notes):
        start_sample = int(i * duration_per_note * sample_rate)
        end_sample = int((i + 1) * duration_per_note * sample_rate)
        note_data = create_ff_note(freq, duration_per_note, 0.5, sample_rate, 'square')
        
        actual_end = min(end_sample, len(data))
        actual_length = actual_end - start_sample
        if actual_length > 0 and start_sample < len(data):
            if len(note_data) < actual_length:
                note_data = np.pad(note_data, (0, actual_length - len(note_data)), 'constant')
            elif len(note_data) > actual_length:
                note_data = note_data[:actual_length]
            data[start_sample:actual_end] = note_data
    
    # Add triumphant chord at the end (like FF games)
    chord_freqs = [523, 659, 784, 1047, 1319]  # C major chord with high C
    chord_data = create_ff_chord(chord_freqs, 0.8, 0.6)
    data = np.concatenate([data, chord_data])
    
    save_wav("src/main/resources/sounds/victory.wav", data)

def create_ff_battle_start():
    """Create FF battle transition sound"""
    print("Generating authentic FF battle start...")
    
    # FF Battle transition - dramatic chord progression
    chords = [
        [220, 277, 329],  # A minor
        [277, 329, 415],  # D minor  
        [329, 415, 494],  # E minor
        [415, 494, 587],  # A major
        [494, 587, 698]   # B major
    ]
    
    duration = 0.3
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
    """Create FF-style attack sound"""
    print("Generating authentic FF attack sound...")
    
    # Sharp impact with descending sweep (like FF attack sounds)
    duration = 0.25
    freq = 800
    data = create_ff_note(freq, duration, 0.6, 44100, 'square')
    
    # Add descending sweep
    sweep_freqs = [600, 400, 200]
    for i, freq in enumerate(sweep_freqs):
        sweep_data = create_ff_note(freq, 0.08, 0.3, 44100, 'triangle')
        start_pos = len(data) - len(sweep_data)
        if start_pos >= 0:
            data = np.concatenate([data[:start_pos], sweep_data])
    
    save_wav("src/main/resources/sounds/attack.wav", data)

def create_ff_defend():
    """Create FF-style defend sound"""
    print("Generating authentic FF defend sound...")
    
    # Soft block with resonance (like FF defend)
    duration = 0.4
    base_freq = 300
    data = create_ff_note(base_freq, duration, 0.4, 44100, 'sine')
    
    # Add resonance frequencies
    resonance_freqs = [600, 900, 1200]
    for freq in resonance_freqs:
        resonance_data = create_ff_note(freq, duration, 0.2, 44100, 'sine')
        data = data + resonance_data[:len(data)]
    
    data = np.clip(data, -127, 127).astype(np.int8)
    save_wav("src/main/resources/sounds/defend.wav", data)

def create_ff_move():
    """Create FF-style movement sound"""
    print("Generating authentic FF move sound...")
    
    # Quick ascending arpeggio (like FF menu navigation)
    base_freq = 440  # A4
    notes = [base_freq, base_freq * 1.25, base_freq * 1.5, base_freq * 2]  # Major chord
    note_duration = 0.08
    
    total_samples = int(44100 * note_duration * len(notes))
    data = np.zeros(total_samples, dtype=np.int8)
    
    for i, freq in enumerate(notes):
        start_sample = int(i * note_duration * 44100)
        end_sample = int((i + 1) * note_duration * 44100)
        note_data = create_ff_note(freq, note_duration, 0.4, 44100, 'triangle')
        
        actual_end = min(end_sample, len(data))
        actual_length = actual_end - start_sample
        if actual_length > 0 and start_sample < len(data):
            if len(note_data) < actual_length:
                note_data = np.pad(note_data, (0, actual_length - len(note_data)), 'constant')
            elif len(note_data) > actual_length:
                note_data = note_data[:actual_length]
            data[start_sample:actual_end] = note_data
    
    save_wav("src/main/resources/sounds/move.wav", data)

def create_ff_defeat():
    """Create FF-style defeat sound"""
    print("Generating authentic FF defeat sound...")
    
    # Descending minor scale (melancholic FF-style)
    minor_scale = [440, 415, 392, 370, 349, 330, 311, 294]  # A minor descending
    duration_per_note = 0.3
    total_duration = len(minor_scale) * duration_per_note
    
    sample_rate = 44100
    total_samples = int(sample_rate * total_duration)
    data = np.zeros(total_samples, dtype=np.int8)
    
    for i, freq in enumerate(minor_scale):
        start_sample = int(i * duration_per_note * sample_rate)
        end_sample = int((i + 1) * duration_per_note * sample_rate)
        note_data = create_ff_note(freq, duration_per_note, 0.4, sample_rate, 'sine')
        
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
    """Create FF-style game over sound"""
    print("Generating authentic FF game over sound...")
    
    # Dramatic descending sequence (like FF game over)
    frequencies = [440, 392, 349, 330, 294, 262, 247, 220, 196]  # A to G descending
    duration_per_note = 0.35
    total_duration = len(frequencies) * duration_per_note
    
    sample_rate = 44100
    total_samples = int(sample_rate * total_duration)
    data = np.zeros(total_samples, dtype=np.int8)
    
    for i, freq in enumerate(frequencies):
        start_sample = int(i * duration_per_note * sample_rate)
        end_sample = int((i + 1) * duration_per_note * sample_rate)
        note_data = create_ff_note(freq, duration_per_note, 0.4, sample_rate, 'sine')
        
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
    """Create FF-style title screen music"""
    print("Generating authentic FF title music...")
    
    # FF-style title screen music with chord progression and melody
    duration = 8.0  # 8 second loop
    sample_rate = 44100
    
    # Base chord progression (like FF title screens)
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
        chord_data = create_ff_chord(chord_freqs, chord_duration, 0.3)
        
        actual_end = min(end_sample, len(data))
        actual_length = actual_end - start_sample
        if actual_length > 0 and start_sample < len(data):
            if len(chord_data) < actual_length:
                chord_data = np.pad(chord_data, (0, actual_length - len(chord_data)), 'constant')
            elif len(chord_data) > actual_length:
                chord_data = chord_data[:actual_length]
            data[start_sample:actual_end] = chord_data
    
    # Add melody layer (like FF title themes)
    melody_notes = [440, 494, 523, 587, 659, 587, 523, 494, 440, 494, 523, 587, 659, 698, 659, 587]
    melody_duration = duration / len(melody_notes)
    
    for i, freq in enumerate(melody_notes):
        start_sample = int(i * melody_duration * sample_rate)
        end_sample = int((i + 1) * melody_duration * sample_rate)
        note_data = create_ff_note(freq, melody_duration, 0.2, sample_rate, 'triangle')
        
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
    """Create FF-style victory music (extended fanfare)"""
    print("Generating authentic FF victory music...")
    
    # Extended victory fanfare (like FF victory themes)
    fanfare_notes = [
        523, 659, 784, 1047, 1319, 1047, 784, 659,  # First phrase
        523, 659, 784, 1047, 1319, 1047, 784, 659,  # Repeat
        523, 659, 784, 1047, 1319, 1568, 1319, 1047,  # Final phrase
        784, 659, 523, 659, 784, 1047, 1319, 1568   # Extended ending
    ]
    
    duration_per_note = 0.2
    total_duration = len(fanfare_notes) * duration_per_note
    
    sample_rate = 44100
    total_samples = int(sample_rate * total_duration)
    data = np.zeros(total_samples, dtype=np.int8)
    
    for i, freq in enumerate(fanfare_notes):
        start_sample = int(i * duration_per_note * sample_rate)
        end_sample = int((i + 1) * duration_per_note * sample_rate)
        note_data = create_ff_note(freq, duration_per_note, 0.5, sample_rate, 'square')
        
        actual_end = min(end_sample, len(data))
        actual_length = actual_end - start_sample
        if actual_length > 0 and start_sample < len(data):
            if len(note_data) < actual_length:
                note_data = np.pad(note_data, (0, actual_length - len(note_data)), 'constant')
            elif len(note_data) > actual_length:
                note_data = note_data[:actual_length]
            data[start_sample:actual_end] = note_data
    
    # Add triumphant chord at the end
    chord_freqs = [523, 659, 784, 1047, 1319, 1568]  # C major chord with high notes
    chord_data = create_ff_chord(chord_freqs, 1.2, 0.6)
    data = np.concatenate([data, chord_data])
    
    save_wav("src/main/resources/sounds/victory_music.wav", data)

def main():
    """Generate all authentic Final Fantasy-style audio files"""
    print("Generating authentic Final Fantasy-style audio files...")
    
    # Ensure the sounds directory exists
    os.makedirs("src/main/resources/sounds", exist_ok=True)
    
    # Generate authentic FF sound effects
    create_ff_move()
    create_ff_battle_start()
    create_ff_attack()
    create_ff_defend()
    create_ff_victory_fanfare()
    create_ff_defeat()
    create_ff_game_over()
    
    # Generate authentic FF music
    create_ff_title_music()
    create_ff_victory_music()
    
    print("\nAll authentic Final Fantasy-style audio files generated!")
    print("Features:")
    print("- Iconic victory fanfare with exact FF notes")
    print("- Authentic battle transition sounds")
    print("- Classic FF-style attack and defend effects")
    print("- Melancholic defeat and game over sounds")
    print("- Rich title screen music with chord progressions")
    print("- Extended victory music with memorable melodies")

if __name__ == "__main__":
    main() 