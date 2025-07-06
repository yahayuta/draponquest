#!/usr/bin/env python3
"""
Generate missing audio files for DraponQuest
Creates: game_over.wav, title.wav, victory_music.wav
"""

import numpy as np
import wave
import struct
import os

def create_8bit_sound(frequency, duration, volume=0.3, sample_rate=44100):
    """Create a simple 8-bit sound wave"""
    t = np.linspace(0, duration, int(sample_rate * duration), False)
    wave_data = np.sin(2 * np.pi * frequency * t) * volume
    return (wave_data * 127).astype(np.int8)

def create_chord(frequencies, duration, volume=0.3, sample_rate=44100):
    """Create a chord from multiple frequencies"""
    t = np.linspace(0, duration, int(sample_rate * duration), False)
    wave_data = np.zeros_like(t)
    for freq in frequencies:
        wave_data += np.sin(2 * np.pi * freq * t)
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

def create_game_over_sound():
    """Create game over sound - descending tone sequence"""
    print("Generating game_over.wav...")
    
    # Create descending tone sequence
    frequencies = [440, 392, 349, 330, 294, 262]  # A, G, F, E, D, C
    duration_per_note = 0.3
    total_duration = len(frequencies) * duration_per_note
    
    # Generate the sequence
    sample_rate = 44100
    total_samples = int(sample_rate * total_duration)
    data = np.zeros(total_samples, dtype=np.int8)
    
    for i, freq in enumerate(frequencies):
        start_sample = int(i * duration_per_note * sample_rate)
        end_sample = int((i + 1) * duration_per_note * sample_rate)
        actual_end = min(end_sample, len(data))
        actual_length = actual_end - start_sample
        if actual_length > 0 and start_sample < len(data):
            note_data = create_8bit_sound(freq, actual_length / sample_rate, 0.4)
            # Pad or trim note_data to match actual_length
            if len(note_data) < actual_length:
                note_data = np.pad(note_data, (0, actual_length - len(note_data)), 'constant')
            elif len(note_data) > actual_length:
                note_data = note_data[:actual_length]
            data[start_sample:actual_end] = note_data
    
    save_wav("src/main/resources/sounds/game_over.wav", data)

def create_title_music():
    """Create title screen music - simple ambient loop"""
    print("Generating title.wav...")
    
    # Create a simple ambient loop with multiple layers
    duration = 4.0  # 4 second loop
    sample_rate = 44100
    
    # Base ambient tone
    base_freq = 220  # A3
    base_data = create_8bit_sound(base_freq, duration, 0.2)
    
    # Add harmonic layer
    harmonic_freq = 440  # A4
    harmonic_data = create_8bit_sound(harmonic_freq, duration, 0.15)
    
    # Add some variation with a third layer
    variation_freq = 330  # E4
    variation_data = create_8bit_sound(variation_freq, duration, 0.1)
    
    # Combine all layers
    combined_data = base_data + harmonic_data + variation_data
    combined_data = np.clip(combined_data, -127, 127).astype(np.int8)
    
    save_wav("src/main/resources/sounds/title.wav", combined_data)

def create_victory_music():
    """Create victory music - triumphant ascending sequence"""
    print("Generating victory_music.wav...")
    
    # Create triumphant ascending sequence
    frequencies = [262, 330, 392, 440, 523, 659, 784]  # C, E, G, A, C, E, G
    duration_per_note = 0.25
    total_duration = len(frequencies) * duration_per_note
    
    # Generate the sequence
    sample_rate = 44100
    total_samples = int(sample_rate * total_duration)
    data = np.zeros(total_samples, dtype=np.int8)
    
    for i, freq in enumerate(frequencies):
        start_sample = int(i * duration_per_note * sample_rate)
        end_sample = int((i + 1) * duration_per_note * sample_rate)
        actual_end = min(end_sample, len(data))
        actual_length = actual_end - start_sample
        if actual_length > 0 and start_sample < len(data):
            note_data = create_8bit_sound(freq, actual_length / sample_rate, 0.5)
            # Pad or trim note_data to match actual_length
            if len(note_data) < actual_length:
                note_data = np.pad(note_data, (0, actual_length - len(note_data)), 'constant')
            elif len(note_data) > actual_length:
                note_data = note_data[:actual_length]
            data[start_sample:actual_end] = note_data
    
    # Add a triumphant chord at the end
    chord_frequencies = [523, 659, 784, 1047]  # C, E, G, C (high)
    chord_data = create_chord(chord_frequencies, 1.0, 0.6)
    
    # Combine sequence and chord
    final_data = np.concatenate([data, chord_data])
    final_data = np.clip(final_data, -127, 127).astype(np.int8)
    
    save_wav("src/main/resources/sounds/victory_music.wav", final_data)

def main():
    """Generate all missing audio files"""
    print("Generating missing audio files for DraponQuest...")
    
    # Ensure the sounds directory exists
    os.makedirs("src/main/resources/sounds", exist_ok=True)
    
    # Generate the missing files
    create_game_over_sound()
    create_title_music()
    create_victory_music()
    
    print("\nAll missing audio files generated successfully!")
    print("Files created:")
    print("- src/main/resources/sounds/game_over.wav")
    print("- src/main/resources/sounds/title.wav") 
    print("- src/main/resources/sounds/victory_music.wav")

if __name__ == "__main__":
    main() 