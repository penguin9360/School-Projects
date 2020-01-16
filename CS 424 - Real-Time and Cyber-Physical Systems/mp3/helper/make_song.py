import sys

notes = ["G", "G#", "A", "A#", "B", "C", "C#", "D", "D#", "E", "F", "F#"]
s_num = 31
e_num = 107

n_to_note = {}
ci = 0
for i in range(s_num, e_num+1):
    octave = ci // len(notes)
    note_name = notes[ci % len(notes)] + str(octave)
    n_to_note[note_name] = i
    ci += 1

def make_note(note):
    return "note_t{"+ "{},{}".format(n_to_note[note], 30) + "}"

def transcribe(notes_file):
    with open(notes_file, "r") as nf:
        notes = nf.read().strip().split(',')
    song = ",".join([make_note(n) for n in notes])
    song = "{"+song+"}"
    print(song)

if __name__ == "__main__":
    if len(sys.argv) != 2:
        raise ValueError("python3 make_song.py {note_file}")
    transcribe(sys.argv[1])
