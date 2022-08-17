package com.beaglebuddy.id3.v24.frame_body;

import java.io.InputStream;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

import com.beaglebuddy.id3.enums.v24.Encoding;
import com.beaglebuddy.id3.enums.v24.FrameType;



/**
 * <table class="logos_width">
 *    <tbody>
 *       <tr>
 *          <td                               ><img src="../../../../../resources/id3v2.gif"                     width="56"  height="54"  alt="ID3 logo"        /></td>
 *          <td class="logos_horz_align_right"><img src="../../../../../resources/beaglebuddy_software_logo.gif" width="340" height="110" alt="Beaglebuddy logo"/></td>
 *       </tr>
 *    </tbody>
 * </table>
 * <p class="beaglebuddy">
 * A <i>text information</i> frame body is associated with the ID3v2.4 {@link com.beaglebuddy.id3.v24.ID3v24Frame frames} listed below, and are the most important frames, containing information
 * like artist, album, song title, and more.  The ID3v2.4 specification defines 45 types of text frames. The <i>text information</i> frame body contains the following fields:
 * <table class="beaglebuddy">
 *    <caption><b>Text Information Frame Body Fields</b></caption>
 *    <thead>
 *       <tr><th class="beaglebuddy">&nbsp;</th><th class="beaglebuddy">Field</th><th class="beaglebuddy">Description</th></tr>
 *    </thead>
 *    <tbody>
 *       <tr><td class="beaglebuddy">1. </td><td class="beaglebuddy">{@link com.beaglebuddy.id3.enums.v24.Encoding encoding}</td><td class="beaglebuddy">character set used to {@link #setEncoding(Encoding) encode} the <i>text</i> field. </td></tr>
 *       <tr><td class="beaglebuddy">2. </td><td class="beaglebuddy">text                                                   </td><td class="beaglebuddy">a string whose value depends on the type of text frame.  see the list below.
 *                                                                                                                                                       All text information frames supports multiple strings, stored as a null separated
 *                                                                                                                                                       list, where null is represented by the termination code for the character encoding.
 *                                                                                                                                                                                                                                          </td></tr>
 *    </tbody>
 * </table>
 * </p>
 * <br/><br/>
 * <p class="beaglebuddy">
 *    <table class="beaglebuddy">
 *       <caption><b>Text Information Frames</b></caption>
 *       <thead>
 *          <tr><th class="beaglebuddy_text_frame_id">Frame Id</th><th class="beaglebuddy">Description</th></tr>
 *       </thead>
 *       <tbody>
 *          <tr><td class="beaglebuddy">TALB</td><td class="beaglebuddy">The 'Album/Movie/Show title' frame is intended for the title of the recording(/source of sound) which the audio in the file is taken from.                 </td></tr>
 *          <tr><td class="beaglebuddy">TBPM</td><td class="beaglebuddy">The 'BPM' frame contains the number of beats per minute in the main part of the audio. The BPM is an integer and represented as a numerical string.        </td></tr>
 *          <tr><td class="beaglebuddy">TCOM</td><td class="beaglebuddy">The 'Composer(s)' frame is intended for the name of the composer(s). They are separated with the "/" character.</td></tr>
 *          <tr><td class="beaglebuddy">TCON</td><td class="beaglebuddy">The 'Content type',  which ID3v1 was stored as a one byte numeric value only, is now a string.  You may use one or several of the ID3v1 types
 *                                                                       as numerical strings, or, since the category list would be impossible to maintain with accurate and up to date categories, define your own.
 *                                                                       <br/></br>
 *                                                                       References to the ID3v1 genres can be made by enclosing a number from the {@link com.beaglebuddy.mp3.MP3#setMusicType(String) genres list}
 *                                                                       with parentheses.  This is optionally followed by a refinement, e.g. "(21)" or "(4)Eurodisco". Several references can be made in the same frame,
 *                                                                       e.g. "(51)(39)".  If the refinement should begin with a parenthesis,  "(",  it should be replaced with "((", e.g. "((I can figure out any genre)"
 *                                                                       or "(55)((I think...)".                                                                                                                                    </td></tr>
 *          <tr><td class="beaglebuddy">TCOP</td><td class="beaglebuddy">The 'Copyright message' frame, which must begin with a year and a space character (making five characters), is intended
 *                                                                       for the copyright holder of the original sound, not the audio file itself. The absence of this frame means only that the
 *                                                                       copyright information is unavailable or has been removed, and must not be interpreted to mean that the sound is public
 *                                                                       domain. Every time this field is displayed the field must be preceded with "Copyright � ".                                                                 </td></tr>
 *          <tr><td class="beaglebuddy">TDEN</td><td class="beaglebuddy">The 'Encoding time' frame contains a {@link com.beaglebuddy.id3.pojo.v24.Timestamp} describing when the audio was encoded. ie, when the .mp3 was created.  </td></tr>
 *          <tr><td class="beaglebuddy">TDLY</td><td class="beaglebuddy">The 'Playlist delay' defines the numbers of milliseconds of silence  that should be inserted before this audio. The value zero indicates
 *                                                                       that this is a part of a multifile audio track that should be played continuously.                                                                         </td></tr>
 *          <tr><td class="beaglebuddy">TDOR</td><td class="beaglebuddy">The 'Original Release time' frame contains a {@link com.beaglebuddy.id3.pojo.v24.Timestamp} when the original recording of the audio was released.         </td></tr>
 *          <tr><td class="beaglebuddy">TDRC</td><td class="beaglebuddy">The 'Recording time'        frame contains a {@link com.beaglebuddy.id3.pojo.v24.Timestamp} describing when the audio was encoded.                         </td></tr>
 *          <tr><td class="beaglebuddy">TDRL</td><td class="beaglebuddy">The 'Release time'          frame contains a {@link com.beaglebuddy.id3.pojo.v24.Timestamp} describing when the audio was first released.                  </td></tr>
 *          <tr><td class="beaglebuddy">TDTG</td><td class="beaglebuddy">The 'Tagging time'          frame contains a {@link com.beaglebuddy.id3.pojo.v24.Timestamp} describing when the audio was tagged.                          </td></tr>
 *          <tr><td class="beaglebuddy">TENC</td><td class="beaglebuddy">The 'Encoded by' frame contains the name of the person or organization that encoded the audio file. This field may contain a copyright message, if
 *                                                                       the audio file also is copyrighted by the encoder.                                                                                                         </td></tr>
 *          <tr><td class="beaglebuddy">TEXT</td><td class="beaglebuddy">The 'Lyricist(s)/Text writer(s)' frame is intended for the writer(s) of the text or lyrics in the recording. They are separated with the "/" character.    </td></tr>
 *          <tr><td class="beaglebuddy">TFLT</td><td class="beaglebuddy">The 'File type' frame indicates which type of audio this tag defines. The following type and refinements are defined: <br/><br/>
 *                                                                       <table class="beaglebuddy">
 *                                                                          <thead>
 *                                                                             <tr><th class="beaglebuddy">Type</th><th class="beaglebuddy">Refinement</th></tr>
 *                                                                          </thead>
 *                                                                          <tbody>
 *                                                                             <tr><td class="beaglebuddy">MPG    </td><td class="beaglebuddy">MPEG Audio                                              </td></tr>
 *                                                                             <tr><td class="beaglebuddy">MPG/1  </td><td class="beaglebuddy">MPEG 1/2 layer I                                        </td></tr>
 *                                                                             <tr><td class="beaglebuddy">MPG/2  </td><td class="beaglebuddy">MPEG 1/2 layer II                                       </td></tr>
 *                                                                             <tr><td class="beaglebuddy">MPG/3  </td><td class="beaglebuddy">MPEG 1/2 layer III                                      </td></tr>
 *                                                                             <tr><td class="beaglebuddy">MPG/2.5</td><td class="beaglebuddy">MPEG 2.5                                                </td></tr>
 *                                                                             <tr><td class="beaglebuddy">MPG/AAC</td><td class="beaglebuddy">Advanced audio compression                              </td></tr>
 *                                                                             <tr><td class="beaglebuddy">VQF    </td><td class="beaglebuddy">Transform-domain Weighted Interleave Vector Quantization</td></tr>
 *                                                                             <tr><td class="beaglebuddy">PCM    </td><td class="beaglebuddy">Pulse Code Modulated audio                              </td></tr>
 *                                                                          </tbody>
 *                                                                        </table>
 *                                                                        but other types may be used, not for these types though. This is used in a similar way to the predefined types in the "TMED" frame, but without
 *                                                                        parentheses. If this frame is not present audio type is assumed to be "MPG".                                                                              </td></tr>
 *          <tr><td class="beaglebuddy">TIPL</td><td class="beaglebuddy">The 'Involved people list' frame is very similar to the musician credits list (TMCL), but maps between functions, like producer, and names.
 *                                                                       Use the {@link #getTextList()} method to retrieve the list.                                                                                                </td></tr>
 *          <tr><td class="beaglebuddy">TIT1</td><td class="beaglebuddy">The 'Content group description' frame is used if the sound belongs to a larger category of sounds/music. For example, classical music is often
 *                                                                       sorted in different musical sections (e.g. "Piano Concerto", "Weather - Hurricane").                                                                       </td></tr>
 *          <tr><td class="beaglebuddy">TIT2</td><td class="beaglebuddy">The 'Title/Songname/Content description' frame is the actual name of the piece (e.g. "Adagio", "Hurricane Donna").                                         </td></tr>
 *          <tr><td class="beaglebuddy">TIT3</td><td class="beaglebuddy">The 'Subtitle/Description refinement' frame is used for information directly related to the contents title (e.g. "Op. 16" or "Performed live at Wembley"). </td></tr>
 *          <tr><td class="beaglebuddy">TKEY</td><td class="beaglebuddy">The 'Initial key' frame contains the musical key in which the sound starts. It is represented as a string with a maximum length of three characters. The
 *                                                                       ground keys are represented with "A","B","C","D","E", "F" and "G" and halfkeys represented with "b" and "#". Minor is represented as "m". Example "Cbm".
 *                                                                       Off key is represented with an "o" only.                                                                                                                   </td></tr>
 *          <tr><td class="beaglebuddy">TLAN</td><td class="beaglebuddy">The 'Language(s)' frame should contain the languages of the text or lyrics spoken or sung in the audio. The
 *                                                                       {@link com.beaglebuddy.id3.enums.Language language} is represented with three characters according to the
 *                                                                       <a href="http://www.loc.gov/standards/iso639-2/php/code_list.php">ISO-639-2</a> specification. If more than one language is used in the text their language
 *                                                                       codes should follow according to their usage.                                                                                                              </td></tr>
 *          <tr><td class="beaglebuddy">TLEN</td><td class="beaglebuddy">The 'Length' frame contains the length of the audiofile in milliseconds, represented as a numeric string.                                                  </td></tr>
 *          <tr><td class="beaglebuddy">TMCL</td><td class="beaglebuddy">The 'Musician credits list' frame contains a mapping between instruments and the musician that played it.
 *                                                                       Every odd field is an instrument and every even is an artist or a comma delimited list of artists.
 *                                                                       Use the {@link #getTextList()} method to retrieve the list.                                                                                                </td></tr>
 *          <tr><td class="beaglebuddy">TMED</td><td class="beaglebuddy">The 'Media type' frame describes from which media the sound originated. This may be a text string or a reference to the predefined media types found in the
 *                                                                       list below. References are made within "(" and ")" and are optionally followed by a text refinement, e.g. "(MC) with four channels". If a text refinement
 *                                                                       should begin with a "(" character it should be replaced with "((" in the same way as in the "TCO" frame. Predefined refinements is appended after the media
 *                                                                       type, e.g. "(CD/A)" or "(VID/PAL/VHS)".<br/><br/>
 *                                                                       <table class="beaglebuddy">
 *                                                                          <thead>
 *                                                                             <tr><th class="beaglebuddy">Type</th><th class="beaglebuddy">Refinement</th><th class="beaglebuddy">Description</th></tr>
 *                                                                          </thead>
 *                                                                          <tbody>
 *                                                                             <tr><td class="beaglebuddy">DIG</td><td class="beaglebuddy">&nbsp;</td><td class="beaglebuddy">Other digital media                              </td></tr>
 *                                                                             <tr><td class="beaglebuddy">DIG</td><td class="beaglebuddy">/A    </td><td class="beaglebuddy">Analog transfer from media                       </td></tr>
 *                                                                             <tr><td class="beaglebuddy">ANA</td><td class="beaglebuddy">&nbsp;</td><td class="beaglebuddy">Other analog media                               </td></tr>
 *                                                                             <tr><td class="beaglebuddy">ANA</td><td class="beaglebuddy">/WAC  </td><td class="beaglebuddy">Wax cylinder                                     </td></tr>
 *                                                                             <tr><td class="beaglebuddy">ANA</td><td class="beaglebuddy">/8CA  </td><td class="beaglebuddy">8-track tape cassette                            </td></tr>
 *                                                                             <tr><td class="beaglebuddy">CD </td><td class="beaglebuddy">&nbsp;</td><td class="beaglebuddy">CD                                               </td></tr>
 *                                                                             <tr><td class="beaglebuddy">CD </td><td class="beaglebuddy"> /A   </td><td class="beaglebuddy">Analog transfer from media                       </td></tr>
 *                                                                             <tr><td class="beaglebuddy">CD </td><td class="beaglebuddy">/DD   </td><td class="beaglebuddy">DDD                                              </td></tr>
 *                                                                             <tr><td class="beaglebuddy">CD </td><td class="beaglebuddy">/AD   </td><td class="beaglebuddy">ADD                                              </td></tr>
 *                                                                             <tr><td class="beaglebuddy">CD </td><td class="beaglebuddy">/AA   </td><td class="beaglebuddy">AAD                                              </td></tr>
 *                                                                             <tr><td class="beaglebuddy">LD </td><td class="beaglebuddy">&nbsp;</td><td class="beaglebuddy">Laserdisc                                        </td></tr>
 *                                                                             <tr><td class="beaglebuddy">LD </td><td class="beaglebuddy">/A    </td><td class="beaglebuddy">Analog transfer from media                       </td></tr>
 *                                                                             <tr><td class="beaglebuddy">TT </td><td class="beaglebuddy">&nbsp;</td><td class="beaglebuddy">Turntable records                                </td></tr>
 *                                                                             <tr><td class="beaglebuddy">TT </td><td class="beaglebuddy">/33   </td><td class="beaglebuddy">33.33 rpm                                        </td></tr>
 *                                                                             <tr><td class="beaglebuddy">TT </td><td class="beaglebuddy">/45   </td><td class="beaglebuddy">45 rpm                                           </td></tr>
 *                                                                             <tr><td class="beaglebuddy">TT </td><td class="beaglebuddy">/71   </td><td class="beaglebuddy">71.29 rpm                                        </td></tr>
 *                                                                             <tr><td class="beaglebuddy">TT </td><td class="beaglebuddy">/76   </td><td class="beaglebuddy">76.59 rpm                                        </td></tr>
 *                                                                             <tr><td class="beaglebuddy">TT </td><td class="beaglebuddy">/78   </td><td class="beaglebuddy">78.26 rpm                                        </td></tr>
 *                                                                             <tr><td class="beaglebuddy">TT </td><td class="beaglebuddy">/80   </td><td class="beaglebuddy">80 rpm                                           </td></tr>
 *                                                                             <tr><td class="beaglebuddy">MD </td><td class="beaglebuddy">&nbsp;</td><td class="beaglebuddy">MiniDisc                                         </td></tr>
 *                                                                             <tr><td class="beaglebuddy">MD </td><td class="beaglebuddy">/A    </td><td class="beaglebuddy">Analog transfer from media                       </td></tr>
 *                                                                             <tr><td class="beaglebuddy">DAT</td><td class="beaglebuddy">&nbsp;</td><td class="beaglebuddy">DAT                                              </td></tr>
 *                                                                             <tr><td class="beaglebuddy">DAT</td><td class="beaglebuddy">/A    </td><td class="beaglebuddy">Analog transfer from media                       </td></tr>
 *                                                                             <tr><td class="beaglebuddy">DAT</td><td class="beaglebuddy">/1    </td><td class="beaglebuddy">standard, 48 kHz/16 bits, linear                 </td></tr>
 *                                                                             <tr><td class="beaglebuddy">DAT</td><td class="beaglebuddy">/2    </td><td class="beaglebuddy">mode 2, 32 kHz/16 bits, linear                   </td></tr>
 *                                                                             <tr><td class="beaglebuddy">DAT</td><td class="beaglebuddy">/3    </td><td class="beaglebuddy">mode 3, 32 kHz/12 bits, nonlinear, low speed     </td></tr>
 *                                                                             <tr><td class="beaglebuddy">DAT</td><td class="beaglebuddy">/4    </td><td class="beaglebuddy">mode 4, 32 kHz/12 bits, 4 channels               </td></tr>
 *                                                                             <tr><td class="beaglebuddy">DAT</td><td class="beaglebuddy">/5    </td><td class="beaglebuddy">mode 5, 44.1 kHz/16 bits, linear                 </td></tr>
 *                                                                             <tr><td class="beaglebuddy">DAT</td><td class="beaglebuddy">/6    </td><td class="beaglebuddy">mode 6, 44.1 kHz/16 bits, 'wide track' play      </td></tr>
 *                                                                             <tr><td class="beaglebuddy">DCC</td><td class="beaglebuddy">&nbsp;</td><td class="beaglebuddy">DCC                                              </td></tr>
 *                                                                             <tr><td class="beaglebuddy">DCC</td><td class="beaglebuddy">/A    </td><td class="beaglebuddy">Analog transfer from media                       </td></tr>
 *                                                                             <tr><td class="beaglebuddy">DVD</td><td class="beaglebuddy">&nbsp;</td><td class="beaglebuddy">DVD                                              </td></tr>
 *                                                                             <tr><td class="beaglebuddy">DVD</td><td class="beaglebuddy">/A    </td><td class="beaglebuddy">Analog transfer from media                       </td></tr>
 *                                                                             <tr><td class="beaglebuddy">TV </td><td class="beaglebuddy">&nbsp;</td><td class="beaglebuddy">Television                                       </td></tr>
 *                                                                             <tr><td class="beaglebuddy">TV </td><td class="beaglebuddy">/PAL  </td><td class="beaglebuddy">PAL                                              </td></tr>
 *                                                                             <tr><td class="beaglebuddy">TV </td><td class="beaglebuddy">/NTSC </td><td class="beaglebuddy">NTSC                                             </td></tr>
 *                                                                             <tr><td class="beaglebuddy">TV </td><td class="beaglebuddy">/SECAM</td><td class="beaglebuddy">SECAM                                            </td></tr>
 *                                                                             <tr><td class="beaglebuddy">VID</td><td class="beaglebuddy">&nbsp;</td><td class="beaglebuddy">Video                                            </td></tr>
 *                                                                             <tr><td class="beaglebuddy">VID</td><td class="beaglebuddy">/PAL  </td><td class="beaglebuddy">PAL                                              </td></tr>
 *                                                                             <tr><td class="beaglebuddy">VID</td><td class="beaglebuddy">/NTSC </td><td class="beaglebuddy">NTSC                                             </td></tr>
 *                                                                             <tr><td class="beaglebuddy">VID</td><td class="beaglebuddy">/SECAM</td><td class="beaglebuddy">SECAM                                            </td></tr>
 *                                                                             <tr><td class="beaglebuddy">VID</td><td class="beaglebuddy">/VHS  </td><td class="beaglebuddy">VHS                                              </td></tr>
 *                                                                             <tr><td class="beaglebuddy">VID</td><td class="beaglebuddy">/SVHS </td><td class="beaglebuddy">S-VHS                                            </td></tr>
 *                                                                             <tr><td class="beaglebuddy">VID</td><td class="beaglebuddy">/BETA </td><td class="beaglebuddy">BETAMAX                                          </td></tr>
 *                                                                             <tr><td class="beaglebuddy">RAD</td><td class="beaglebuddy">&nbsp;</td><td class="beaglebuddy">Radio                                            </td></tr>
 *                                                                             <tr><td class="beaglebuddy">RAD</td><td class="beaglebuddy">/FM   </td><td class="beaglebuddy">FM                                               </td></tr>
 *                                                                             <tr><td class="beaglebuddy">RAD</td><td class="beaglebuddy">/AM   </td><td class="beaglebuddy">AM                                               </td></tr>
 *                                                                             <tr><td class="beaglebuddy">RAD</td><td class="beaglebuddy">/LW   </td><td class="beaglebuddy">LW                                               </td></tr>
 *                                                                             <tr><td class="beaglebuddy">RAD</td><td class="beaglebuddy">/MW   </td><td class="beaglebuddy">MW                                               </td></tr>
 *                                                                             <tr><td class="beaglebuddy">TEL</td><td class="beaglebuddy">&nbsp;</td><td class="beaglebuddy">Telephone                                        </td></tr>
 *                                                                             <tr><td class="beaglebuddy">TEL</td><td class="beaglebuddy">/I    </td><td class="beaglebuddy">ISDN                                             </td></tr>
 *                                                                             <tr><td class="beaglebuddy">MC </td><td class="beaglebuddy">&nbsp;</td><td class="beaglebuddy">MC (normal cassette)                             </td></tr>
 *                                                                             <tr><td class="beaglebuddy">MC </td><td class="beaglebuddy">  /4  </td><td class="beaglebuddy">4.75 cm/s (normal speed for a two sided cassette)</td></tr>
 *                                                                             <tr><td class="beaglebuddy">MC </td><td class="beaglebuddy">  /9  </td><td class="beaglebuddy">9.5 cm/s                                         </td></tr>
 *                                                                             <tr><td class="beaglebuddy">MC </td><td class="beaglebuddy">  /I  </td><td class="beaglebuddy">Type I cassette (ferric/normal)                  </td></tr>
 *                                                                             <tr><td class="beaglebuddy">MC </td><td class="beaglebuddy"> /II  </td><td class="beaglebuddy">Type II cassette (chrome)                        </td></tr>
 *                                                                             <tr><td class="beaglebuddy">MC </td><td class="beaglebuddy">/III  </td><td class="beaglebuddy">Type III cassette (ferric chrome)                </td></tr>
 *                                                                             <tr><td class="beaglebuddy">MC </td><td class="beaglebuddy"> /IV  </td><td class="beaglebuddy">Type IV cassette (metal)                         </td></tr>
 *                                                                             <tr><td class="beaglebuddy">REE</td><td class="beaglebuddy">&nbsp;</td><td class="beaglebuddy">Reel                                             </td></tr>
 *                                                                             <tr><td class="beaglebuddy">REE</td><td class="beaglebuddy">  /9  </td><td class="beaglebuddy">9.5 cm/s                                         </td></tr>
 *                                                                             <tr><td class="beaglebuddy">REE</td><td class="beaglebuddy"> /19  </td><td class="beaglebuddy">19 cm/s                                          </td></tr>
 *                                                                             <tr><td class="beaglebuddy">REE</td><td class="beaglebuddy"> /38  </td><td class="beaglebuddy">38 cm/s                                          </td></tr>
 *                                                                             <tr><td class="beaglebuddy">REE</td><td class="beaglebuddy"> /76  </td><td class="beaglebuddy">76 cm/s                                          </td></tr>
 *                                                                             <tr><td class="beaglebuddy">REE</td><td class="beaglebuddy">  /I  </td><td class="beaglebuddy">Type I cassette (ferric/normal)                  </td></tr>
 *                                                                             <tr><td class="beaglebuddy">REE</td><td class="beaglebuddy"> /II  </td><td class="beaglebuddy">Type II cassette (chrome)                        </td></tr>
 *                                                                             <tr><td class="beaglebuddy">REE</td><td class="beaglebuddy">/III  </td><td class="beaglebuddy">Type III cassette (ferric chrome)                </td></tr>
 *                                                                             <tr><td class="beaglebuddy">REE</td><td class="beaglebuddy"> /IV  </td><td class="beaglebuddy">Type IV cassette (metal)                         </td></tr>
 *                                                                          </tbody>
 *                                                                       </table>
 *                                                                       </td></tr>
 *          <tr><td class="beaglebuddy">TMOO</td><td class="beaglebuddy">The 'Mood' frame is to reflect the mood of the audio with a few keywords, e.g. "Romantic" or "Sad".                                                   </td></tr>
 *          <tr><td class="beaglebuddy">TOAL</td><td class="beaglebuddy">The 'Original album/movie/show title' frame is intended for the title of the original recording (or source of sound), if for example the music in the
 *                                                                       file should be a cover of a previously released song.                                                                                                 </td></tr>
 *          <tr><td class="beaglebuddy">TOFN</td><td class="beaglebuddy">The 'Original filename' frame contains the preferred filename for the file, since some media doesn't allow the desired length of the filename. The
 *                                                                       filename is case sensitive and includes its suffix.                                                                                                   </td></tr>
 *          <tr><td class="beaglebuddy">TOLY</td><td class="beaglebuddy">The 'Original lyricist(s)/text writer(s)' frame is intended for the text writer(s) of the original recording, if for example the music in the file
 *                                                                       should be a cover of a previously released song. The text writers are seperated with the "/" character.                                               </td></tr>
 *          <tr><td class="beaglebuddy">TOPE</td><td class="beaglebuddy">The 'Original artist(s)/performer(s)' frame is intended for the performer(s) of the original recording, if for example the music in the file should be
 *                                                                       a cover of a previously released song. The performers are seperated with the "/" character.                                                           </td></tr>
 *          <tr><td class="beaglebuddy">TOWN</td><td class="beaglebuddy">The 'File owner/licensee' frame contains the name of the owner or licensee of the file and it's contents.                                             </td></tr>
 *          <tr><td class="beaglebuddy">TPE1</td><td class="beaglebuddy">The 'Lead artist(s)/Lead performer(s)/Soloist(s)/Performing group' is used for the main artist(s). They are separated with the "/" character.         </td></tr>
 *          <tr><td class="beaglebuddy">TPE2</td><td class="beaglebuddy">The 'Band/Orchestra/Accompaniment' frame is used for additional information about the performers in the recording.                                    </td></tr>
 *          <tr><td class="beaglebuddy">TPE3</td><td class="beaglebuddy">The 'Conductor' frame is used for the name of the conductor.                                                                                          </td></tr>
 *          <tr><td class="beaglebuddy">TPE4</td><td class="beaglebuddy">The 'Interpreted, remixed, or otherwise modified by' frame contains more information about the people behind a remix and similar interpretations of
 *                                                                       another existing piece.                                                                                                                               </td></tr>
 *          <tr><td class="beaglebuddy">TPOS</td><td class="beaglebuddy">The 'Part of a set' frame is a numeric string that describes which part of a set the audio came from. This frame is used if the source described in
 *                                                                       the "TALB" frame is divided into several mediums, e.g. a double CD. The value may be extended with a "/" character and a numeric string containing
 *                                                                       the total number of parts in the set. E.g. "1/2".                                                                                                     </td></tr>
 *          <tr><td class="beaglebuddy">TPRO</td><td class="beaglebuddy">The 'Produced notice' frame is intended for the production copyright holder of the original sound, not the audio
 *                                                                       file itself. The absence of this frame means only that the production copyright information is unavailable or has been removed, and must
 *                                                                       not be interpreted to mean that the audio is public domain. The text must begin with a year followed by a space character (making five characters).
 *                                                                       Every time this field is displayed the field must be preceded with "Produced " (P) " ", where (P) is one character showing a P in a circle.           </td></tr>
 *          <tr><td class="beaglebuddy">TPUB</td><td class="beaglebuddy">The 'Publisher' frame simply contains the name of the label or publisher.                                                                             </td></tr>
 *          <tr><td class="beaglebuddy">TRCK</td><td class="beaglebuddy">The 'Track number/Position in set' frame is a numeric string containing the order number of the audio-file on its original recording. This may be
 *                                                                       extended with a "/" character and a numeric string containing the total numer of tracks/elements on the original recording. E.g. "4/9".               </td></tr>
 *          <tr><td class="beaglebuddy">TRSN</td><td class="beaglebuddy">The 'Internet radio station name' frame contains the name of the internet radio station from which the audio is streamed.                             </td></tr>
 *          <tr><td class="beaglebuddy">TRSO</td><td class="beaglebuddy">The 'Internet radio station owner' frame contains the name of the owner of the internet radio station from which the audio is streamed.               </td></tr>
 *          <tr><td class="beaglebuddy">TSOA</td><td class="beaglebuddy">The 'Album sort order' frame is a intended to be used to define the name to be used to sort this mp3 song instead of the album name (TALB). E.g. an
 *                                                                       album named "A Soundtrack" might preferably be sorted as "Soundtrack".                                                                                </td></tr>
 *          <tr><td class="beaglebuddy">TSOP</td><td class="beaglebuddy">The 'Performer sort order' frame defines a string which should be used instead of the performer (TPE2) for sorting purposes.                          </td></tr>
 *          <tr><td class="beaglebuddy">TSOT</td><td class="beaglebuddy">The 'Title sort order' frame defines a string which should be used instead of the title (TIT2) for sorting purposes.                                  </td></tr>
 *          <tr><td class="beaglebuddy">TSRC</td><td class="beaglebuddy">The 'ISRC' frame should contain the International Standard Recording Code (ISRC) (12 characters).                                                     </td></tr>
 *          <tr><td class="beaglebuddy">TSSE</td><td class="beaglebuddy">The 'Software/Hardware and settings used for encoding' frame includes the used audio encoder and its settings when the file was encoded. Hardware
 *                                                                       refers to hardware encoders, not the computer on which a program was run.                                                                             </td></tr>
 *       </tbody>
 *    </table>
 * </p>
 * <p class="beaglebuddy">
 * There may only be one <i>text information</i> frame of its kind in an ID3v2.4 {@link com.beaglebuddy.id3.v24.ID3v24Tag tag}
 * </p>
 * @see com.beaglebuddy.id3.v24.ID3v24Frame
 * @see <a href="http://id3.org/id3v2.4.0-frames"  target="_blank">ID3 tag version 2.4.0 standard</a>
 * @see <a href="http://en.wikipedia.org/wiki/ID3" target="_blank">wikipedia history of ID3 tags</a>
 */
public class ID3v24FrameBodyTextInformation extends ID3v24FrameBody
{
   // data members
   private Encoding encoding;      // charset used to encode the text field
   private String   text;          // text




   /**
    * The constructor is called when creating a new frame.
    * The default values used are:
    * <ul>
    *    <li>ISO-8859-1 encoding</li>
    *    <li>empty text</li>
    * </ul>
    * <br/><br/>
    * @param frameType   It must be one of the 45 following ID3v2.4 Text frame types.  Note: All text frame types begin with the letter 'T'.<br/><br/>
    * <table class="beaglebuddy">
    *    <caption><b>ID3v2.4 Text Frame Typess</b></caption>
    *    <thead>
    *       <tr><th class="beaglebuddy">&nbsp;</th><th class="beaglebuddy">Frame Type</th><th class="beaglebuddy">Frame id</th><th class="beaglebuddy">Description</th></tr>
    *    </thead>
    *    <tbody>
    *       <tr><td class="beaglebuddy">1. </td><td class="beaglebuddy">ALBUM_TITLE                           </td><td class="beaglebuddy">TALB</td><td class="beaglebuddy">album/movie/show title                               </td></tr>
    *       <tr><td class="beaglebuddy">2. </td><td class="beaglebuddy">BEATS_PER_MINUTE                      </td><td class="beaglebuddy">TBPM</td><td class="beaglebuddy">bpm (beats per minute)                               </td></tr>
    *       <tr><td class="beaglebuddy">3. </td><td class="beaglebuddy">COMPOSER                              </td><td class="beaglebuddy">TCOM</td><td class="beaglebuddy">composer                                             </td></tr>
    *       <tr><td class="beaglebuddy">4. </td><td class="beaglebuddy">CONTENT_TYPE                          </td><td class="beaglebuddy">TCON</td><td class="beaglebuddy">content type                                         </td></tr>
    *       <tr><td class="beaglebuddy">5. </td><td class="beaglebuddy">COPYRIGHT_MESSAGE                     </td><td class="beaglebuddy">TCOP</td><td class="beaglebuddy">copyright message                                    </td></tr>
    *       <tr><td class="beaglebuddy">6. </td><td class="beaglebuddy">ENCODING_TIME                         </td><td class="beaglebuddy">TDEN</td><td class="beaglebuddy">encoding date/time                                   </td></tr>
    *       <tr><td class="beaglebuddy">7. </td><td class="beaglebuddy">PLAYLIST_DELAY                        </td><td class="beaglebuddy">TDLY</td><td class="beaglebuddy">playlist delay                                       </td></tr>
    *       <tr><td class="beaglebuddy">8. </td><td class="beaglebuddy">ORIGINAL_RELEASE_TIME                 </td><td class="beaglebuddy">TDOR</td><td class="beaglebuddy">original release date/time                           </td></tr>
    *       <tr><td class="beaglebuddy">9. </td><td class="beaglebuddy">RECORDING_TIME                        </td><td class="beaglebuddy">TDRC</td><td class="beaglebuddy">recording date/time                                  </td></tr>
    *       <tr><td class="beaglebuddy">10.</td><td class="beaglebuddy">RELEASE_TIME                          </td><td class="beaglebuddy">TDRL</td><td class="beaglebuddy">release date/time                                    </td></tr>
    *       <tr><td class="beaglebuddy">11.</td><td class="beaglebuddy">TAGGING_TIME                          </td><td class="beaglebuddy">TDTG</td><td class="beaglebuddy">tagging date/time                                    </td></tr>
    *       <tr><td class="beaglebuddy">12.</td><td class="beaglebuddy">ENCODED_BY                            </td><td class="beaglebuddy">TENC</td><td class="beaglebuddy">encoded by                                           </td></tr>
    *       <tr><td class="beaglebuddy">13.</td><td class="beaglebuddy">LYRICIST                              </td><td class="beaglebuddy">TEXT</td><td class="beaglebuddy">lyricist/text writer                                 </td></tr>
    *       <tr><td class="beaglebuddy">14.</td><td class="beaglebuddy">FILE_TYPE                             </td><td class="beaglebuddy">TFLT</td><td class="beaglebuddy">file type                                            </td></tr>
    *       <tr><td class="beaglebuddy">15.</td><td class="beaglebuddy">INVOLVED_PEOPLE_LIST                  </td><td class="beaglebuddy">TIPL</td><td class="beaglebuddy">involved people list                                 </td></tr>
    *       <tr><td class="beaglebuddy">16.</td><td class="beaglebuddy">CONTENT_GROUP_DESCRIPTION             </td><td class="beaglebuddy">TIT1</td><td class="beaglebuddy">content group description                            </td></tr>
    *       <tr><td class="beaglebuddy">17.</td><td class="beaglebuddy">SONG_TITLE                            </td><td class="beaglebuddy">TIT2</td><td class="beaglebuddy">title/songname/content description                   </td></tr>
    *       <tr><td class="beaglebuddy">18.</td><td class="beaglebuddy">SUBTITLE_REFINEMENT                   </td><td class="beaglebuddy">TIT3</td><td class="beaglebuddy">subtitle/description refinement                      </td></tr>
    *       <tr><td class="beaglebuddy">19.</td><td class="beaglebuddy">INITIAL_KEY                           </td><td class="beaglebuddy">TKEY</td><td class="beaglebuddy">initial key                                          </td></tr>
    *       <tr><td class="beaglebuddy">20.</td><td class="beaglebuddy">LANGUAGE                              </td><td class="beaglebuddy">TLAN</td><td class="beaglebuddy">language(s)                                          </td></tr>
    *       <tr><td class="beaglebuddy">21.</td><td class="beaglebuddy">LENGTH                                </td><td class="beaglebuddy">TLEN</td><td class="beaglebuddy">length of the song (in ms)                           </td></tr>
    *       <tr><td class="beaglebuddy">22.</td><td class="beaglebuddy">MUSICIANS_CREDIT_LIST                 </td><td class="beaglebuddy">TMCL</td><td class="beaglebuddy">musician credits lis                                 </td></tr>
    *       <tr><td class="beaglebuddy">23.</td><td class="beaglebuddy">MEDIA_TYPE                            </td><td class="beaglebuddy">TMED</td><td class="beaglebuddy">media type                                           </td></tr>
    *       <tr><td class="beaglebuddy">24.</td><td class="beaglebuddy">MOOD                                  </td><td class="beaglebuddy">TMOO</td><td class="beaglebuddy">mood                                                 </td></tr>
    *       <tr><td class="beaglebuddy">25.</td><td class="beaglebuddy">ORIGINAL_ALBUM_TITLE                  </td><td class="beaglebuddy">TOAL</td><td class="beaglebuddy">original album/movie/show title                      </td></tr>
    *       <tr><td class="beaglebuddy">26.</td><td class="beaglebuddy">ORIGINAL_FILENAME                     </td><td class="beaglebuddy">TOFN</td><td class="beaglebuddy">original filename                                    </td></tr>
    *       <tr><td class="beaglebuddy">27.</td><td class="beaglebuddy">ORIGINAL_LYRICIST                     </td><td class="beaglebuddy">TOLY</td><td class="beaglebuddy">original lyricist(s)/text writer(s)                  </td></tr>
    *       <tr><td class="beaglebuddy">28.</td><td class="beaglebuddy">ORIGINAL_ARTIST                       </td><td class="beaglebuddy">TOPE</td><td class="beaglebuddy">original artist(s)/performer(s)                      </td></tr>
    *       <tr><td class="beaglebuddy">29.</td><td class="beaglebuddy">FILE_OWNER                            </td><td class="beaglebuddy">TOWN</td><td class="beaglebuddy">file owner/licensee                                  </td></tr>
    *       <tr><td class="beaglebuddy">30.</td><td class="beaglebuddy">LEAD_PERFORMER                        </td><td class="beaglebuddy">TPE1</td><td class="beaglebuddy">lead performer(s)/soloist(s)                         </td></tr>
    *       <tr><td class="beaglebuddy">31.</td><td class="beaglebuddy">BAND                                  </td><td class="beaglebuddy">TPE2</td><td class="beaglebuddy">band/orchestra/accompaniment                         </td></tr>
    *       <tr><td class="beaglebuddy">32.</td><td class="beaglebuddy">CONDUCTOR                             </td><td class="beaglebuddy">TPE3</td><td class="beaglebuddy">conductor/performer refinement                       </td></tr>
    *       <tr><td class="beaglebuddy">33.</td><td class="beaglebuddy">INTERPRETED_MODIFIED_BY               </td><td class="beaglebuddy">TPE4</td><td class="beaglebuddy">interpreted, remixed, or otherwise modified by       </td></tr>
    *       <tr><td class="beaglebuddy">34.</td><td class="beaglebuddy">PART_OF_A_SET                         </td><td class="beaglebuddy">TPOS</td><td class="beaglebuddy">part of a set                                        </td></tr>
    *       <tr><td class="beaglebuddy">35.</td><td class="beaglebuddy">PRODUCED_NOTICE                       </td><td class="beaglebuddy">TPRO</td><td class="beaglebuddy">produced notice                                      </td></tr>
    *       <tr><td class="beaglebuddy">36.</td><td class="beaglebuddy">PUBLISHER                             </td><td class="beaglebuddy">TPUB</td><td class="beaglebuddy">publisher                                            </td></tr>
    *       <tr><td class="beaglebuddy">37.</td><td class="beaglebuddy">TRACK_NUMBER                          </td><td class="beaglebuddy">TRCK</td><td class="beaglebuddy">track number/position in set                         </td></tr>
    *       <tr><td class="beaglebuddy">38.</td><td class="beaglebuddy">INTERNET_RADIO_STATION_NAME           </td><td class="beaglebuddy">TRSN</td><td class="beaglebuddy">internet radio station name                          </td></tr>
    *       <tr><td class="beaglebuddy">39.</td><td class="beaglebuddy">INTERNET_RADIO_STATION_OWNER          </td><td class="beaglebuddy">TRSO</td><td class="beaglebuddy">internet radio station owner                         </td></tr>
    *       <tr><td class="beaglebuddy">40.</td><td class="beaglebuddy">ALBUM_SORT_ORDER                      </td><td class="beaglebuddy">TSOA</td><td class="beaglebuddy">album sort order                                     </td></tr>
    *       <tr><td class="beaglebuddy">41.</td><td class="beaglebuddy">PERFORMER_SORT_ORDER                  </td><td class="beaglebuddy">TSOP</td><td class="beaglebuddy">performer sort order                                 </td></tr>
    *       <tr><td class="beaglebuddy">42.</td><td class="beaglebuddy">TITLE_SORT_ORDER                      </td><td class="beaglebuddy">TSOT</td><td class="beaglebuddy">title sort order                                     </td></tr>
    *       <tr><td class="beaglebuddy">43.</td><td class="beaglebuddy">INTERNATIONAL_STANDARD_RECORDING_CODE </td><td class="beaglebuddy">TSRC</td><td class="beaglebuddy">isrc (international standard recording code)         </td></tr>
    *       <tr><td class="beaglebuddy">44.</td><td class="beaglebuddy">SOFTWARE_HARDWARE_ENCODING_SETTINGS   </td><td class="beaglebuddy">TSSE</td><td class="beaglebuddy">software/hardware and settings used for encoding     </td></tr>
    *       <tr><td class="beaglebuddy">45.</td><td class="beaglebuddy">SET_SUBTITLE                          </td><td class="beaglebuddy">TSST</td><td class="beaglebuddy">set subtitle                                         </td></tr>
    *    </tbody>
    * </table>
    * @throws IllegalArgumentException   if the frame type is not a Text frame type.  ie, if the frame id does not start with the letter 'T'.
    */
   public ID3v24FrameBodyTextInformation(FrameType frameType) throws IllegalArgumentException
   {
      this(frameType, Encoding.ISO_8859_1, " ");
   }

   /**
    * This constructor is called when creating a new frame.
    * <br/><br/>
    * @param frameType  one of the 45 ID3v2.4 Text frame types.  See {@link #ID3v24FrameBodyTextInformation(FrameType)}
    * @param encoding   character set used to encode the text.
    * @param text       text.
    * @throws IllegalArgumentException   if the frame type is not a Text frame type.  ie, if the frame id does not start with the letter 'T'.
    */
   public ID3v24FrameBodyTextInformation(FrameType frameType, Encoding encoding, String text) throws IllegalArgumentException
   {
      super(frameType);

      if (!frameType.getId().startsWith("T"))
         throw new IllegalArgumentException("Invalid ID3v2.4 text frame type, " + frameType + ".  All ID3v2.4 text frame id's start with the letter \'T\'.");

      setEncoding(encoding);
      setText    (text);
      dirty = true;      // values have not yet been saved to the frame body's internal byte buffer
   }

   /**
    * This constructor is called when reading in an existing frame from an .mp3 file.  This is then followed by a call to the parse() method.
    * @param inputStream    input stream pointing to a text information frame body in the .mp3 file.
    * @param frameType      the type of ID3v2.4 frame.
    * @param frameBodySize  size (in bytes) of the frame's body.
    * @throws IOException   if there is an error while reading the frame body.
    */
   public ID3v24FrameBodyTextInformation(InputStream inputStream, FrameType frameType, int frameBodySize) throws IOException
   {
      super(inputStream, frameType,  frameBodySize);
   }

   /**
    * parses the raw bytes of the frame body and stores the parsed values in the frame's fields.
    * @throws IllegalArgumentException  if an invalid value is detected while parsing the frame body's raw bytes.
    */
   @Override
   public void parse() throws IllegalArgumentException
   {
      try
      {
         setEncoding(Encoding.valueOf(buffer[0]));
      }
      catch (IllegalArgumentException ex)
      {  // ignore the bad value and set it to ISO-8859-1 so we can continue parsing the tag
         setEncoding(Encoding.ISO_8859_1);
      }
      // read in the text
      setText(new String(buffer, 1, buffer.length-1, encoding.getCharacterSet()).trim());
      dirty = false;    // we just read in the frame info, so the frame body's internal byte buffer is up to date
   }

   /**
    * gets the character encoding of the text.
    * @return the character encoding of the text.
    * @see #setEncoding(Encoding)
    */
   public Encoding getEncoding()
   {
      return encoding;
   }

   /**
    * sets the character encoding of the text.
    * @param encoding    the character set used to encode the text.  Only ISO 8859-1 and UTF-16 are allowed.
    * @see #getEncoding()
    */
   public void setEncoding(Encoding encoding)
   {
      if (encoding == null)
         throw new IllegalArgumentException("The encoding field in the ID3v2.4 " + frameType.getId() + " frame may not be null.");

      this.encoding = encoding;
      this.dirty    = true;
   }

   /**
    * gets the text.
    * @return the text.
    * @see #setText(String)
    */
   public String getText()
   {
      return text;
   }

   /**
    * sets the text.
    * @param text   the text.
    * @throws IllegalArgumentException  if the text is null or an empty string.
    * @see #getText()
    */
   public void setText(String text)
   {
      if (text == null || text.length() == 0)
         throw new IllegalArgumentException("The text field in the ID3v2.4 " + frameType.getId() + " frame may not be empty.");

      this.dirty = true;
      this.text  = text;
   }

   /**
    * gets the text as a list.  The ID3v2.4 specification allows the text field to contain multiple strings, each separated by the encoding's
    * null terminator character (0x00, or 0x00 0x00), depending on the character set used to encode the string.  While this doesn't make sense for most of the text frames,
    * it is used for things like the* <i>Involved People List</i> and the <i>Musician Credits List</i> frames.
    * @return the text as a list of strings.
    * @see #setTextList(List)
    */
   public List<String> getTextList()
   {
      List<String> textList = new Vector<String>();

      for(int index=1, nextNullTerminator=1, length=0; index < buffer.length; index += (length + encoding.getNumBytesInNullTerminator()))
      {
         nextNullTerminator = getNextNullTerminator(buffer, index, encoding);
         // just in case the string list is not formatted correctly with an encoding null terminator between each string,
         // don't go past the end of frame body buffer looking for a null terminator character
         length = nextNullTerminator <= buffer.length ? nextNullTerminator - index : buffer.length - index;
         textList.add(new String(buffer, index, length, encoding.getCharacterSet()).trim());
      }
      return textList;
   }

   /**
    * sets the text from the list of strings.
    * @param textList   a list of strings.
    * @throws IllegalArgumentException  if the text list is null or empty or only contains empty strings.
    * @see #getText()
    */
   public void setTextList(List<String> textList)
   {
      if (textList == null || textList.size() == 0)
         throw new IllegalArgumentException("The text field in the ID3v2.4 " + frameType.getId() + " frame may not be empty.");

      StringBuffer stringBuffer = new StringBuffer();

      for(String string : textList)
      {
         stringBuffer.append(string);
//       System.out.println("         1. string buffer length: " + stringBuffer.length());
         stringBuffer.append((char)0);
//       System.out.println("         2. string buffer length: " + stringBuffer.length());
         if (encoding.getNumBytesInNullTerminator() == 2)
            stringBuffer.append((char)0);
      }
      // TODO: Make sure this works
      this.text  = stringBuffer.toString();
      this.dirty = true;
   }

   /**
    * If the frame body's values have been modified, then resize the raw binary buffer and store the new values there.
    * When finished, the dirty flag is reset to indicate that the buffer is up to date, and the frame is now ready to be saved to the .mp3 file.
    */
   @Override
   public void setBuffer()
   {
      if (isDirty())
      {
         byte[] textBytes = stringToBytes(encoding, text);
         buffer = new byte[1 + textBytes.length];

         buffer[0] = (byte)encoding.ordinal();
         System.arraycopy(textBytes, 0, buffer, 1, textBytes.length);
         dirty = false;
      }
   }

   /**
    * gets a string representation of the <i>text information</i> frame body.
    * @return a string representation of the frame body.
    */
   @Override
   public String toString()
   {
      StringBuffer buffer = new StringBuffer();

      buffer.append("frame body: text information\n");
      buffer.append("   bytes...: " + this.buffer.length   + " bytes\n");
      buffer.append("             " + hex(this.buffer, 13) + "\n");
      buffer.append("   encoding: " + encoding             + "\n");
      List<String> list = getTextList();
      if ((frameType == FrameType.INVOLVED_PEOPLE_LIST || frameType == FrameType.MUSICIANS_CREDIT_LIST) && list.size() >= 2)
      {
         buffer.append("   text....: " );
         String[] array     = new String[list.size()];
                  array     = list.toArray(array);
         int      maxLength = 0;

         // find the largest text string in the list
         for(String text : list)
            if (text.length() > maxLength)
               maxLength = text.length();

         // display the first string pair
         buffer.append(array[0] + pad(maxLength - array[0].length()) + "  -  " + array[1] + "\n");
         // display the remaining string pairs
         for(int i=2; i<array.length; i+=2)
            buffer.append(pad(13) + array[i] + pad(maxLength - array[i].length()) + "  -  " + (i + 1 < array.length ? array[i+1] : "") + "\n");
      }
      else
      {
         buffer.append("   text....: " + text                 + "\n");
      }
      return buffer.toString();
   }
}
