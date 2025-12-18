import numpy as np
import wave
import struct

SAMPLE_RATE = 44100
TEMPO = 120  # Slower tempo for a town
BEAT_DURATION = 60 / TEMPO
VOLUME = 0.25

NOTE_FREQS = {
    'C4': 261.63, 'D4': 293.66, 'E4': 329.63, 'F4': 349.23, 'G4': 392.00, 'A4': 440.00, 'B4': 493.88,
    'C5': 523.25, 'D5': 587.33, 'E5': 659.25, 'F5': 698.46, 'G5': 783.99
}

melody = [
    ('C4', 1), ('E4', 1), ('G4', 1), ('C5', 1),
    ('G4', 1), ('E4', 1), ('C4', 2),
]

harmony = [
    ('C3', 2), ('G3', 2),
    ('F3', 2), ('C3', 2),
]

def sine_wave(frequency, duration, volume=1.0):
    t = np.linspace(0, duration, int(SAMPLE_RATE * duration), False)
    wave = 0.5 * np.sin(2 * np.pi * frequency * t)
    return (volume * wave).astype(np.float32)

def generate_track(notes, volume=1.0):
    track = np.array([], dtype=np.float32)
    for note, dur in notes:
        freq = NOTE_FREQS.get(note, 0)
        if freq > 0:
            note_wave = sine_wave(freq, dur * BEAT_DURATION, volume)
        else:
            note_wave = np.zeros(int(SAMPLE_RATE * dur * BEAT_DURATION), dtype=np.float32)
        track = np.concatenate((track, note_wave))
    return track

melody_track = generate_track(melody, VOLUME)
harmony_track = generate_track(harmony, VOLUME * 0.6)

# Pad shorter track
length = max(len(melody_track), len(harmony_track))
melody_track = np.pad(melody_track, (0, length - len(melody_track)))
harmony_track = np.pad(harmony_track, (0, length - len(harmony_track)))

final_track = melody_track + harmony_track
final_track = final_track / np.max(np.abs(final_track))

def write_wav(filename, audio, sample_rate):
    audio_int16 = np.int16(audio * 32767)
    with wave.open(filename, 'w') as wf:
        wf.setnchannels(1)
        wf.setsampwidth(2)
        wf.setframerate(sample_rate)
        wf.writeframes(audio_int16.tobytes())

write_wav('bgm_town.wav', final_track, SAMPLE_RATE)

print('Town music generated as bgm_town.wav')
