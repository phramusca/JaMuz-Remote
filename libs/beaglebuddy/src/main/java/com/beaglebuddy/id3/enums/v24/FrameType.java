 package com.beaglebuddy.id3.enums.v24;

import java.io.InputStream;
import java.lang.reflect.Constructor;

import com.beaglebuddy.id3.v24.frame_body.*;


/**
 * <table class="logos_width">
 *    <tbody>
 *       <tr>
 *          <td                               ><img src="../../../../../resources/id3v2.gif"                     width="56"  height="54"  alt="ID3 logo"        /></td>
 *          <td class="logos_horz_align_right"><img src="../../../../../resources/beaglebuddy_software_logo.gif" width="340" height="110" alt="Beaglebuddy logo"/></td>
 *       </tr>
 *    </tbody>
 * </table>
 * The <a href="http://id3.org/id3v2.4.0-frames">ID3v2.4 Tag Specification</a> defines 83 types of frames.
 */
public enum FrameType
{                                                                                                                                                                    /** frame id - AENC, audio encryption                                          */
   AUDIO_ENCRYPTION                        ("AENC", "audio encryption"                                     , ID3v24FrameBodyAudioEncryption.class                ),  /** frame id - APIC, attached picture                                          */
   ATTACHED_PICTURE                        ("APIC", "attached picture"                                     , ID3v24FrameBodyAttachedPicture.class                ),  /** frame id - ASPI, audio seek point index                                    */
   AUDIO_SEEK_POINT_INDEX                  ("ASPI", "audio seek point index"                               , ID3v24FrameBodyAudioSeekPointIndex.class            ),  /** frame id - COMM, comments                                                  */
   COMMENTS                                ("COMM", "comments"                                             , ID3v24FrameBodyComments.class                       ),  /** frame id - COMR, commercial                                                */
   COMMERCIAL                              ("COMR", "commercial"                                           , ID3v24FrameBodyCommercial.class                     ),  /** frame id - ENCR, encryption method registration                            */
   ENCRYPTION_METHOD_REGISTRATION          ("ENCR", "encryption method registration"                       , ID3v24FrameBodyEncryptionMethodRegistration.class   ),  /** frame id - EQU2, equalization (2)                                          */
   EQUALIZATION                            ("EQU2", "equalization (2)"                                     , ID3v24FrameBodyEqualization.class                   ),  /** frame id - ETCO, event timing codes                                        */
   EVENT_TIMING_CODES                      ("ETCO", "event timing codes"                                   , ID3v24FrameBodyEventTimingCodes.class               ),  /** frame id - GEOB, general encapsulated object                               */
   GENERAL_ENCAPSULATED_OBJECT             ("GEOB", "general encapsulated object"                          , ID3v24FrameBodyGeneralEncapsulatedObject.class      ),  /** frame id - GRID, group identification registration                         */
   GROUP_IDENTIFICATION_REGISTRATION       ("GRID", "group identification registration"                    , ID3v24FrameBodyGroupIdentificationRegistration.class),  /** frame id - IPLS, involved people list                                      */
   LINKED_INFORMATION                      ("LINK", "linked information"                                   , ID3v24FrameBodyLinkedInformation.class              ),  /** frame id - MCDI, music cd identifier                                       */
   MUSIC_CD_IDENTIFIER                     ("MCDI", "music cd identifier"                                  , ID3v24FrameBodyMusicCDIdentifier.class              ),  /** frame id - MLLT, mpeg location lookup table                                */
   MPEG_LOCATION_LOOKUP_TABLE              ("MLLT", "mpeg location lookup table"                           , ID3v24FrameBodyMPEGLocationLookupTable.class        ),  /** frame id - OWNE, ownership                                                 */
   OWNERSHIP                               ("OWNE", "ownership"                                            , ID3v24FrameBodyOwnership.class                      ),  /** frame id - PCNT, play counter                                              */
   PLAY_COUNTER                            ("PCNT", "play counter"                                         , ID3v24FrameBodyPlayCounter.class                    ),  /** frame id - POPM, popularimeter                                             */
   POPULARIMETER                           ("POPM", "popularimeter"                                        , ID3v24FrameBodyPopularimeter.class                  ),  /** frame id - POSS, position synchronization                                  */
   POSITION_SYNCHRONIZATION                ("POSS", "position synchronization"                             , ID3v24FrameBodyPositionSynchronization.class        ),  /** frame id - PRIV, private                                                   */
   PRIVATE                                 ("PRIV", "private"                                              , ID3v24FrameBodyPrivate.class                        ),  /** frame id - RBUF, recommended buffer size                                   */
   RECOMMENDED_BUFFER_SIZE                 ("RBUF", "recommended buffer size"                              , ID3v24FrameBodyRecommendedBufferSize.class          ),  /** frame id - RVA2, relative volume adjustment (2)                            */
   RELATIVE_VOLUME_ADJUSTMENT              ("RVA2", "relative volume adjustment (2)"                       , ID3v24FrameBodyRelativeVolumeAdjustment.class       ),  /** frame id - RVRB, reverb settings                                           */
   REVERB                                  ("RVRB", "reverb settings"                                      , ID3v24FrameBodyReverb.class                         ),  /** frame id - SEEK, seek frame                                                */
   SEEK_FRAME                              ("SEEK", "seek frame"                                           , ID3v24FrameBodySeek.class                           ),  /** frame id - SIGN, signature frame                                           */
   SIGNATURE                               ("SIGN", "signature frame"                                      , ID3v24FrameBodySignature.class                      ),  /** frame id - SYLT, synchronized lyric/text                                   */
   SYNCHRONIZED_LYRIC_TEXT                 ("SYLT", "synchronized lyric/text"                              , ID3v24FrameBodySynchronizedLyricsText.class         ),  /** frame id - SYTC, synchronized tempo codes                                  */
   SYNCHRONIZED_TEMPO_CODES                ("SYTC", "synchronized tempo codes"                             , ID3v24FrameBodySynchronizedTempoCodes.class         ),  /** frame id - TALB, album/movie/show title                                    */
   ALBUM_TITLE                             ("TALB", "album/movie/show title"                               , ID3v24FrameBodyTextInformation.class                ),  /** frame id - TBPM, bpm (beats per minute)                                    */
   BEATS_PER_MINUTE                        ("TBPM", "bpm (beats per minute)"                               , ID3v24FrameBodyTextInformation.class                ),  /** frame id - TCOM, composer                                                  */
   COMPOSER                                ("TCOM", "composer"                                             , ID3v24FrameBodyTextInformation.class                ),  /** frame id - TCON, content type                                              */
   CONTENT_TYPE                            ("TCON", "content type"                                         , ID3v24FrameBodyTextInformation.class                ),  /** frame id - TCOP, copyright message                                         */
   COPYRIGHT_MESSAGE                       ("TCOP", "copyright message"                                    , ID3v24FrameBodyTextInformation.class                ),  /** frame id - TDEN, encoding date/time                                        */
   ENCODING_TIME                           ("TDEN", "encoding date/time"                                   , ID3v24FrameBodyTextInformation.class                ),  /** frame id - TDLY, number of ms of silence between every song in a play list */
   PLAYLIST_DELAY                          ("TDLY", "playlist delay"                                       , ID3v24FrameBodyTextInformation.class                ),  /** frame id - TDOR, original release date/time                                */
   ORIGINAL_RELEASE_TIME                   ("TDOR", "original release date/time"                           , ID3v24FrameBodyTextInformation.class                ),  /** frame id - TDRC, date and time the song was recorded                       */
   RECORDING_TIME                          ("TDRC", "recording date/time"                                  , ID3v24FrameBodyTextInformation.class                ),  /** frame id - TDRL, date and time the song was released                       */
   RELEASE_TIME                            ("TDRL", "release date/time"                                    , ID3v24FrameBodyTextInformation.class                ),  /** frame id - TDTG, date and time the song was tagged                         */
   TAGGING_TIME                            ("TDTG", "tagging date/time"                                    , ID3v24FrameBodyTextInformation.class                ),  /** frame id - TENC, encoded by                                                */
   ENCODED_BY                              ("TENC", "encoded by"                                           , ID3v24FrameBodyTextInformation.class                ),  /** frame id - TEXT, lyricist/text writer                                      */
   LYRICIST                                ("TEXT", "lyricist/text writer"                                 , ID3v24FrameBodyTextInformation.class                ),  /** frame id - TFLT, file type                                                 */
   FILE_TYPE                               ("TFLT", "file type"                                            , ID3v24FrameBodyTextInformation.class                ),  /** frame id - TIPL, involved people list                                      */
   INVOLVED_PEOPLE_LIST                    ("TIPL", "involved people list"                                 , ID3v24FrameBodyTextInformation.class                ),  /** frame id - TIT1, content group description                                 */
   CONTENT_GROUP_DESCRIPTION               ("TIT1", "content group description"                            , ID3v24FrameBodyTextInformation.class                ),  /** frame id - TIT2, title/songname/content description                        */
   SONG_TITLE                              ("TIT2", "title/songname/content description"                   , ID3v24FrameBodyTextInformation.class                ),  /** frame id - TIT3, subtitle/description refinement                           */
   SUBTITLE_REFINEMENT                     ("TIT3", "subtitle/description refinement"                      , ID3v24FrameBodyTextInformation.class                ),  /** frame id - TKEY, initial key                                               */
   INITIAL_KEY                             ("TKEY", "initial key"                                          , ID3v24FrameBodyTextInformation.class                ),  /** frame id - TLAN, language(s) the song is sung in                           */
   LANGUAGE                                ("TLAN", "language(s)"                                          , ID3v24FrameBodyTextInformation.class                ),  /** frame id - TLEN, length of the song (in ms)                                */
   LENGTH                                  ("TLEN", "length of the song (in ms)"                           , ID3v24FrameBodyTextInformation.class                ),  /** frame id - TMCL, musician credits list                                     */
   MUSICIANS_CREDIT_LIST                   ("TMCL", "musician credits list"                                , ID3v24FrameBodyTextInformation.class                ),  /** frame id - TMED, media type                                                */
   MEDIA_TYPE                              ("TMED", "media type"                                           , ID3v24FrameBodyTextInformation.class                ),  /** frame id - TMOO, mood                                                      */
   MOOD                                    ("TMOO", "mood"                                                 , ID3v24FrameBodyTextInformation.class                ),  /** frame id - TOAL, original album/movie/show title                           */
   ORIGINAL_ALBUM_TITLE                    ("TOAL", "original album/movie/show title"                      , ID3v24FrameBodyTextInformation.class                ),  /** frame id - TOFN, original filename                                         */
   ORIGINAL_FILENAME                       ("TOFN", "original filename"                                    , ID3v24FrameBodyTextInformation.class                ),  /** frame id - TOLY, original lyricist(s)/text writer(s)                       */
   ORIGINAL_LYRICIST                       ("TOLY", "original lyricist(s)/text writer(s)"                  , ID3v24FrameBodyTextInformation.class                ),  /** frame id - TOPE, original artist(s)/performer(s)                           */
   ORIGINAL_ARTIST                         ("TOPE", "original artist(s)/performer(s)"                      , ID3v24FrameBodyTextInformation.class                ),  /** frame id - TOWN, file owner/licensee                                       */
   FILE_OWNER                              ("TOWN", "file owner/licensee"                                  , ID3v24FrameBodyTextInformation.class                ),  /** frame id - TPE1, lead performer(s)/soloist(s)                              */
   LEAD_PERFORMER                          ("TPE1", "lead performer(s)/soloist(s)"                         , ID3v24FrameBodyTextInformation.class                ),  /** frame id - TPE2, band/orchestra/accompaniment                              */
   BAND                                    ("TPE2", "band/orchestra/accompaniment"                         , ID3v24FrameBodyTextInformation.class                ),  /** frame id - TPE3, conductor/performer refinement                            */
   CONDUCTOR                               ("TPE3", "conductor/performer refinement"                       , ID3v24FrameBodyTextInformation.class                ),  /** frame id - TPE4, interpreted, remixed, or otherwise modified by            */
   INTERPRETED_MODIFIED_BY                 ("TPE4", "interpreted, remixed, or otherwise modified by"       , ID3v24FrameBodyTextInformation.class                ),  /** frame id - TPOS, part of a set                                             */
   PART_OF_A_SET                           ("TPOS", "part of a set"                                        , ID3v24FrameBodyTextInformation.class                ),  /** frame id - TPRO, produced notice                                           */
   PRODUCED_NOTICE                         ("TPRO", "produced notice"                                      , ID3v24FrameBodyTextInformation.class                ),  /** frame id - TPUB, publisher                                                 */
   PUBLISHER                               ("TPUB", "publisher"                                            , ID3v24FrameBodyTextInformation.class                ),  /** frame id - TRCK, track number/position in set                              */
   TRACK_NUMBER                            ("TRCK", "track number/position in set"                         , ID3v24FrameBodyTextInformation.class                ),  /** frame id - TRSN, internet radio station name                               */
   INTERNET_RADIO_STATION_NAME             ("TRSN", "internet radio station name"                          , ID3v24FrameBodyTextInformation.class                ),  /** frame id - TRSO, internet radio station owner                              */
   INTERNET_RADIO_STATION_OWNER            ("TRSO", "internet radio station owner"                         , ID3v24FrameBodyTextInformation.class                ),  /** frame id - TSOA, album sort order                                          */
   ALBUM_SORT_ORDER                        ("TSOA", "album sort order"                                     , ID3v24FrameBodyTextInformation.class                ),  /** frame id - TSOP, performer sort order                                      */
   PERFORMER_SORT_ORDER                    ("TSOP", "performer sort order"                                 , ID3v24FrameBodyTextInformation.class                ),  /** frame id - TSOT, title sort order                                          */
   TITLE_SORT_ORDER                        ("TSOT", "title sort order"                                     , ID3v24FrameBodyTextInformation.class                ),  /** frame id - TSRC, isrc (international standard recording code)              */
   INTERNATIONAL_STANDARD_RECORDING_CODE   ("TSRC", "isrc (international standard recording code)"         , ID3v24FrameBodyTextInformation.class                ),  /** frame id - TSSE, software/hardware and settings used for encoding          */
   SOFTWARE_HARDWARE_ENCODING_SETTINGS     ("TSSE", "software/hardware and settings used for encoding"     , ID3v24FrameBodyTextInformation.class                ),  /** frame id - TSST, set subtitle                                              */
   SET_SUBTITLE                            ("TSST", "set subtitle"                                         , ID3v24FrameBodyTextInformation.class                ),  /** frame id - TXXX, user defined text information frame                       */
   USER_DEFINED_TEXT_INFORMATION           ("TXXX", "user defined text information frame"                  , ID3v24FrameBodyUserDefinedTextInformation.class     ),  /** frame id - UFID, unique file identifier                                    */
   UNIQUE_FILE_IDENTIFIER                  ("UFID", "unique file identifier"                               , ID3v24FrameBodyUniqueFileIdentifier.class           ),  /** frame id - USER, terms of use                                              */
   TERMS_OF_USE                            ("USER", "terms of use"                                         , ID3v24FrameBodyTermsOfUse.class                     ),  /** frame id - USLT, unsychronized lyrics/text transcription                   */
   UNSYCHRONIZED_LYRICS                    ("USLT", "unsychronized lyrics/text transcription"              , ID3v24FrameBodyUnsynchronizedLyrics.class           ),  /** frame id - WCOM, commercial information                                    */
   COMMERCIAL_INFORMATION                  ("WCOM", "commercial information"                               , ID3v24FrameBodyURLLink.class                        ),  /** frame id - WCOP, copyright/legal information                               */
   COPYRIGHT_LEGAL_INFORMATION             ("WCOP", "copyright/legal information"                          , ID3v24FrameBodyURLLink.class                        ),  /** frame id - WOAF, official audio file webpage                               */
   OFFICIAL_AUDIO_FILE_WEBPAGE             ("WOAF", "official audio file webpage"                          , ID3v24FrameBodyURLLink.class                        ),  /** frame id - WOAR, official artist/performer webpage                         */
   OFFICIAL_ARTIST_WEBPAGE                 ("WOAR", "official artist/performer webpage"                    , ID3v24FrameBodyURLLink.class                        ),  /** frame id - WOAS, official audio source webpage                             */
   OFFICIAL_AUDIO_SOURCE_WEBPAGE           ("WOAS", "official audio source webpage"                        , ID3v24FrameBodyURLLink.class                        ),  /** frame id - WORS, official internet radio station homepage                  */
   OFFICIAL_INTERNET_RADIO_STATION_HOMEPAGE("WORS", "official internet radio station homepage"             , ID3v24FrameBodyURLLink.class                        ),  /** frame id - WPAY, payment                                                   */
   PAYMENT                                 ("WPAY", "payment"                                              , ID3v24FrameBodyURLLink.class                        ),  /** frame id - WPUB, publishers official webpage                               */
   PUBLISHERS_OFFICIAL_WEBPAGE             ("WPUB", "publishers official webpage"                          , ID3v24FrameBodyURLLink.class                        ),  /** frame id - WXXX, user defined url link                                     */
   USER_DEFINED_URL_LINK_FRAME             ("WXXX", "user defined url link"                                , ID3v24FrameBodyUserDefinedURLLink.class             );


   // class members
                                                 /** length of ID3v2.4 frame ids */
   public static final int FRAME_ID_LENGTH = 4;

   // data members
   private String         id;                    // 4 character id
   private String         description;           // description of the frame type
   private Class<?>       frameBodyClass;        // class used to implement the frame's body
   private Constructor<?> frameBodyConstructor;  // frame body class's constructor used to parse the frame body from an .mp3 file



   /**
    * constructor.
    * @param id                     4 character ID3v2.4 frame id.
    * @param description            description of the frame type.
    * @param frameBodyClass         class used to implement the frame's body.
    */
   private FrameType(String id, String description, Class<?> frameBodyClass)
   {
      this.id             = id;
      this.description    = description;
      this.frameBodyClass = frameBodyClass;

      try
      {
         // the ID3v24FrameBodyTextInformation and ID3v24FrameBodyURLLink classes implement multiple ID3v2.4 frame types and therefore need a frame id when parsing
         // their frame body from an .mp3 file.
         if (frameBodyClass == ID3v24FrameBodyTextInformation.class || frameBodyClass == ID3v24FrameBodyURLLink.class)
            this.frameBodyConstructor = frameBodyClass.getConstructor(new Class<?>[] {InputStream.class, FrameType.class, int.class});
         else
            this.frameBodyConstructor = frameBodyClass.getConstructor(new Class<?>[] {InputStream.class,                  int.class});
      }
      catch (NoSuchMethodException ex)
      {
         // this can never happen
         ex.printStackTrace();
      }
   }

   /**
    * gets the name of the frame type.
    * @return the name of the frame type.
    */
   public String getName()
   {
      return super.toString();
   }

   /**
    * gets the ID3v2.4 frame id.
    * @return the ID3v2.4 frame id.
    */
   public String getId()
   {
      return id;
   }

   /**
    * gets the description of the frame type.
    * @return the description of the frame type.
    */
   public String getDescription()
   {
      return description;
   }

   /**
    * gets the class used to implement the frame's body.
    * @return the class used to implement the frame's body.
    */
   public Class<?> getFrameBodyClass()
   {
      return frameBodyClass;
   }

   /**
    * gets the constructor for the frame body's class used to parse the frame's body from an .mp3 file.
    * @return the frame body class's constructor used to parse an existing frame body from an .mp3 file.
    */
   public Constructor<?> getFrameBodyConstructor()
   {
      return frameBodyConstructor;
   }

   /**
    * convert a string value to its corresponding frame type enum.
    * @return the FrameType enum corresponding to the string value.
    * @param frameId  string value to be converted to a FrameType enum.
    * @throws IllegalArgumentException   if the integral value does not correspond to a valid FrameType.
    */
   public static FrameType getFrameType(String frameId) throws IllegalArgumentException
   {
      for (FrameType ft : FrameType.values())
         if (frameId.equals(ft.getId()))
            return ft;
      throw new IllegalArgumentException("Invalid ID3v2.4 frame type " + frameId + ".");
   }

   /**
    * gets  a string representation of the frame type enum.
    * @return a string representation of the frame type enum.
    */
   public String toString()
   {
      return id + ": " + getName() + " - " + description;
   }
}
