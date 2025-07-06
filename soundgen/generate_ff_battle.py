import numpy as np
import wave

SAMPLE_RATE = 44100
TEMPO = 150  # BPM from sheet
BEAT_DURATION = 60 / TEMPO
VOLUME = 0.22

# Note frequencies (A4 = 440 Hz)
NOTE_FREQS = {
    'C4': 261.63, 'D4': 293.66, 'Eb4': 311.13, 'E4': 329.63, 'F4': 349.23, 'G4': 392.00, 'A4': 440.00, 'Bb4': 466.16, 'B4': 493.88,
    'C5': 523.25, 'D5': 587.33, 'Eb5': 622.25, 'E5': 659.25, 'F5': 698.46, 'G5': 783.99, 'A5': 880.00, 'Bb5': 932.33, 'B5': 987.77,
    'C6': 1046.50, 'D6': 1174.66, 'Eb6': 1244.51, 'E6': 1318.51, 'F6': 1396.91, 'G6': 1567.98, 'A6': 1760.00, 'Bb6': 1864.66, 'B6': 1975.53,
    'C3': 130.81, 'D3': 146.83, 'Eb3': 155.56, 'E3': 164.81, 'F3': 174.61, 'G3': 196.00, 'A3': 220.00, 'Bb3': 233.08, 'B3': 246.94
}

# Full melody and harmony transcription (bars 1-16, both hands, simplified for chiptune)
# Each tuple: (note, duration in beats)
melody = [
    # Bar 1-2 (opening run)
    ('G4', 0.25), ('A4', 0.25), ('Bb4', 0.25), ('C5', 0.25), ('D5', 0.25), ('Eb5', 0.25), ('F5', 0.25), ('G5', 0.25),
    ('F5', 0.5), ('Eb5', 0.5), ('D5', 0.5), ('C5', 0.5),
    # Bar 3-4
    ('Bb4', 0.5), ('C5', 0.5), ('D5', 0.5), ('Eb5', 0.5),
    ('F5', 0.5), ('G5', 0.5), ('A5', 0.5), ('Bb5', 0.5),
    # Bar 5-6
    ('G5', 0.5), ('F5', 0.5), ('Eb5', 0.5), ('D5', 0.5),
    ('C5', 0.5), ('Bb4', 0.5), ('A4', 0.5), ('G4', 0.5),
    # Bar 7-8
    ('F4', 0.5), ('G4', 0.5), ('A4', 0.5), ('Bb4', 0.5),
    ('C5', 0.5), ('D5', 0.5), ('Eb5', 0.5), ('F5', 0.5),
    # Bar 9-10
    ('D5', 0.5), ('C5', 0.5), ('Bb4', 0.5), ('A4', 0.5),
    ('G4', 0.5), ('F4', 0.5), ('E4', 0.5), ('D4', 0.5),
    # Bar 11-12
    ('C4', 0.5), ('D4', 0.5), ('E4', 0.5), ('F4', 0.5),
    ('G4', 0.5), ('A4', 0.5), ('Bb4', 0.5), ('C5', 0.5),
    # Bar 13-14
    ('A4', 0.5), ('G4', 0.5), ('F4', 0.5), ('E4', 0.5),
    ('D4', 0.5), ('C4', 0.5), ('Bb3', 0.5), ('A3', 0.5),
    # Bar 15-16
    ('G3', 0.5), ('A3', 0.5), ('Bb3', 0.5), ('C4', 0.5),
    ('D4', 0.5), ('E4', 0.5), ('F4', 0.5), ('G4', 0.5),
]

# Harmony (left hand, simplified arpeggios)
harmony = [
    # Bar 1-2
    ('G3', 0.5), ('D4', 0.5), ('G4', 0.5), ('B4', 0.5),
    ('E4', 0.5), ('B4', 0.5), ('E5', 0.5), ('G5', 0.5),
    # Bar 3-4
    ('C4', 0.5), ('G4', 0.5), ('C5', 0.5), ('E5', 0.5),
    ('G4', 0.5), ('D5', 0.5), ('G5', 0.5), ('B5', 0.5),
    # Bar 5-6
    ('F4', 0.5), ('C5', 0.5), ('F5', 0.5), ('A5', 0.5),
    ('Bb3', 0.5), ('F4', 0.5), ('Bb4', 0.5), ('D5', 0.5),
    # Bar 7-8
    ('Eb4', 0.5), ('Bb4', 0.5), ('Eb5', 0.5), ('G5', 0.5),
    ('Ab3', 0.5), ('Eb4', 0.5), ('Ab4', 0.5), ('C5', 0.5),
    # Bar 9-10
    ('Bb3', 0.5), ('F4', 0.5), ('Bb4', 0.5), ('D5', 0.5),
    ('C4', 0.5), ('G4', 0.5), ('C5', 0.5), ('E5', 0.5),
    # Bar 11-12
    ('F3', 0.5), ('C4', 0.5), ('F4', 0.5), ('A4', 0.5),
    ('G3', 0.5), ('D4', 0.5), ('G4', 0.5), ('B4', 0.5),
    # Bar 13-14
    ('E3', 0.5), ('B3', 0.5), ('E4', 0.5), ('G4', 0.5),
    ('A3', 0.5), ('E4', 0.5), ('A4', 0.5), ('C5', 0.5),
    # Bar 15-16
    ('D3', 0.5), ('A3', 0.5), ('D4', 0.5), ('F4', 0.5),
    ('G3', 0.5), ('D4', 0.5), ('G4', 0.5), ('B4', 0.5),
]

# Square wave with harmonics
def square_wave(frequency, duration, volume=1.0):
    t = np.linspace(0, duration, int(SAMPLE_RATE * duration), False)
    wave = 0.5 * np.sign(np.sin(2 * np.pi * frequency * t))
    # Add 2nd and 3rd harmonics for richer sound
    wave += 0.2 * np.sign(np.sin(2 * np.pi * frequency * 2 * t))
    wave += 0.1 * np.sign(np.sin(2 * np.pi * frequency * 3 * t))
    return (volume * wave / 1.3).astype(np.float32)

def generate_track(notes, volume=1.0):
    track = np.array([], dtype=np.float32)
    for note, dur in notes:
        freq = NOTE_FREQS.get(note, 0)
        if freq > 0:
            note_wave = square_wave(freq, dur * BEAT_DURATION, volume)
        else:
            note_wave = np.zeros(int(SAMPLE_RATE * dur * BEAT_DURATION), dtype=np.float32)
        track = np.concatenate((track, note_wave))
    return track

melody_track = generate_track(melody, VOLUME)
harmony_track = generate_track(harmony, VOLUME * 0.7)

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

write_wav('bgm_battle.wav', final_track, SAMPLE_RATE)
print('Battle music generated as bgm_battle.wav') 