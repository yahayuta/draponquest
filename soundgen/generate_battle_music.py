import numpy as np
import wave
import struct

SAMPLE_RATE = 44100
TEMPO = 140  # BPM
BEAT_DURATION = 60 / TEMPO  # seconds per quarter note
VOLUME = 0.25

# Note frequencies (A4 = 440 Hz)
NOTE_FREQS = {
    'C3': 130.81, 'D3': 146.83, 'Eb3': 155.56, 'F3': 174.61, 'G3': 196.00, 'Ab3': 207.65, 'Bb3': 233.08,
    'C4': 261.63, 'D4': 293.66, 'Eb4': 311.13, 'F4': 349.23, 'G4': 392.00, 'Ab4': 415.30, 'A4': 440.00, 'Bb4': 466.16,
    'C5': 523.25, 'D5': 587.33, 'Eb5': 622.25, 'F5': 698.46, 'G5': 783.99
}

# Melody (right hand)
melody = [
    'G4', 'Bb4', 'G4', 'Bb4', 'G4', 'Bb4', 'G4', 'Bb4',
    'F4', 'G4', 'F4', 'G4', 'F4', 'G4', 'F4', 'G4',
    'Eb4', 'F4', 'Eb4', 'F4', 'Eb4', 'F4', 'Eb4', 'F4',
    'D4', 'Eb4', 'D4', 'Eb4', 'D4', 'Eb4', 'D4', 'Eb4',
]

# Bass/Chords (left hand)
bass = [
    'Eb3', 'Bb3', 'G4', 'Bb3', 'Eb4', 'Bb3', 'G4', 'Bb3',
    'F3', 'C4', 'A4', 'C4', 'F4', 'C4', 'A4', 'C4',
    'Ab3', 'Eb4', 'C4', 'Eb4', 'Ab4', 'Eb4', 'C4', 'Eb4',
    'Bb3', 'F4', 'D4', 'F4', 'Bb4', 'F4', 'D4', 'F4',
]

# Each note is an 8th note (half a beat)
NOTE_LEN = BEAT_DURATION / 2

# Simple square wave generator
def square_wave(frequency, duration, volume=1.0):
    t = np.linspace(0, duration, int(SAMPLE_RATE * duration), False)
    wave = 0.5 * np.sign(np.sin(2 * np.pi * frequency * t))
    return (volume * wave).astype(np.float32)

# Mix two tracks together
def mix_tracks(track1, track2):
    length = max(len(track1), len(track2))
    t1 = np.pad(track1, (0, length - len(track1)))
    t2 = np.pad(track2, (0, length - len(track2)))
    return np.clip(t1 + t2, -1.0, 1.0)

# Generate melody track
def generate_track(notes, note_len, volume=1.0):
    track = np.array([], dtype=np.float32)
    for note in notes:
        freq = NOTE_FREQS[note]
        note_wave = square_wave(freq, note_len, volume)
        track = np.concatenate((track, note_wave))
    return track

# Main generation
melody_track = generate_track(melody, NOTE_LEN, VOLUME)
bass_track = generate_track(bass, NOTE_LEN, VOLUME * 0.7)

# Repeat to make it longer (loop 4 times)
melody_track = np.tile(melody_track, 4)
bass_track = np.tile(bass_track, 4)

final_track = mix_tracks(melody_track, bass_track)

# Normalize
final_track = final_track / np.max(np.abs(final_track))

# Write to WAV file
def write_wav(filename, audio, sample_rate):
    audio_int16 = np.int16(audio * 32767)
    with wave.open(filename, 'w') as wf:
        wf.setnchannels(1)
        wf.setsampwidth(2)
        wf.setframerate(sample_rate)
        wf.writeframes(audio_int16.tobytes())

write_wav('bgm_battle.wav', final_track, SAMPLE_RATE)

print('Battle music generated as bgm_battle.wav') 