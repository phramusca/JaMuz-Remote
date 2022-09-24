package com.beaglebuddy.id3.enums;



/**
 * <table class="logos_width">
 *    <tbody>
 *       <tr>
 *          <td                               ><img src="../../../../resources/id3v2.gif"                     width="56"  height="54"  alt="ID3 logo"        /></td>
 *          <td class="logos_horz_align_right"><img src="../../../../resources/beaglebuddy_software_logo.gif" width="340" height="110" alt="Beaglebuddy logo"/></td>
 *       </tr>
 *    </tbody>
 * </table>
 * standard ID3v2.x genres.
 */
public enum Genre
{                                                         /** Blues                               */
   BLUES                    ("Blues"                   ), /** Classic Rock                        */
   CLASSIC_ROCK             ("Classic Rock"            ), /** Country                             */
   COUNTRY                  ("Country"                 ), /** Dance                               */
   DANCE                    ("Dance"                   ), /** Disco                               */
   DISCO                    ("Disco"                   ), /** Funk                                */
   FUNK                     ("Funk"                    ), /** Grunge                              */
   GRUNGE                   ("Grunge"                  ), /** Hip-Hop                             */
   HIP_HOP                  ("Hip-Hop"                 ), /** Jazz                                */
   JAZZ                     ("Jazz"                    ), /** Metal                               */
   METAL                    ("Metal"                   ), /** New Age                             */
   NEW_AGE                  ("New Age"                 ), /** Oldies                              */
   OLDIES                   ("Oldies"                  ), /** Other                               */
   OTHER                    ("Other"                   ), /** Pop                                 */
   POP                      ("Pop"                     ), /** R&B                                 */
   R_AND_B                  ("R&B (Rhythem and Blues)" ), /** Rap                                 */
   RAP                      ("Rap"                     ), /** Reggae                              */
   REGGAE                   ("Reggae"                  ), /** Rock                                */
   ROCK                     ("Rock"                    ), /** Techno                              */
   TECHNO                   ("Techno"                  ), /** Industrial                          */
   INDUSTRIAL               ("Industrial"              ), /** Alternative                         */
   ALTERNATIVE              ("Alternative"             ), /** Ska                                 */
   SKA                      ("Ska"                     ), /** Death Metal                         */
   DEATH_METAL              ("Death Metal"             ), /** Pranks                              */
   PRANKS                   ("Pranks"                  ), /** Soundtrack                          */
   SOUNDTRACK               ("Soundtrack"              ), /** Euro-Techno                         */
   EURO_TECHNO              ("Euro-Techno"             ), /** Ambient                             */
   AMBIENT                  ("Ambient"                 ), /** Trip-Hop                            */
   TRIP_HOP                 ("Trip-Hop"                ), /** Vocal                               */
   VOCAL                    ("Vocal"                   ), /** Jazz+Funk                           */
   JAZZ_FUNK                ("Jazz+Funk"               ), /** Fusion                              */
   FUSION                   ("Fusion"                  ), /** Trance                              */
   TRANCE                   ("Trance"                  ), /** Classical                           */
   CLASSICAL                ("Classical"               ), /** Instrumental                        */
   INSTRUMENTAL             ("Instrumental"            ), /** Acid                                */
   ACID                     ("Acid"                    ), /** House                               */
   HOUSE                    ("House"                   ), /** Game                                */
   GAME                     ("Game"                    ), /** Sound Clip                          */
   SOUND_CLIP               ("Sound Clip"              ), /** Gospel                              */
   GOSPEL                   ("Gospel"                  ), /** Noise                               */
   NOISE                    ("Noise"                   ), /** AlternRock                          */
   ALTERNATIVE_ROCK         ("Alternative Rock"        ), /** Bass                                */
   BASS                     ("Bass"                    ), /** Soul                                */
   SOUL                     ("Soul"                    ), /** Punk                                */
   PUNK                     ("Punk"                    ), /** Space                               */
   SPACE                    ("Space"                   ), /** Meditative                          */
   MEDITATIVE               ("Meditative"              ), /** Instrumental Pop                    */
   INSTRUMENTAL_POP         ("Instrumental Pop"        ), /** Instrumental Rock                   */
   INSTRUMENTAL_ROCK        ("Instrumental Rock"       ), /** Ethnic                              */
   ETHNIC                   ("Ethnic"                  ), /** Gothic                              */
   GOTHIC                   ("Gothic"                  ), /** Darkwave                            */
   DARKWAVE                 ("Darkwave"                ), /** Techno-Industrial                   */
   TECHNO_INDUSTRIAL        ("Techno-Industrial"       ), /** Electronic                          */
   ELECTRONIC               ("Electronic"              ), /** Pop-Folk                            */
   POP_FOLK                 ("Pop-Folk"                ), /** Eurodance                           */
   EURODANCE                ("Eurodance"               ), /** Dream                               */
   DREAM                    ("Dream"                   ), /** Southern Rock                       */
   SOUTHERN_ROCK            ("Southern Rock"           ), /** Comedy                              */
   COMEDY                   ("Comedy"                  ), /** Cult                                */
   CULT                     ("Cult"                    ), /** Gangsta                             */
   GANGSTA                  ("Gangsta"                 ), /** Top 40                              */
   TOP_40                   ("Top 40"                  ), /** Christian Rap                       */
   CHRISTIAN_RAP            ("Christian Rap"           ), /** Pop/Funk                            */
   POP_FUNK                 ("Pop/Funk"                ), /** Jungle                              */
   JUNGLE                   ("Jungle"                  ), /** Native American                     */
   NATIVE_AMERICAN          ("Native American"         ), /** Cabaret                             */
   CABARET                  ("Cabaret"                 ), /** New Wave                            */
   NEW_WAVE                 ("New Wave"                ), /** Psychadelic                         */
   PSYCHADELIC              ("Psychadelic"             ), /** Rave                                */
   RAVE                     ("Rave"                    ), /** Showtunes                           */
   SHOWTUNES                ("Showtunes"               ), /** Trailer                             */
   TRAILER                  ("Trailer"                 ), /** Lo-Fi                               */
   LO_FI                    ("Lo-Fi"                   ), /** Tribal                              */
   TRIBAL                   ("Tribal"                  ), /** Acid Punk                           */
   ACID_PUNK                ("Acid Punk"               ), /** Acid Jazz                           */
   ACID_JAZZ                ("Acid Jazz"               ), /** Polka                               */
   POLKA                    ("Polka"                   ), /** Retro                               */
   RETRO                    ("Retro"                   ), /** Musical                             */
   MUSICAL                  ("Musical"                 ), /** Rock & Roll                         */
   ROCK_N_ROLL              ("Rock & Roll"             ), /** Hard Rock                           */
   HARD_ROCK                ("Hard Rock"               ), /** Folk                                */
   FOLK                     ("Folk"                    ), /** Folk-Rock                           */
   FOLK_ROCK                ("Folk-Rock"               ), /** National Folk                       */
   NATIONAL_FOLK            ("National Folk"           ), /** Swing                               */
   SWING                    ("Swing"                   ), /** Fast Fusion                         */
   FAST_FUSION              ("Fast Fusion"             ), /** Bebob                               */
   BEBOB                    ("Bebob"                   ), /** Latin                               */
   LATIN                    ("Latin"                   ), /** Revival                             */
   REVIVAL                  ("Revival"                 ), /** Celtic                              */
   CELTIC                   ("Celtic"                  ), /** Bluegrass                           */
   BLUEGRASS                ("Bluegrass"               ), /** Avantgarde                          */
   AVANTGARDE               ("Avantgarde"              ), /** Gothic Rock                         */
   GOTHIC_ROCK              ("Gothic Rock"             ), /** Progressive Rock                    */
   PROGRESSIVE_ROCK         ("Progressive Rock"        ), /** Psychedelic Rock                    */
   PSYCHEDELIC_ROCK         ("Psychedelic Rock"        ), /** Symphonic Rock                      */
   SYMPHONIC_ROCK           ("Symphonic Rock"          ), /** Slow Rock                           */
   SLOW_ROCK                ("Slow Rock"               ), /** Big Band                            */
   BIG_BAND                 ("Big Band"                ), /** Chorus                              */
   CHORUS                   ("Chorus"                  ), /** Easy Listening                      */
   EASY_LISTENING           ("Easy Listening"          ), /** Acoustic                            */
   ACOUSTIC                 ("Acoustic"                ), /** Humour                              */
   HUMOUR                   ("Humour"                  ), /** Speech                              */
   SPEECH                   ("Speech"                  ), /** Chanson                             */
   CHANSON                  ("Chanson"                 ), /** Opera                               */
   OPERA                    ("Opera"                   ), /** Chamber Music                       */
   CHAMBER_MUSIC            ("Chamber Music"           ), /** Sonata                              */
   SONATA                   ("Sonata"                  ), /** Symphony                            */
   SYMPHONY                 ("Symphony"                ), /** Booty Brass                         */
   BOOTY_BRASS              ("Booty Brass"             ), /** Primus                              */
   PRIMUS                   ("Primus"                  ), /** Porn Groove                         */
   PORN_GROOVE              ("Porn Groove"             ), /** Satire                              */
   SATIRE                   ("Satire"                  ), /** Slow Jam                            */
   SLOW_JAM                 ("Slow Jam"                ), /** Club                                */
   CLUB                     ("Club"                    ), /** Tango                               */
   TANGO                    ("Tango"                   ), /** Samba                               */
   SAMBA                    ("Samba"                   ), /** Folklore                            */
   FOLKLORE                 ("Folklore"                ), /** Ballad                              */
   BALLAD                   ("Ballad"                  ), /** Power Ballad                        */
   POWER_BALLAD             ("Power Ballad"            ), /** Rhytmic Soul                        */
   RHYTMIC_SOUL             ("Rhytmic Soul"            ), /** Freestyle                           */
   FREESTYLE                ("Freestyle"               ), /** Duet                                */
   DUET                     ("Duet"                    ), /** Punk Rock                           */
   PUNK_ROCK                ("Punk Rock"               ), /** Drum Solo                           */
   DRUM_SOLO                ("Drum Solo"               ), /** A Capela                            */
   A_CAPELA                 ("A Capela"                ), /** Euro-House                          */
   EURO_HOUSE               ("Euro-House"              ), /** Dance Hall                          */
   DANCE_HALL               ("Dance Hall"              ), /** Goa                                 */
   GOA                      ("Goa"                     ), /** Drum & Bass                         */
   DRUM_AND_BASS            ("Drum & Bass"             ), /** Club-House                          */
   CLUB_HOUSE               ("Club-House"              ), /** Hardcore                            */
   HARDCORE                 ("Hardcore"                ), /** Terror                              */
   TERROR                   ("Terror"                  ), /** Indie                               */
   INDIE                    ("Indie"                   ), /** British Pop                         */
   BRITPOP                  ("British Pop"             ), /** Negerpunk                           */
   NEGERPUNK                ("Negerpunk"               ), /** Polsk Punk                          */
   POLSK_PUNK               ("Polsk Punk"              ), /** Beat                                */
   BEAT                     ("Beat"                    ), /** Christian Gangsta                   */
   CHRISTIAN_GANGSTA        ("Christian Gangsta"       ), /** Heavy Metal                         */
   HEAVY_METAL              ("Heavy Metal"             ), /** Black Metal                         */
   BLACK_METAL              ("Black Metal"             ), /** Crossover                           */
   CROSSOVER                ("Crossover"               ), /** Contemporary C                      */
   CONTEMPORARY_C           ("Contemporary C"          ), /** Christian Rock                      */
   CHRISTIAN_ROCK           ("Christian Rock"          ), /** Merengue                            */
   MERENGUE                 ("Merengue"                ), /** Salsa                               */
   SALSA                    ("Salsa"                   ), /** Thrash Metal                        */
   THRASH_METAL             ("Thrash Metal"            ), /** Anime                               */
   ANIME                    ("Anime"                   ), /** JPop                                */
   JPOP                     ("JPop"                    ), /** SynthPop                            */
   SYNTH_POP                ("SynthPop"                ), /** Remix                               */
   REMIX                    ("Remix"                   ), /** Cover                               */
   COVER                    ("Cover"                   );





   // data members
   private String name;

   /**
    * constructor.
    * @param name  name of the genre.
    */
   private Genre(String name)
   {
      this.name = name;
   }

   /**
    * converts an integral value to its corresponding genre enum.
    * @return a genre enum corresponding to the integral value.
    * @param genre  integral value to be converted to a genre enum.
    * @throws IllegalArgumentException   if the value is not a valid genre.
    */
   public static Genre valueOf(byte genre) throws IllegalArgumentException
   {
      for (Genre g : Genre.values())
         if (genre == g.ordinal())
            return g;
      throw new IllegalArgumentException("Invalid genre " + genre + ".");
   }

   /**
    * return the name of the genre.
    * @return  thename of the genre.
    */
   public String getName()
   {
      return name;
   }

   /**
    * gets a string representation of the genre enum.
    * @return a string representation of the genre enum.
    */
   @Override
   public String toString()
   {
      return "(" + ordinal() + ")";
   }
}
