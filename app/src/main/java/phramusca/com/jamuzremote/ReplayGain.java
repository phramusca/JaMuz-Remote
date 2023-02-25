/*
 * Copyright (C) 2017 phramusca ( https://github.com/phramusca/JaMuz/ )
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package phramusca.com.jamuzremote;

import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.TagException;
import org.jaudiotagger.tag.flac.FlacTag;
import org.jaudiotagger.tag.id3.AbstractID3v2Frame;
import org.jaudiotagger.tag.id3.AbstractID3v2Tag;
import org.jaudiotagger.tag.id3.AbstractTagFrameBody;
import org.jaudiotagger.tag.id3.framebody.FrameBodyTXXX;
import org.jaudiotagger.tag.vorbiscomment.VorbisCommentTag;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author phramusca ( https://github.com/phramusca/JaMuz/ )
 */
public class ReplayGain {

    //TODO: Find a way to read replaygain from "content://" paths (mediaStore)

    public static class GainValues implements Serializable {
        private float albumGain = Float.NaN;
        private float trackGain = Float.NaN;
        private float trackPeak; //Not used
        private float albumPeak; //Not used
        String MP3GAIN_MINMAX; //mp3gain only, Not used
        String MP3GAIN_ALBUM_MINMAX; //mp3gain only, Not used
        String MP3GAIN_UNDO; //mp3gain only, Not used
        String REPLAYGAIN_REFERENCE_LOUDNESS; //Flac only, Not used

        public GainValues() {
        }

        public GainValues(float trackGain, float albumGain) {
            this.trackGain = trackGain;
            this.albumGain = albumGain;
        }

        @Override
        public String toString() {
            return "albumGain=" + albumGain + ", trackGain=" + trackGain; //NON-NLS //NON-NLS
        }

        public boolean isValid() {
            return !Float.isNaN(albumGain) && !Float.isNaN(trackGain);
        }

        public float getAlbumGain() {
            return albumGain;
        }

        public float getTrackGain() {
            return trackGain;
        }

        public void setAlbumGain(float albumGain) {
            this.albumGain = albumGain;
        }

        public void setTrackGain(float trackGain) {
            this.trackGain = trackGain;
        }
    }
}
